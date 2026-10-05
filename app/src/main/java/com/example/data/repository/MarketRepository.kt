package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.PaperTradeEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.BuildupType
import com.example.data.model.CandleData
import com.example.data.model.FOAnalysisData
import com.example.data.model.FiiDiiActivity
import com.example.data.model.IndianBrokerStatus
import com.example.data.model.IntradaySentimentSummary
import com.example.data.model.IntradaySignal
import com.example.data.model.MarketBreadth
import com.example.data.model.MarketIndex
import com.example.data.model.NewsImpact
import com.example.data.model.NewsItem
import com.example.data.model.NewsSentimentResult
import com.example.data.model.OptionChainRow
import com.example.data.model.PcrAlertSettings
import com.example.data.model.PcrAlertType
import com.example.data.model.PcrNotificationEvent
import com.example.data.model.SectorPerformance
import com.example.data.model.SignalType
import com.example.data.model.StockItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

class MarketRepository(private val database: AppDatabase) {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _indices = MutableStateFlow<List<MarketIndex>>(emptyList())
    val indices: StateFlow<List<MarketIndex>> = _indices.asStateFlow()

    private val _stocks = MutableStateFlow<List<StockItem>>(emptyList())
    val stocks: StateFlow<List<StockItem>> = _stocks.asStateFlow()

    private val _intradaySignals = MutableStateFlow<List<IntradaySignal>>(emptyList())
    val intradaySignals: StateFlow<List<IntradaySignal>> = _intradaySignals.asStateFlow()

    private val _sectors = MutableStateFlow<List<SectorPerformance>>(emptyList())
    val sectors: StateFlow<List<SectorPerformance>> = _sectors.asStateFlow()

    private val _marketBreadth = MutableStateFlow(
        MarketBreadth(
            advances = 1420,
            declines = 910,
            unchanged = 115,
            fearGreedIndex = 68,
            sentimentLabelEn = "Greed (Bullish)",
            sentimentLabelGu = "લોભ (તેજીનું વલણ)"
        )
    )
    val marketBreadth: StateFlow<MarketBreadth> = _marketBreadth.asStateFlow()

    private val _niftyFO = MutableStateFlow<FOAnalysisData?>(null)
    val niftyFO: StateFlow<FOAnalysisData?> = _niftyFO.asStateFlow()

    private val _bankNiftyFO = MutableStateFlow<FOAnalysisData?>(null)
    val bankNiftyFO: StateFlow<FOAnalysisData?> = _bankNiftyFO.asStateFlow()

    private val _fiiDii = MutableStateFlow(
        FiiDiiActivity(
            fiiNetBuyCr = 1485.40,
            diiNetBuyCr = 2210.80,
            fiiFnoLongPercent = 64.2
        )
    )
    val fiiDii: StateFlow<FiiDiiActivity> = _fiiDii.asStateFlow()

    private val _sentimentSummary = MutableStateFlow(
        IntradaySentimentSummary(
            overallSentimentEn = "Strong Bullish Expansion",
            overallSentimentGu = "મજબૂત તેજીનું વલણ (Buy On Dips)",
            score = 74,
            vixValue = 13.18,
            vixStatusEn = "Low Volatility (Favorable for Trend Traders)",
            vixStatusGu = "શાંત વોલેટિલિટી (ટ્રેન્ડ ટ્રેડિંગ માટે અનુકૂળ)",
            niftyPcr = 1.18,
            bankNiftyPcr = 1.24,
            advanceCount = 1420,
            declineCount = 910,
            keySupport = 24980.0,
            keyResistance = 25200.0,
            aiIntradayVerdictEn = "Nifty sustaining above VWAP (25,050). Heavy Put writing at 25,000 strike. High probability continuation towards 25,200.",
            aiIntradayVerdictGu = "નિફ્ટી VWAP (25,050) ઉપર મજબૂત. 25,000 સ્ટ્રાઇક પર ભારે પુટ રાઇટિંગ તેજીને ટેકો આપે છે. 25,200 તરફ આગળ વધવાની શક્યતા."
        )
    )
    val sentimentSummary: StateFlow<IntradaySentimentSummary> = _sentimentSummary.asStateFlow()

    val brokerStatusList = listOf(
        IndianBrokerStatus("Zerodha Kite", "REST & WebSocket", true, 42),
        IndianBrokerStatus("Angel One", "SmartAPI", true, 38),
        IndianBrokerStatus("Groww", "Direct Connect", true, 52),
        IndianBrokerStatus("Upstox", "API v2", true, 45)
    )

    private val _newsSentiment = MutableStateFlow(createInitialNewsSentiment())
    val newsSentiment: StateFlow<NewsSentimentResult> = _newsSentiment.asStateFlow()

