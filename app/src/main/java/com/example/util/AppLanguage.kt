package com.example.util

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flagEmoji: String
) {
    GUJARATI("gu", "Gujarati", "ગુજરાતી", "🇮🇳"),
    HINDI("hi", "Hindi", "हिंदी", "🇮🇳"),
    ENGLISH("en", "English", "English", "🌐"),
    MARATHI("mr", "Marathi", "मराठी", "🇮🇳")
}
