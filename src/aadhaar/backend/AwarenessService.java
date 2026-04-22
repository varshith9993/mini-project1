package aadhaar.backend;

import java.util.ArrayList;
import java.util.List;

public final class AwarenessService {
    public enum LanguageOption {
        ENGLISH("English"),
        HINDI("Hindi"),
        TELUGU("Telugu"),
        KANNADA("Kannada"),
        TAMIL("Tamil"),
        MALAYALAM("Malayalam"),
        BENGALI("Bengali"),
        MARATHI("Marathi"),
        ODIA("Odia"),
        GUJARATI("Gujarati"),
        ASSAMESE("Assamese");

        private final String label;

        LanguageOption(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public record AppContent(
            UiCopy ui,
            LearnSection learn,
            ComparisonSection comparison,
            CheckAccountSection checkAccount,
            List<VideoItem> videos,
            List<ResourceItem> resources,
            List<QuizQuestion> quizQuestions) {
    }

    public record UiCopy(
            String windowTitle,
            String appTitle,
            String appSubtitle,
            String learnTab,
            String comparisonTab,
            String accountTab,
            String videoTab,
            String quizTab,
            String resourcesTab,
            String aiAssistantTab,
            String footerNote,
            String comparisonTitle,
            String comparisonSubtitle,
            String factorHeader,
            String linkedHeader,
            String dbtHeader,
            String impactHeader) {
    }

    public record LearnSection(
            String title,
            String subtitle,
            List<String> overviewPoints,
            List<String> detailedPoints,
            String awarenessNote) {
    }

    public record ComparisonSection(
            String title,
            String subtitle,
            List<ComparisonPoint> comparisonPoints,
            String awarenessNote) {
    }

    public record ComparisonPoint(String factor, String aadhaarLinked, String dbtEnabled, String impact) {
    }

    public record CheckAccountSection(
            String title,
            String subtitle,
            List<String> prompts,
            String linkedOnlyTitle,
            String linkedOnlyBody,
            String dbtEnabledTitle,
            String dbtEnabledBody,
            String cautionTitle,
            String cautionBody,
            String needsVisitTitle,
            String needsVisitBody,
            List<String> nextSteps) {
    }

    public record VideoItem(String title, String description, String source, String url, String embedId) {
    }

    public record ResourceItem(String title, String description, String source, String url) {
    }

    public record QuizQuestion(String prompt, List<String> options, int correctIndex, String explanation) {
    }

    public AppContent getContent(LanguageOption language) {
        return new AppContent(ui(), learn(), comparison(), check(), videos(), resources(), quizQuestions());
    }

    private UiCopy ui() {
        return new UiCopy(
                "Aadhaar DBT Awareness System",
                "Aadhaar DBT Awareness System",
                "Understand Aadhaar linking, DBT readiness, official resources, and awareness checks.",
                "Learn",
                "Comparison",
                "Check Account",
                "Watch Video",
                "Quiz",
                "Resources",
                "AI Assistant",
                "Official references included from UIDAI, DBT Bharat, NPCI, and related public guidance sources.",
                "Aadhaar Linked vs DBT Enabled Bank Accounts",
                "Detailed distinction using bank-side linkage, Aadhaar seeding, NPCI mapper routing, and citizen-facing examples.",
                "Factor",
                "Aadhaar linked bank account",
                "DBT enabled Aadhaar seeded account",
                "Citizen impact");
    }

    private LearnSection learn() {
        return new LearnSection(
                "Learn Phase",
                "Understand the role of Aadhaar linking, what it changes at the bank level, and why official status verification still matters before expecting a DBT credit.",
                List.of(
                        "Aadhaar linking usually means the bank has associated your Aadhaar identity with a bank account record.",
                        "Linking supports identity-related workflows, service matching, and some benefit delivery processes.",
                        "A citizen should still check the official bank-linking or seeding status instead of assuming that branch submission alone completed every backend step.",
                        "If you changed banks, opened a new account, or shared Aadhaar with multiple banks over time, the latest routed bank can differ from what you remember."),
                List.of(
                        "Direct Benefit Transfer is meant to send government benefits directly to the beneficiary without unnecessary intermediaries.",
                        "Aadhaar helps identify the beneficiary consistently across service systems, but the final payment destination depends on the active bank linkage and routing state.",
                        "Many users confuse basic Aadhaar linking with DBT readiness. That is why this portal separates Learn, Comparison, and Check Account into different phases.",
                        "The correct sequence is: first understand the concepts, then compare the two states carefully, then follow the official checking workflow, and only then decide whether further bank action is required.",
                        "Official verification matters because live routing and resident-facing results come from official portals or bank confirmation, not from assumptions or old branch receipts.",
                        "Use this phase to build the foundation first, then move into Comparison for the detailed difference between Aadhaar linked and DBT enabled accounts."),
                "Aadhaar linking is important, but it is not the same thing as guaranteed DBT receipt. Users should always confirm the current official status.");
    }

    private ComparisonSection comparison() {
        return new ComparisonSection(
                ui().comparisonTitle(),
                ui().comparisonSubtitle(),
                List.of(
                        new ComparisonPoint("What it confirms", "The bank has associated Aadhaar with an account record.", "The account is positioned to receive Aadhaar-based benefit routing through the active seeded path.", "A linked account can still differ from the live DBT destination bank."),
                        new ComparisonPoint("Bank involvement", "Usually done when the bank accepts Aadhaar details and records the relationship.", "Requires not only recorded linkage but also the right backend readiness for Aadhaar-based DBT routing.", "Users should not stop at a bank-side acknowledgement alone."),
                        new ComparisonPoint("Routing meaning", "Shows Aadhaar-account association.", "Shows readiness for Aadhaar-based subsidy transfer through the intended bank path.", "This is the key difference behind many citizen complaints."),
                        new ComparisonPoint("Multiple bank scenario", "More than one bank may say Aadhaar is linked if the user submitted details at different times.", "Only one current mapped destination may effectively receive Aadhaar-based credits at a time.", "The latest active mapping can decide the real destination bank."),
                        new ComparisonPoint("User expectation", "People often assume money will arrive here automatically.", "This is the status closer to actual benefit delivery readiness.", "Misunderstanding this difference causes confusion when credits go elsewhere or do not arrive."),
                        new ComparisonPoint("When to verify", "Verify when you newly linked Aadhaar or changed branch details.", "Verify especially before expecting a scheme benefit or after changing banks.", "Official resident checks and bank confirmation remain essential.")),
                "The safest approach is to treat Aadhaar linked as an important indicator, but not final proof that the account is the active DBT destination.");
    }

    private CheckAccountSection check() {
        return new CheckAccountSection(
                "Check Account",
                "Follow the official workflow and answer practical prompts to understand whether your account is only linked or also positioned for DBT.",
                List.of(
                        "I submitted my Aadhaar to the bank and it was verified against my account.",
                        "The bank or official resident status service shows Aadhaar seeding or active bank-linking status.",
                        "The bank shown in the official result is the bank where I expect Aadhaar-based DBT to come.",
                        "The scheme I am checking actually uses Aadhaar-based DBT or an Aadhaar-linked bank route."),
                "Aadhaar Linked Only",
                "Your account may be linked at the bank level, but the available information does not yet indicate complete DBT readiness. Use the official status services and confirm with the bank.",
                "Likely Ready For Official Confirmation",
                "Your answers suggest the account may be positioned correctly for DBT, but you should still trust the official resident-facing result and bank confirmation rather than this awareness tool alone.",
                "Bank Mismatch Or Routing Risk",
                "Your answers suggest a mismatch between the bank you expect and the bank that may currently be active for routing. Recheck the official status and contact the bank about NPCI mapper or seeding confirmation.",
                "Needs Official Check",
                "The information you provided is not enough to treat the account as DBT-ready. Start with the official resident bank-linking or seeding status workflow.",
                List.of(
                        "Open MyAadhaar or the official UIDAI service hub and use the resident bank linking or seeding related service when available.",
                        "Use the registered mobile number and OTP flow required by the official portal.",
                        "Read the bank name shown in the response carefully and compare it with the bank where you expect DBT credits.",
                        "If there is any mismatch, ask the bank whether NPCI mapper seeding is active on the intended account.",
                        "If you recently changed banks, wait for the official update to reflect and recheck before assuming the benefit path is final."));
    }

    private List<VideoItem> videos() {
        return List.of(
                new VideoItem("How to Register and Login to Aadhaar App", "Official UIDAI tutorial that shows the Aadhaar app entry flow and helps residents understand the starting point for mobile-based Aadhaar services.", "UIDAI / YouTube", "https://www.youtube.com/watch?v=t2RfIMizWwc", "t2RfIMizWwc"),
                new VideoItem("How to Download Masked or Unmasked Aadhaar", "Official UIDAI tutorial explaining how residents can access Aadhaar download options from the Aadhaar app flow.", "UIDAI / YouTube", "https://www.youtube.com/watch?v=jNaqPc8-eeA", "jNaqPc8-eeA"),
                new VideoItem("Aadhaar App Launch Video", "Official UIDAI awareness video introducing the Aadhaar app and its purpose for resident-facing digital services.", "UIDAI / YouTube", "https://www.youtube.com/watch?v=z5Hl0UlqBKk", "z5Hl0UlqBKk"),
                new VideoItem("OVSE Use Cases Video", "Official UIDAI use-case video that explains where Aadhaar app features support identity verification in practical situations.", "UIDAI / YouTube", "https://www.youtube.com/watch?v=fBBQycZ7UhI", "fBBQycZ7UhI"),
                new VideoItem("Share Contact Details", "Official UIDAI short video showing a real Aadhaar app use case that improves practical understanding of the platform.", "UIDAI / YouTube", "https://www.youtube.com/watch?v=UsQ_68qBi-4", "UsQ_68qBi-4"),
                new VideoItem("Identity Verification at Event Entry Point", "Official UIDAI short video demonstrating Aadhaar-supported identity verification in an event access scenario.", "UIDAI / YouTube", "https://www.youtube.com/watch?v=yHwra5eVAW8", "yHwra5eVAW8"),
                new VideoItem("mAadhaar Video KYC", "Official Aadhaar UIDAI video showing a real resident service flow that helps users understand app-based service usage.", "UIDAI / YouTube", "https://www.youtube.com/watch?v=H02T5q0h_fA", "H02T5q0h_fA"),
                new VideoItem("Book Online Appointment For Aadhaar Update", "Official Aadhaar UIDAI video useful for understanding service access and update workflow.", "UIDAI / YouTube", "https://www.youtube.com/watch?v=8ZurY3nrv7U", "8ZurY3nrv7U"),
                new VideoItem("UIDAI Official Channel Introduction", "Official channel launch reference from UIDAI for authentic Aadhaar awareness videos.", "UIDAI / YouTube", "https://www.youtube.com/watch?v=_JSMjK8wv40", "_JSMjK8wv40"),
                new VideoItem("UIDAI Aadhaar App Videos", "Official UIDAI landing page that groups multiple Aadhaar app awareness videos and service tutorials.", "UIDAI", "https://uidai.gov.in/en/my-aadhaar/aadhaar-on-your-mobile/aadhaar-app-videos.html", ""),
                new VideoItem("DBT Bharat Multimedia", "Government multimedia page for DBT background material and awareness support content.", "DBT Bharat", "https://dbtbharat.gov.in/multimedia/video", ""));
    }
    private List<ResourceItem> resources() {
        List<ResourceItem> items = new ArrayList<>();
        items.add(resource("MyAadhaar Portal", "Official resident portal for Aadhaar services and status-related workflows.", "UIDAI", "https://myaadhaar.uidai.gov.in/"));
        items.add(resource("Avail Aadhaar Services", "Service hub for resident-facing Aadhaar services.", "UIDAI", "https://uidai.gov.in/en/my-aadhaar/avail-aadhaar-services.html"));
        items.add(resource("Get Aadhaar", "Official guidance for enrolment and Aadhaar-related onboarding.", "UIDAI", "https://uidai.gov.in/en/my-aadhaar/get-aadhaar"));
        items.add(resource("Update Aadhaar", "Official update section for demographic and resident updates.", "UIDAI", "https://uidai.gov.in/en/my-aadhaar/update-aadhaar.html"));
        items.add(resource("Book Appointment", "Book Aadhaar enrolment or update appointments online.", "UIDAI", "https://appointments.uidai.gov.in/bookappointment.aspx"));
        items.add(resource("Locate Enrolment Center", "Find an Aadhaar Seva Kendra or enrolment center.", "UIDAI", "https://bhuvan.nrsc.gov.in/aadhaar/"));
        items.add(resource("Aadhaar Myth Busters", "Clarifies common misconceptions about Aadhaar, privacy, and service usage.", "UIDAI", "https://uidai.gov.in/en/my-aadhaar/about-your-aadhaar/aadhaar-myth-busters.html"));
        items.add(resource("UIDAI FAQ Hub", "Official FAQ entry point for Aadhaar services and resident support.", "UIDAI", "https://uidai.gov.in/en/contact-support/have-any-question/277-faqs.html"));
        items.add(resource("Direct Benefit Transfer FAQ", "UIDAI FAQ page about DBT and Aadhaar-based benefit delivery.", "UIDAI", "https://uidai.gov.in/en/308-faqs/direct-benefit-transfer-dbt.html"));
        items.add(resource("How To Check Bank Linking", "UIDAI-hosted guidance on checking your bank account linking with Aadhaar.", "UIDAI", "https://uidai.gov.in/en/media-resources/media/aadhaar-in-prints/5220-how-to-check-your-bank-account-linking-with-aadhaar.html"));
        items.add(resource("Aadhaar App Videos", "Official UIDAI page grouping Aadhaar app videos and tutorials.", "UIDAI", "https://uidai.gov.in/en/my-aadhaar/aadhaar-on-your-mobile/aadhaar-app-videos.html"));
        items.add(resource("UIDAI Video Gallery", "Official UIDAI video gallery page.", "UIDAI", "https://uidai.gov.in/en/14-english-uk/1009-videos.html"));
        items.add(resource("Aadhaar Official YouTube Channel Note", "UIDAI press release about the official YouTube channel.", "UIDAI", "https://uidai.gov.in/en/media-resources/media/press-releases/4625-uidai-launches-aadhaar-official-channel-on-youtube.html"));
        items.add(resource("Authentication History FAQ", "Understand Aadhaar authentication history and resident-facing checks.", "UIDAI", "https://uidai.gov.in/en/contact-support/have-any-question/285-faqs/aadhaar-online-services/6134-how-do-i-check-aadhaar-authentication-history.html"));
        items.add(resource("Paperless Offline e-KYC FAQ", "Official explanation of paperless Aadhaar and offline e-KYC.", "UIDAI", "https://uidai.gov.in/en/contact-support/have-any-question/285-faqs/aadhaar-online-services/6133-what-is-paperless-offline-e-kyc.html"));
        items.add(resource("Resident Downloads", "Forms, update support material, and resident downloads.", "UIDAI", "https://uidai.gov.in/en/my-aadhaar/downloads.html"));
        items.add(resource("Enrolment And Update Forms", "Official forms required for Aadhaar enrolment and updates.", "UIDAI", "https://uidai.gov.in/en/my-aadhaar/forms.html"));
        items.add(resource("Charges For Aadhaar Services", "Reference for service charges at Aadhaar centers.", "UIDAI", "https://uidai.gov.in/images/Aadhaar_Updation_Charges_19032024.pdf"));
        items.add(resource("About Your Aadhaar", "Overview section about Aadhaar purpose, usage, and resident concerns.", "UIDAI", "https://uidai.gov.in/en/my-aadhaar/about-your-aadhaar.html"));
        items.add(resource("Aadhaar Security Guidance", "General Aadhaar safety and secure usage guidance.", "UIDAI", "https://uidai.gov.in/en/contact-support/have-any-question/281-faqs/your-aadhaar/use-aadhaar-freely.html"));
        items.add(resource("UIDAI Dashboard", "Official UIDAI dashboard and public data references.", "UIDAI", "https://uidai.gov.in/en/914-dashboard.html"));
        items.add(resource("Resident Grievance Support", "Official support and grievance channels.", "UIDAI", "https://uidai.gov.in/en/contact-support/grievance-redressal.html"));
        items.add(resource("Regional Offices", "Official UIDAI regional office directory.", "UIDAI", "https://uidai.gov.in/en/about-uidai/regional-offices.html"));
        items.add(resource("Headquarters Contact", "Official UIDAI headquarters contact details.", "UIDAI", "https://uidai.gov.in/en/about-uidai/contact-us.html"));
        items.add(resource("Aadhaar Usage Information", "Background information on how Aadhaar is used in service delivery.", "UIDAI", "https://uidai.gov.in/en/my-aadhaar/about-your-aadhaar/aadhaar-usage.html"));
        items.add(resource("DBT Bharat Home", "Government DBT awareness portal home page.", "DBT Bharat", "https://dbtbharat.gov.in/index.php"));
        items.add(resource("DBT Bharat About", "Background on the DBT mission and its purpose.", "DBT Bharat", "https://dbtbharat.gov.in/static-page-content/spagecont?id=1"));
        items.add(resource("DBT Bharat Service List", "DBT service references and support directory.", "DBT Bharat", "https://dbtbharat.gov.in/dbtcell/service-list"));
        items.add(resource("DBT Bharat Multimedia", "Government multimedia page for DBT videos and awareness material.", "DBT Bharat", "https://dbtbharat.gov.in/multimedia/video"));
        items.add(resource("DBT Bharat Documents", "DBT document repository for citizens and reference reading.", "DBT Bharat", "https://dbtbharat.gov.in/document"));
        items.add(resource("DBT Bharat Circulars", "Circulars and administrative DBT updates.", "DBT Bharat", "https://dbtbharat.gov.in/circular"));
        items.add(resource("DBT Bharat Press Releases", "DBT-related releases and programme updates.", "DBT Bharat", "https://dbtbharat.gov.in/pressrelease"));
        items.add(resource("DBT Bharat Aadhaar And UIDAI Section", "DBT Bharat reference category for Aadhaar and UIDAI-linked material.", "DBT Bharat", "https://dbtbharat.gov.in/document/aadhaar-and-uidai"));
        items.add(resource("DBT Bharat Payments Section", "DBT Bharat document category related to payments and transfers.", "DBT Bharat", "https://dbtbharat.gov.in/document/payments"));
        items.add(resource("NPCI Customer FAQ", "Official NPCI explanation of Aadhaar mapper seeding and customer questions.", "NPCI", "https://www.npci.org.in/what-we-do/nach/faqs/customers"));
        items.add(resource("Find Nearby Bank", "Government-supported bank finder utility useful during Aadhaar-bank workflow follow-up.", "FindMyBank", "https://findmybank.gov.in/FMB/"));
        items.add(resource("Aadhaar Linked Bank FAQs", "FAQ content about receiving benefits under Aadhaar-linked DBT workflows.", "UIDAI", "https://uidai.gov.in/en/308-faqs/direct-benefit-transfer-dbt/12663-how-do-i-receive-benefits-under-the-govt-schemes-in-my-bank-account.html"));
        items.add(resource("How To Change Bank Account For DBT", "Resident FAQ about changing the bank account used for DBT benefits.", "UIDAI", "https://uidai.gov.in/en/308-faqs/direct-benefit-transfer-dbt/12670-how-can-i-change-my-bank-account-to-receive-dbt-benefits.html"));
        items.add(resource("DBT And Multiple Bank Accounts FAQ", "FAQ explaining what happens when a resident has multiple bank accounts.", "UIDAI", "https://uidai.gov.in/en/308-faqs/direct-benefit-transfer-dbt/12672-i-have-multiple-bank-accounts-where-will-i-receive-my-dbt-benefits.html"));
        items.add(resource("Resident Bank Seeding Status Article", "Helpful public explainer page on resident bank seeding checks.", "UIDAI", "https://uidai.gov.in/en/media-resources/media/aadhaar-in-prints/5220-how-to-check-your-bank-account-linking-with-aadhaar.html"));
        items.add(resource("UIDAI Media Resources", "Official media and awareness reference hub.", "UIDAI", "https://uidai.gov.in/en/media-resources.html"));
        items.add(resource("UIDAI Press Releases", "Official press release archive for Aadhaar-related service announcements.", "UIDAI", "https://uidai.gov.in/en/media-resources/media/press-releases.html"));
        items.add(resource("UIDAI Contact Support", "Support landing page for resident queries and assistance.", "UIDAI", "https://uidai.gov.in/en/contact-support.html"));
        items.add(resource("UIDAI FAQ Your Aadhaar", "Resident-focused FAQ grouping about safe Aadhaar use and understanding services.", "UIDAI", "https://uidai.gov.in/en/contact-support/have-any-question/281-faqs/your-aadhaar.html"));
        items.add(resource("DBT Bharat Citizen Landing", "Entry point used by many residents for DBT information and navigation.", "DBT Bharat", "https://dbtbharat.gov.in/"));
        items.add(resource("DBT Bharat Multimedia Home", "Extra multimedia landing page useful after checking status.", "DBT Bharat", "https://dbtbharat.gov.in/multimedia"));
        return items;
    }

    private List<QuizQuestion> quizQuestions() {
        return List.of(
                q("Which statement is most accurate?", "Every Aadhaar linked bank account is automatically DBT enabled.", "Aadhaar linking and DBT readiness can be different backend states.", "DBT works only if UIDAI stores your bank account number.", "Aadhaar linking is not needed anywhere.", 1, "Basic bank-side Aadhaar linkage and DBT readiness are not always the same thing."),
                q("Why does NPCI mapper matter in Aadhaar-based DBT?", "It stores debit card PINs.", "It helps determine the mapped bank for Aadhaar-based routing.", "It replaces the Aadhaar number.", "It closes old bank accounts.", 1, "Mapper-related routing is one reason a linked account can differ from the actual DBT destination."),
                q("If you seeded Aadhaar with two banks over time, what is the safer assumption?", "Both banks will always receive the same DBT credit.", "No bank can receive DBT anymore.", "The latest active routing update may decide the destination bank.", "UIDAI splits the benefit equally.", 2, "The current active mapped bank can matter more than an older linked bank."),
                q("Which source is best for authentic DBT information?", "Random social media posts.", "Unverified blog screenshots.", "Official UIDAI, DBT Bharat, or NPCI pages.", "Only a friend's bank message.", 2, "Official sources should be trusted for service guidance and confirmation."),
                q("What does Aadhaar linked usually confirm?", "That a DBT credit was already paid.", "That the bank has associated Aadhaar with the account record.", "That every government scheme is active.", "That the account cannot change banks.", 1, "Aadhaar linked usually indicates account association, not guaranteed benefit routing."),
                q("Why should a resident recheck status after changing banks?", "Because Aadhaar becomes invalid.", "Because the active destination bank may change after update or seeding.", "Because DBT stops forever.", "Because UIDAI blocks old Aadhaar numbers.", 1, "Bank changes can affect which account is active for Aadhaar-based routing."),
                q("Which page is useful to start a resident Aadhaar service check?", "MyAadhaar or the UIDAI service hub.", "A random document download website.", "Any social media page.", "A private chat group.", 0, "The resident-facing official portal should be the starting point."),
                q("What is the main risk of assuming linked means ready?", "You may ignore the actual active DBT destination bank.", "The Aadhaar number disappears.", "The bank card gets blocked automatically.", "The scheme closes permanently.", 0, "This confusion is the central problem the comparison phase explains."),
                q("What should you compare in the official result?", "Only the OTP expiry time.", "The bank name shown versus the bank you expect to receive DBT.", "Only the color of the portal page.", "Only the number of branches nearby.", 1, "The bank shown in the official result matters most."),
                q("Why is Learn separate from Comparison in this site?", "To separate concept learning from side-by-side distinction.", "Because comparison is unrelated.", "Because videos replace learning.", "Because the quiz cannot use learning content.", 0, "Users understand the flow better when learning and comparison are separated."),
                q("What is a common citizen mistake?", "Checking official resident status.", "Assuming branch submission alone proves final DBT routing.", "Comparing bank names carefully.", "Using official FAQ pages.", 1, "Branch acknowledgement alone may not reflect the final routed status."),
                q("Which step comes after understanding the concept?", "Go to Comparison and then Check Account.", "Ignore official portals.", "Trust memory only.", "Wait without checking anything.", 0, "The site structure intentionally guides the user through those phases."),
                q("What does the Check Account awareness form do?", "It gives a final legal guarantee.", "It helps users think through practical readiness questions.", "It changes bank routing directly.", "It edits UIDAI records.", 1, "The tool is for awareness, not official backend modification."),
                q("What is the best action if the official bank shown is not your intended bank?", "Ignore it and wait forever.", "Contact the bank and confirm current seeding or mapper status.", "Create a new Aadhaar number.", "Delete the account immediately.", 1, "Mismatch requires official follow-up with the bank."),
                q("Why does the site include official videos?", "To provide visual understanding of the workflow inside the site.", "To replace all official portal checks.", "To avoid reading anything.", "To submit bank updates automatically.", 0, "Videos are for understanding, not for replacing official confirmation."),
                q("What is the safest trust rule for this awareness site?", "Use it for understanding, but rely on official portals for live status.", "Treat it as the official backend source.", "Use it instead of all bank communication.", "Ignore official sources after reading it.", 0, "The site is educational and should not replace live official status checks."),
                q("What does DBT try to improve?", "Benefit delivery directly to the intended beneficiary account.", "Bank branch crowding only.", "Printing more paper forms.", "Private account sharing.", 0, "DBT is designed to improve direct delivery of benefits."),
                q("Which phrase best describes DBT enabled?", "Any account with a passbook.", "An account ready for Aadhaar-based benefit routing.", "Any account with internet banking.", "An account with two mobile numbers.", 1, "DBT enabled refers to payment routing readiness rather than generic account features."),
                q("What should a resident do before expecting a subsidy credit?", "Check the scheme, the mapped bank, and the official status.", "Only ask a neighbor.", "Only look at an old receipt.", "Only rely on memory.", 0, "Final confidence should come from current status and scheme context."),
                q("Why is a video on bank-linking status useful on the Check page?", "It visually supports the official checking steps.", "It changes your account status.", "It bypasses OTP.", "It replaces UIDAI entirely.", 0, "Visual walkthroughs help users follow the proper sequence."),
                q("Which section should a user open for 40-plus helpful links?", "Resources.", "Only Quiz.", "Only Comparison.", "Only Learn.", 0, "Resources combines official links and helpful references."),
                q("What is the role of the AI Assistant phase?", "To answer Aadhaar and DBT questions on the same page.", "To edit UIDAI databases.", "To send DBT payments.", "To close bank accounts.", 0, "It is meant for guided explanation and clarification."),
                q("Which answer best describes a mismatch risk?", "The bank you expect and the officially shown bank are different.", "Your page color changed.", "The browser refreshed.", "The portal asked for OTP.", 0, "A mismatch between expected bank and shown bank is the real risk signal."),
                q("Why does the Comparison page use examples?", "To show realistic situations where linked and DBT enabled differ.", "To replace the table entirely.", "To avoid official sources.", "To reduce accuracy.", 0, "Examples make the backend difference easier for users to recognize."),
                q("What should you do if the scheme does not use Aadhaar-based DBT?", "Do not assume Aadhaar linking alone guarantees that scheme's payment path.", "Delete Aadhaar.", "Close your bank account.", "Ignore the scheme information.", 0, "Scheme context matters along with account readiness."),
                q("Why is the final quiz score shown only after all five questions?", "To keep the flow like a quiz game and show the result at the end.", "Because the app cannot count earlier.", "Because answers are hidden forever.", "Because the browser does not allow mid-quiz updates.", 0, "The final result view is intentional and cleaner for the user."),
                q("What should happen when the user restarts the quiz?", "A fresh set of five questions should be chosen again.", "The same exact five must always repeat.", "The score should stay fixed.", "The quiz should skip the first question.", 0, "Randomized five-question sets make the awareness quiz more useful."),
                q("Which section should a new user ideally visit first?", "Learn.", "Resources only.", "Quiz only.", "AI only.", 0, "The site is structured to start with foundational understanding."),
                q("What is the purpose of the Home page cards?", "To give direct navigation into each phase in a simple landing layout.", "To replace the top navigation bar.", "To hide all sections.", "To show only one section forever.", 0, "The Home page acts as the landing dashboard."),
                q("Which source explains mapper seeding best among the listed links?", "NPCI customer FAQ and official UIDAI or DBT references.", "Only a random forum.", "Only an image screenshot.", "Any unrelated finance blog.", 0, "NPCI and UIDAI references are the right places for mapper and routing understanding."));
    }

    private static ResourceItem resource(String title, String description, String source, String url) {
        return new ResourceItem(title, description, source, url);
    }

    private static QuizQuestion q(String prompt, String a, String b, String c, String d, int correctIndex, String explanation) {
        return new QuizQuestion(prompt, List.of(a, b, c, d), correctIndex, explanation);
    }
}