    private fun createInitialNewsSentiment(): NewsSentimentResult {
        val headlines = listOf(
            NewsItem(
                id = "N1",
                headlineEn = "RBI Monetary Policy Committee maintains repo rate at 6.50%, keeps stance focused on growth",
                headlineGu = "RBI મોનેટરી પોલિસી કમિટીએ રેપો રેટ 6.50% પર સ્થિર રાખ્યો, વૃદ્ધિ પર ધ્યાન કેન્દ્રિત",
                source = "Economic Times",
                timeAgo = "15m ago",
                impact = NewsImpact.BULLISH,
                sentimentScore = 84
            ),
            NewsItem(
                id = "N2",
                headlineEn = "FIIs turn aggressive net buyers in Indian cash equities, injecting ₹3,600+ Cr this week",
                headlineGu = "વિદેશી રોકાણકારો (FII) એ ભારતીય કેશ માર્કેટમાં આ સપ્તાહે ₹3,600+ કરોડની આક્રમક ખરીદી કરી",
                source = "Moneycontrol",
                timeAgo = "32m ago",
                impact = NewsImpact.BULLISH,
                sentimentScore = 88
            ),
            NewsItem(
                id = "N3",
                headlineEn = "Tata Motors & Auto index surge on strong EV domestic delivery numbers and margin expansion",
                headlineGu = "ટાટા મોટર્સ અને ઓટો ઇન્ડેક્સમાં મજબૂત EV ડિલિવરી અને માર્જિન વધારાથી તેજી",
                source = "LiveMint",
                timeAgo = "45m ago",
                impact = NewsImpact.BULLISH,
                sentimentScore = 80
            ),
            NewsItem(
                id = "N4",
                headlineEn = "Brent Crude oil stabilizes near $74/bbl, easing imported inflation concerns for India",
                headlineGu = "બ્રેન્ટ ક્રૂડ ઓઇલ $74 પ્રતિ બેરલ નજીક સ્થિર, ભારત માટે મોંઘવારીનું જોખમ ઘટ્યું",
                source = "CNBC-TV18",
                timeAgo = "1h ago",
                impact = NewsImpact.BULLISH,
                sentimentScore = 72
            ),
            NewsItem(
                id = "N5",
                headlineEn = "US Federal Reserve signals calibrated rate cut trajectory, supporting Asian emerging market currencies",
                headlineGu = "યુએસ ફેડરલ રિઝર્વે વ્યાજદરમાં ઘટાડાના સંકેતો આપ્યા, એશિયન કરન્સી અને ઇક્વિટીને ટેકો",
                source = "Bloomberg",
                timeAgo = "2h ago",
                impact = NewsImpact.NEUTRAL,
                sentimentScore = 65
            ),
            NewsItem(
                id = "N6",
                headlineEn = "Global geopolitical tensions in Middle East keep defense and energy stocks volatile",
                headlineGu = "મિડલ ઇસ્ટમાં વૈશ્વિક તણાવને કારણે ડિફેન્સ અને એનર્જી શેરોમાં અસ્થિરતા",
                source = "Reuters",
                timeAgo = "3h ago",
                impact = NewsImpact.BEARISH,
                sentimentScore = 38
            )
        )

        return NewsSentimentResult(
            overallScore = 78,
            sentimentLabelEn = "Bullish Momentum",
            sentimentLabelGu = "સ્પષ્ટ તેજીનું વલણ",
            isBullish = true,
            bullishPercentage = 75,
            bearishPercentage = 15,
            neutralPercentage = 10,
            totalHeadlinesAnalyzed = headlines.size,
            summaryEn = "Daily headlines show strong macroeconomic stability with heavy FII institutional inflows, favorable crude prices, and robust domestic earnings outlook.",
            summaryGu = "આજના મુખ્ય સમાચાર મજબૂત આર્થિક સ્થિરતા, FII ની મોટી ખરીદી, ક્રૂડ ઓઇલના સાનુકૂળ ભાવ અને દેશી કંપનીઓના સારા પરિણામો સાથે તેજીની પુષ્ટિ કરે છે.",
            keyTakeawayEn = "Market sentiment heavily tilted towards 'Buy on Dips'. High institutional accumulation in Banking and Auto.",
            keyTakeawayGu = "બજાર સેન્ટિમેન્ટ 'ઘટાડે ખરીદી' તરફી. બેંકિંગ અને ઓટો સેક્ટરમાં સંસ્થાકીય ખરીદીનું મોજું.",
            headlines = headlines,
            analyzedTime = "Live Gemini AI Analysis"
        )
    }

    suspend fun updateNewsSentiment(geminiScore: Int, verdict: String, isGujarati: Boolean) {
        val current = _newsSentiment.value
        _newsSentiment.value = current.copy(
            overallScore = geminiScore,
            sentimentLabelEn = if (geminiScore >= 60) "Bullish Momentum" else if (geminiScore <= 40) "Bearish Drag" else "Neutral Range",
            sentimentLabelGu = if (geminiScore >= 60) "સ્પષ્ટ તેજીનું વલણ" else if (geminiScore <= 40) "મંદીનું દબાણ" else "સંતુલિત રેન્જ",
            isBullish = geminiScore >= 50,
            bullishPercentage = if (geminiScore >= 50) geminiScore else (100 - geminiScore),
            summaryEn = if (!isGujarati) verdict else current.summaryEn,
            summaryGu = if (isGujarati) verdict else current.summaryGu,
            analyzedTime = "Updated just now via Gemini AI"
        )
    }

    private val _pcrAlertSettings = MutableStateFlow(PcrAlertSettings())
    val pcrAlertSettings: StateFlow<PcrAlertSettings> = _pcrAlertSettings.asStateFlow()

    private val _activePcrAlert = MutableStateFlow<PcrNotificationEvent?>(null)
    val activePcrAlert: StateFlow<PcrNotificationEvent?> = _activePcrAlert.asStateFlow()

    fun updatePcrThresholds(overbought: Double, oversold: Double) {
        _pcrAlertSettings.value = _pcrAlertSettings.value.copy(
            overboughtThreshold = overbought,
            oversoldThreshold = oversold
        )
    }

    fun togglePcrAlerts(enabled: Boolean) {
        _pcrAlertSettings.value = _pcrAlertSettings.value.copy(isEnabled = enabled)
    }

