package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiStockAgentService
import com.example.data.local.AppDatabase
import com.example.data.local.PaperTradeEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.CandleData
import com.example.data.model.ChatMessage
import com.example.data.model.FOAnalysisData
import com.example.data.model.FiiDiiActivity
import com.example.data.model.IndianBrokerStatus
import com.example.data.model.IntradaySentimentSummary
import com.example.data.model.IntradaySignal
import com.example.data.model.MarketBreadth
import com.example.data.model.MarketIndex
import com.example.data.model.NewsSentimentResult
import com.example.data.model.OptionChainRow
import com.example.data.model.PcrAlertSettings
import com.example.data.model.PcrAlertType
import com.example.data.model.PcrNotificationEvent
import com.example.data.model.SectorPerformance
import com.example.data.model.StockItem
import com.example.data.repository.MarketRepository
import com.example.util.AppLanguage
import com.example.util.AppLocalization
import com.example.util.PcrNotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MarketViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = MarketRepository(database)
    private val geminiService = GeminiStockAgentService()

    val indices: StateFlow<List<MarketIndex>> = repository.indices
    val allStocks: StateFlow<List<StockItem>> = repository.stocks
    val intradaySignals: StateFlow<List<IntradaySignal>> = repository.intradaySignals
    val sectors: StateFlow<List<SectorPerformance>> = repository.sectors
    val marketBreadth: StateFlow<MarketBreadth> = repository.marketBreadth
    val niftyFO: StateFlow<FOAnalysisData?> = repository.niftyFO
    val bankNiftyFO: StateFlow<FOAnalysisData?> = repository.bankNiftyFO
    val fiiDii: StateFlow<FiiDiiActivity> = repository.fiiDii
    val sentimentSummary: StateFlow<IntradaySentimentSummary> = repository.sentimentSummary
    val brokerStatusList: List<IndianBrokerStatus> = repository.brokerStatusList
    val newsSentiment: StateFlow<NewsSentimentResult> = repository.newsSentiment
    val pcrAlertSettings: StateFlow<PcrAlertSettings> = repository.pcrAlertSettings
    val activePcrAlert: StateFlow<PcrNotificationEvent?> = repository.activePcrAlert

    private val _isAnalyzingNews = MutableStateFlow(false)
    val isAnalyzingNews: StateFlow<Boolean> = _isAnalyzingNews.asStateFlow()

    val watchlist: StateFlow<List<WatchlistEntity>> = repository.watchlistFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paperTrades: StateFlow<List<PaperTradeEntity>> = repository.paperTradesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI state toggles
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.GUJARATI)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _isGujarati = MutableStateFlow(true)
    val isGujarati: StateFlow<Boolean> = _isGujarati.asStateFlow()

    // Live Trading & Broker Gateway state
    private val _isLiveTrading = MutableStateFlow(false)
    val isLiveTrading: StateFlow<Boolean> = _isLiveTrading.asStateFlow()

    private val _connectedBroker = MutableStateFlow("Zerodha Kite")
    val connectedBroker: StateFlow<String> = _connectedBroker.asStateFlow()

    private val _liveMargin = MutableStateFlow(248500.0)
    val liveMargin: StateFlow<Double> = _liveMargin.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStock = MutableStateFlow<StockItem?>(null)
    val selectedStock: StateFlow<StockItem?> = _selectedStock.asStateFlow()

    private val _tradeSheetStock = MutableStateFlow<StockItem?>(null)
    val tradeSheetStock: StateFlow<StockItem?> = _tradeSheetStock.asStateFlow()

    // Interactive Candlestick Chart state
    private val _selectedChartStock = MutableStateFlow<StockItem?>(null)
    val selectedChartStock: StateFlow<StockItem?> = _selectedChartStock.asStateFlow()

    private val _chartCandles = MutableStateFlow<List<CandleData>>(emptyList())
    val chartCandles: StateFlow<List<CandleData>> = _chartCandles.asStateFlow()

    private val _chartTimeframe = MutableStateFlow("15M")
    val chartTimeframe: StateFlow<String> = _chartTimeframe.asStateFlow()

    private val _isChartLoading = MutableStateFlow(false)
    val isChartLoading: StateFlow<Boolean> = _isChartLoading.asStateFlow()

    private val _intradayFilter = MutableStateFlow("ALL")
    val intradayFilter: StateFlow<String> = _intradayFilter.asStateFlow()

    // AI Chat state
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _aiStockReport = MutableStateFlow<String?>(null)
    val aiStockReport: StateFlow<String?> = _aiStockReport.asStateFlow()

    private val _isAiStockReportLoading = MutableStateFlow(false)
    val isAiStockReportLoading: StateFlow<Boolean> = _isAiStockReportLoading.asStateFlow()

    // Filtered stocks by search query
    val filteredStocks: StateFlow<List<StockItem>> = combine(allStocks, searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.symbol.contains(query, ignoreCase = true) ||
            it.nameEn.contains(query, ignoreCase = true) ||
            it.nameGu.contains(query, ignoreCase = true) ||
            it.sector.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        initInitialChat()
        viewModelScope.launch {
            allStocks.collect { list ->
                if (list.isNotEmpty() && _selectedChartStock.value == null) {
                    val defaultStock = list.first()
                    _selectedChartStock.value = defaultStock
                    loadChartCandles(defaultStock.symbol, _chartTimeframe.value)
                }
            }
        }
    }

    private fun initInitialChat() {
        val initialGreeting = AppLocalization.aiGreeting(_currentLanguage.value)

        _chatMessages.value = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                isUser = false,
                messageText = initialGreeting,
                timestamp = currentTimeString()
            )
        )
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        _isGujarati.value = (language == AppLanguage.GUJARATI)
        initInitialChat()
    }

    fun toggleLanguage() {
        val next = when (_currentLanguage.value) {
            AppLanguage.GUJARATI -> AppLanguage.HINDI
            AppLanguage.HINDI -> AppLanguage.ENGLISH
            AppLanguage.ENGLISH -> AppLanguage.MARATHI
            AppLanguage.MARATHI -> AppLanguage.GUJARATI
        }
        setLanguage(next)
    }

    fun toggleTradingMode(isLive: Boolean) {
        _isLiveTrading.value = isLive
    }

    fun setConnectedBroker(broker: String) {
        _connectedBroker.value = broker
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setIntradayFilter(filter: String) {
        _intradayFilter.value = filter
    }

    fun openStockDetails(stock: StockItem) {
        _selectedStock.value = stock
        _aiStockReport.value = null
    }

    fun closeStockDetails() {
        _selectedStock.value = null
        _aiStockReport.value = null
    }

    fun openTradeSheet(stock: StockItem) {
        _tradeSheetStock.value = stock
    }

    fun closeTradeSheet() {
        _tradeSheetStock.value = null
    }

    fun toggleWatchlist(symbol: String) {
        viewModelScope.launch {
            repository.toggleWatchlist(symbol)
        }
    }

    fun executePaperTrade(
        stock: StockItem,
        isBuy: Boolean,
        quantity: Int,
        target: Double,
        stopLoss: Double
    ) {
        viewModelScope.launch {
            repository.placePaperTrade(
                symbol = stock.symbol,
                isBuy = isBuy,
                quantity = quantity,
                price = stock.currentPrice,
                targetPrice = target,
                stopLossPrice = stopLoss
            )
            closeTradeSheet()
        }
    }

    fun closePaperTrade(trade: PaperTradeEntity) {
        viewModelScope.launch {
            val currentLtp = allStocks.value.find { it.symbol == trade.symbol }?.currentPrice ?: trade.buyPrice
            repository.closePaperTrade(
                tradeId = trade.id,
                exitPrice = currentLtp,
                buyPrice = trade.buyPrice,
                quantity = trade.quantity,
                isBuy = trade.isBuy
            )
        }
    }

    fun resetPaperTrading() {
        viewModelScope.launch {
            repository.resetPaperTrades()
        }
    }

    fun sendUserMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            isUser = true,
            messageText = text,
            timestamp = currentTimeString()
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _isAiThinking.value = true

        val currentNifty = indices.value.find { it.id == "NIFTY50" }?.currentPrice ?: 25088.0
        val currentBankNifty = indices.value.find { it.id == "BANKNIFTY" }?.currentPrice ?: 53842.0
        val pcr = niftyFO.value?.pcr ?: 1.15
        val context = "NIFTY 50: $currentNifty, BANK NIFTY: $currentBankNifty, NIFTY PCR: $pcr"

        viewModelScope.launch {
            val response = geminiService.analyzeMarketQuery(
                userPrompt = text,
                isGujarati = _isGujarati.value,
                marketContext = context
            )
            val aiMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                isUser = false,
                messageText = response,
                timestamp = currentTimeString()
            )
            _chatMessages.value = _chatMessages.value + aiMsg
            _isAiThinking.value = false
        }
    }

    fun requestAiStockAnalysis(stock: StockItem) {
        _isAiStockReportLoading.value = true
        _aiStockReport.value = null
        val prompt = if (_isGujarati.value) {
            "કૃપા કરીને ${stock.nameGu} (${stock.symbol}) શેરનું વિગતવાર ઇન્ટ્રાડે અને સ્વિંગ એનાલિસિસ આપો. વર્તમાન ભાવ: ₹${stock.currentPrice}, RSI: ${stock.rsi}, VWAP: ₹${stock.vwap}, સપોર્ટ ૧: ₹${stock.support1}, રેઝિસ્ટન્સ ૧: ₹${stock.resistance1}."
        } else {
            "Please provide a comprehensive technical & intraday breakdown for ${stock.nameEn} (${stock.symbol}). Current LTP: ₹${stock.currentPrice}, RSI: ${stock.rsi}, VWAP: ₹${stock.vwap}, S1: ₹${stock.support1}, R1: ₹${stock.resistance1}."
        }

        viewModelScope.launch {
            val response = geminiService.analyzeMarketQuery(
                userPrompt = prompt,
                isGujarati = _isGujarati.value,
                marketContext = "Stock: ${stock.symbol}, LTP: ${stock.currentPrice}, Buildup: ${stock.buildup.labelEn}"
            )
            _aiStockReport.value = response
            _isAiStockReportLoading.value = false
        }
    }

    fun refreshNewsSentimentWithGemini() {
        if (_isAnalyzingNews.value) return
        _isAnalyzingNews.value = true
        viewModelScope.launch {
            val headlines = newsSentiment.value.headlines.map { 
                if (_isGujarati.value) it.headlineGu else it.headlineEn 
            }
            val (score, verdict) = geminiService.analyzeNewsSentiment(
                headlines = headlines,
                isGujarati = _isGujarati.value
            )
            repository.updateNewsSentiment(
                geminiScore = score,
                verdict = verdict,
                isGujarati = _isGujarati.value
            )
            _isAnalyzingNews.value = false
        }
    }

    fun updatePcrThresholds(overbought: Double, oversold: Double) {
        repository.updatePcrThresholds(overbought, oversold)
    }

    fun togglePcrAlerts(enabled: Boolean) {
        repository.togglePcrAlerts(enabled)
    }

    fun dismissPcrAlert() {
        repository.dismissPcrAlert()
    }

    fun simulatePcrAlert(context: android.content.Context, type: PcrAlertType) {
        val event = repository.simulatePcrAlert(type)
        PcrNotificationHelper.sendPcrNotification(
            context = context,
            event = event,
            isGujarati = _isGujarati.value
        )
    }

    fun selectStockForChart(stock: StockItem) {
        _selectedChartStock.value = stock
        loadChartCandles(stock.symbol, _chartTimeframe.value)
    }

    fun setChartTimeframe(timeframe: String) {
        _chartTimeframe.value = timeframe
        _selectedChartStock.value?.let { stock ->
            loadChartCandles(stock.symbol, timeframe)
        }
    }

    fun refreshChartData() {
        val stock = _selectedChartStock.value ?: allStocks.value.firstOrNull() ?: return
        loadChartCandles(stock.symbol, _chartTimeframe.value)
    }

    private fun loadChartCandles(symbol: String, interval: String) {
        viewModelScope.launch {
            _isChartLoading.value = true
            val candles = repository.fetchIntradayHistoricalCandles(symbol, interval)
            _chartCandles.value = candles
            _isChartLoading.value = false
        }
    }

    private fun currentTimeString(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }
}
