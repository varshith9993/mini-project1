package aadhaar.web;

import aadhaar.backend.AwarenessService;
import aadhaar.backend.AwarenessService.AppContent;
import aadhaar.backend.AwarenessService.CheckAccountSection;
import aadhaar.backend.AwarenessService.LanguageOption;
import aadhaar.backend.AwarenessService.QuizQuestion;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;

public final class AwarenessWebServer {
    public enum Section {
        HOME("home"),
        LEARN("learn"),
        COMPARISON("comparison"),
        CHECK_ACCOUNT("check-account"),
        VIDEOS("videos"),
        QUIZ("quiz"),
        RESOURCES("resources"),
        AI_ASSISTANT("ai-assistant"),
        GOVERNMENT("government");

        private final String routeKey;

        Section(String routeKey) {
            this.routeKey = routeKey;
        }

        public String routeKey() {
            return routeKey;
        }

        public static Section from(String value) {
            if (value == null || value.isBlank()) {
                return HOME;
            }
            for (Section section : values()) {
                if (section.routeKey.equalsIgnoreCase(value)) {
                    return section;
                }
            }
            return HOME;
        }
    }

    public record CheckAssessment(String title, String body, String toneClass) {
    }

    public record AadhaarLookupResult(String aadhaarNumber, String title, String body, String toneClass) {
    }

    public record CheckFormState(String aadhaarNumber, boolean linked, boolean seeded, boolean preferredBank,
            boolean schemeUsesDbt) {
    }

    public record QuizAssessment(long seed, int step, int score, int total, int selectedIndex, boolean showFeedback,
            boolean completed, String explanation) {
    }

    public record AiAssistantState(String selectedFaqId, String aiQuestion, String aiAnswer, String aiError) {
    }

    public record ViewState(
            Section section,
            LanguageOption language,
            CheckFormState checkFormState,
            CheckAssessment checkAssessment,
            AadhaarLookupResult aadhaarLookupResult,
            QuizAssessment quizAssessment,
            AiAssistantState aiAssistantState) {
    }

    private final AwarenessService service;
    private final Path assetsDir;
    private final int port;
    private final boolean openBrowser;
    private final PageRenderer renderer;
    private HttpServer server;