    fun dismissPcrAlert() {
        _activePcrAlert.value = null
    }

    fun simulatePcrAlert(type: PcrAlertType): PcrNotificationEvent {
        val now = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
        val event = when (type) {
            PcrAlertType.OVERBOUGHT -> PcrNotificationEvent(
                id = java.util.UUID.randomUUID().toString(),
                alertType = PcrAlertType.OVERBOUGHT,
                pcrValue = 1.38,
                titleEn = "🚨 NIFTY PCR Alert: Overbought (1.38)",
                titleGu = "🚨 નિફ્ટી PCR ચેતવણી: અતિ તેજી (Overbought 1.38)",
                messageEn = "NIFTY PCR breached overbought threshold (1.35). High probability of intraday profit booking & reversal.",
                messageGu = "નિફ્ટી PCR 1.35 થ્રેશોલ્ડને પાર કરી ગયો છે. ઉચ્ચ સ્તરેથી પ્રોફિટ બુકિંગ અને રિવર્સલનું જોખમ છે. નવા કોલ ખરીદવામાં સાવચેત રહો.",
                timestamp = now
            )
            PcrAlertType.OVERSOLD -> PcrNotificationEvent(
                id = java.util.UUID.randomUUID().toString(),
                alertType = PcrAlertType.OVERSOLD,
                pcrValue = 0.58,
                titleEn = "🟢 NIFTY PCR Alert: Oversold (0.58)",
                titleGu = "🟢 નિફ્ટી PCR ચેતવણી: અતિ મંદી (Oversold 0.58)",
                messageEn = "NIFTY PCR dropped below oversold threshold (0.65). Short covering bounce expected from key support.",
                messageGu = "નિફ્ટી PCR 0.65 ની નીચે આવી ગયો છે. બજાર અતિ વેચાયેલું છે. સપોર્ટ સ્તરેથી તીવ્ર શોર્ટ કવરિંગ ઉછાળો આવી શકે છે.",
                timestamp = now
            )
            else -> PcrNotificationEvent(
                id = java.util.UUID.randomUUID().toString(),
                alertType = PcrAlertType.BULLISH_BIAS,
                pcrValue = 1.18,
                titleEn = "📊 NIFTY PCR: Bullish Continuation (1.18)",
                titleGu = "📊 નિફ્ટી PCR: મજબૂત તેજીનું વલણ (1.18)",
                messageEn = "Put writers dominating. Buy on dips strategy active.",
                messageGu = "પુટ રાઇટર્સનું પ્રભુત્વ યથાવત છે. ઘટાડે ખરીદીની રણનીતિ સક્રિય છે.",
                timestamp = now
            )
        }
        _activePcrAlert.value = event
        _pcrAlertSettings.value = _pcrAlertSettings.value.copy(
            lastNotifiedType = type,
            lastNotifiedTime = now
        )
        return event
    }

    fun checkPcrForAlert(currentPcr: Double): PcrNotificationEvent? {
        val settings = _pcrAlertSettings.value
        if (!settings.isEnabled) return null

        val now = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())

