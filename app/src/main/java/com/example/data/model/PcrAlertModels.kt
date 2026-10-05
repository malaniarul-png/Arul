package com.example.data.model

enum class PcrAlertType(val labelEn: String, val labelGu: String) {
    OVERBOUGHT("Overbought Alert (અતિ તેજી)", "અતિ તેજી ચેતવણી (Overbought)"),
    OVERSOLD("Oversold Alert (અતિ મંદી)", "અતિ મંદી ચેતવણી (Oversold)"),
    BULLISH_BIAS("Bullish Bias", "તેજી તરફી વલણ"),
    BEARISH_BIAS("Bearish Bias", "મંદી તરફી વલણ"),
    NORMAL("Neutral / Range-bound", "સામાન્ય / રેન્જ-બાઉન્ડ")
}

data class PcrAlertSettings(
    val isEnabled: Boolean = true,
    val overboughtThreshold: Double = 1.35,
    val oversoldThreshold: Double = 0.65,
    val notifyOnTelegram: Boolean = false,
    val lastNotifiedType: PcrAlertType? = null,
    val lastNotifiedTime: String? = null
)

data class PcrNotificationEvent(
    val id: String,
    val alertType: PcrAlertType,
    val pcrValue: Double,
    val titleEn: String,
    val titleGu: String,
    val messageEn: String,
    val messageGu: String,
    val timestamp: String,
    val isDismissed: Boolean = false
)
