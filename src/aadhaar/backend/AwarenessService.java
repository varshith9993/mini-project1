package aadhaar.backend;

import java.util.List;

public final class AwarenessService {
    public enum LanguageOption {
        ENGLISH("English"),
        HINDI("हिन्दी"),
        TELUGU("తెలుగు"),
        TAMIL("தமிழ்");

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
            CheckAccountSection checkAccount,
            List<VideoItem> videos,
            List<ResourceItem> governmentLinks,
            List<ResourceItem> resources,
            List<QuizQuestion> quizQuestions) {
    }

    public record UiCopy(
            String windowTitle,
            String appTitle,
            String appSubtitle,
            String languageLabel,
            String learnTab,
            String accountTab,
            String videoTab,
            String governmentTab,
            String quizTab,
            String resourcesTab,
            String phaseTrail,
            String comparisonTitle,
            String comparisonSubtitle,
            String factorHeader,
            String linkedHeader,
            String dbtHeader,
            String impactHeader,
            String learnMoreButton,
            String assessmentButton,
            String openVideoButton,
            String openLinkButton,
            String keyPointsTitle,
            String nextStepsTitle,
            String sectionSourcesSubtitle,
            String quizTitle,
            String quizSubtitle,
            String submitQuizButton,
            String resetQuizButton,
            String quizScorePrefix,
            String officialSourcesTitle,
            String footerNote,
            String dbtNotGuaranteedNote) {
    }

