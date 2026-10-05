package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.TopMarketHeader
import com.example.ui.screens.AiAgentScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FOScreen
import com.example.ui.screens.IntradayScannerScreen
import com.example.ui.screens.PaperTradingScreen
import com.example.ui.screens.StockDetailSheet
import com.example.ui.screens.TradeOrderDialog
import com.example.ui.theme.AppTheme
import com.example.ui.theme.MarketCardBorder
import com.example.ui.theme.MarketDarkBg
import com.example.ui.theme.MarketSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MarketViewModel
import com.example.util.AppLanguage
import com.example.util.AppLocalization

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val marketViewModel: MarketViewModel = viewModel()
            val isDarkMode by marketViewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                MainStockApp(marketViewModel = marketViewModel)
            }
        }
    }
}

@Composable
fun MainStockApp(marketViewModel: MarketViewModel = viewModel()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val colors = AppTheme.colors
    val selectedTab by marketViewModel.selectedTab.collectAsState()
    val isGujarati by marketViewModel.isGujarati.collectAsState()
    val currentLanguage by marketViewModel.currentLanguage.collectAsState()
    val isDarkMode by marketViewModel.isDarkMode.collectAsState()
    val isLiveTrading by marketViewModel.isLiveTrading.collectAsState()
    val connectedBroker by marketViewModel.connectedBroker.collectAsState()
    val liveMargin by marketViewModel.liveMargin.collectAsState()
    val indices by marketViewModel.indices.collectAsState()
    val stocks by marketViewModel.allStocks.collectAsState()
    val intradaySignals by marketViewModel.intradaySignals.collectAsState()
    val sectors by marketViewModel.sectors.collectAsState()
    val breadth by marketViewModel.marketBreadth.collectAsState()
    val niftyFO by marketViewModel.niftyFO.collectAsState()
    val bankNiftyFO by marketViewModel.bankNiftyFO.collectAsState()
    val fiiDii by marketViewModel.fiiDii.collectAsState()
    val sentimentSummary by marketViewModel.sentimentSummary.collectAsState()
    val brokers = marketViewModel.brokerStatusList
    val newsSentiment by marketViewModel.newsSentiment.collectAsState()
    val isAnalyzingNews by marketViewModel.isAnalyzingNews.collectAsState()
    val pcrSettings by marketViewModel.pcrAlertSettings.collectAsState()
    val activePcrAlert by marketViewModel.activePcrAlert.collectAsState()
    val selectedChartStock by marketViewModel.selectedChartStock.collectAsState()
    val chartCandles by marketViewModel.chartCandles.collectAsState()
    val chartTimeframe by marketViewModel.chartTimeframe.collectAsState()
    val isChartLoading by marketViewModel.isChartLoading.collectAsState()
    val watchlist by marketViewModel.watchlist.collectAsState()
    val paperTrades by marketViewModel.paperTrades.collectAsState()

    val selectedStock by marketViewModel.selectedStock.collectAsState()
    val tradeSheetStock by marketViewModel.tradeSheetStock.collectAsState()
    val aiStockReport by marketViewModel.aiStockReport.collectAsState()
    val isAiStockLoading by marketViewModel.isAiStockReportLoading.collectAsState()

    val chatMessages by marketViewModel.chatMessages.collectAsState()
    val isAiThinking by marketViewModel.isAiThinking.collectAsState()
    val intradayFilter by marketViewModel.intradayFilter.collectAsState()

    val navItems = listOf(
        NavigationItem(
            titleEn = "Dashboard",
            titleGu = "ડેશબોર્ડ",
            selectedIcon = Icons.Filled.Dashboard,
            unselectedIcon = Icons.Outlined.Dashboard,
            tag = "nav_dashboard"
        ),
        NavigationItem(
            titleEn = "Intraday",
            titleGu = "ઇન્ટ્રાડે",
            selectedIcon = Icons.Filled.ElectricBolt,
            unselectedIcon = Icons.Outlined.ElectricBolt,
            tag = "nav_intraday"
        ),
        NavigationItem(
            titleEn = "F&O Options",
            titleGu = "ઓપ્શન્સ",
            selectedIcon = Icons.Filled.QueryStats,
            unselectedIcon = Icons.Outlined.QueryStats,
            tag = "nav_fo"
        ),
        NavigationItem(
            titleEn = "AI Agent",
            titleGu = "AI એજન્ટ",
            selectedIcon = Icons.Filled.AutoAwesome,
            unselectedIcon = Icons.Outlined.AutoAwesome,
            tag = "nav_ai_agent"
        ),
        NavigationItem(
            titleEn = "Paper Trade",
            titleGu = "પેપર ટ્રેડ",
            selectedIcon = Icons.Filled.Wallet,
            unselectedIcon = Icons.Outlined.Wallet,
            tag = "nav_paper_trading"
        )
    )

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopMarketHeader(
                indices = indices,
                isGujarati = isGujarati,
                currentLanguage = currentLanguage,
                isDarkMode = isDarkMode,
                onToggleTheme = { marketViewModel.toggleDarkMode() },
                onToggleLanguage = { marketViewModel.toggleLanguage() },
                onSelectLanguage = { marketViewModel.setLanguage(it) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = colors.surface,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav_bar")
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { marketViewModel.setSelectedTab(index) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.titleEn
                            )
                        },
                        label = {
                            Text(
                                text = when (currentLanguage) {
                                    AppLanguage.GUJARATI -> item.titleGu
                                    AppLanguage.HINDI -> when (index) {
                                        0 -> "डैशबोर्ड"
                                        1 -> "इंट्राडे"
                                        2 -> "ऑप्शंस"
                                        3 -> "AI एजेंट"
                                        else -> "ट्रेडिंग"
                                    }
                                    AppLanguage.MARATHI -> when (index) {
                                        0 -> "डॅशबोर्ड"
                                        1 -> "इंट्राडे"
                                        2 -> "ऑप्शन्स"
                                        3 -> "AI एजंट"
                                        else -> "ट्रेडिंग"
                                    }
                                    else -> item.titleEn
                                },
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = colors.cyan,
                            indicatorColor = colors.cyan,
                            unselectedIconColor = colors.textSecondary,
                            unselectedTextColor = colors.textSecondary
                        ),
                        modifier = Modifier.testTag(item.tag)
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(
                    indices = indices,
                    stocks = stocks,
                    sectors = sectors,
                    breadth = breadth,
                    fiiDii = fiiDii,
                    sentimentSummary = sentimentSummary,
                    brokers = brokers,
                    newsSentiment = newsSentiment,
                    isAnalyzingNews = isAnalyzingNews,
                    niftyFO = niftyFO,
                    bankNiftyFO = bankNiftyFO,
                    pcrSettings = pcrSettings,
                    activePcrAlert = activePcrAlert,
                    selectedChartStock = selectedChartStock,
                    chartCandles = chartCandles,
                    chartTimeframe = chartTimeframe,
                    isChartLoading = isChartLoading,
                    watchlist = watchlist,
                    isGujarati = isGujarati,
                    onStockClick = { marketViewModel.openStockDetails(it) },
                    onToggleWatchlist = { marketViewModel.toggleWatchlist(it) },
                    onNavigateToAi = { marketViewModel.setSelectedTab(3) },
                    onNavigateToIntraday = { marketViewModel.setSelectedTab(1) },
                    onNavigateToFO = { marketViewModel.setSelectedTab(2) },
                    onRefreshNewsSentiment = { marketViewModel.refreshNewsSentimentWithGemini() },
                    onTogglePcrAlerts = { marketViewModel.togglePcrAlerts(it) },
                    onUpdatePcrThresholds = { ob, os -> marketViewModel.updatePcrThresholds(ob, os) },
                    onDismissPcrAlert = { marketViewModel.dismissPcrAlert() },
                    onSimulatePcrAlert = { marketViewModel.simulatePcrAlert(context, it) },
                    onSelectChartStock = { marketViewModel.selectStockForChart(it) },
                    onSelectChartTimeframe = { marketViewModel.setChartTimeframe(it) },
                    onRefreshChart = { marketViewModel.refreshChartData() }
                )
                1 -> IntradayScannerScreen(
                    signals = intradaySignals,
                    stocks = stocks,
                    selectedFilter = intradayFilter,
                    isGujarati = isGujarati,
                    onFilterChange = { marketViewModel.setIntradayFilter(it) },
                    onStockClick = { marketViewModel.openStockDetails(it) },
                    onTradeClick = { marketViewModel.openTradeSheet(it) }
                )
                2 -> FOScreen(
                    niftyFO = niftyFO,
                    bankNiftyFO = bankNiftyFO,
                    isGujarati = isGujarati
                )
                3 -> AiAgentScreen(
                    messages = chatMessages,
                    isThinking = isAiThinking,
                    isGujarati = isGujarati,
                    onSendMessage = { marketViewModel.sendUserMessage(it) }
                )
                4 -> PaperTradingScreen(
                    trades = paperTrades,
                    stocks = stocks,
                    watchlist = watchlist,
                    isGujarati = isGujarati,
                    isLiveTrading = isLiveTrading,
                    connectedBroker = connectedBroker,
                    liveMargin = liveMargin,
                    onToggleLiveTrading = { marketViewModel.toggleTradingMode(it) },
                    onSelectBroker = { marketViewModel.setConnectedBroker(it) },
                    onCloseTrade = { marketViewModel.closePaperTrade(it) },
                    onResetTrades = { marketViewModel.resetPaperTrading() },
                    onStockClick = { marketViewModel.openStockDetails(it) },
                    onToggleWatchlist = { marketViewModel.toggleWatchlist(it) }
                )
            }
        }
    }

    // Stock Detail Bottom Sheet
    selectedStock?.let { stock ->
        StockDetailSheet(
            stock = stock,
            isGujarati = isGujarati,
            aiReport = aiStockReport,
            isAiLoading = isAiStockLoading,
            onDismiss = { marketViewModel.closeStockDetails() },
            onRequestAiAnalysis = { marketViewModel.requestAiStockAnalysis(stock) },
            onOpenTradeSheet = { marketViewModel.openTradeSheet(it) }
        )
    }

    // Quick Paper Trade Order Dialog
    tradeSheetStock?.let { stock ->
        TradeOrderDialog(
            stock = stock,
            isGujarati = isGujarati,
            onDismiss = { marketViewModel.closeTradeSheet() },
            onExecuteTrade = { s, isBuy, qty, target, sl ->
                marketViewModel.executePaperTrade(s, isBuy, qty, target, sl)
            }
        )
    }
}

data class NavigationItem(
    val titleEn: String,
    val titleGu: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val tag: String
)