    public AwarenessWebServer(AwarenessService service, Path assetsDir, int port, boolean openBrowser) {
        this.service = Objects.requireNonNull(service);
        this.assetsDir = Objects.requireNonNull(assetsDir).toAbsolutePath().normalize();
        this.port = port;
        this.openBrowser = openBrowser;
        this.renderer = new PageRenderer();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/assets/", new AssetHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        if (openBrowser) {
            new Thread(this::openDefaultBrowser, "browser-opener").start();
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public String baseUrl() {
        return "http://localhost:" + port + "/";
    }

    private void openDefaultBrowser() {
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().browse(new URI(baseUrl()));
            }
        } catch (Exception ignored) {
        }
    }

    private final class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                ViewState viewState = resolveViewState(exchange);
                AppContent content = service.getContent(LanguageOption.ENGLISH);
                String page = renderer.renderPage(viewState, content);
                writeString(exchange, 200, "text/html; charset=UTF-8", page);
            } catch (RuntimeException exception) {
                writeString(exchange, 500, "text/html; charset=UTF-8",
                        renderer.renderErrorPage(exception.getMessage()));
            } finally {
                exchange.close();
            }
        }

        private ViewState resolveViewState(HttpExchange exchange) throws IOException {
            Map<String, List<String>> params = parseParams(exchange.getRequestURI());
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                merge(params, parseFormBody(exchange.getRequestBody()));
            }

            Section section = Section.from(first(params, "section"));
            if (section == Section.GOVERNMENT) {
                section = Section.RESOURCES;
            }

            LanguageOption language = LanguageOption.ENGLISH;
            AppContent content = service.getContent(language);
            CheckFormState checkFormState = buildCheckFormState(params);
            CheckAssessment checkAssessment = null;
            AadhaarLookupResult aadhaarLookupResult = null;
            if (section == Section.CHECK_ACCOUNT && "POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                checkAssessment = buildCheckAssessment(params, content.checkAccount());
                aadhaarLookupResult = buildAadhaarLookupResult(checkFormState.aadhaarNumber());
            }

            QuizAssessment quizAssessment = buildQuizAssessment(params, exchange.getRequestMethod(),
                    content.quizQuestions());
            AiAssistantState aiAssistantState = buildAiAssistantState(section, params, exchange.getRequestMethod());
            return new ViewState(section, language, checkFormState, checkAssessment, aadhaarLookupResult,
                    quizAssessment, aiAssistantState);
        }
    }

    private final class AssetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String requestPath = exchange.getRequestURI().getPath().replaceFirst("^/assets/", "");
                Path requested = assetsDir.resolve(requestPath).normalize();
                if (!requested.startsWith(assetsDir) || !Files.isRegularFile(requested)) {
                    writeString(exchange, 404, "text/plain; charset=UTF-8", "Asset not found");
                    return;
                }
                byte[] bytes = Files.readAllBytes(requested);
                exchange.getResponseHeaders().set("Content-Type", contentType(requested));
                exchange.getResponseHeaders().set("Cache-Control", "no-store");
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            } finally {
                exchange.close();
            }
        }
    }

    private static CheckFormState buildCheckFormState(Map<String, List<String>> params) {
        return new CheckFormState(
                sanitizeAadhaar(first(params, "aadhaarNumber")),
                isChecked(params, "linked"),
                isChecked(params, "seeded"),
                isChecked(params, "preferredBank"),
                isChecked(params, "schemeUsesDbt"));
    }

    private static CheckAssessment buildCheckAssessment(Map<String, List<String>> params, CheckAccountSection section) {
        boolean linked = isChecked(params, "linked");
        boolean seeded = isChecked(params, "seeded");
        boolean preferredBank = isChecked(params, "preferredBank");
        boolean schemeUsesDbt = isChecked(params, "schemeUsesDbt");
        if (!linked) {
            return new CheckAssessment(section.needsVisitTitle(), section.needsVisitBody(), "danger");
        }
        if (!seeded) {
            return new CheckAssessment(section.linkedOnlyTitle(), section.linkedOnlyBody(), "warning");
        }
        if (!preferredBank || !schemeUsesDbt) {
            return new CheckAssessment(section.cautionTitle(), section.cautionBody(), "warning");
        }
        return new CheckAssessment(section.dbtEnabledTitle(), section.dbtEnabledBody(), "success");
    }

    private static AadhaarLookupResult buildAadhaarLookupResult(String aadhaarNumber) {
        if (aadhaarNumber == null || aadhaarNumber.isBlank()) {
            return null;
        }
        if (!isValidAadhaar(aadhaarNumber)) {
            return new AadhaarLookupResult(aadhaarNumber, "Invalid Aadhaar Number Format",
                    "Enter a valid 12-digit Aadhaar number. This website validates the format only; live linked status must still be checked from official resident services.",
                    "danger");
        }
        return new AadhaarLookupResult(aadhaarNumber, "Aadhaar Number Format Verified",
                "The number format looks valid. For the actual linked or not-linked bank result, continue to the official UIDAI and bank workflow listed on this page.",
                "success");
    }

    private static QuizAssessment buildQuizAssessment(Map<String, List<String>> params, String requestMethod,
            List<QuizQuestion> allQuestions) {
        String startQuiz = first(params, "startQuiz");
        String restartQuiz = first(params, "restartQuiz");
        String resetQuiz = first(params, "resetQuiz");
        if ("1".equals(startQuiz) || "1".equals(restartQuiz) || "1".equals(resetQuiz)) {
            if ("1".equals(resetQuiz)) {
                return new QuizAssessment(0L, -1, 0, 5, -1, false, false, "");
            }
            return new QuizAssessment(System.currentTimeMillis(), 0, 0, 5, -1, false, false, "");
        }

        long seed = parseLong(first(params, "quizSeed"), 0L);
        int step = parseInt(first(params, "quizStep"), -1);
        int score = parseInt(first(params, "quizScore"), 0);
        if (seed == 0L || step < 0) {
            return new QuizAssessment(0L, -1, 0, 5, -1, false, false, "");
        }

        List<QuizQuestion> questions = pickQuestions(allQuestions, seed, 5);
        if (!"POST".equalsIgnoreCase(requestMethod)) {
            return new QuizAssessment(seed, Math.min(step, questions.size() - 1), score, questions.size(), -1, false,
                    false, "");
        }

        int selectedIndex = parseInt(first(params, "quizOption"), -1);
        QuizQuestion question = questions.get(step);
        int updatedScore = score + (selectedIndex == question.correctIndex() ? 1 : 0);
        boolean completed = step >= questions.size() - 1;
        return new QuizAssessment(seed, step, updatedScore, questions.size(), selectedIndex, true, completed,
                question.explanation());
    }

    private static List<QuizQuestion> pickQuestions(List<QuizQuestion> allQuestions, long seed, int count) {
        List<QuizQuestion> shuffled = new ArrayList<>(allQuestions);
        Collections.shuffle(shuffled, new Random(seed));
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }

    private static AiAssistantState buildAiAssistantState(Section section, Map<String, List<String>> params,
            String method) {
        if (section != Section.AI_ASSISTANT) {
            return new AiAssistantState("", "", "", "");
        }
        String faq = blankToEmpty(first(params, "faq"));
        String question = blankToEmpty(first(params, "aiQuestion"));
        if (!faq.isBlank()) {
            return new AiAssistantState(faq, "", faqAnswer(faq), "");
        }
        if ("POST".equalsIgnoreCase(method) && !question.isBlank()) {
            return new AiAssistantState("", question, localAssistantAnswer(question), "");
        }
        return new AiAssistantState("", "", "", "");
    }

    private static String faqAnswer(String faq) {
        return switch (faq) {
            case "link-difference" ->
                "Aadhaar linked usually means the bank has associated Aadhaar with the account. DBT enabled is the stronger condition where the account is positioned for Aadhaar-based benefit routing through the active path.";
            case "how-to-check" ->
                "Use the official resident portal path first, verify the bank shown in the result, then confirm with the bank if the shown bank does not match the bank where you expect DBT credits.";
            case "multiple-banks" ->
                "If Aadhaar was shared with more than one bank over time, do not assume the newest or oldest account is active. Check the current official result and confirm the mapped destination with the bank.";
            case "bank-change" ->
                "After changing banks, submit the update at the intended bank and recheck later using the official resident-facing service before expecting new DBT credits there.";
            case "npci-mapper" ->
                "NPCI mapper is part of the explanation behind Aadhaar-based routing. It helps explain why a bank-linked account can still differ from the actual destination used for Aadhaar-based benefits.";
            case "dbt-delay" ->
                "A DBT delay can happen because the scheme, the bank routing state, or the mapped destination is not what the resident expects. Start with the official status and then confirm with the bank.";
            case "mobile-number" ->
                "The registered mobile number is important because many official resident services use OTP verification before showing status or allowing service access.";
            case "aadhaar-number-check" ->
                "The Aadhaar number field in this site only checks whether the number looks like a valid 12-digit format. It does not claim to fetch live UIDAI or bank data.";
            case "dbt-meaning" ->
                "Direct Benefit Transfer (DBT) is a government mechanism to transfer subsidies and benefits directly into the bank accounts of beneficiaries using Aadhaar-based routing or bank account records.";
            case "bank-visit" ->
                "You should visit the bank if there is a mismatch between your expected bank and the official resident status result, or if you need to submit a fresh Aadhaar seeding or NPCI mapper update.";
            case "scheme-context" ->
                "Different government schemes may use Aadhaar differently. Some use Aadhaar-based DBT routing through NPCI mapper, while others might use your bank account number linked to Aadhaar.";
            case "seeding-process" ->
                "Aadhaar seeding is the process of associating a resident's Aadhaar number with their bank account record and ensuring it is active for receiving government benefits through official routing channels.";
            case "otp-issues" ->
                "If you don't receive an OTP, ensure your mobile number is linked to Aadhaar. You can check the network, try again later, or visit an Aadhaar Seva Kendra to verify your registered mobile number.";
            case "mobile-update" ->
                "To update your mobile number in Aadhaar, you must visit an authorized Aadhaar Enrolment Center or Aadhaar Seva Kendra. Use the UIDAI portal to find the nearest center or book an appointment.";
            case "check-mapper" ->
                "You can check your NPCI mapper status by visiting the official UIDAI website or the MyAadhaar portal. Look for 'Bank Seeded Status' under the resident services section.";
            case "change-time" ->
                "A bank account change for DBT usually takes 2 to 4 weeks to reflect in the official records, as both the bank and the central NPCI mapper must complete their backend updates.";
            default ->
                "Use the comparison, check account, and resources phases together if you need a complete understanding of Aadhaar linking and DBT readiness.";
        };
    }

    private static String localAssistantAnswer(String question) {
        String lower = question.toLowerCase();
        if (lower.contains("difference") || lower.contains("linked") || lower.contains("dbt enabled")) {
            return faqAnswer("link-difference");
        }
        if (lower.contains("check") || lower.contains("status")) {
            return faqAnswer("how-to-check");
        }
        if (lower.contains("two bank") || lower.contains("multiple bank") || lower.contains("many bank")) {
            return faqAnswer("multiple-banks");
        }
        if (lower.contains("change bank") || lower.contains("new bank")) {
            return faqAnswer("bank-change");
        }
        if (lower.contains("npci") || lower.contains("mapper")) {
            return faqAnswer("npci-mapper");
        }
        if (lower.contains("delay") || lower.contains("not received") || lower.contains("not getting")) {
            return faqAnswer("dbt-delay");
        }
        if (lower.contains("mobile") || lower.contains("otp")) {
            return faqAnswer("mobile-number");
        }
        if (lower.contains("aadhaar number")) {
            return faqAnswer("aadhaar-number-check");
        }
        if (lower.contains("what is dbt") || lower.contains("dbt meaning")) {
            return faqAnswer("dbt-meaning");
        }
        if (lower.contains("visit bank") || lower.contains("go to bank") || lower.contains("branch")) {
            return faqAnswer("bank-visit");
        }
        if (lower.contains("scheme") || lower.contains("government")) {
            return faqAnswer("scheme-context");
        }
        if (lower.contains("seeding") || lower.contains("process")) {
            return faqAnswer("seeding-process");
        }
        if (lower.contains("otp") || lower.contains("not received otp") || lower.contains("connectivity")) {
            return faqAnswer("otp-issues");
        }
        if (lower.contains("update mobile") || lower.contains("phone number") || lower.contains("enrolment center")) {
            return faqAnswer("mobile-update");
        }
        if (lower.contains("online") || lower.contains("myaadhaar portal") || lower.contains("check mapper")) {
            return faqAnswer("check-mapper");
        }
        if (lower.contains("how long") || lower.contains("time") || lower.contains("weeks")) {
            return faqAnswer("change-time");
        }
        return "Use Learn for concept building, Comparison for the exact difference, Check Account for official steps, and Resources for detailed portals and reference links. For live status, always trust official portals and your bank confirmation.";
    }

    private static void writeString(HttpExchange exchange, int statusCode, String contentType, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
    }

    private static String contentType(Path requested) {
        String name = requested.getFileName().toString().toLowerCase();
        if (name.endsWith(".png")) {
            return "image/png";
        }
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (name.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "application/octet-stream";
    }

    private static Map<String, List<String>> parseParams(URI uri) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        String query = uri.getRawQuery();
        if (query == null || query.isBlank()) {
            return result;
        }
        for (String pair : query.split("&")) {
            String[] bits = pair.split("=", 2);
            String key = decode(bits[0]);
            String value = bits.length > 1 ? decode(bits[1]) : "";
            result.computeIfAbsent(key, ignored -> new ArrayList<>()).add(value);
        }
        return result;
    }

    private static Map<String, List<String>> parseFormBody(InputStream body) throws IOException {
        String form = new String(body.readAllBytes(), StandardCharsets.UTF_8);
        Map<String, List<String>> result = new LinkedHashMap<>();
        if (form.isBlank()) {
            return result;
        }
        for (String pair : form.split("&")) {
            String[] bits = pair.split("=", 2);
            String key = decode(bits[0]);
            String value = bits.length > 1 ? decode(bits[1]) : "";
            result.computeIfAbsent(key, ignored -> new ArrayList<>()).add(value);
        }
        return result;
    }

    private static void merge(Map<String, List<String>> target, Map<String, List<String>> source) {
        for (Map.Entry<String, List<String>> entry : source.entrySet()) {
            target.put(entry.getKey(), entry.getValue());
        }
    }

    private static String first(Map<String, List<String>> params, String key) {
        List<String> values = params.get(key);
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    private static boolean isChecked(Map<String, List<String>> params, String key) {
        String value = first(params, key);
        return value != null && (value.equalsIgnoreCase("on") || value.equalsIgnoreCase("true") || value.equals("1"));
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static String sanitizeAadhaar(String value) {
        return value == null ? "" : value.replaceAll("[^0-9]", "");
    }

    private static boolean isValidAadhaar(String value) {
        return value != null && value.matches("\\d{12}");
    }

    private static String blankToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private static int parseInt(String value, int fallback) {
        try {
            return value == null ? fallback : Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static long parseLong(String value, long fallback) {
        try {
            return value == null ? fallback : Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
