package com.example.data.model

data class MarketIndex(
    val id: String,
    val nameEn: String,
    val nameGu: String,
    val currentPrice: Double,
    val change: Double,
    val changePercent: Double,
    val openPrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val sparkline: List<Float>
)

data class CandleData(
    val timeLabel: String,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Long
)

enum class BuildupType(val labelEn: String, val labelGu: String, val isBullish: Boolean) {
    LONG_BUILDUP("Long Buildup (તેજી)", "લાંબી ખરીદી (તેજી)", true),
    SHORT_BUILDUP("Short Buildup (મંદી)", "શોર્ટ બિલ્ડઅપ (મંદી)", false),
    SHORT_COVERING("Short Covering (ઉછાળો)", "શોર્ટ કવરિંગ (રિકવરી)", true),
    LONG_UNWINDING("Long Unwinding (પ્રોફિટ બુકિંગ)", "નફો બુકિંગ (ઘટાડો)", false)
}

enum class SignalType(val labelEn: String, val labelGu: String, val isBullish: Boolean) {
    ORB_BREAKOUT("15m Breakout", "૧૫ મિ. બ્રેકઆઉટ", true),
    ORB_BREAKDOWN("15m Breakdown", "૧૫ મિ. બ્રેકડાઉન", false),
    VWAP_BULL_CROSS("VWAP Golden Cross", "VWAP તેજી ક્રોસ", true),
    VWAP_BEAR_CROSS("VWAP Breakdown", "VWAP મંદી કટ", false),
    VOLUME_SPIKE("Volume Shocker", "વોલ્યુમ શોકર", true),
    RSI_OVERSOLD("RSI Oversold Bounce", "RSI ઓવરસોલ્ડ રિકવરી", true),
    RSI_OVERBOUGHT("RSI Overbought Fall", "RSI ઓવરબોટ ઘટાડો", false)
}

data class StockItem(
    val symbol: String,
    val nameEn: String,
    val nameGu: String,
    val sector: String,
    val currentPrice: Double,
    val change: Double,
    val changePercent: Double,
    val dayHigh: Double,
    val dayLow: Double,
    val volume: Long,
    val vwap: Double,
    val rsi: Double,
    val macdStatus: String,
    val pivotPoint: Double,
    val support1: Double,
    val resistance1: Double,
    val buildup: BuildupType,
    val candles: List<CandleData>
)

data class IntradaySignal(
    val id: String,
    val stockSymbol: String,
    val stockName: String,
    val signalType: SignalType,
    val entryPrice: Double,
    val target1: Double,
    val target2: Double,
    val stopLoss: Double,
    val riskReward: String,
    val confidence: Int, // e.g. 88%
    val rationaleEn: String,
    val rationaleGu: String,
    val timestamp: String
)

data class OptionChainRow(
    val strikePrice: Double,
    val callLtp: Double,
    val callChange: Double,
    val callOi: Long, // in contracts
    val callOiChange: Long,
    val putLtp: Double,
    val putChange: Double,
    val putOi: Long,
    val putOiChange: Long,
    val isAtm: Boolean = false
)

data class FOAnalysisData(
    val underlyingIndex: String,
    val spotPrice: Double,
    val pcr: Double, // Put Call Ratio
    val pcrSentimentEn: String,
    val pcrSentimentGu: String,
    val maxPain: Double,
    val totalCallOi: Long,
    val totalPutOi: Long,
    val highestCallStrike: Double, // Major resistance
    val highestPutStrike: Double, // Major support
    val optionChain: List<OptionChainRow>
)

data class SectorPerformance(
    val sectorNameEn: String,
    val sectorNameGu: String,
    val changePercent: Double,
    val leadingStock: String
)

data class MarketBreadth(
    val advances: Int,
    val declines: Int,
    val unchanged: Int,
    val fearGreedIndex: Int, // 0 to 100
    val sentimentLabelEn: String,
    val sentimentLabelGu: String
)

data class FiiDiiActivity(
    val fiiNetBuyCr: Double,
    val diiNetBuyCr: Double,
    val fiiFnoLongPercent: Double, // e.g. 64.5%
    val dateLabelEn: String = "Today's Flow",
    val dateLabelGu: String = "આજનો પ્રવાહ"
)

data class IntradaySentimentSummary(
    val overallSentimentEn: String,
    val overallSentimentGu: String,
    val score: Int, // 0 to 100
    val vixValue: Double,
    val vixStatusEn: String,
    val vixStatusGu: String,
    val niftyPcr: Double,
    val bankNiftyPcr: Double,
    val advanceCount: Int,
    val declineCount: Int,
    val keySupport: Double,
    val keyResistance: Double,
    val aiIntradayVerdictEn: String,
    val aiIntradayVerdictGu: String
)

data class IndianBrokerStatus(
    val name: String,
    val type: String,
    val isConnected: Boolean,
    val latencyMs: Int
)

enum class NewsImpact(val labelEn: String, val labelGu: String) {
    BULLISH("Bullish", "તેજી"),
    BEARISH("Bearish", "મંદી"),
    NEUTRAL("Neutral", "તટસ્થ")
}

data class NewsItem(
    val id: String,
    val headlineEn: String,
    val headlineGu: String,
    val source: String,
    val timeAgo: String,
    val impact: NewsImpact,
    val sentimentScore: Int // 0 to 100
)

data class NewsSentimentResult(
    val overallScore: Int, // 0 to 100
    val sentimentLabelEn: String,
    val sentimentLabelGu: String,
    val isBullish: Boolean,
    val bullishPercentage: Int,
    val bearishPercentage: Int,
    val neutralPercentage: Int,
    val totalHeadlinesAnalyzed: Int,
    val summaryEn: String,
    val summaryGu: String,
    val keyTakeawayEn: String,
    val keyTakeawayGu: String,
    val headlines: List<NewsItem>,
    val analyzedTime: String
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val messageText: String,
    val timestamp: String,
    val isSuggestedPrompt: Boolean = false
)
