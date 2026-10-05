package com.example.util

object AppLocalization {

    fun appName(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "શેરબજાર AI"
        AppLanguage.HINDI -> "शेयरबाजार AI"
        AppLanguage.ENGLISH -> "MarketPulse AI"
        AppLanguage.MARATHI -> "शेअरबाजार AI"
    }

    fun tagline(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "NSE • BSE • F&O એનાલિસિસ"
        AppLanguage.HINDI -> "NSE • BSE • F&O विश्लेषण"
        AppLanguage.ENGLISH -> "NSE • BSE • F&O Terminal"
        AppLanguage.MARATHI -> "NSE • BSE • F&O विश्लेषण"
    }

    fun dashboard(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "ડેશબોર્ડ"
        AppLanguage.HINDI -> "डैशबोर्ड"
        AppLanguage.ENGLISH -> "Dashboard"
        AppLanguage.MARATHI -> "डॅशबोर्ड"
    }

    fun intraday(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "ઇન્ટ્રાડે"
        AppLanguage.HINDI -> "इंट्राडे"
        AppLanguage.ENGLISH -> "Intraday"
        AppLanguage.MARATHI -> "इंट्राडे"
    }

    fun fAndO(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "F&O ડેટા"
        AppLanguage.HINDI -> "F&O डेटा"
        AppLanguage.ENGLISH -> "F&O"
        AppLanguage.MARATHI -> "F&O डेरिव्हेटिव्ह्ज"
    }

    fun aiAgent(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "AI એજન્ટ"
        AppLanguage.HINDI -> "AI एजेंट"
        AppLanguage.ENGLISH -> "AI Agent"
        AppLanguage.MARATHI -> "AI एजंट"
    }

    fun trading(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "ટ્રેડિંગ"
        AppLanguage.HINDI -> "ट्रेडिंग"
        AppLanguage.ENGLISH -> "Trading"
        AppLanguage.MARATHI -> "ट्रेडिंग"
    }

    fun live(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "લાઈવ"
        AppLanguage.HINDI -> "लाइव"
        AppLanguage.ENGLISH -> "LIVE"
        AppLanguage.MARATHI -> "लाइव्ह"
    }

    fun overbought(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "અતિ તેજી (Overbought)"
        AppLanguage.HINDI -> "अति तेज़ी (Overbought)"
        AppLanguage.ENGLISH -> "Overbought"
        AppLanguage.MARATHI -> "अति तेजी (Overbought)"
    }

    fun oversold(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "અતિ મંદી (Oversold)"
        AppLanguage.HINDI -> "अति मंदी (Oversold)"
        AppLanguage.ENGLISH -> "Oversold"
        AppLanguage.MARATHI -> "अति मंदी (Oversold)"
    }

    fun liveTrading(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "લાઈવ ટ્રેડિંગ"
        AppLanguage.HINDI -> "लाइव ट्रेडिंग"
        AppLanguage.ENGLISH -> "Live Trading"
        AppLanguage.MARATHI -> "लाइव्ह ट्रेडिंग"
    }

    fun paperTrading(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "પેપર ટ્રેડિંગ"
        AppLanguage.HINDI -> "पेपर ट्रेडिंग"
        AppLanguage.ENGLISH -> "Paper Trading"
        AppLanguage.MARATHI -> "पेपर ट्रेडिंग"
    }

    fun lightMode(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "લાઇટ મોડ"
        AppLanguage.HINDI -> "लाइट मोड"
        AppLanguage.ENGLISH -> "Light Mode"
        AppLanguage.MARATHI -> "लाइट मोड"
    }

    fun darkMode(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "ડાર્ક મોડ"
        AppLanguage.HINDI -> "डार्क मोड"
        AppLanguage.ENGLISH -> "Dark Mode"
        AppLanguage.MARATHI -> "डार्क मोड"
    }

    fun aiGreeting(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> """
            નમસ્તે! 🙏 હું તમારો **શેરબજાર AI એજન્ટ** છું.
            
            હું તમને મદદ કરી શકું છું:
            • **ઇન્ટ્રાડે (Intraday) સેટઅપ:** 15m ORB બ્રેકઆઉટ, VWAP ક્રોસઓવર, એન્ટ્રી-ટાર્ગેટ-સ્ટોપ લોસ.
            • **F&O ડેરિવેટિવ્ઝ:** નિફ્ટી/બેંક નિફ્ટી PCR, ઓપન ઇન્ટરેસ્ટ (OI) અને ઓપ્શન ચેઇન ડેટા.
            • **કોઈપણ શેરનું ટેકનિકલ એનાલિસિસ:** RSI, MACD, મૂવિંગ એવરેજ અને સપોર્ટ/રેઝિસ્ટન્સ.
            
            નીચે આપેલા પ્રોમ્પ્ટ પર ક્લિક કરો અથવા તમારો પ્રશ્ન પૂછો!
        """.trimIndent()

        AppLanguage.HINDI -> """
            नमस्ते! 🙏 मैं आपका **शेयरबाजार AI एजेंट** हूँ।
            
            मैं आपकी सहायता कर सकता हूँ:
            • **इंट्राडे (Intraday) सेटअप:** 15m ORB ब्रेकआउट, VWAP क्रॉसओवर, एंट्री-टारगेट-स्टॉप लॉस।
            • **F&O डेरिवेटिव्स:** निफ्टी/बैंक निफ्टी PCR, ओपन इंटरेस्ट (OI) और ऑप्शन चेन डेटा।
            • **किसी भी स्टॉक का टेक्निकल एनालिसिस:** RSI, MACD, मूविंग एवरेज और सपोर्ट/रेजिस्टेंस।
            
            नीचे दिए गए प्रॉम्प्ट पर क्लिक करें या अपना सवाल पूछें!
        """.trimIndent()

        AppLanguage.ENGLISH -> """
            Hello! 👋 I am your **StockMarket AI Agent**.
            
            I specialize in:
            • **Intraday Breakout Strategies:** 15m ORB, VWAP crosses, Stop-Loss & Targets.
            • **F&O Derivatives:** Nifty/BankNifty PCR, Open Interest (OI) buildup, Max Pain.
            • **Technical Deep-Dive:** RSI, MACD, Moving Averages & Pivots for any Nifty 50 stock.
            
            Tap a quick prompt below or ask me any market question!
        """.trimIndent()

        AppLanguage.MARATHI -> """
            नमस्कार! 🙏 मी तुमचा **शेअरबाजार AI एजंट** आहे.
            
            मी तुम्हाला मदत करू शकतो:
            • **इंट्राडे (Intraday) सेटअप:** 15m ORB ब्रेकआउट, VWAP क्रॉसओव्हर, एन्ट्री-टार्गेट-स्टॉप लॉस.
            • **F&O डेरिव्हेटिव्ह्ज:** निफ्टी/बँक निफ्टी PCR, ओपन इंटरेस्ट (OI) आणि ऑप्शन चेन डेटा.
            • **कोणत्याही शेअरचे टेक्निकल अ‍ॅनालिसिस:** RSI, MACD, मूव्हिंग अ‍ॅव्हरेज आणि सपोर्ट/रेझिस्टन्स.
            
            खालील प्रॉम्प्टवर क्लिक करा किंवा तुमचा प्रश्न विचारा!
        """.trimIndent()
    }
}