    public record LearnSection(
            String title,
            String subtitle,
            List<String> learnBullets,
            List<String> keyPoints,
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

    public record VideoItem(String title, String description, String source, String url) {
    }

    public record ResourceItem(String title, String description, String source, String url) {
    }

    public record QuizQuestion(String prompt, List<String> options, int correctIndex, String explanation) {
    }

    public AppContent getContent(LanguageOption language) {
        return new AppContent(ui(language), learn(language), check(language), videos(), governmentLinks(), resources(), quiz(language));
    }

    private UiCopy ui(LanguageOption language) {
        return switch (language) {
            case ENGLISH -> new UiCopy(
                    "Aadhaar DBT Awareness System",
                    "Aadhaar DBT Awareness System",
                    "Understand Aadhaar linking, DBT readiness, official resources, and awareness checks.",
                    "Language",
                    "Learn",
                    "Check Account",
                    "Watch Video",
                    "Govt Links",
                    "Quiz",
                    "Resources",
                    "Learn Phase",
                    "Aadhaar Linked vs DBT Enabled Aadhaar Seeded Account",
                    "Comparison based on UIDAI, NPCI, and DBT Bharat references verified on 30 March 2026.",
                    "Factor",
                    "Aadhaar linked bank account",
                    "DBT enabled Aadhaar seeded account",
                    "Citizen impact",
                    "Learn More About DBT and Aadhaar",
                    "Assess My Status",
                    "Open Video",
                    "Open Link",
                    "Key Points",
                    "Recommended Next Steps",
                    "Open verified official pages for authentic information and service guidance.",
                    "Quiz and Awareness Check",
                    "Answer a few questions to verify your understanding of Aadhaar linking and DBT routing.",
                    "Submit Quiz",
                    "Reset Quiz",
                    "Score",
                    "Verified Official Sources",
                    "Official references included: UIDAI FAQ pages, UIDAI Myth Busters, NPCI customer FAQs, and DBT Bharat.",
                    "This does not guarantee a credit until a scheme actually uses Aadhaar-based DBT for your benefit."
            );
            case HINDI -> new UiCopy(
                    "आधार DBT जागरूकता प्रणाली",
                    "आधार DBT जागरूकता प्रणाली",
                    "आधार लिंकिंग, DBT तैयारी, सरकारी स्रोत और जागरूकता जाँच को सरल तरीके से समझें।",
                    "भाषा",
                    "सीखें",
                    "खाता जाँचें",
                    "वीडियो देखें",
                    "सरकारी लिंक",
                    "क्विज़",
                    "स्रोत",
                    "सीखने का चरण",
                    "आधार लिंक्ड खाता बनाम DBT सक्षम आधार सीडेड खाता",
                    "यह तुलना UIDAI, NPCI और DBT Bharat के आधिकारिक संदर्भों पर आधारित है।",
                    "कारक",
                    "आधार लिंक्ड बैंक खाता",
                    "DBT सक्षम आधार सीडेड खाता",
                    "नागरिक पर प्रभाव",
                    "DBT और आधार के बारे में और जानें",
                    "मेरी स्थिति जाँचें",
                    "वीडियो खोलें",
                    "लिंक खोलें",
                    "मुख्य बिंदु",
                    "अगले सुझाए गए कदम",
                    "प्रामाणिक जानकारी के लिए सत्यापित आधिकारिक पेज खोलें।",
                    "क्विज़ और जागरूकता जाँच",
                    "आधार लिंकिंग और DBT रूटिंग पर अपनी समझ जाँचें।",
                    "क्विज़ जमा करें",
                    "रीसेट करें",
                    "स्कोर",
                    "सत्यापित आधिकारिक स्रोत",
                    "ऐप में UIDAI FAQ, UIDAI Myth Busters, NPCI FAQ और DBT Bharat शामिल हैं।",
                    "जब तक किसी योजना में Aadhaar आधारित DBT उपयोग न हो, तब तक भुगतान की गारंटी नहीं होती।"
            );
            case TELUGU -> new UiCopy(
                    "ఆధార్ DBT అవగాహన వ్యవస్థ",
                    "ఆధార్ DBT అవగాహన వ్యవస్థ",
                    "ఆధార్ లింకింగ్, DBT సిద్ధత, అధికారిక వనరులు మరియు అవగాహన తనిఖీని సులభంగా తెలుసుకోండి.",
                    "భాష",
                    "తెలుసుకోండి",
                    "ఖాతా తనిఖీ",
                    "వీడియోలు",
                    "ప్రభుత్వ లింకులు",
                    "క్విజ్",
                    "వనరులు",
                    "లెర్న్ దశ",
                    "Aadhaar linked ఖాతా మరియు DBT enabled Aadhaar seeded ఖాతా మధ్య తేడా",
                    "UIDAI, NPCI, DBT Bharat ఆధారిత అధికారిక పోలిక.",
                    "అంశం",
                    "Aadhaar linked బ్యాంక్ ఖాతా",
                    "DBT enabled Aadhaar seeded ఖాతా",
                    "పౌరుడిపై ప్రభావం",
                    "DBT మరియు ఆధార్ గురించి మరింత తెలుసుకోండి",
                    "నా స్థితిని అంచనా వేయండి",
                    "వీడియో తెరవండి",
                    "లింక్ తెరవండి",
                    "ముఖ్య అంశాలు",
                    "తదుపరి సూచనలు",
                    "అధికారిక సమాచారం కోసం ధృవీకరించిన పేజీలను తెరవండి.",
                    "క్విజ్ మరియు అవగాహన పరీక్ష",
                    "Aadhaar linking మరియు DBT routing పై మీ అవగాహనను పరీక్షించుకోండి.",
                    "క్విజ్ సమర్పించండి",
                    "రీసెట్",
                    "స్కోర్",
                    "ధృవీకరించిన అధికారిక వనరులు",
                    "యాప్‌లో UIDAI FAQ, NPCI FAQ, DBT Bharat వంటి అధికారిక వనరులు ఉన్నాయి.",
                    "పథకం Aadhaar ఆధారిత DBT ఉపయోగించినప్పుడు మాత్రమే లబ్ధి రావచ్చు."
            );
            case TAMIL -> new UiCopy(
                    "ஆதார் DBT விழிப்புணர்வு அமைப்பு",
                    "ஆதார் DBT விழிப்புணர்வு அமைப்பு",
                    "ஆதார் இணைப்பு, DBT தயார்நிலை, அரசு ஆதாரங்கள் மற்றும் விழிப்புணர்வு சோதனையை எளிதாக அறிக.",
                    "மொழி",
                    "கற்போம்",
                    "கணக்கு நிலை",
                    "வீடியோ",
                    "அரசு இணைப்புகள்",
                    "வினாடி வினா",
                    "ஆதாரங்கள்",
                    "கற்றல் கட்டம்",
                    "Aadhaar linked கணக்கு மற்றும் DBT enabled Aadhaar seeded கணக்கு இடையிலான வேறுபாடு",
                    "UIDAI, NPCI, DBT Bharat அடிப்படையிலான அதிகாரப்பூர்வ ஒப்பீடு.",
                    "அம்சம்",
                    "Aadhaar linked வங்கி கணக்கு",
                    "DBT enabled Aadhaar seeded கணக்கு",
                    "பொதுமகன் மீது தாக்கம்",
                    "DBT மற்றும் ஆதார் பற்றி மேலும் அறிக",
                    "என் நிலையை மதிப்பிடு",
                    "வீடியோ திற",
                    "இணைப்பை திற",
                    "முக்கிய அம்சங்கள்",
                    "அடுத்தடுத்த படிகள்",
                    "உண்மையான தகவலுக்கு சரிபார்க்கப்பட்ட அதிகாரப்பூர்வ பக்கங்களை திறக்கவும்.",
                    "வினாடி வினா மற்றும் விழிப்புணர்வு சோதனை",
                    "Aadhaar linking மற்றும் DBT routing பற்றிய உங்கள் புரிதலை சோதியுங்கள்.",
                    "சமர்ப்பிக்கவும்",
                    "மீட்டமை",
                    "மதிப்பெண்",
                    "சரிபார்க்கப்பட்ட அதிகாரப்பூர்வ ஆதாரங்கள்",
                    "UIDAI FAQ, NPCI FAQ, DBT Bharat போன்ற அதிகாரப்பூர்வ ஆதாரங்கள் சேர்க்கப்பட்டுள்ளன.",
                    "ஒரு திட்டம் Aadhaar அடிப்படையிலான DBT ஐ பயன்படுத்தினால் மட்டுமே தொகை வர வாய்ப்பு உள்ளது."
            );
        };
    }

    private LearnSection learn(LanguageOption language) {
        return switch (language) {
            case ENGLISH -> new LearnSection(
                    "Learn Phase",
                    "Understand why linking Aadhaar to the correct bank account matters for Direct Benefit Transfer services.",
                    List.of("Bank-side Aadhaar linking establishes the identity relationship in the bank system.", "DBT payments may also need Aadhaar seeding for NPCI mapper-based routing.", "A linked account is not automatically the same as the DBT destination account.", "The latest mapper update usually becomes the active Aadhaar-based DBT destination."),
                    List.of("DBT transfers subsidies and welfare benefits directly into beneficiary accounts.", "Aadhaar linking alone may not complete DBT routing readiness.", "NPCI mapper seeding matters when benefits move through Aadhaar Payment Bridge.", "Citizens should verify the active mapped bank if multiple accounts were seeded."),
                    comparisonPoints(),
                    "Use official portals or your bank branch to confirm your live DBT status before relying on any subsidy credit."
            );
            case HINDI -> new LearnSection(
                    "सीखने का चरण",
                    "समझें कि सही बैंक खाते से आधार लिंक करना प्रत्यक्ष लाभ अंतरण के लिए क्यों महत्वपूर्ण है।",
                    List.of("बैंक में Aadhaar linking से पहचान संबंध स्थापित होता है।", "DBT भुगतान के लिए NPCI mapper आधारित seeding की आवश्यकता हो सकती है।", "Linked खाता हमेशा DBT destination खाता नहीं होता।", "नवीनतम mapper update वाला बैंक DBT गंतव्य बन सकता है।"),
                    List.of("DBT लाभ और सब्सिडी सीधे खाते में भेजता है।", "केवल linking, DBT readiness के लिए पर्याप्त नहीं भी हो सकती है।", "Aadhaar Payment Bridge में NPCI mapper seeding महत्वपूर्ण है।", "यदि कई खाते seed हुए हों तो active bank की पुष्टि करें।"),
                    comparisonPointsHindi(),
                    "सब्सिडी की अपेक्षा करने से पहले आधिकारिक पोर्टल या बैंक से live DBT स्थिति की पुष्टि करें।"
            );
            case TELUGU -> new LearnSection(
                    "లెర్న్ దశ",
                    "సరైన బ్యాంక్ ఖాతాతో ఆధార్ లింక్ ఎందుకు ముఖ్యమో తెలుసుకోండి.",
                    List.of("బ్యాంక్‌లో Aadhaar linking వలన గుర్తింపు సంబంధం నమోదు అవుతుంది.", "DBT కోసం NPCI mapper seeding అవసరం కావచ్చు.", "Linked ఖాతా అంటే అదే DBT destination ఖాతా అని కాదు.", "తాజా mapper update ఉన్న బ్యాంక్ active destination అవుతుంది."),
                    List.of("DBT ద్వారా లబ్ధులు నేరుగా ఖాతాలోకి వస్తాయి.", "కేవలం linking ఉంటే సరిపోదు.", "Aadhaar Payment Bridge కోసం NPCI mapper ముఖ్యం.", "బహుళ ఖాతాలు ఉంటే active bank ఏదో తెలుసుకోండి."),
                    comparisonPointsTelugu(),
                    "సబ్సిడీపై ఆధారపడే ముందు అధికారిక పోర్టల్ లేదా బ్యాంక్ ద్వారా live DBT స్థితిని తెలుసుకోండి."
            );
            case TAMIL -> new LearnSection(
                    "கற்றல் கட்டம்",
                    "சரியான வங்கி கணக்குடன் ஆதாரை இணைப்பது DBT க்கு ஏன் முக்கியமென அறிக.",
                    List.of("வங்கி Aadhaar linking மூலம் அடையாள உறவு பதிவு செய்யப்படுகிறது.", "DBT க்கு NPCI mapper seeding தேவைப்படலாம்.", "Linked account என்பது அவசியம் DBT destination account அல்ல.", "சமீபத்திய mapper update கொண்ட வங்கி active destination ஆக இருக்கும் வாய்ப்பு உள்ளது."),
                    List.of("DBT மூலம் நலன்கள் நேரடியாக கணக்கில் செல்கின்றன.", "Linking மட்டும் இருந்தால் போதாமல் இருக்கலாம்.", "Aadhaar Payment Bridge க்கு NPCI mapper முக்கியம்.", "பல கணக்குகள் இருந்தால் active bank எது என்பதை உறுதி செய்யவும்."),
                    comparisonPointsTamil(),
                    "சலுகை தொகையை எதிர்பார்ப்பதற்கு முன் அதிகாரப்பூர்வ portal அல்லது வங்கியில் live DBT நிலையைச் சரிபார்க்கவும்."
            );
        };
    }

    private CheckAccountSection check(LanguageOption language) {
        return switch (language) {
            case ENGLISH -> new CheckAccountSection("Check Account", "Answer these practical questions to understand whether your account is only linked or also DBT enabled.", List.of("I submitted my Aadhaar to the bank and it was verified against my account.", "I gave consent for Aadhaar seeding / NPCI mapper update.", "This is the latest bank account where I want Aadhaar-based DBT credits.", "My scheme or benefit uses Aadhaar-based DBT routing."), "Likely Aadhaar linked only", "Your account appears linked with Aadhaar, but there is no strong signal that DBT seeding is active.", "Likely DBT enabled", "Your responses indicate the account may be positioned for Aadhaar-based DBT routing.", "DBT may be routed to another bank", "If another bank was seeded later, that bank may be active in the mapper for Aadhaar-based credits.", "Bank visit or official confirmation needed", "The responses do not yet show even the basic bank-side Aadhaar link. Complete verification first.", List.of("Check Aadhaar-bank linking status using DBT Bharat or your bank workflow.", "Ask the bank specifically whether NPCI mapper seeding is active.", "Confirm which bank is the latest mapped destination if you changed it recently.", "Keep your mobile number updated with the bank."));
            case HINDI -> new CheckAccountSection("खाता जाँचें", "इन प्रश्नों से समझें कि खाता केवल linked है या DBT enabled भी है।", List.of("मैंने आधार बैंक में जमा किया और वह मेरे खाते से verify हुआ।", "मैंने Aadhaar seeding / NPCI mapper update के लिए सहमति दी।", "यही वह नवीनतम बैंक खाता है जिसमें मुझे DBT चाहिए।", "मेरी योजना Aadhaar आधारित DBT routing का उपयोग करती है।"), "संभवतः केवल linked", "खाता linked दिखता है, पर DBT seeding सक्रिय होने का स्पष्ट संकेत नहीं है।", "संभवतः DBT enabled", "उत्तर बताते हैं कि खाता Aadhaar आधारित DBT के लिए तैयार हो सकता है।", "DBT किसी अन्य बैंक में जा सकता है", "यदि बाद में किसी और बैंक में seeding हुई, तो वही active हो सकता है।", "बैंक पुष्टि आवश्यक", "उत्तर यह नहीं दिखाते कि बैंक-side Aadhaar link भी पूरा है। पहले verification पूरा करें।", List.of("DBT Bharat या बैंक workflow से Aadhaar-bank status देखें।", "बैंक से पूछें कि NPCI mapper seeding active है या नहीं।", "यदि preferred bank बदला है तो latest mapped bank की पुष्टि करें।", "बैंक में mobile number updated रखें।"));
            case TELUGU -> new CheckAccountSection("ఖాతా తనిఖీ", "మీ ఖాతా linked మాత్రమేనా లేదా DBT enabled కూడా అయిందా తెలుసుకోండి.", List.of("నేను Aadhaar ను బ్యాంక్‌కు ఇచ్చి verify చేయించాను.", "నేను Aadhaar seeding / NPCI mapper update కు సమ్మతి ఇచ్చాను.", "ఇదే నాకు DBT రావాల్సిన తాజా బ్యాంక్ ఖాతా.", "నా పథకం Aadhaar ఆధారిత DBT ను ఉపయోగిస్తుంది."), "linked మాత్రమే అయి ఉండవచ్చు", "ఖాతా linked గా ఉన్నట్లు కనిపిస్తోంది కానీ DBT seeding క్రియాశీలంగా ఉందని స్పష్టత లేదు.", "DBT enabled అయి ఉండవచ్చు", "మీ సమాధానాలు ఖాతా DBT routing కు సిద్ధంగా ఉండవచ్చని సూచిస్తున్నాయి.", "DBT మరో బ్యాంక్‌కు వెళ్లి ఉండవచ్చు", "తర్వాత మరో బ్యాంక్‌లో seeding జరిగితే అది active అయి ఉండవచ్చు.", "బ్యాంక్ ధృవీకరణ అవసరం", "ప్రాథమిక bank-side Aadhaar link పూర్తైందని కూడా ఈ సమాధానాలు చూపడం లేదు.", List.of("DBT Bharat లేదా బ్యాంక్ workflow ద్వారా status చూడండి.", "NPCI mapper active ఉందా అని బ్యాంక్‌ను అడగండి.", "latest mapped bank ఏదో నిర్ధారించండి.", "బ్యాంక్‌లో mobile number update చేయండి."));
            case TAMIL -> new CheckAccountSection("கணக்கு நிலை", "உங்கள் கணக்கு linked மட்டும் தானா அல்லது DBT enabled ஆகவும் உள்ளதா என்பதை அறிக.", List.of("நான் Aadhaar ஐ வங்கியில் சமர்ப்பித்து verify செய்தேன்.", "Aadhaar seeding / NPCI mapper update க்கு ஒப்புதல் அளித்தேன்.", "இதே எனக்கு DBT வரவேண்டிய சமீபத்திய வங்கி கணக்கு.", "என் திட்டம் Aadhaar அடிப்படையிலான DBT ஐ பயன்படுத்துகிறது."), "linked மட்டும் இருக்கலாம்", "கணக்கு linked போல தோன்றுகிறது; ஆனால் DBT seeding செயலில் உள்ளதென உறுதி இல்லை.", "DBT enabled இருக்கலாம்", "இந்த கணக்கு Aadhaar அடிப்படையிலான DBT routing க்கு தயாராக இருக்கலாம்.", "DBT வேறு வங்கிக்கு செல்லலாம்", "பின்னர் வேறு வங்கியில் seeding நடந்திருந்தால் அது active bank ஆக இருக்கலாம்.", "வங்கி உறுதி தேவை", "அடிப்படை bank-side Aadhaar link கூட முழுமையாக உறுதி செய்யப்படவில்லை.", List.of("DBT Bharat அல்லது வங்கி workflow மூலம் status பார்க்கவும்.", "NPCI mapper active உள்ளதா என்று வங்கியிடம் கேளுங்கள்.", "latest mapped bank எது என்பதை உறுதி செய்யுங்கள்.", "வங்கியில் mobile number புதுப்பிக்கவும்."));
        };
    }

    private List<ComparisonPoint> comparisonPoints() {
        return List.of(
                new ComparisonPoint("Primary purpose", "Shows that your Aadhaar is linked with the bank account in the bank system.", "Shows that the bank has also seeded Aadhaar for Aadhaar-based DBT routing.", "The second status matters for Aadhaar Payment Bridge based routing."),
                new ComparisonPoint("Bank action", "Bank verifies and records Aadhaar against your account.", "Bank updates NPCI mapper after consent and seeding.", "This backend step helps route Aadhaar-based benefit payments."),
                new ComparisonPoint("Subsidy readiness", "May still need extra seeding or mapper activation.", "Ready for Aadhaar-based routing when scheme rules and eligibility are met.", "Linked only is not always enough for Aadhaar-based DBT credit."),
                new ComparisonPoint("If multiple accounts exist", "More than one bank may show Aadhaar linkage.", "The latest mapper update generally becomes the active DBT destination bank.", "Citizens should confirm which bank is currently active for DBT.")
        );
    }

    private List<ComparisonPoint> comparisonPointsHindi() {
        return List.of(
                new ComparisonPoint("मुख्य उद्देश्य", "यह दिखाता है कि बैंक ने आधार को खाते से लिंक किया है।", "यह दिखाता है कि बैंक ने DBT routing हेतु Aadhaar seeding भी की है।", "दूसरी स्थिति Aadhaar Payment Bridge के लिए महत्वपूर्ण है।"),
                new ComparisonPoint("बैंक की कार्रवाई", "बैंक आधार को verify करके रिकॉर्ड करता है।", "बैंक सहमति के बाद NPCI mapper update करता है।", "इसी backend कदम से Aadhaar आधारित भुगतान route होता है।"),
                new ComparisonPoint("सब्सिडी तैयारी", "अतिरिक्त seeding की जरूरत हो सकती है।", "पात्रता पूरी होने पर Aadhaar आधारित routing के लिए तैयार।", "केवल linked स्थिति हमेशा पर्याप्त नहीं होती।"),
                new ComparisonPoint("यदि कई खाते हों", "एक से अधिक बैंक linkage दिखा सकते हैं।", "नवीनतम mapper update वाला बैंक active destination बन सकता है।", "नागरिक को active DBT bank की पुष्टि करनी चाहिए।")
        );
    }

    private List<ComparisonPoint> comparisonPointsTelugu() {
        return List.of(
                new ComparisonPoint("ప్రధాన ఉద్దేశ్యం", "బ్యాంక్ ఖాతాతో Aadhaar linked అయిందని చూపిస్తుంది.", "DBT routing కోసం Aadhaar seeding కూడా జరిగిందని చూపిస్తుంది.", "రెండో స్థితి Aadhaar Payment Bridge కోసం ముఖ్యమైనది."),
                new ComparisonPoint("బ్యాంక్ చర్య", "బ్యాంక్ Aadhaar ను verify చేసి నమోదు చేస్తుంది.", "సమ్మతి తర్వాత NPCI mapper update చేస్తుంది.", "ఇదే backend routing దశ."),
                new ComparisonPoint("సబ్సిడీ సిద్ధత", "ఇంకా అదనపు seeding అవసరం కావచ్చు.", "అర్హతలు తీరితే Aadhaar ఆధారిత routing కు సిద్ధం.", "linked మాత్రమే ఉంటే సరిపోకపోవచ్చు."),
                new ComparisonPoint("బహుళ ఖాతాలు ఉంటే", "ఒకటి కంటే ఎక్కువ బ్యాంకులు linkage చూపవచ్చు.", "తాజా mapper update active DBT destination అవుతుంది.", "ప్రస్తుతం active bank ఏదో నిర్ధారించాలి.")
        );
    }

    private List<ComparisonPoint> comparisonPointsTamil() {
        return List.of(
                new ComparisonPoint("முக்கிய நோக்கம்", "வங்கி கணக்குடன் Aadhaar linked என்பதை காட்டுகிறது.", "DBT routing க்கு Aadhaar seeding செய்யப்பட்டதையும் காட்டுகிறது.", "இரண்டாவது நிலை Aadhaar Payment Bridge க்கு முக்கியம்."),
                new ComparisonPoint("வங்கியின் செயல்", "வங்கி Aadhaar ஐ verify செய்து பதிவு செய்கிறது.", "ஒப்புதலுக்குப் பிறகு NPCI mapper update செய்கிறது.", "இதுவே backend routing படி."),
                new ComparisonPoint("உதவித்தொகை தயார்நிலை", "மேலும் seeding தேவைப்படலாம்.", "தகுதி பூர்த்தியானால் Aadhaar அடிப்படையிலான routing க்கு தயாராகும்.", "linked மட்டும் போதாமல் இருக்கலாம்."),
                new ComparisonPoint("பல கணக்குகள் இருந்தால்", "பல வங்கிகளில் linkage தோன்றலாம்.", "சமீபத்திய mapper update active DBT destination ஆகும் வாய்ப்பு உள்ளது.", "active bank எது என்பதை உறுதி செய்ய வேண்டும்.")
        );
    }

    private List<VideoItem> videos() {
        return List.of(
                new VideoItem("UIDAI Video Gallery", "Official UIDAI video gallery with awareness clips and Aadhaar service explainers.", "UIDAI", "https://www.uidai.gov.in/en/14-english-uk/1009-videos.html"),
                new VideoItem("Aadhaar App Videos", "Official UIDAI page listing Aadhaar app awareness and tutorial videos.", "UIDAI", "https://www.uidai.gov.in/pu/my-aadhaar/aadhaar-on-your-mobile/aadhaar-app-videos.html"),
                new VideoItem("Aadhaar Official Channel Reference", "UIDAI press note confirming its official YouTube awareness channel.", "UIDAI", "https://uidai.gov.in/en/media-resources/media/press-releases/4625-uidai-launches-aadhaar-official-channel-on-youtube.html")
        );
    }

    private List<ResourceItem> governmentLinks() {
        return List.of(
                new ResourceItem("UIDAI FAQ", "Official Aadhaar FAQs for safety, linking, and service checks.", "UIDAI", "https://www.uidai.gov.in/en/contact-support/have-any-question/277-faqs.html"),
                new ResourceItem("Use Aadhaar Freely", "UIDAI guidance on safe Aadhaar usage and public concerns.", "UIDAI", "https://www.uidai.gov.in/en/contact-support/have-any-question/281-faqs/your-aadhaar/use-aadhaar-freely.html"),
                new ResourceItem("NPCI Customer FAQ", "Official NPCI explanation of Aadhaar linking, mapper seeding, and routing.", "NPCI", "https://www.npci.org.in/what-we-do/nach/faqs/customers"),
                new ResourceItem("DBT Bharat Portal", "Government portal for DBT information and citizen-facing services.", "DBT Bharat", "https://dbtbharat.gov.in/")
        );
    }

    private List<ResourceItem> resources() {
        return List.of(
                new ResourceItem("UIDAI Myth Busters", "Clarifies misconceptions around Aadhaar, privacy, and bank account details.", "UIDAI", "https://uidai.gov.in/en/my-aadhaar/about-your-aadhaar/aadhaar-myth-busters.html"),
                new ResourceItem("How to Check Bank Linking", "UIDAI-hosted reference on checking Aadhaar-bank linkage.", "UIDAI", "https://uidai.gov.in/en/media-resources/media/aadhaar-in-prints/5220-how-to-check-your-bank-account-linking-with-aadhaar.html"),
                new ResourceItem("DBT Bharat About Us", "Background on the DBT mission and the purpose of direct benefit transfer.", "DBT Bharat", "https://dbtbharat.gov.in/static-page-content/spagecont?id=1"),
                new ResourceItem("DBT Aadhaar Services Directory", "Official service directory for Aadhaar-enabled DBT support references.", "DBT Bharat", "https://dbtbharat.gov.in/dbtcell/service-list")
        );
    }

    private List<QuizQuestion> quiz(LanguageOption language) {
        return switch (language) {
            case ENGLISH -> List.of(
                    new QuizQuestion("Which statement is most accurate?", List.of("Every Aadhaar linked bank account is automatically DBT enabled.", "Aadhaar linking and DBT seeding can be different backend states.", "DBT can work without bank-side Aadhaar verification.", "UIDAI directly stores your bank account number for DBT."), 1, "NPCI seeding / mapper activation can differ from basic bank-side Aadhaar linkage."),
                    new QuizQuestion("Why does NPCI mapper matter in Aadhaar-based DBT?", List.of("It stores debit card PINs.", "It routes Aadhaar-based benefit payments to the mapped bank.", "It replaces the bank account number everywhere.", "It creates a second Aadhaar number."), 1, "NPCI mapper helps determine the destination bank for Aadhaar-based routing."),
                    new QuizQuestion("If you seeded Aadhaar in two banks over time, what is the safer assumption?", List.of("Both banks will always receive the same DBT credit.", "No bank can receive DBT anymore.", "The latest mapper update may decide the active DBT destination bank.", "UIDAI will split the subsidy equally."), 2, "The latest mapper update generally becomes the active destination."),
                    new QuizQuestion("Which site is appropriate for authentic DBT information?", List.of("An unofficial discussion forum", "Random social media reposts", "DBT Bharat and official UIDAI or NPCI pages", "Only private blog posts"), 2, "Use official government or regulated institution pages for confirmation.")
            );
            case HINDI -> List.of(
                    new QuizQuestion("सबसे सही कथन कौन सा है?", List.of("हर linked खाता स्वतः DBT enabled होता है।", "Aadhaar linking और DBT seeding अलग backend states हो सकते हैं।", "DBT बिना bank verification के काम कर सकता है।", "UIDAI सीधे आपका bank account number रखता है।"), 1, "बुनियादी linkage और NPCI mapper seeding अलग चरण हो सकते हैं।"),
                    new QuizQuestion("Aadhaar आधारित DBT में NPCI mapper क्यों महत्वपूर्ण है?", List.of("यह debit card PIN रखता है।", "यह mapped bank तक Aadhaar आधारित payment route करता है।", "यह account number को हर जगह बदल देता है।", "यह दूसरा Aadhaar number बनाता है।"), 1, "NPCI mapper destination bank तय करने में मदद करता है।"),
                    new QuizQuestion("यदि आपने समय के साथ दो बैंकों में Aadhaar seed किया हो तो सुरक्षित मान्यता क्या है?", List.of("दोनों बैंक हमेशा वही DBT credit पाएंगे।", "अब कोई बैंक DBT नहीं लेगा।", "नवीनतम mapper update active destination तय कर सकता है।", "UIDAI subsidy बाँट देगा।"), 2, "आमतौर पर नवीनतम mapper update active destination बनता है।"),
                    new QuizQuestion("प्रामाणिक DBT जानकारी के लिए कौन सा विकल्प सही है?", List.of("अनौपचारिक forum", "random social media posts", "DBT Bharat और आधिकारिक UIDAI / NPCI pages", "केवल private blogs"), 2, "आधिकारिक सरकारी या विनियमित संस्थान की वेबसाइटें उपयोग करें।")
            );
            case TELUGU -> List.of(
                    new QuizQuestion("ఏ వాక్యం సరైనది?", List.of("ప్రతి linked ఖాతా ఆటోమేటిక్‌గా DBT enabled అవుతుంది.", "Aadhaar linking మరియు DBT seeding వేర్వేరు backend స్థితులు కావచ్చు.", "bank verification లేకుండానే DBT పని చేస్తుంది.", "UIDAI నేరుగా bank account number నిల్వ చేస్తుంది."), 1, "ప్రాథమిక linkage మరియు mapper seeding వేర్వేరు దశలు కావచ్చు."),
                    new QuizQuestion("Aadhaar ఆధారిత DBT లో NPCI mapper ఎందుకు ముఖ్యం?", List.of("ఇది debit card PINలను నిల్వ చేస్తుంది.", "ఇది Aadhaar ఆధారిత లబ్ధిని mapped bank కు route చేస్తుంది.", "ఇది account number ను మార్చేస్తుంది.", "ఇది మరో Aadhaar number సృష్టిస్తుంది."), 1, "mapper destination bank ను నిర్ణయించడంలో సహాయపడుతుంది."),
                    new QuizQuestion("మీరు రెండు బ్యాంకుల్లో seed చేసి ఉంటే సురక్షిత అంచనా ఏది?", List.of("రెండు బ్యాంకులూ అదే DBT credit పొందుతాయి.", "ఏ బ్యాంక్‌కీ DBT రాదు.", "తాజా mapper update active destination ను నిర్ణయించవచ్చు.", "UIDAI subsidy ని భాగాలుగా పంపుతుంది."), 2, "సాధారణంగా తాజా mapper update active destination అవుతుంది."),
                    new QuizQuestion("ప్రామాణిక DBT సమాచారం కోసం సరైన వనరు ఏది?", List.of("unofficial forum", "random social media posts", "DBT Bharat మరియు అధికారిక UIDAI / NPCI పేజీలు", "private blogs మాత్రమే"), 2, "అధికారిక ప్రభుత్వ లేదా నియంత్రిత సంస్థల వెబ్‌సైట్లు ఉపయోగించాలి.")
            );
            case TAMIL -> List.of(
                    new QuizQuestion("சரியான கூற்று எது?", List.of("ஒவ்வொரு linked account-மும் தானாகவே DBT enabled ஆகும்.", "Aadhaar linking மற்றும் DBT seeding வேறு backend நிலைகள் ஆக இருக்கலாம்.", "bank verification இல்லாமலும் DBT இயங்கும்.", "UIDAI நேரடியாக bank account number சேமிக்கிறது."), 1, "அடிப்படை linkage மற்றும் mapper seeding வேறு படிகள் ஆக இருக்கலாம்."),
                    new QuizQuestion("Aadhaar அடிப்படையிலான DBT யில் NPCI mapper ஏன் முக்கியம்?", List.of("இது debit card PIN களை சேமிக்கிறது.", "இது Aadhaar அடிப்படையிலான நிதியை mapped bank க்கு அனுப்ப உதவுகிறது.", "இது account number ஐ முழுவதும் மாற்றிவிடுகிறது.", "இது இன்னொரு Aadhaar உருவாக்குகிறது."), 1, "mapper destination bank ஐ தீர்மானிக்க உதவுகிறது."),
                    new QuizQuestion("நீங்கள் இரண்டு வங்கிகளில் seed செய்திருந்தால் பாதுகாப்பான கருதுகோள் எது?", List.of("இரு வங்கிகளும் அதே DBT credit பெறும்.", "எந்த வங்கிக்கும் DBT வராது.", "சமீபத்திய mapper update active destination bank ஐ தீர்மானிக்கலாம்.", "UIDAI subsidy ஐ பிரித்து அனுப்பும்."), 2, "சாதாரணமாக சமீபத்திய mapper update active destination ஆகும்."),
                    new QuizQuestion("உண்மையான DBT தகவலுக்கு சரியான ஆதாரம் எது?", List.of("unofficial forum", "random social media posts", "DBT Bharat மற்றும் UIDAI / NPCI அதிகாரப்பூர்வ பக்கங்கள்", "private blogs மட்டும்"), 2, "அதிகாரப்பூர்வ அரசு அல்லது ஒழுங்குபடுத்தப்பட்ட நிறுவன வலைத்தளங்களை பயன்படுத்த வேண்டும்.")
            );
        };
    }
}