        if (currentPcr >= settings.overboughtThreshold && settings.lastNotifiedType != PcrAlertType.OVERBOUGHT) {
            val event = PcrNotificationEvent(
                id = java.util.UUID.randomUUID().toString(),
                alertType = PcrAlertType.OVERBOUGHT,
                pcrValue = currentPcr,
                titleEn = "🚨 NIFTY PCR Alert: Overbought ($currentPcr)",
                titleGu = "🚨 નિફ્ટી PCR ચેતવણી: અતિ તેજી ($currentPcr)",
                messageEn = "PCR exceeded ${settings.overboughtThreshold}. Market in heavy Call/Put divergence; protect long gains.",
                messageGu = "નિફ્ટી PCR ${settings.overboughtThreshold} થ્રેશોલ્ડને વટાવી ગયો છે. ઉચ્ચ સ્તરે પ્રોફિટ બુકિંગ શક્ય છે.",
                timestamp = now
            )
            _activePcrAlert.value = event
            _pcrAlertSettings.value = settings.copy(lastNotifiedType = PcrAlertType.OVERBOUGHT, lastNotifiedTime = now)
            return event
        } else if (currentPcr <= settings.oversoldThreshold && settings.lastNotifiedType != PcrAlertType.OVERSOLD) {
            val event = PcrNotificationEvent(
                id = java.util.UUID.randomUUID().toString(),
                alertType = PcrAlertType.OVERSOLD,
                pcrValue = currentPcr,
                titleEn = "🟢 NIFTY PCR Alert: Oversold ($currentPcr)",
                titleGu = "🟢 નિફ્ટી PCR ચેતવણી: અતિ મંદી ($currentPcr)",
                messageEn = "PCR dropped below ${settings.oversoldThreshold}. Strong short covering bounce likely from key supports.",
                messageGu = "નિફ્ટી PCR ${settings.oversoldThreshold} નીચે ગયો છે. સપોર્ટથી રિકવરી બાઉન્સ શક્ય છે.",
                timestamp = now
            )
            _activePcrAlert.value = event
            _pcrAlertSettings.value = settings.copy(lastNotifiedType = PcrAlertType.OVERSOLD, lastNotifiedTime = now)
            return event
        } else if (currentPcr in (settings.oversoldThreshold + 0.1)..(settings.overboughtThreshold - 0.1)) {
            // Reset state if returned to normal range
            if (settings.lastNotifiedType != null) {
                _pcrAlertSettings.value = settings.copy(lastNotifiedType = null)
            }
        }
        return null
    }

    val watchlistFlow: Flow<List<WatchlistEntity>> = database.watchlistDao().getAllWatchlist()
    val paperTradesFlow: Flow<List<PaperTradeEntity>> = database.paperTradeDao().getAllTrades()

    init {
        initializeInitialData()
        startLiveMarketSimulation()
    }

    private fun initializeInitialData() {
        _indices.value = listOf(
            MarketIndex(
                id = "NIFTY50",
                nameEn = "NIFTY 50",
                nameGu = "નિફ્ટી ૫૦",
                currentPrice = 25088.40,
                change = 142.60,
                changePercent = 0.57,
                openPrice = 24980.0,
                highPrice = 25115.30,
                lowPrice = 24962.10,
                sparkline = listOf(24980f, 25010f, 24995f, 25040f, 25035f, 25070f, 25088.4f)
            ),
            MarketIndex(
                id = "BANKNIFTY",
                nameEn = "BANK NIFTY",
                nameGu = "બેંક નિફ્ટી",
                currentPrice = 53842.15,
                change = 385.20,
                changePercent = 0.72,
                openPrice = 53520.0,
                highPrice = 53910.0,
                lowPrice = 53480.0,
                sparkline = listOf(53520f, 53600f, 53580f, 53710f, 53790f, 53842.15f)
            ),
            MarketIndex(
                id = "SENSEX",
                nameEn = "SENSEX",
                nameGu = "સેન્સેક્સ",
                currentPrice = 82410.60,
                change = 420.30,
                changePercent = 0.51,
                openPrice = 82050.0,
                highPrice = 82490.0,
                lowPrice = 82010.0,
                sparkline = listOf(82050f, 82180f, 82140f, 82320f, 82410.6f)
            ),
            MarketIndex(
                id = "INDIAVIX",
                nameEn = "INDIA VIX",
                nameGu = "ઇન્ડિયા VIX",
                currentPrice = 13.18,
                change = -0.42,
                changePercent = -3.09,
                openPrice = 13.60,
                highPrice = 13.75,
                lowPrice = 13.10,
                sparkline = listOf(13.6f, 13.55f, 13.4f, 13.3f, 13.18f)
            ),
            MarketIndex(
                id = "FINNIFTY",
                nameEn = "FINNIFTY",
                nameGu = "ફિન નિફ્ટી",
                currentPrice = 24180.50,
                change = 186.20,
                changePercent = 0.78,
                openPrice = 24020.0,
                highPrice = 24220.0,
                lowPrice = 23990.0,
                sparkline = listOf(24020f, 24090f, 24140f, 24180.5f)
            ),
            MarketIndex(
                id = "MIDCAP100",
                nameEn = "NIFTY MIDCAP",
                nameGu = "મિડકેપ ૧૦૦",
                currentPrice = 58940.30,
                change = 512.10,
                changePercent = 0.88,
                openPrice = 58450.0,
                highPrice = 59020.0,
                lowPrice = 58400.0,
                sparkline = listOf(58450f, 58650f, 58820f, 58940.3f)
            )
        )

        _stocks.value = createInitialStocks()
        _sectors.value = listOf(
            SectorPerformance("Nifty IT", "નિફ્ટી આઈટી", 1.84, "TCS"),
            SectorPerformance("Nifty Auto", "નિફ્ટી ઓટો", 1.45, "TATAMOTORS"),
            SectorPerformance("Nifty Bank", "નિફ્ટી બેંક", 0.72, "HDFCBANK"),
            SectorPerformance("Nifty Energy", "નિફ્ટી એનર્જી", 0.65, "RELIANCE"),
            SectorPerformance("Nifty Pharma", "નિફ્ટી ફાર્મા", 0.28, "SUNPHARMA"),
            SectorPerformance("Nifty Metal", "નિફ્ટી મેટલ", -0.34, "TATASTEEL"),
            SectorPerformance("Nifty FMCG", "નિફ્ટી FMCG", -0.15, "ITC")
        )

        updateDerivativesData(25088.40, 53842.15)
        refreshIntradaySignals(_stocks.value)
    }

    private fun createInitialStocks(): List<StockItem> {
        return listOf(
            StockItem(
                symbol = "RELIANCE",
                nameEn = "Reliance Industries Ltd",
                nameGu = "રિલાયન્સ ઇન્ડસ્ટ્રીઝ",
                sector = "Energy",
                currentPrice = 2985.50,
                change = 38.40,
                changePercent = 1.30,
                dayHigh = 2998.0,
                dayLow = 2942.0,
                volume = 4850200,
                vwap = 2972.10,
                rsi = 64.2,
                macdStatus = "Bullish Cross",
                pivotPoint = 2960.0,
                support1 = 2935.0,
                resistance1 = 3015.0,
                buildup = BuildupType.LONG_BUILDUP,
                candles = generateCandles(2950f, 2985.5f)
            ),
            StockItem(
                symbol = "HDFCBANK",
                nameEn = "HDFC Bank Ltd",
                nameGu = "એચડીએફસી બેંક",
                sector = "Banking",
                currentPrice = 1682.30,
                change = 18.75,
                changePercent = 1.13,
                dayHigh = 1690.0,
                dayLow = 1665.0,
                volume = 9240000,
                vwap = 1676.40,
                rsi = 59.8,
                macdStatus = "Positive Divergence",
                pivotPoint = 1670.0,
                support1 = 1655.0,
                resistance1 = 1695.0,
                buildup = BuildupType.LONG_BUILDUP,
                candles = generateCandles(1665f, 1682.3f)
            ),
            StockItem(
                symbol = "TATAMOTORS",
                nameEn = "Tata Motors Ltd",
                nameGu = "ટાટા મોટર્સ",
                sector = "Auto",
                currentPrice = 964.80,
                change = 28.50,
                changePercent = 3.04,
                dayHigh = 972.0,
                dayLow = 938.0,
                volume = 8120500,
                vwap = 955.20,
                rsi = 68.5,
                macdStatus = "Strong Bullish",
                pivotPoint = 948.0,
                support1 = 932.0,
                resistance1 = 980.0,
                buildup = BuildupType.LONG_BUILDUP,
                candles = generateCandles(940f, 964.8f)
            ),
            StockItem(
                symbol = "TCS",
                nameEn = "Tata Consultancy Services",
                nameGu = "ટીસીએસ (TCS)",
                sector = "IT",
                currentPrice = 4280.00,
                change = 65.20,
                changePercent = 1.55,
                dayHigh = 4295.0,
                dayLow = 4210.0,
                volume = 1950000,
                vwap = 4255.0,
                rsi = 63.1,
                macdStatus = "Bullish Momentum",
                pivotPoint = 4240.0,
                support1 = 4190.0,
                resistance1 = 4320.0,
                buildup = BuildupType.LONG_BUILDUP,
                candles = generateCandles(4220f, 4280f)
            ),
            StockItem(
                symbol = "INFY",
                nameEn = "Infosys Ltd",
                nameGu = "ઇન્ફોસિસ લિ.",
                sector = "IT",
                currentPrice = 1895.40,
                change = 36.10,
                changePercent = 1.94,
                dayHigh = 1904.0,
                dayLow = 1860.0,
                volume = 4320000,
                vwap = 1882.0,
                rsi = 65.8,
                macdStatus = "Golden Cross",
                pivotPoint = 1870.0,
                support1 = 1845.0,
                resistance1 = 1915.0,
                buildup = BuildupType.LONG_BUILDUP,
                candles = generateCandles(1860f, 1895.4f)
            ),
            StockItem(
                symbol = "ICICIBANK",
                nameEn = "ICICI Bank Ltd",
                nameGu = "આઈસીઆઈસીઆઈ બેંક",
                sector = "Banking",
                currentPrice = 1264.20,
                change = 9.80,
                changePercent = 0.78,
                dayHigh = 1272.0,
                dayLow = 1252.0,
                volume = 6120000,
                vwap = 1260.50,
                rsi = 57.2,
                macdStatus = "Consolidation",
                pivotPoint = 1258.0,
                support1 = 1245.0,
                resistance1 = 1278.0,
                buildup = BuildupType.SHORT_COVERING,
                candles = generateCandles(1255f, 1264.2f)
            ),
            StockItem(
                symbol = "SBIN",
                nameEn = "State Bank of India",
                nameGu = "સ્ટેટ બેંક ઓફ ઇન્ડિયા (SBI)",
                sector = "PSU Bank",
                currentPrice = 812.50,
                change = 6.40,
                changePercent = 0.79,
                dayHigh = 818.0,
                dayLow = 804.0,
                volume = 7800000,
                vwap = 809.10,
                rsi = 54.0,
                macdStatus = "Neutral",
                pivotPoint = 808.0,
                support1 = 798.0,
                resistance1 = 822.0,
                buildup = BuildupType.SHORT_COVERING,
                candles = generateCandles(805f, 812.5f)
            ),
            StockItem(
                symbol = "ITC",
                nameEn = "ITC Ltd",
                nameGu = "આઈટીસી લિ.",
                sector = "FMCG",
                currentPrice = 482.10,
                change = -1.90,
                changePercent = -0.39,
                dayHigh = 486.0,
                dayLow = 480.0,
                volume = 5400000,
                vwap = 483.50,
                rsi = 46.2,
                macdStatus = "Bearish Drift",
                pivotPoint = 484.0,
                support1 = 478.0,
                resistance1 = 488.0,
                buildup = BuildupType.LONG_UNWINDING,
                candles = generateCandles(485f, 482.1f)
            ),
            StockItem(
                symbol = "TATASTEEL",
                nameEn = "Tata Steel Ltd",
                nameGu = "ટાટા સ્ટીલ",
                sector = "Metals",
                currentPrice = 158.40,
                change = -1.80,
                changePercent = -1.12,
                dayHigh = 161.0,
                dayLow = 157.2,
                volume = 12500000,
                vwap = 159.20,
                rsi = 42.5,
                macdStatus = "Bearish Cross",
                pivotPoint = 160.0,
                support1 = 155.0,
                resistance1 = 163.0,
                buildup = BuildupType.SHORT_BUILDUP,
                candles = generateCandles(161f, 158.4f)
            ),
            StockItem(
                symbol = "BHARTIARTL",
                nameEn = "Bharti Airtel Ltd",
                nameGu = "ભારતી એરટેલ",
                sector = "Telecom",
                currentPrice = 1650.00,
                change = 22.00,
                changePercent = 1.35,
                dayHigh = 1658.0,
                dayLow = 1625.0,
                volume = 3200000,
                vwap = 1641.0,
                rsi = 67.0,
                macdStatus = "Strong Bullish",
                pivotPoint = 1635.0,
                support1 = 1615.0,
                resistance1 = 1668.0,
                buildup = BuildupType.LONG_BUILDUP,
                candles = generateCandles(1630f, 1650f)
            ),
            StockItem(
                symbol = "LT",
                nameEn = "Larsen & Toubro Ltd",
                nameGu = "લાર્સન એન્ડ ટુબ્રો (L&T)",
                sector = "Infrastructure",
                currentPrice = 3680.00,
                change = 44.00,
                changePercent = 1.21,
                dayHigh = 3705.0,
                dayLow = 3630.0,
                volume = 1450000,
                vwap = 3662.0,
                rsi = 61.3,
                macdStatus = "Bullish",
                pivotPoint = 3650.0,
                support1 = 3610.0,
                resistance1 = 3720.0,
                buildup = BuildupType.LONG_BUILDUP,
                candles = generateCandles(3640f, 3680f)
            ),
            StockItem(
                symbol = "BAJFINANCE",
                nameEn = "Bajaj Finance Ltd",
                nameGu = "બજાજ ફાઇનાન્સ",
                sector = "Finance",
                currentPrice = 7240.00,
                change = -48.00,
                changePercent = -0.66,
                dayHigh = 7320.0,
                dayLow = 7210.0,
                volume = 1100000,
                vwap = 7265.0,
                rsi = 47.8,
                macdStatus = "Neutral",
                pivotPoint = 7280.0,
                support1 = 7180.0,
                resistance1 = 7360.0,
                buildup = BuildupType.SHORT_BUILDUP,
                candles = generateCandles(7300f, 7240f)
            )
        )
    }

    private fun generateCandles(start: Float, end: Float): List<CandleData> {
        val list = mutableListOf<CandleData>()
        var current = start
        val times = listOf("09:15", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00")
        for (t in times) {
            val delta = (Random.nextFloat() - 0.45f) * (start * 0.006f)
            val open = current
            val close = current + delta
            val high = maxOf(open, close) + Random.nextFloat() * (start * 0.003f)
            val low = minOf(open, close) - Random.nextFloat() * (start * 0.003f)
            list.add(CandleData(t, open, high, low, close, (Random.nextLong(20000, 150000))))
            current = close
        }
        return list
    }

    suspend fun fetchIntradayHistoricalCandles(symbol: String, interval: String): List<CandleData> {
        delay(250) // simulate network latency
        val stock = _stocks.value.firstOrNull { it.symbol == symbol } ?: _stocks.value.first()
        val basePrice = stock.currentPrice.toFloat()
        val list = mutableListOf<CandleData>()

        val (times, count) = when (interval) {
            "5M" -> Pair(
                listOf("09:15", "09:20", "09:25", "09:30", "09:35", "09:40", "09:45", "09:50", "09:55", "10:00", "10:05", "10:10", "10:15", "10:20", "10:25", "10:30", "10:35", "10:40", "10:45", "10:50", "10:55", "11:00", "11:05", "11:10"),
                24
            )
            "15M" -> Pair(
                listOf("09:15", "09:30", "09:45", "10:00", "10:15", "10:30", "10:45", "11:00", "11:15", "11:30", "11:45", "12:00", "12:15", "12:30", "12:45", "13:00", "13:15", "13:30"),
                18
            )
            "1H" -> Pair(
                listOf("09:15", "10:15", "11:15", "12:15", "13:15", "14:15", "15:15", "09:15(D-1)", "11:15(D-1)", "13:15(D-1)", "15:15(D-1)", "09:15(D-2)"),
                12
            )
            else -> Pair(
                listOf("22 Sep", "23 Sep", "24 Sep", "25 Sep", "26 Sep", "27 Sep", "29 Sep", "30 Sep", "01 Oct", "02 Oct", "03 Oct", "04 Oct", "05 Oct"),
                13
            )
        }

        var curPrice = basePrice * (1f - (stock.changePercent.toFloat() / 100f * 0.7f))
        for (i in 0 until minOf(times.size, count)) {
            val t = times[i]
            val pctDelta = (Random.nextFloat() - 0.46f) * (basePrice * 0.005f)
            val open = curPrice
            val close = (open + pctDelta).coerceAtLeast(1f)
            val maxOc = maxOf(open, close)
            val minOc = minOf(open, close)
            val high = maxOc + Random.nextFloat() * (basePrice * 0.0035f)
            val low = (minOc - Random.nextFloat() * (basePrice * 0.0035f)).coerceAtLeast(1f)
            val vol = Random.nextLong(15000, 240000)

            list.add(CandleData(t, open, high, low, close, vol))
            curPrice = close
        }
        return list
    }

    private fun refreshIntradaySignals(stocks: List<StockItem>) {
        val signals = mutableListOf<IntradaySignal>()

        for (stock in stocks) {
            if (stock.changePercent > 1.2 && stock.currentPrice > stock.vwap) {
                val entry = (stock.currentPrice * 100).roundToInt() / 100.0
                val t1 = ((entry * 1.015) * 100).roundToInt() / 100.0
                val t2 = ((entry * 1.03) * 100).roundToInt() / 100.0
                val sl = ((entry * 0.99) * 100).roundToInt() / 100.0
                signals.add(
                    IntradaySignal(
                        id = "SIG_${stock.symbol}_BUY",
                        stockSymbol = stock.symbol,
                        stockName = stock.nameEn,
                        signalType = if (stock.volume > 5000000) SignalType.VOLUME_SPIKE else SignalType.ORB_BREAKOUT,
                        entryPrice = entry,
                        target1 = t1,
                        target2 = t2,
                        stopLoss = sl,
                        riskReward = "1:2.2",
                        confidence = 88,
                        rationaleEn = "15-minute Opening Range Breakout sustained above VWAP with high institutional volume expansion.",
                        rationaleGu = "પ્રથમ ૧૫ મિનિટની રેન્જનું સફળ બ્રેકઆઉટ, શેર ભાવ VWAP ઉપર ટકી રહ્યો છે અને વોલ્યુમ ૧.૮ ગણું વધ્યું છે.",
                        timestamp = "Live Intraday"
                    )
                )
            } else if (stock.changePercent < -0.8 && stock.currentPrice < stock.vwap) {
                val entry = (stock.currentPrice * 100).roundToInt() / 100.0
                val t1 = ((entry * 0.985) * 100).roundToInt() / 100.0
                val t2 = ((entry * 0.97) * 100).roundToInt() / 100.0
                val sl = ((entry * 1.01) * 100).roundToInt() / 100.0
                signals.add(
                    IntradaySignal(
                        id = "SIG_${stock.symbol}_SELL",
                        stockSymbol = stock.symbol,
                        stockName = stock.nameEn,
                        signalType = SignalType.ORB_BREAKDOWN,
                        entryPrice = entry,
                        target1 = t1,
                        target2 = t2,
                        stopLoss = sl,
                        riskReward = "1:2.0",
                        confidence = 82,
                        rationaleEn = "Slipping below intraday pivot point & VWAP breakdown with rising short buildup.",
                        rationaleGu = "ઇન્ટ્રાડે સપોર્ટ અને VWAP નીચે ઘટાડો, શોર્ટ બિલ્ડઅપ વધી રહ્યું હોવાથી મંદીનું જોખમ.",
                        timestamp = "Live Intraday"
                    )
                )
            }
        }
        _intradaySignals.value = signals
    }

    private fun updateDerivativesData(niftySpot: Double, bankNiftySpot: Double) {
        // Build realistic option chain around Nifty ATM
        val niftyAtm = (niftySpot / 50.0).roundToInt() * 50.0
        val niftyRows = mutableListOf<OptionChainRow>()
        var totalNiftyCalls = 0L
        var totalNiftyPuts = 0L

        for (offset in -4..4) {
            val strike = niftyAtm + (offset * 50)
            val dist = (strike - niftySpot)
            val callLtp = maxOf(12.0, if (dist <= 0) (-dist + 80.0) else (180.0 - dist * 0.8))
            val putLtp = maxOf(10.0, if (dist >= 0) (dist + 65.0) else (160.0 + dist * 0.8))
            val callOi = (Random.nextLong(30000, 180000))
            val putOi = (Random.nextLong(35000, 210000))
            totalNiftyCalls += callOi
            totalNiftyPuts += putOi

            niftyRows.add(
                OptionChainRow(
                    strikePrice = strike,
                    callLtp = (callLtp * 10).roundToInt() / 10.0,
                    callChange = (Random.nextDouble(-15.0, 25.0) * 10).roundToInt() / 10.0,
                    callOi = callOi,
                    callOiChange = Random.nextLong(-8000, 18000),
                    putLtp = (putLtp * 10).roundToInt() / 10.0,
                    putChange = (Random.nextDouble(-20.0, 20.0) * 10).roundToInt() / 10.0,
                    putOi = putOi,
                    putOiChange = Random.nextLong(-5000, 22000),
                    isAtm = (offset == 0)
                )
            )
        }

        val niftyPcr = if (totalNiftyCalls > 0) ((totalNiftyPuts.toDouble() / totalNiftyCalls) * 100).roundToInt() / 100.0 else 1.15
        val pcrSentimentEn = when {
            niftyPcr > 1.3 -> "Extremely Bullish (Heavy Put Writing)"
            niftyPcr > 1.0 -> "Bullish Bias (Support holding)"
            niftyPcr > 0.8 -> "Neutral Consolidation"
            else -> "Bearish (Call Writing Dominant)"
        }
        val pcrSentimentGu = when {
            niftyPcr > 1.3 -> "અતિ તેજી (મજબૂત પુટ રાઇટિંગ)"
            niftyPcr > 1.0 -> "તેજીનું વલણ (સપોર્ટ મજબૂત)"
            niftyPcr > 0.8 -> "સંતુલિત રેન્જ બાઉન્ડ"
            else -> "મંદીનું દબાણ (કોલ રાઇટિંગ વધુ)"
        }

        _niftyFO.value = FOAnalysisData(
            underlyingIndex = "NIFTY 50",
            spotPrice = niftySpot,
            pcr = niftyPcr,
            pcrSentimentEn = pcrSentimentEn,
            pcrSentimentGu = pcrSentimentGu,
            maxPain = niftyAtm,
            totalCallOi = totalNiftyCalls,
            totalPutOi = totalNiftyPuts,
            highestCallStrike = niftyAtm + 150,
            highestPutStrike = niftyAtm - 150,
            optionChain = niftyRows
        )

        // Build Bank Nifty Option Chain
        val bnAtm = (bankNiftySpot / 100.0).roundToInt() * 100.0
        val bnRows = mutableListOf<OptionChainRow>()
        var totalBnCalls = 0L
        var totalBnPuts = 0L

        for (offset in -3..3) {
            val strike = bnAtm + (offset * 100)
            val dist = (strike - bankNiftySpot)
            val callLtp = maxOf(35.0, if (dist <= 0) (-dist + 220.0) else (340.0 - dist * 0.7))
            val putLtp = maxOf(30.0, if (dist >= 0) (dist + 200.0) else (320.0 + dist * 0.7))
            val callOi = Random.nextLong(15000, 95000)
            val putOi = Random.nextLong(20000, 110000)
            totalBnCalls += callOi
            totalBnPuts += putOi

            bnRows.add(
                OptionChainRow(
                    strikePrice = strike,
                    callLtp = (callLtp * 10).roundToInt() / 10.0,
                    callChange = (Random.nextDouble(-30.0, 45.0) * 10).roundToInt() / 10.0,
                    callOi = callOi,
                    callOiChange = Random.nextLong(-4000, 9000),
                    putLtp = (putLtp * 10).roundToInt() / 10.0,
                    putChange = (Random.nextDouble(-35.0, 35.0) * 10).roundToInt() / 10.0,
                    putOi = putOi,
                    putOiChange = Random.nextLong(-3000, 11000),
                    isAtm = (offset == 0)
                )
            )
        }

        val bnPcr = if (totalBnCalls > 0) ((totalBnPuts.toDouble() / totalBnCalls) * 100).roundToInt() / 100.0 else 1.18
        _bankNiftyFO.value = FOAnalysisData(
            underlyingIndex = "BANK NIFTY",
            spotPrice = bankNiftySpot,
            pcr = bnPcr,
            pcrSentimentEn = if (bnPcr > 1.0) "Bullish Outperformance" else "Cautious Range",
            pcrSentimentGu = if (bnPcr > 1.0) "બેંકિંગમાં મજબૂત તેજી" else "સાવચેતીભરી રેન્જ",
            maxPain = bnAtm,
            totalCallOi = totalBnCalls,
            totalPutOi = totalBnPuts,
            highestCallStrike = bnAtm + 300,
            highestPutStrike = bnAtm - 300,
            optionChain = bnRows
        )
    }

    private fun startLiveMarketSimulation() {
        scope.launch {
            while (isActive) {
                delay(3500)
                // Micro-tick for indices
                val updatedIndices = _indices.value.map { index ->
                    val deltaRatio = (Random.nextDouble(-0.0012, 0.0015))
                    val newPrice = (index.currentPrice * (1 + deltaRatio) * 100).roundToInt() / 100.0
                    val change = (newPrice - index.openPrice * 100).roundToInt() / 100.0
                    val changePct = ((change / index.openPrice) * 10000).roundToInt() / 100.0
                    val updatedSpark = (index.sparkline + newPrice.toFloat()).takeLast(8)
                    index.copy(
                        currentPrice = newPrice,
                        change = change,
                        changePercent = changePct,
                        highPrice = maxOf(index.highPrice, newPrice),
                        lowPrice = minOf(index.lowPrice, newPrice),
                        sparkline = updatedSpark
                    )
                }
                _indices.value = updatedIndices

                // Micro-tick for stocks
                val updatedStocks = _stocks.value.map { stock ->
                    val delta = (Random.nextDouble(-0.002, 0.0025))
                    val newPrice = (stock.currentPrice * (1 + delta) * 100).roundToInt() / 100.0
                    val change = (newPrice - (stock.currentPrice - stock.change) * 100).roundToInt() / 100.0
                    val changePct = (stock.changePercent + (delta * 100)).let { (it * 100).roundToInt() / 100.0 }
                    stock.copy(
                        currentPrice = newPrice,
                        change = change,
                        changePercent = changePct,
                        dayHigh = maxOf(stock.dayHigh, newPrice),
                        dayLow = minOf(stock.dayLow, newPrice),
                        volume = stock.volume + Random.nextLong(200, 2500)
                    )
                }
                _stocks.value = updatedStocks

                // Update derivatives
                val n50 = updatedIndices.find { it.id == "NIFTY50" }?.currentPrice ?: 25088.0
                val bn = updatedIndices.find { it.id == "BANKNIFTY" }?.currentPrice ?: 53842.0
                updateDerivativesData(n50, bn)
            }
        }
    }

    // Room DB operations
    suspend fun toggleWatchlist(symbol: String) {
        val isFav = database.watchlistDao().isFavorite(symbol)
        if (isFav) {
            database.watchlistDao().delete(symbol)
        } else {
            database.watchlistDao().insert(WatchlistEntity(symbol = symbol))
        }
    }

    suspend fun placePaperTrade(
        symbol: String,
        isBuy: Boolean,
        quantity: Int,
        price: Double,
        targetPrice: Double,
        stopLossPrice: Double
    ): Long {
        return database.paperTradeDao().insertTrade(
            PaperTradeEntity(
                symbol = symbol,
                isBuy = isBuy,
                quantity = quantity,
                buyPrice = price,
                targetPrice = targetPrice,
                stopLossPrice = stopLossPrice,
                isOpen = true,
                pnl = 0.0
            )
        )
    }

    suspend fun closePaperTrade(tradeId: Long, exitPrice: Double, buyPrice: Double, quantity: Int, isBuy: Boolean) {
        val pnl = if (isBuy) (exitPrice - buyPrice) * quantity else (buyPrice - exitPrice) * quantity
        database.paperTradeDao().closeTrade(
            id = tradeId,
            exitPrice = exitPrice,
            pnl = (pnl * 100).roundToInt() / 100.0,
            closedAt = System.currentTimeMillis()
        )
    }

    suspend fun resetPaperTrades() {
        database.paperTradeDao().clearAllTrades()
    }
}
