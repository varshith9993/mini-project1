package aadhaar.web;

import aadhaar.backend.AwarenessService.AppContent;
import aadhaar.backend.AwarenessService.CheckAccountSection;
import aadhaar.backend.AwarenessService.ComparisonPoint;
import aadhaar.backend.AwarenessService.QuizQuestion;
import aadhaar.backend.AwarenessService.ResourceItem;
import aadhaar.backend.AwarenessService.UiCopy;
import aadhaar.backend.AwarenessService.VideoItem;
import aadhaar.web.AwarenessWebServer.AadhaarLookupResult;
import aadhaar.web.AwarenessWebServer.AiAssistantState;
import aadhaar.web.AwarenessWebServer.CheckAssessment;
import aadhaar.web.AwarenessWebServer.QuizAssessment;
import aadhaar.web.AwarenessWebServer.Section;
import aadhaar.web.AwarenessWebServer.ViewState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class PageRenderer {
    public String renderPage(ViewState viewState, AppContent content) {
        UiCopy ui = content.ui();
        String sectionHtml = switch (viewState.section()) {
            case HOME -> renderHomeSection(viewState, content);
            case LEARN -> renderLearnSection(viewState, content);
            case COMPARISON -> renderComparisonSection(viewState, content);
            case CHECK_ACCOUNT -> renderCheckSection(viewState, content);
            case VIDEOS -> renderVideoSection(content);
            case QUIZ -> renderQuizSection(viewState, content);
            case RESOURCES, GOVERNMENT -> renderResourcesSection(content);
            case AI_ASSISTANT -> renderAiAssistantSection(viewState);
        };
        String phaseHtml = viewState.section() == Section.HOME ? "" : renderPhaseStrip(viewState);
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>%s</title>
                  <style>%s</style>
                </head>
                <body>
                  <div class="page-shell">
                    <div class="window-bar">
                      <span class="window-dot red"></span>
                      <span class="window-dot amber"></span>
                      <span class="window-dot green"></span>
                    </div>
                    <header class="hero-shell">
                      <div class="brand-row">
                        <div class="brand-lockup">
                          <img class="brand-logo" src="/assets/aadhaar-logo.png" alt="Aadhaar logo">
                          <div>
                            <h1>%s</h1>
                            <p>%s</p>
                          </div>
                        </div>
                        %s
                      </div>
                      <nav class="top-nav">%s</nav>
                    </header>
                    <main class="content-wrap">
                      %s
                      %s
                      <footer class="page-footer">%s</footer>
                    </main>
                  </div>
                  <div id="google_translate_element" class="google-translate-anchor" aria-hidden="true"></div>
                  %s
                </body>
                </html>
                """.formatted(
                escape(ui.windowTitle()),
                styles(),
                escape(ui.appTitle()),
                escape(ui.appSubtitle()),
                renderLanguageControl(),
                renderNavigation(viewState, ui),
                phaseHtml,
                sectionHtml,
                escape(ui.footerNote()),
                behaviorScript());
    }

    public String renderErrorPage(String message) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Error</title><style>%s</style></head>
                <body><div class="page-shell"><main class="content-wrap"><section class="section-card solo-card"><h2>Something went wrong</h2><p>%s</p></section></main></div></body>
                </html>
                """.formatted(styles(), escape(message == null ? "Unexpected error." : message));
    }

    private String renderLanguageControl() {
        return """
                <div class="language-tools notranslate" translate="no">
                  <label class="language-chip" translate="no">
                    <span class="flag-badge">IN</span>
                    <select id="languageSelect" onchange="changeLanguage(this.value)" translate="no">
                      <option value="ENGLISH">English</option>
                      <option value="HINDI">Hindi</option>
                      <option value="TELUGU">Telugu</option>
                      <option value="KANNADA">Kannada</option>
                      <option value="TAMIL">Tamil</option>
                      <option value="MALAYALAM">Malayalam</option>
                      <option value="BENGALI">Bengali</option>
                      <option value="MARATHI">Marathi</option>
                      <option value="ODIA">Odia</option>
                      <option value="GUJARATI">Gujarati</option>
                      <option value="ASSAMESE">Assamese</option>
                    </select>
                  </label>
                </div>
                """;
    }

    private String renderNavigation(ViewState viewState, UiCopy ui) {
        return navLink(viewState, Section.HOME, "Home")
                + navLink(viewState, Section.LEARN, ui.learnTab())
                + navLink(viewState, Section.COMPARISON, ui.comparisonTab())
                + navLink(viewState, Section.CHECK_ACCOUNT, ui.accountTab())
                + navLink(viewState, Section.VIDEOS, ui.videoTab())
                + navLink(viewState, Section.QUIZ, ui.quizTab())
                + navLink(viewState, Section.RESOURCES, ui.resourcesTab())
                + navLink(viewState, Section.AI_ASSISTANT, ui.aiAssistantTab());
    }

    private String navLink(ViewState viewState, Section section, String label) {
        return """
                <a class="nav-link%s" href="%s">
                  <span class="nav-badge %s">%s</span>
                  <span class="nav-text">%s</span>
                </a>
                """.formatted(
                viewState.section() == section ? " active" : "",
                sectionHref(section),
                escape(section.routeKey()),
                escape(navBadge(section)),
                escape(label));
    }

    private String renderPhaseStrip(ViewState viewState) {
        return """
                <div class="phase-shell">
                  <div class="phase-trail">Aadhaar DBT Journey</div>
                  <div class="phase-steps">
                    %s
                    %s
                    %s
                    %s
                  </div>
                </div>
                """.formatted(
                phaseCard(viewState, Section.LEARN, "1", "Learn"),
                phaseCard(viewState, Section.COMPARISON, "2", "Comparison"),
                phaseCard(viewState, Section.CHECK_ACCOUNT, "3", "Check Account"),
                phaseCard(viewState, Section.VIDEOS, "4", "Watch Video"));
    }

    private String phaseCard(ViewState viewState, Section section, String step, String label) {
        return "<a class=\"phase-card%s\" href=\"%s\"><span class=\"phase-number\">%s</span><span class=\"phase-label\">%s</span><span class=\"phase-arrow\">&rsaquo;</span></a>"
                .formatted(viewState.section() == section ? " active" : "", sectionHref(section), escape(step), escape(label));
    }

    private String renderHomeSection(ViewState viewState, AppContent content) {
        return """
                <section class="main-section home-section">
                  <div class="section-heading">
                    <h2>Home Overview</h2>
                    <p>Navigate through our core DBT modules below to learn, compare, check your status, watch videos, and visit resources.</p>
                  </div>
                  <div class="home-grid">
                    %s
                    %s
                    %s
                    %s
                    %s
                    %s
                    %s
                  </div>
                </section>
                """.formatted(
                homeCard(viewState, Section.LEARN, "learn", "Learn About DBT", "Understand Aadhaar & DBT benefits", "Read More"),
                homeCard(viewState, Section.COMPARISON, "compare", "Comparison", "Check linked vs seeded status", "View Comparison"),
                homeCard(viewState, Section.CHECK_ACCOUNT, "check", "Check Account Status", "Verify DBT eligibility of your account", "Check Now"),
                homeCard(viewState, Section.VIDEOS, "video", "Watch Awareness Video", "Official videos on Aadhaar & DBT", "Play Video"),
                homeCard(viewState, Section.RESOURCES, "resources", "Resources", "Open official portals, FAQs, and helpful references", "Visit Links"),
                homeCard(viewState, Section.QUIZ, "quiz", "Quiz And Awareness", "Take a five-question quiz and review the correct explanations", "Start Quiz"),
                homeCard(viewState, Section.AI_ASSISTANT, "ai", "AI Assistant", "Open suggested Aadhaar DBT questions and ask your own", "Open Assistant"));
    }

    private String homeCard(ViewState viewState, Section section, String iconClass, String title, String description, String actionLabel) {
        return """
                <article class="section-card home-card">
                  <div class="home-card-header">
                    <div class="home-icon-shell %s">%s</div>
                    <div class="home-card-copy">
                      <h3>%s</h3>
                      <p>%s</p>
                    </div>
                  </div>
                  <a class="home-card-button" href="%s">%s</a>
                </article>
                """.formatted(escape(iconClass), homeIconSvg(iconClass), escape(title), escape(description), sectionHref(section), escape(actionLabel));
    }

    private String renderLearnSection(ViewState viewState, AppContent content) {
        StringBuilder overview = new StringBuilder();
        for (String point : content.learn().overviewPoints()) {
            overview.append("<li>").append(escape(point)).append("</li>");
        }
        StringBuilder details = new StringBuilder();
        for (String point : content.learn().detailedPoints()) {
            details.append("<li>").append(escape(point)).append("</li>");
        }
        return """
                <section class="main-section">
                  <div class="section-heading">
                    <h2>%s</h2>
                    <p>%s</p>
                  </div>
                  <div class="section-card section-banner">
                    <div class="section-banner-brand">
                      <img src="/assets/aadhaar-logo.png" alt="Aadhaar logo">
                      <div>
                        <span class="pill-label">Aadhaar Learning Module</span>
                        <h3>Learn The Full Aadhaar To DBT Readiness Story</h3>
                      </div>
                    </div>
                    <p>This learning page is the foundation of the entire website. It explains how Aadhaar linking starts at the bank, why residents often confuse linking with DBT receipt, and what a person should understand before checking any official status result.</p>
                  </div>
                  <div class="hero-grid">
                    <article class="section-card learn-hero-card">
                      <img src="/assets/learn-panel.png" alt="Aadhaar DBT awareness">
                      <div class="learn-hero-copy">
                        <span class="pill-label">Foundation</span>
                        <h3>Understand Aadhaar Linking Before You Check Status</h3>
                        <p>This module focuses on the concept only. It explains what Aadhaar linking means, why it matters for service delivery, and why it should not be confused with the final DBT destination account.</p>
                        <ul class="detail-list">%s</ul>
                      </div>
                    </article>
                    <article class="section-card key-card">
                      <div class="key-title">What You Should Understand Here</div>
                      <p class="support-copy">A resident usually begins by hearing that Aadhaar must be linked to a bank account. That message is only the first layer. In reality, the resident should understand identity linkage, bank-side recording, scheme eligibility, and the need to verify the current destination bank before expecting DBT credits.</p>
                      <ul class="key-list">%s</ul>
                      <div class="button-row">
                        <a class="primary-button" href="%s">Open Comparison Phase</a>
                        <a class="ghost-button" href="%s">Open Check Account</a>
                      </div>
                    </article>
                  </div>
                  <div class="insight-grid">
                    <article class="section-card info-tile"><h3>Why Linking Matters</h3><p>It creates the bank-side identity relationship that many residents assume is the final step, even though routing readiness may still need separate confirmation.</p></article>
                    <article class="section-card info-tile"><h3>Why Confusion Happens</h3><p>Citizens often share Aadhaar with more than one bank over time and later assume the newest remembered account is the live destination.</p></article>
                    <article class="section-card info-tile"><h3>What To Do Next</h3><p>After reading this phase, move to Comparison and then Check Account to follow the proper official verification sequence.</p></article>
                  </div>
                  <div class="knowledge-grid">
                    <article class="section-card feature-copy">
                      <h3>Detailed Learning Notes</h3>
                      <p>The purpose of this page is not only to introduce Aadhaar and DBT, but to help the resident think in the correct order. First understand what the bank has recorded, then understand how the DBT path works, then verify the live result on the official portals.</p>
                      <ul class="steps-list">%s</ul>
                    </article>
                    <article class="section-card diagram-card">
                      <h3>Simple Flow</h3>
                      <div class="diagram-stack">
                        <div class="diagram-node">Aadhaar Submitted To Bank</div>
                        <div class="diagram-arrow">&darr;</div>
                        <div class="diagram-node">Bank Records Aadhaar Linkage</div>
                        <div class="diagram-arrow">&darr;</div>
                        <div class="diagram-node accent-node">Official Status Must Still Be Checked</div>
                      </div>
                    </article>
                  </div>
                  <div class="knowledge-grid">
                    <article class="section-card branded-info-card">
                      <div class="section-banner-brand compact-brand">
                        <img src="/assets/aadhaar-logo.png" alt="Aadhaar logo">
                        <div>
                          <h3>What Residents Usually Miss</h3>
                          <p>People often remember the bank where they last submitted Aadhaar, but they do not always verify whether that same bank is the one currently aligned for Aadhaar-based benefits.</p>
                        </div>
                      </div>
                      <div class="branded-points">
                        <div class="diagram-node soft-node">Submitting Aadhaar at a branch is not the same as confirming current DBT destination status.</div>
                        <div class="diagram-node">A bank can acknowledge Aadhaar linkage while the resident still needs an official status check.</div>
                        <div class="diagram-node accent-node">The safest approach is to learn, compare, and then verify through official portals.</div>
                      </div>
                    </article>
                    <article class="section-card learn-image-card">
                      <img src="/assets/learn-panel.png" alt="Residents learning Aadhaar and DBT concepts">
                      <div class="learn-image-caption">
                        <h3>Visual Learning Support</h3>
                        <p>The illustration reinforces the main learning idea: a resident should understand the relationship between Aadhaar, bank records, and DBT flow before assuming that benefit transfer is already active.</p>
                      </div>
                    </article>
                  </div>
                  <div class="insight-grid">
                    <article class="section-card info-tile"><h3>Example: New Account Opened</h3><p>A resident opens a new account and submits Aadhaar there. The resident should still verify whether the newly preferred bank is the live bank reflected in the official Aadhaar-bank result.</p></article>
                    <article class="section-card info-tile"><h3>Example: Old Bank Still Active</h3><p>A resident may remember using the latest bank, but an older bank path can still be relevant until the official update fully reflects the intended change.</p></article>
                    <article class="section-card info-tile"><h3>Example: Scheme Confusion</h3><p>Some users assume every scheme uses the same delivery path. Learning the distinction helps residents ask the correct question before visiting a branch or portal.</p></article>
                  </div>
                  <aside class="note-panel">%s</aside>
                </section>
                """.formatted(
                escape(content.learn().title()),
                escape(content.learn().subtitle()),
                overview,
                overview,
                sectionHref(Section.COMPARISON),
                sectionHref(Section.CHECK_ACCOUNT),
                details,
                escape(content.learn().awarenessNote()));
    }

    private String renderComparisonSection(ViewState viewState, AppContent content) {
        StringBuilder rows = new StringBuilder();
        for (ComparisonPoint point : content.comparison().comparisonPoints()) {
            rows.append("<tr><td>").append(escape(point.factor())).append("</td><td>").append(escape(point.aadhaarLinked())).append("</td><td>").append(escape(point.dbtEnabled())).append("</td><td>").append(escape(point.impact())).append("</td></tr>");
        }
        return """
                <section class="main-section">
                  <div class="section-heading">
                    <h2>%s</h2>
                    <p>%s</p>
                  </div>
                  <div class="section-card section-banner comparison-banner">
                    <div class="section-banner-brand">
                      <img src="/assets/aadhaar-logo.png" alt="Aadhaar logo">
                      <div>
                        <span class="pill-label">Comparison Module</span>
                        <h3>Aadhaar Linked Vs DBT Enabled Bank Accounts</h3>
                      </div>
                    </div>
                    <p>This phase distinguishes the two ideas properly. Aadhaar linked answers whether the bank has associated Aadhaar with the account. DBT enabled is the stronger idea related to actual Aadhaar-based benefit routing readiness. A resident should never treat both phrases as identical.</p>
                  </div>
                  <div class="hero-grid comparison-grid">
                    <article class="section-card compare-card">
                      <div class="compare-header">
                        <span class="pill-label">Comparison Phase</span>
                        <h3>What Exactly Is Different?</h3>
                        <p>Aadhaar linked and DBT enabled are related, but they answer different questions. One confirms a bank-side Aadhaar association. The other is closer to actual Aadhaar-based benefit routing readiness.</p>
                      </div>
                      <div class="compare-columns">
                        <div class="compare-panel linked-panel">
                          <h4>Aadhaar Linked</h4>
                          <ul class="key-list">
                            <li>The bank has recorded Aadhaar with the account.</li>
                            <li>This does not automatically prove that DBT will arrive there.</li>
                            <li>Multiple banks may show linkage over time if the resident updated details more than once.</li>
                          </ul>
                        </div>
                        <div class="compare-panel dbt-panel">
                          <h4>DBT Enabled</h4>
                          <ul class="key-list">
                            <li>The account is aligned with the active Aadhaar-based payment route.</li>
                            <li>This status is closer to the actual destination used for benefits.</li>
                            <li>Residents should verify this especially after changing banks.</li>
                          </ul>
                        </div>
                      </div>
                      <div class="compare-example-grid">
                        <div class="compare-example"><strong>Example 1</strong><p>Ravi linked Aadhaar with Bank A years ago and later updated Bank B. Bank B may say Aadhaar is linked, but the active destination can still be Bank A until the official route changes.</p></div>
                        <div class="compare-example"><strong>Example 2</strong><p>Suma submitted Aadhaar at the branch and received acknowledgement. If routing readiness is still incomplete, her account may be linked without yet being the effective DBT destination.</p></div>
                      </div>
                      <div class="compare-example-grid">
                        <div class="compare-example"><strong>Example 3</strong><p>Imran checks his benefit expectation only through a branch conversation. The better method is to compare the official status result with the bank where he actually expects Aadhaar-based DBT to arrive.</p></div>
                        <div class="compare-example"><strong>Example 4</strong><p>Lakshmi changed her bank recently. Her latest memory may be correct, but the official destination bank must still be confirmed before she assumes the new account is the active Aadhaar-based route.</p></div>
                      </div>
                    </article>
                    <article class="section-card feature-copy comparison-visual-card">
                      <div class="section-banner-brand compact-brand">
                        <img src="/assets/aadhaar-logo.png" alt="Aadhaar logo">
                        <div>
                          <h3>Visual Difference</h3>
                          <p>The comparison becomes clearer when the resident sees the stages visually. Use this video, the diagrams, and the status-reading notes together.</p>
                        </div>
                      </div>
                      <div class="compare-video"><iframe src="https://www.youtube.com/embed/t2RfIMizWwc" title="Aadhaar linked versus DBT enabled" loading="lazy" allowfullscreen></iframe></div>
                      <div class="diagram-split">
                        <div class="diagram-panel"><span>Linked</span><div class="diagram-node">Aadhaar linked in bank record</div><div class="diagram-node soft-node">Association exists</div></div>
                        <div class="diagram-panel"><span>DBT Enabled</span><div class="diagram-node accent-node">Official route points to intended bank</div><div class="diagram-node">Benefit can reach the right destination</div></div>
                      </div>
                      <div class="comparison-flow-card">
                        <div class="comparison-mini-flow">
                          <img src="/assets/aadhaar-logo.png" alt="Aadhaar logo">
                          <div class="diagram-arrow">&rarr;</div>
                          <div class="diagram-node">Bank Link Created</div>
                          <div class="diagram-arrow">&rarr;</div>
                          <div class="diagram-node accent-node">DBT Destination Confirmed</div>
                        </div>
                      </div>
                    </article>
                  </div>
                  <div class="insight-grid">
                    <article class="section-card info-tile"><h3>What Linked Tells You</h3><p>It indicates that Aadhaar has been associated with a bank account record. This is useful, but it is still only part of the complete DBT picture.</p></article>
                    <article class="section-card info-tile"><h3>What DBT Enabled Tells You</h3><p>It is closer to the actual payment destination logic, which is why residents should pay extra attention to the bank shown in the official result.</p></article>
                    <article class="section-card info-tile"><h3>Why Comparison Matters</h3><p>Without this distinction, users may complain that they linked Aadhaar but did not receive DBT, when the real issue is the currently active destination path.</p></article>
                  </div>
                  <section class="info-block">
                    <h3>%s</h3>
                    <p>%s</p>
                    <div class="table-shell"><table class="comparison-table"><thead><tr><th>%s</th><th>%s</th><th>%s</th><th>%s</th></tr></thead><tbody>%s</tbody></table></div>
                  </section>
                  <div class="knowledge-grid">
                    <article class="section-card feature-copy">
                      <h3>How To Read The Difference Properly</h3>
                      <ul class="steps-list">
                        <li>First ask whether Aadhaar is merely associated with the account at the bank level.</li>
                        <li>Then ask whether the official resident-facing result points to the same bank for Aadhaar-based benefit routing.</li>
                        <li>If the bank shown in the official result differs from your expected bank, do not assume the latest branch visit already finished the process.</li>
                        <li>Use this comparison phase as the interpretation guide before opening the Check Account workflow.</li>
                      </ul>
                    </article>
                    <article class="section-card branded-info-card">
                      <div class="section-banner-brand compact-brand">
                        <img src="/assets/learn-panel.png" alt="Aadhaar and DBT awareness illustration">
                        <div>
                          <h3>Why This Page Looks Detailed</h3>
                          <p>This module is meant to remove the exact confusion that causes residents to mix up linkage with benefit-readiness. The extra examples and visuals are included so the distinction feels authentic and easy to remember.</p>
                        </div>
                      </div>
                    </article>
                  </div>
                  <aside class="note-panel">%s</aside>
                </section>
                """.formatted(
                escape(content.comparison().title()),
                escape(content.comparison().subtitle()),
                escape(content.ui().comparisonTitle()),
                escape(content.ui().comparisonSubtitle()),
                escape(content.ui().factorHeader()),
                escape(content.ui().linkedHeader()),
                escape(content.ui().dbtHeader()),
                escape(content.ui().impactHeader()),
                rows,
                escape(content.comparison().awarenessNote()));
    }
    private String renderCheckSection(ViewState viewState, AppContent content) {
        CheckAccountSection section = content.checkAccount();
        String prompts = checkPrompt("linked", section.prompts().get(0), viewState.checkFormState().linked())
                + checkPrompt("seeded", section.prompts().get(1), viewState.checkFormState().seeded())
                + checkPrompt("preferredBank", section.prompts().get(2), viewState.checkFormState().preferredBank())
                + checkPrompt("schemeUsesDbt", section.prompts().get(3), viewState.checkFormState().schemeUsesDbt());

        StringBuilder nextSteps = new StringBuilder();
        for (String step : section.nextSteps()) {
            nextSteps.append("<li>").append(escape(step)).append("</li>");
        }

        String assessmentCard = "";
        CheckAssessment assessment = viewState.checkAssessment();
        if (assessment != null) {
            assessmentCard = "<div class=\"assessment-box %s\"><h3>%s</h3><p>%s</p></div>".formatted(assessment.toneClass(), escape(assessment.title()), escape(assessment.body()));
        }

        String lookupCard = "";
        AadhaarLookupResult lookup = viewState.aadhaarLookupResult();
        if (lookup != null) {
            lookupCard = "<div class=\"assessment-box %s\"><h3>%s</h3><p>%s</p></div>".formatted(lookup.toneClass(), escape(lookup.title()), escape(lookup.body()));
        }

        return """
                <section class="main-section">
                  <div class="section-heading">
                    <h2>%s</h2>
                    <p>%s</p>
                  </div>
                  <div class="split-grid">
                    <form class="section-card form-card" method="post" action="/?section=check-account">
                      <div class="aadhaar-lookup">
                        <h3>Aadhaar Number Check</h3>
                        <p>Enter a 12-digit Aadhaar number. This website validates only the format and then directs the user to the official checking workflow.</p>
                        <label class="field-label" for="aadhaarNumber">12-digit Aadhaar number</label>
                        <input class="aadhaar-input" id="aadhaarNumber" name="aadhaarNumber" type="text" inputmode="numeric" maxlength="12" pattern="[0-9]{12}" placeholder="Enter 12 digits" value="%s">
                      </div>
                      %s
                      <div class="check-grid">%s</div>
                      <div class="button-row form-actions">
                        <button class="primary-button" type="submit">Assess My Status</button>
                        <a class="ghost-button" href="https://myaadhaar.uidai.gov.in/" target="_blank" rel="noreferrer">Check Official Status</a>
                      </div>
                      %s
                    </form>
                    <aside class="section-card steps-card">
                      <div class="site-link-group">
                        <a class="ghost-button" href="https://myaadhaar.uidai.gov.in/" target="_blank" rel="noreferrer">UIDAI</a>
                        <a class="ghost-button" href="https://dbtbharat.gov.in/" target="_blank" rel="noreferrer">DBT Bharat</a>
                        <a class="ghost-button" href="https://www.npci.org.in/what-we-do/nach/faqs/customers" target="_blank" rel="noreferrer">NPCI</a>
                      </div>
                      <h3>How To Check Officially</h3>
                      <ul class="steps-list">%s</ul>
                    </aside>
                  </div>
                  <div class="portal-process-grid">
                    <article class="section-card portal-card">
                      <span class="source-badge">MyAadhaar / UIDAI</span>
                      <h3>Direct Status Check</h3>
                      <ol class="steps-list">
                        <li>Open MyAadhaar or the UIDAI service hub and select the resident-facing bank linking or seeding related service.</li>
                        <li>Enter the Aadhaar details and complete OTP verification with the registered mobile number.</li>
                        <li>Read the bank name shown in the result and compare it with the bank where you expect DBT credits.</li>
                        <li>If the shown bank is different, do not guess. Contact the bank and ask what the latest active status is.</li>
                      </ol>
                    </article>
                    <article class="section-card portal-card">
                      <span class="source-badge">DBT Bharat</span>
                      <h3>Portal Guided Check</h3>
                      <ol class="steps-list">
                        <li>Use DBT Bharat when the resident needs service context, document support, or guided navigation toward the official check path.</li>
                        <li>Review the relevant citizen pages and then continue to the official status service from the listed references.</li>
                        <li>Use the documents and multimedia pages if the resident wants more understanding after checking the status.</li>
                        <li>Keep comparing the bank shown in the official response with the bank actually intended for DBT.</li>
                      </ol>
                    </article>
                    <article class="section-card portal-card">
                      <span class="source-badge">NPCI / Bank</span>
                      <h3>Mapper Confirmation Path</h3>
                      <ol class="steps-list">
                        <li>Read the NPCI customer FAQ to understand why mapper and seeding explanations matter for Aadhaar-based routing.</li>
                        <li>After checking the official resident-facing result, ask the bank if the intended account is active for Aadhaar-based benefit routing.</li>
                        <li>If you recently changed banks, ask which account is currently active for Aadhaar-linked DBT credits.</li>
                        <li>If needed, submit a fresh update at the intended bank and recheck later using the official portal.</li>
                      </ol>
                    </article>
                  </div>
                  <div class="video-feature">
                    <article class="section-card feature-copy">
                      <h3>Watch The Status Check Flow</h3>
                      <p>This embedded video supports the same official checking process listed above, so the user can see the workflow visually inside the website.</p>
                    </article>
                    <div class="video-frame"><iframe src="https://www.youtube.com/embed/t2RfIMizWwc" title="How to check Aadhaar bank linking status" loading="lazy" allowfullscreen></iframe></div>
                  </div>
                </section>
                """.formatted(escape(section.title()), escape(section.subtitle()), escape(viewState.checkFormState().aadhaarNumber()), lookupCard, prompts, assessmentCard, nextSteps);
    }

    private String renderVideoSection(AppContent content) {
        List<VideoItem> embeddedVideos = new ArrayList<>();
        StringBuilder links = new StringBuilder();
        for (VideoItem video : content.videos()) {
            if (!video.embedId().isBlank()) {
                embeddedVideos.add(video);
            } else {
                links.append(resourceCard(video.title(), video.description(), "Useful when the resident wants more related official video material or grouped tutorials.", video.source(), video.url(), "Open Video Page"));
            }
        }
        VideoItem featuredVideo = embeddedVideos.isEmpty() ? null : embeddedVideos.get(0);
        StringBuilder spotlightCards = new StringBuilder();
        StringBuilder galleryCards = new StringBuilder();
        for (int index = 1; index < embeddedVideos.size(); index++) {
            VideoItem video = embeddedVideos.get(index);
            String card = index <= 3 ? miniVideoCard(video) : videoGalleryCard(video);
            if (index <= 3) {
                spotlightCards.append(card);
            } else {
                galleryCards.append(card);
            }
        }
        if (galleryCards.isEmpty() && featuredVideo != null) {
            for (int index = 1; index < embeddedVideos.size(); index++) {
                galleryCards.append(videoGalleryCard(embeddedVideos.get(index)));
            }
        }
        return """
                <section class="main-section">
                  <div class="section-heading">
                    <h2>Watch Video</h2>
                    <p>Official Aadhaar awareness videos are embedded here so the user can watch them directly inside the website without leaving this page.</p>
                  </div>
                  %s
                  <div class="video-collection-intro section-card">
                    <div>
                      <span class="pill-label">In-Website Video Library</span>
                      <h3>More Official Aadhaar Awareness Videos</h3>
                      <p>These videos continue the same learning flow with app guidance, Aadhaar service use cases, verification walkthroughs, and public-awareness explainers.</p>
                    </div>
                  </div>
                  <div class="video-embed-grid">%s</div>
                  <div class="resource-grid">%s</div>
                </section>
                """.formatted(featuredVideo == null ? "" : featuredVideoLayout(featuredVideo, spotlightCards.toString()), galleryCards, links);
    }

    private String renderResourcesSection(AppContent content) {
        StringBuilder cards = new StringBuilder();
        for (ResourceItem item : content.resources()) {
            cards.append(resourceCard(item.title(), item.description(), resourcePurpose(item.title(), item.url()), item.source(), item.url(), "Open Link"));
        }
        return """
                <section class="main-section">
                  <div class="section-heading">
                    <h2>Resources</h2>
                    <p>This combined resources page includes official portals, service pages, explainers, FAQs, and related helpful references. It replaces the separate government links section.</p>
                  </div>
                  <div class="resource-summary">
                    <article class="section-card feature-copy"><h3>How To Use These Resources</h3><ul class="steps-list"><li>Start with UIDAI or MyAadhaar when you want live resident-facing services.</li><li>Use NPCI references when you want to understand mapper or routing logic.</li><li>Use DBT Bharat for government DBT background, documents, and multimedia support.</li></ul></article>
                    <article class="section-card feature-copy"><h3>What You Will Find Here</h3><p>The page shows more than forty links in three columns with titles, descriptions, and why each link is useful.</p></article>
                  </div>
                  <div class="resource-grid">%s</div>
                </section>
                """.formatted(cards);
    }

    private String renderQuizSection(ViewState viewState, AppContent content) {
        QuizAssessment quiz = viewState.quizAssessment();
        List<QuizQuestion> questions = quiz.seed() == 0L ? List.of() : pickQuestions(content.quizQuestions(), quiz.seed(), 5);
        if (quiz.seed() == 0L || quiz.step() < 0) {
            return """
                    <section class="main-section" id="quiz-panel">
                      <div class="section-heading">
                        <h2>Quiz And Awareness</h2>
                        <p>Start a fresh five-question quiz. The question bank stores thirty questions and each new start picks five again.</p>
                      </div>
                      <div class="hero-grid">
                        <article class="section-card feature-copy">
                          <h3>Start The Quiz</h3>
                          <p>Each question appears one by one. After you submit an answer, the page shows whether the answer is correct and then lets you continue. The final score appears only after all five questions are completed.</p>
                          <ul class="steps-list"><li>Five questions per round.</li><li>Random set chosen from a larger bank.</li><li>Restart creates a new set again.</li></ul>
                          <a class="primary-button" href="/?section=quiz&startQuiz=1#quiz-panel" onclick="rememberScroll('quiz-panel')">Start Quiz</a>
                        </article>
                        <article class="section-card key-card"><div class="key-title">Quiz Flow</div><ul class="key-list"><li>One question is shown at a time.</li><li>Correct or incorrect feedback appears after each answer.</li><li>The final result card appears only after question five.</li></ul></article>
                      </div>
                    </section>
                    """;
        }

        QuizQuestion question = questions.get(quiz.step());
        if (!quiz.showFeedback()) {
            StringBuilder options = new StringBuilder();
            for (int index = 0; index < question.options().size(); index++) {
                options.append("<label class=\"quiz-option\"><input type=\"radio\" name=\"quizOption\" value=\"").append(index).append("\" required><span>").append(escape(question.options().get(index))).append("</span></label>");
            }
            return """
                    <section class="main-section" id="quiz-panel">
                      <div class="section-heading"><h2>Quiz And Awareness</h2><p>Answer the question and move to the next step.</p></div>
                      <form class="section-card quiz-card" method="post" action="/?section=quiz#quiz-panel" onsubmit="rememberScroll('quiz-panel')">
                        <input type="hidden" name="quizSeed" value="%s">
                        <input type="hidden" name="quizStep" value="%s">
                        <input type="hidden" name="quizScore" value="%s">
                        <div class="quiz-progress">Question %s of %s</div>
                        <h3>%s</h3>
                        <div class="quiz-options">%s</div>
                        <div class="quiz-actions"><button class="primary-button" type="submit">Next</button></div>
                      </form>
                    </section>
                    """.formatted(quiz.seed(), quiz.step(), quiz.score(), quiz.step() + 1, quiz.total(), escape(question.prompt()), options);
        }

        if (quiz.completed()) {
            return """
                    <section class="main-section" id="quiz-panel">
                      <div class="section-card final-score-card">
                        <div class="quiz-complete-badge">Quiz Completed</div>
                        <p class="final-score-subtitle">%s/%s Questions Completed</p>
                        <h2 class="final-score-title">Final Quiz Result</h2>
                        <div class="quiz-result-metrics">
                          <div class="final-metric"><span>Total Questions</span><strong>%s</strong></div>
                          <div class="final-metric"><span>Correct Answers</span><strong>%s/%s</strong></div>
                          <div class="final-metric highlight"><span>Your Score</span><strong>%s/%s</strong></div>
                        </div>
                        <p class="final-score-subtitle strong">You got %s/%s correct.</p>
                        <p class="final-score-text">%s</p>
                        <div class="quiz-actions centered-actions">
                          <a class="primary-button" href="/?section=quiz&restartQuiz=1#quiz-panel" onclick="rememberScroll('quiz-panel')">Restart Quiz</a>
                          <a class="ghost-button" href="/?section=quiz#quiz-panel" onclick="rememberScroll('quiz-panel')">Back</a>
                        </div>
                      </div>
                    </section>
                    """.formatted(quiz.total(), quiz.total(), quiz.total(), quiz.score(), quiz.total(), quiz.score(), quiz.total(), quiz.score(), quiz.total(), escape(finalQuizSummary(quiz.score(), quiz.total())));
        }

        StringBuilder feedbackOptions = new StringBuilder();
        for (int index = 0; index < question.options().size(); index++) {
            String css = "quiz-option";
            if (index == question.correctIndex()) {
                css += " correct-option";
            } else if (index == quiz.selectedIndex()) {
                css += " wrong-option";
            }
            feedbackOptions.append("<div class=\"").append(css).append("\"><span>").append(escape(question.options().get(index))).append("</span></div>");
        }
        return """
                <section class="main-section" id="quiz-panel">
                  <div class="section-heading"><h2>Quiz And Awareness</h2><p>Review the explanation, then continue to the next question.</p></div>
                  <div class="section-card quiz-card">
                    <div class="quiz-progress">Question %s of %s</div>
                    <h3>%s</h3>
                    <div class="quiz-options">%s</div>
                    <div class="assessment-box %s quiz-feedback-box"><h3>%s</h3><p>%s</p></div>
                    <div class="quiz-actions"><a class="primary-button" href="/?section=quiz&quizSeed=%s&quizStep=%s&quizScore=%s#quiz-panel" onclick="rememberScroll('quiz-panel')">Next Question</a></div>
                  </div>
                </section>
                """.formatted(
                quiz.step() + 1,
                quiz.total(),
                escape(question.prompt()),
                feedbackOptions,
                quiz.selectedIndex() == question.correctIndex() ? "success" : "warning",
                quiz.selectedIndex() == question.correctIndex() ? "Correct Answer" : "Not Correct",
                escape(quiz.explanation()),
                quiz.seed(),
                quiz.step() + 1,
                quiz.score());
    }

    private String renderAiAssistantSection(ViewState viewState) {
        AiAssistantState state = viewState.aiAssistantState();
        String answerCard = "";
        if (!state.aiAnswer().isBlank()) {
            answerCard = "<div class=\"assessment-box success\"><h3>%s</h3><p>%s</p></div>".formatted(escape(answerTitle(state)), escape(state.aiAnswer()));
        } else if (!state.aiError().isBlank()) {
            answerCard = "<div class=\"assessment-box danger\"><h3>AI Assistant Error</h3><p>%s</p></div>".formatted(escape(state.aiError()));
        }
        return """
                <section class="main-section">
                  <div class="section-heading">
                    <h2>AI Assistant</h2>
                    <p>Use suggested questions or ask your own Aadhaar and DBT question. The answer appears on this same page.</p>
                  </div>
                  <div class="split-grid">
                    <article class="section-card feature-copy">
                      <h3>Suggested Questions</h3>
                      <div class="faq-list">
                        <a class="ghost-button" href="/?section=ai-assistant&faq=link-difference">Aadhaar linked vs DBT enabled</a>
                        <a class="ghost-button" href="/?section=ai-assistant&faq=how-to-check">How do I check the status?</a>
                        <a class="ghost-button" href="/?section=ai-assistant&faq=multiple-banks">What if I used two banks?</a>
                        <a class="ghost-button" href="/?section=ai-assistant&faq=bank-change">How do I change the DBT bank?</a>
                        <a class="ghost-button" href="/?section=ai-assistant&faq=npci-mapper">What is NPCI mapper?</a>
                        <a class="ghost-button" href="/?section=ai-assistant&faq=dbt-delay">Why is my DBT delayed?</a>
                        <a class="ghost-button" href="/?section=ai-assistant&faq=mobile-number">Why does mobile number matter?</a>
                        <a class="ghost-button" href="/?section=ai-assistant&faq=aadhaar-number-check">What does Aadhaar number check do?</a>
                      </div>
                      %s
                    </article>
                    <form class="section-card feature-copy" method="post" action="/?section=ai-assistant">
                      <h3>Ask More</h3>
                      <label class="field-label" for="aiQuestion">Your question</label>
                      <textarea class="ai-textarea" id="aiQuestion" name="aiQuestion" placeholder="Ask about Aadhaar linking, DBT, UIDAI, NPCI, or bank routing.">%s</textarea>
                      <div class="button-row form-actions"><button class="primary-button" type="submit">Ask AI Assistant</button></div>
                    </form>
                  </div>
                </section>
                """.formatted(answerCard, escape(state.aiQuestion()));
    }
    private String checkPrompt(String name, String prompt, boolean checked) {
        return "<label class=\"check-option\"><input type=\"checkbox\" name=\"%s\"%s><span>%s</span></label>".formatted(escape(name), checked ? " checked" : "", escape(prompt));
    }

    private String resourceCard(String title, String description, String whyUse, String source, String url, String buttonLabel) {
        return """
                <article class="section-card resource-card">
                  <div class="resource-copy">
                    <span class="source-badge">%s</span>
                    <h3>%s</h3>
                    <p>%s</p>
                    <p class="resource-why"><strong>Why use this:</strong> %s</p>
                  </div>
                  <div class="resource-actions"><a class="primary-button" href="%s" target="_blank" rel="noreferrer">%s</a></div>
                </article>
                """.formatted(escape(source), escape(title), escape(description), escape(whyUse), escape(url), escape(buttonLabel));
    }

    private String resourcePurpose(String title, String url) {
        String key = (title + " " + url).toLowerCase();
        if (key.contains("npci") || key.contains("mapper")) {
            return "Useful for understanding mapper seeding, routing, and why a linked bank can differ from the active DBT destination.";
        }
        if (key.contains("myth") || key.contains("faq")) {
            return "Useful when the resident needs authoritative clarification instead of relying on assumptions or reposted answers.";
        }
        if (key.contains("video") || key.contains("multimedia")) {
            return "Useful when the resident prefers guided visual explanation along with portal-based learning.";
        }
        if (key.contains("myaadhaar") || key.contains("service") || key.contains("appointment")) {
            return "Useful when the resident wants an actual official service or service navigation page.";
        }
        return "Useful as a trusted reference source while learning, comparing, or checking Aadhaar and DBT status.";
    }

    private String answerTitle(AiAssistantState state) {
        if (!state.selectedFaqId().isBlank()) {
            return switch (state.selectedFaqId()) {
                case "link-difference" -> "Aadhaar Linked vs DBT Enabled";
                case "how-to-check" -> "How To Check The Status";
                case "multiple-banks" -> "Multiple Banks";
                case "bank-change" -> "Changing The DBT Bank";
                case "npci-mapper" -> "NPCI Mapper";
                case "dbt-delay" -> "DBT Delay";
                case "mobile-number" -> "Mobile Number And OTP";
                default -> "AI Assistant Answer";
            };
        }
        return "AI Assistant Answer";
    }

    private String featuredVideoLayout(VideoItem featuredVideo, String spotlightCards) {
        return """
                <div class="video-stage-grid">
                  <article class="section-card featured-video-card">
                    <div class="featured-video-copy">
                      <span class="pill-label">Featured Official Video</span>
                      <h3>%s</h3>
                      <p>%s</p>
                      <div class="featured-video-meta">
                        <span class="source-badge">%s</span>
                        <a class="ghost-button" href="%s" target="_blank" rel="noreferrer">Open On YouTube</a>
                      </div>
                    </div>
                    <div class="video-frame featured-frame"><iframe src="https://www.youtube.com/embed/%s" title="%s" loading="lazy" allowfullscreen></iframe></div>
                  </article>
                  <div class="video-spotlight-list">%s</div>
                </div>
                """.formatted(
                escape(featuredVideo.title()),
                escape(featuredVideo.description()),
                escape(featuredVideo.source()),
                escape(featuredVideo.url()),
                escape(featuredVideo.embedId()),
                escape(featuredVideo.title()),
                spotlightCards);
    }

    private String miniVideoCard(VideoItem video) {
        return """
                <article class="section-card mini-video-card">
                  <div class="mini-video-frame"><iframe src="https://www.youtube.com/embed/%s" title="%s" loading="lazy" allowfullscreen></iframe></div>
                  <div class="embed-meta">
                    <span class="source-badge">%s</span>
                    <h3>%s</h3>
                    <p>%s</p>
                  </div>
                </article>
                """.formatted(
                escape(video.embedId()),
                escape(video.title()),
                escape(video.source()),
                escape(video.title()),
                escape(video.description()));
    }

    private String videoGalleryCard(VideoItem video) {
        return """
                <article class="section-card embed-card">
                  <div class="embed-frame"><iframe src="https://www.youtube.com/embed/%s" title="%s" loading="lazy" allowfullscreen></iframe></div>
                  <div class="embed-meta"><span class="source-badge">%s</span><h3>%s</h3><p>%s</p></div>
                </article>
                """.formatted(
                escape(video.embedId()),
                escape(video.title()),
                escape(video.source()),
                escape(video.title()),
                escape(video.description()));
    }

    private String finalQuizSummary(int score, int total) {
        if (score == total) {
            return "Excellent. You answered all five questions correctly.";
        }
        if (score >= 3) {
            return "Good understanding. Review the explanations once more and restart if you want a fresh set of questions.";
        }
        return "Review the explanations and try another random set. The restart button loads a new five-question quiz.";
    }

    private String navBadge(Section section) {
        return switch (section) {
            case HOME -> "H";
            case LEARN -> "L";
            case COMPARISON -> "CP";
            case CHECK_ACCOUNT -> "C";
            case VIDEOS -> "V";
            case QUIZ -> "Q";
            case RESOURCES, GOVERNMENT -> "R";
            case AI_ASSISTANT -> "AI";
        };
    }

    private String homeIconSvg(String iconClass) {
        return switch (iconClass) {
            case "learn" -> """
                    <svg class="home-icon-svg" viewBox="0 0 64 64" aria-hidden="true">
                      <circle cx="32" cy="24" r="12" fill="#ffc107"/>
                      <rect x="26" y="38" width="12" height="12" rx="4" fill="#3078df"/>
                    </svg>
                    """;
            case "check" -> """
                    <svg class="home-icon-svg" viewBox="0 0 64 64" aria-hidden="true">
                      <circle cx="32" cy="32" r="22" fill="#3078df" fill-opacity="0.1"/>
                      <path d="M20 32l8 8 16-16" fill="none" stroke="#3078df" stroke-width="6" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    """;
            case "compare" -> """
                    <svg class="home-icon-svg" viewBox="0 0 64 64" aria-hidden="true">
                      <rect x="14" y="16" width="36" height="32" rx="6" fill="#f44336" fill-opacity="0.1" stroke="#f44336" stroke-width="4"/>
                      <path d="M22 28h20M22 36h12" stroke="#f44336" stroke-width="4" stroke-linecap="round"/>
                    </svg>
                    """;
            case "video" -> """
                    <svg class="home-icon-svg" viewBox="0 0 64 64" aria-hidden="true">
                      <circle cx="32" cy="32" r="22" fill="#3078df" fill-opacity="0.1"/>
                      <path d="M26 22l18 10-18 10z" fill="#3078df"/>
                    </svg>
                    """;
            case "resources" -> """
                    <svg class="home-icon-svg" viewBox="0 0 64 64" aria-hidden="true">
                      <path d="M32 12l20 10H12z" fill="#ffc107"/>
                      <rect x="16" y="24" width="32" height="28" rx="2" fill="#3078df"/>
                    </svg>
                    """;
            case "quiz" -> """
                    <svg class="home-icon-svg" viewBox="0 0 64 64" aria-hidden="true">
                      <circle cx="32" cy="24" r="14" fill="#ff9800"/>
                      <path d="M32 38v4" stroke="#fff" stroke-width="6" stroke-linecap="round"/>
                    </svg>
                    """;
            case "ai" -> """
                    <svg class="home-icon-svg" viewBox="0 0 64 64" aria-hidden="true">
                      <rect x="12" y="16" width="40" height="32" rx="10" fill="#3078df" fill-opacity="0.1" stroke="#3078df" stroke-width="4"/>
                      <circle cx="24" cy="28" r="3" fill="#3078df"/>
                      <circle cx="32" cy="28" r="3" fill="#3078df"/>
                      <circle cx="40" cy="28" r="3" fill="#3078df"/>
                    </svg>
                    """;
            default -> "";
        };
    }

    private List<QuizQuestion> pickQuestions(List<QuizQuestion> allQuestions, long seed, int count) {
        List<QuizQuestion> shuffled = new ArrayList<>(allQuestions);
        Collections.shuffle(shuffled, new Random(seed));
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }

    private String sectionHref(Section section) {
        return "/?section=" + section.routeKey();
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private String behaviorScript() {
        return """
                <script>
                  const CODEX_LANGUAGE_MAP = {
                    ENGLISH: 'en', HINDI: 'hi', TELUGU: 'te', KANNADA: 'kn', TAMIL: 'ta', MALAYALAM: 'ml', BENGALI: 'bn', MARATHI: 'mr', ODIA: 'or', GUJARATI: 'gu', ASSAMESE: 'as'
                  };
                  function setTranslateCookie(targetCode) {
                    const value = '/en/' + targetCode;
                    document.cookie = 'googtrans=' + value + ';path=/;SameSite=Lax';
                    document.cookie = 'googtrans=' + value + ';path=/';
                  }
                  function withTranslateCombo(callback) {
                    const existing = document.querySelector('.goog-te-combo');
                    if (existing) { callback(existing); return; }
                    const observer = new MutationObserver(function() {
                      const combo = document.querySelector('.goog-te-combo');
                      if (combo) { observer.disconnect(); callback(combo); }
                    });
                    observer.observe(document.body, { childList: true, subtree: true });
                    setTimeout(function() { observer.disconnect(); }, 8000);
                  }
                  function applyLanguageInternal(languageKey, persist) {
                    const target = CODEX_LANGUAGE_MAP[languageKey] || 'en';
                    if (persist) {
                      try { localStorage.setItem('codex-selected-language', languageKey); } catch (error) {}
                    }
                    setTranslateCookie(target);
                    const select = document.getElementById('languageSelect');
                    if (select && select.value !== languageKey) { select.value = languageKey; }
                    withTranslateCombo(function(combo) {
                      if (combo.value !== target) {
                        combo.value = target;
                        combo.dispatchEvent(new Event('change'));
                      }
                    });
                  }
                  function googleTranslateElementInit() {
                    if (!window.google || !google.translate || !google.translate.TranslateElement) { return; }
                    new google.translate.TranslateElement({ pageLanguage: 'en', includedLanguages: 'en,hi,te,kn,ta,ml,bn,mr,or,gu,as', autoDisplay: false }, 'google_translate_element');
                    setTimeout(function() { initializeLanguageSelection(); }, 250);
                  }
                  function changeLanguage(languageKey) { applyLanguageInternal(languageKey, true); }
                  function initializeLanguageSelection() {
                    let key = 'ENGLISH';
                    try { key = localStorage.getItem('codex-selected-language') || 'ENGLISH'; } catch (error) {}
                    applyLanguageInternal(key, false);
                  }
                  function rememberScroll(targetId) {
                    try { sessionStorage.setItem('codex-scroll-target', targetId); } catch (error) {}
                  }
                  window.addEventListener('load', function() {
                    try {
                      const targetId = window.location.hash ? window.location.hash.substring(1) : sessionStorage.getItem('codex-scroll-target');
                      if (targetId) {
                        const node = document.getElementById(targetId);
                        if (node) { node.scrollIntoView({ behavior: 'auto', block: 'start' }); }
                      }
                      sessionStorage.removeItem('codex-scroll-target');
                    } catch (error) {}
                  });
                </script>
                <script src="https://translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>
                """;
    }

    private String styles() {
        return """
                :root {
                  --primary: #3078df;
                  --primary-dark: #245eb5;
                  --accent: #ffca54;
                  --surface: #ffffff;
                  --surface-soft: #f7f9fc;
                  --border: #e2e8f1;
                  --text: #1a2333;
                  --muted: #64748b;
                  --success: #ecfdf5;
                  --success-text: #065f46;
                  --warning: #fffbeb;
                  --warning-text: #92400e;
                  --danger: #fef2f2;
                  --danger-text: #b91c1c;
                  --shadow: 0 18px 42px rgba(48, 83, 146, 0.12);
                }
                * { box-sizing: border-box; }
                body { margin: 0; padding: 20px; background: linear-gradient(180deg, #f4f7fc 0%, #eef4fb 100%); color: var(--text); font-family: "Segoe UI", system-ui, -apple-system, sans-serif; -webkit-font-smoothing: antialiased; }
                a { color: inherit; text-decoration: none; }
                .page-shell { max-width: 1320px; margin: 0 auto; background: var(--surface); border: 1px solid #d9e2ef; border-radius: 18px; overflow: hidden; box-shadow: var(--shadow); }
                .window-bar { display: flex; gap: 8px; align-items: center; height: 40px; padding: 0 16px; background: #fff; border-bottom: 1px solid var(--border); }
                .window-dot { width: 12px; height: 12px; border-radius: 50%; opacity: 0.8; }
                .window-dot.red { background: #ff5f57; } .window-dot.amber { background: #febc2e; } .window-dot.green { background: #28c840; }
                .hero-shell { padding-bottom: 0; border-bottom: 1px solid var(--border); }
                .brand-row { display: flex; justify-content: space-between; align-items: center; padding: 24px 32px; }
                .brand-lockup { display: flex; align-items: center; gap: 24px; }
                .brand-logo { width: 90px; height: auto; }
                h1 { margin: 0 0 4px; font-size: 2.25rem; font-weight: 800; color: #1e293b; letter-spacing: -0.02em; }
                .brand-row p { margin: 0; color: var(--muted); font-size: 1.1rem; }
                .google-translate-anchor { position: absolute; width: 0; height: 0; overflow: hidden; }
                body > .skiptranslate, iframe.skiptranslate { display: none !important; }
                body { top: 0 !important; }
                .language-chip { display: inline-flex; align-items: center; gap: 8px; padding: 8px 16px; border-radius: 10px; border: 1px solid var(--border); background: #fff; cursor: pointer; }
                .flag-badge { font-weight: 700; color: var(--muted); font-size: 0.85rem; }
                .language-chip select { border: 0; background: transparent; font-size: 0.95rem; font-weight: 500; outline: none; cursor: pointer; color: var(--text); }
                .top-nav { display: flex; background: var(--primary); padding: 0 24px; overflow-x: auto; scrollbar-width: none; }
                .top-nav::-webkit-scrollbar { display: none; }
                .nav-link { display: flex; align-items: center; gap: 10px; padding: 16px 20px; color: #fff; font-weight: 600; font-size: 0.95rem; white-space: nowrap; transition: all 0.2s; border-radius: 0; }
                .nav-link.active { background: #eef5ff; color: var(--primary); border-top-left-radius: 8px; border-top-right-radius: 8px; }
                .nav-badge { background: rgba(255, 255, 255, 0.2); color: #fff; padding: 2px 8px; border-radius: 6px; font-size: 0.75rem; font-weight: 800; min-width: 24px; text-align: center; }
                .nav-link.active .nav-badge { background: var(--primary); color: #fff; }
                .content-wrap { padding: 40px 32px; background: #fff; }
                .main-section { display: flex; flex-direction: column; gap: 28px; }
                .section-heading { margin-bottom: 40px; }
                .section-heading h2 { font-size: 2.5rem; font-weight: 800; margin: 0 0 12px; color: #1e293b; letter-spacing: -0.03em; }
                .section-heading p { margin: 0; font-size: 1.15rem; color: var(--muted); max-width: 900px; line-height: 1.6; }
                .section-card, .note-panel, .assessment-box { border: 1px solid var(--border); border-radius: 24px; background: var(--surface); box-shadow: 0 14px 32px rgba(84, 110, 160, 0.08); }
                .solo-card { padding: 28px; }
                .phase-shell { margin-bottom: 28px; }
                .phase-trail { margin-bottom: 12px; color: var(--muted); font-weight: 600; }
                .phase-steps { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 14px; }
                .phase-card { display: flex; align-items: center; gap: 12px; padding: 14px 16px; border-radius: 18px; border: 1px solid var(--border); background: #f8fbff; color: var(--muted); }
                .phase-card.active { background: linear-gradient(90deg, var(--primary), var(--primary-dark)); color: #fff; border-color: transparent; }
                .phase-number { display: grid; place-items: center; width: 34px; height: 34px; border-radius: 11px; background: rgba(48, 120, 223, 0.12); font-weight: 800; }
                .phase-card.active .phase-number { background: rgba(255,255,255,0.18); }
                .phase-label { font-weight: 800; }
                .phase-arrow { margin-left: auto; font-size: 1.4rem; }
                .hero-grid, .split-grid, .knowledge-grid, .resource-summary, .video-feature { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 24px; }
                .insight-grid, .portal-process-grid, .resource-grid, .video-embed-grid { display: grid; gap: 20px; }
                .insight-grid, .portal-process-grid, .resource-grid, .video-embed-grid { grid-template-columns: repeat(3, minmax(0, 1fr)); }
                .home-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 32px; }
                .home-card { padding: 32px; border-radius: 24px; border: 1px solid var(--border); background: #fff; display: flex; flex-direction: column; gap: 24px; transition: transform 0.2s, box-shadow 0.2s; position: relative; }
                .home-card:hover { transform: translateY(-4px); box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.05); }
                .home-card-header { display: flex; align-items: flex-start; gap: 20px; }
                .home-icon-shell { width: 64px; height: 64px; border-radius: 16px; display: grid; place-items: center; flex-shrink: 0; }
                .home-icon-shell.learn { background: #fff8e1; }
                .home-icon-shell.compare { background: #fff1f2; }
                .home-icon-shell.check { background: #eff6ff; }
                .home-icon-shell.video { background: #f0f9ff; }
                .home-icon-shell.resources { background: #fff7ed; }
                .home-icon-shell.quiz { background: #fdf2f8; }
                .home-icon-shell.ai { background: #f5f3ff; }
                .home-icon-svg { width: 32px; height: 32px; }
                .home-card-copy { flex: 1; }
                .home-card-copy h3 { margin: 0 0 6px; font-size: 1.25rem; font-weight: 700; color: #1e293b; }
                .home-card-copy p { margin: 0; color: var(--muted); line-height: 1.5; font-size: 1rem; }
                .home-card-button { display: inline-flex; align-items: center; justify-content: center; width: 100%; padding: 12px; background: #f1f5f9; color: var(--primary); font-weight: 700; border-radius: 12px; font-size: 0.95rem; transition: background 0.2s; }
                .home-card-button:hover { background: #e2e8f0; }
                .section-banner { padding: 24px; display: grid; gap: 14px; background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%); }
                .comparison-banner { background: linear-gradient(180deg, #fffdf6 0%, #f8fbff 100%); }
                .section-banner-brand { display: flex; align-items: center; gap: 16px; }
                .section-banner-brand img { width: 84px; max-width: 100%; height: auto; flex: 0 0 auto; object-fit: contain; }
                .compact-brand { align-items: flex-start; }
                .compact-brand img { width: 70px; border-radius: 16px; }
                .section-banner p, .support-copy, .feature-copy p, .compare-example p, .resource-card p, .portal-card p, .note-panel, .learn-image-caption p { color: var(--muted); line-height: 1.65; }
                .learn-hero-card, .key-card, .form-card, .steps-card, .feature-copy, .portal-card, .resource-card, .quiz-card, .compare-card, .diagram-card, .info-tile, .learn-image-card, .branded-info-card { padding: 24px; }
                .learn-hero-card img, .learn-image-card img { width: 100%; height: auto; display: block; border-radius: 18px; object-fit: cover; }
                .learn-hero-copy, .learn-image-caption, .branded-info-card, .branded-points, .compare-card, .comparison-visual-card, .embed-meta, .featured-video-copy { display: grid; gap: 14px; }
                .pill-label, .source-badge, .quiz-progress, .quiz-complete-badge { display: inline-flex; align-items: center; width: fit-content; padding: 6px 11px; border-radius: 999px; background: #edf4ff; color: var(--primary-dark); font-size: 0.8rem; font-weight: 800; }
                .key-card { background: linear-gradient(180deg, #fff9ea 0%, #fff4d8 100%); }
                .key-title { font-size: 1.35rem; font-weight: 800; margin-bottom: 14px; }
                .key-list, .steps-list, .detail-list { margin: 0; padding-left: 22px; display: grid; gap: 10px; line-height: 1.65; }
                .diagram-stack { display: grid; gap: 10px; margin-top: 16px; }
                .diagram-node { padding: 14px 16px; border-radius: 16px; background: #f5f8ff; border: 1px solid #dbe4f3; text-align: center; font-weight: 800; }
                .accent-node { background: #fff4d8; border-color: #f1cf72; }
                .soft-node { background: #eef4ff; }
                .diagram-arrow { text-align: center; color: var(--primary-dark); font-size: 1.2rem; font-weight: 800; }
                .compare-columns, .compare-example-grid, .diagram-split { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 16px; }
                .compare-panel, .compare-example, .diagram-panel, .comparison-flow-card { padding: 16px; border-radius: 18px; border: 1px solid #dce5f6; background: #fff; }
                .linked-panel { background: linear-gradient(180deg, #ffffff 0%, #f8faff 100%); }
                .dbt-panel { background: linear-gradient(180deg, #fffaf0 0%, #fff0ce 100%); }
                .comparison-mini-flow { display: grid; grid-template-columns: 70px 28px 1fr 28px 1fr; align-items: center; gap: 10px; }
                .comparison-mini-flow img { width: 60px; height: auto; display: block; margin: 0 auto; }
                .compare-video, .video-frame, .embed-frame, .mini-video-frame { overflow: hidden; border-radius: 18px; background: linear-gradient(135deg, #26334d 0%, #3078df 100%); }
                .compare-video iframe, .video-frame iframe, .embed-frame iframe, .mini-video-frame iframe { width: 100%; min-height: 260px; height: 100%; border: 0; display: block; }
                .table-shell { overflow: auto; border-radius: 18px; border: 1px solid var(--border); background: #fff; box-shadow: 0 14px 32px rgba(142,160,198,0.12); }
                table { width: 100%; border-collapse: collapse; }
                thead { background: #eaf2ff; }
                th, td { padding: 16px 18px; text-align: left; vertical-align: top; border-bottom: 1px solid #e6ebf7; line-height: 1.6; }
                .note-panel { padding: 20px 22px; background: linear-gradient(180deg, #fff9e7 0%, #fff3d1 100%); color: #7e5d10; }
                .button-row, .site-link-group, .quiz-actions, .featured-video-meta { display: flex; flex-wrap: wrap; gap: 12px; align-items: center; }
                .primary-button, .ghost-button { display: inline-flex; align-items: center; justify-content: center; gap: 10px; padding: 12px 18px; border-radius: 12px; border: 0; cursor: pointer; font-size: 0.92rem; font-weight: 800; }
                .primary-button { background: linear-gradient(90deg, var(--primary) 0%, var(--primary-dark) 100%); color: #fff; box-shadow: 0 14px 30px rgba(48,120,223,0.22); }
                .ghost-button { background: #eef3fe; color: var(--primary-dark); }
                .check-grid, .quiz-options { display: grid; gap: 12px; margin-bottom: 18px; }
                .check-option, .quiz-option { display: flex; align-items: flex-start; gap: 12px; padding: 13px 15px; border-radius: 14px; background: var(--surface-soft); border: 1px solid #e3e9f6; }
                input[type="checkbox"], input[type="radio"] { margin-top: 5px; accent-color: var(--primary-dark); }
                .aadhaar-lookup { display: grid; gap: 10px; margin-bottom: 18px; padding: 16px; border-radius: 16px; border: 1px solid #dde6f6; background: linear-gradient(180deg, #fbfcff 0%, #f4f7fd 100%); }
                .field-label { font-size: 0.85rem; font-weight: 800; color: #53627f; }
                .aadhaar-input, .ai-textarea { width: 100%; padding: 12px 14px; border-radius: 12px; border: 1px solid #cfdbf0; background: #fff; color: var(--text); font: inherit; }
                .ai-textarea { min-height: 160px; resize: vertical; }
                .assessment-box { margin-top: 18px; padding: 18px 20px; }
                .assessment-box.success { background: var(--success); color: var(--success-text); }
                .assessment-box.warning { background: var(--warning); color: var(--warning-text); }
                .assessment-box.danger { background: var(--danger); color: var(--danger-text); }
                .correct-option { border-color: #83d1a4; background: #eefaf2; }
                .wrong-option { border-color: #f1a7a7; background: #fff1f1; }
                .resource-card, .embed-card, .portal-card { display: grid; gap: 14px; }
                .resource-actions { display: flex; margin-top: auto; }
                .resource-why { color: var(--muted); }
                .video-stage-grid { display: grid; grid-template-columns: minmax(0, 1.35fr) minmax(0, 0.95fr); gap: 22px; }
                .featured-video-card, .mini-video-card, .video-collection-intro { padding: 22px; display: grid; gap: 16px; }
                .featured-frame iframe { min-height: 360px; }
                .video-spotlight-list { display: grid; gap: 16px; }
                .mini-video-frame iframe { min-height: 164px; }
                .final-score-card { text-align: center; max-width: 860px; margin: 0 auto; padding: 34px; display: grid; gap: 16px; background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%); }
                .final-score-title { margin: 0; font-size: 1.8rem; }
                .final-score-subtitle { margin: 0; font-size: 1rem; color: var(--muted); line-height: 1.6; }
                .final-score-subtitle.strong { font-size: 1.08rem; font-weight: 800; color: var(--text); }
                .final-score-text { margin: 0; color: var(--muted); line-height: 1.65; }
                .quiz-result-metrics { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 14px; }
                .final-metric { display: grid; gap: 8px; padding: 18px; border-radius: 18px; background: #f5f8ff; border: 1px solid #dce6f7; }
                .final-metric span { color: var(--muted); font-size: 0.92rem; }
                .final-metric strong { font-size: 1.5rem; color: var(--text); }
                .final-metric.highlight { background: linear-gradient(180deg, #edf4ff 0%, #dfeaff 100%); border-color: #cbdcf8; }
                .centered-actions { justify-content: center; }
                .faq-list { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 10px; margin-top: 12px; }
                .page-footer { text-align: center; margin-top: 60px; padding-top: 30px; border-top: 1px solid var(--border); color: var(--muted); font-size: 0.9rem; }
                @media (max-width: 1120px) {
                  .hero-grid, .split-grid, .knowledge-grid, .resource-summary, .video-feature, .portal-process-grid, .resource-grid, .video-embed-grid, .insight-grid, .home-grid, .compare-columns, .compare-example-grid, .diagram-split, .phase-steps, .video-stage-grid, .quiz-result-metrics, .comparison-mini-flow { grid-template-columns: 1fr; }
                  .faq-list { grid-template-columns: 1fr; }
                }
                @media (max-width: 768px) {
                  body { padding: 8px; }
                  h1 { font-size: 1.75rem; }
                  .brand-row { flex-direction: column; align-items: flex-start; gap: 20px; padding: 22px 20px; }
                  .brand-lockup { flex-direction: column; align-items: flex-start; gap: 12px; }
                  .content-wrap { padding: 30px 20px; }
                  .section-heading h2 { font-size: 2rem; }
                  .home-card-header, .section-banner-brand { align-items: flex-start; }
                  .featured-frame iframe { min-height: 240px; }
                }
                """;
    }
}
