package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WatchlistEntity
import com.example.data.model.CandleData
import com.example.data.model.FOAnalysisData
import com.example.data.model.FiiDiiActivity
import com.example.data.model.IndianBrokerStatus
import com.example.data.model.IntradaySentimentSummary
import com.example.data.model.MarketBreadth
import com.example.data.model.MarketIndex
import com.example.data.model.NewsSentimentResult
import com.example.data.model.PcrAlertSettings
import com.example.data.model.PcrAlertType
import com.example.data.model.PcrNotificationEvent
import com.example.data.model.SectorPerformance
import com.example.data.model.StockItem
import com.example.ui.components.BuildupBadge
import com.example.ui.components.CurrencyFormatter
import com.example.ui.components.InteractiveCandlestickChartCard
import com.example.ui.components.NewsSentimentGaugeCard
import com.example.ui.components.NsePythonEngineCard
import com.example.ui.components.OpenInterestCanvasChart
import com.example.ui.components.PcrNotificationTriggerCard
import com.example.ui.components.PctFormatter
import com.example.ui.components.PriceChangeBadge
import com.example.ui.components.SparklineCanvas
import com.example.ui.components.VolatilityHeatmapCard
import com.example.ui.theme.BearRed
import com.example.ui.theme.BearRedSoft
import com.example.ui.theme.BullGreen
import com.example.ui.theme.BullGreenSoft
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GoldAmberSoft
import com.example.ui.theme.MarketCardBorder
import com.example.ui.theme.MarketDarkBg
import com.example.ui.theme.MarketSurface
import com.example.ui.theme.MarketSurfaceVariant
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TechCyanSoft
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DashboardScreen(
    indices: List<MarketIndex>,
    stocks: List<StockItem>,
    sectors: List<SectorPerformance>,
    breadth: MarketBreadth,
    fiiDii: FiiDiiActivity,
    sentimentSummary: IntradaySentimentSummary,
    brokers: List<IndianBrokerStatus>,
    newsSentiment: NewsSentimentResult,
    isAnalyzingNews: Boolean = false,
    niftyFO: FOAnalysisData? = null,
    bankNiftyFO: FOAnalysisData? = null,
    pcrSettings: PcrAlertSettings = PcrAlertSettings(),
    activePcrAlert: PcrNotificationEvent? = null,
    selectedChartStock: StockItem? = null,
    chartCandles: List<CandleData> = emptyList(),
    chartTimeframe: String = "15M",
    isChartLoading: Boolean = false,
    watchlist: List<WatchlistEntity>,
    isGujarati: Boolean,
    onStockClick: (StockItem) -> Unit,
    onToggleWatchlist: (String) -> Unit,
    onNavigateToAi: () -> Unit,
    onNavigateToIntraday: () -> Unit = {},
    onNavigateToFO: () -> Unit = {},
    onRefreshNewsSentiment: () -> Unit = {},
    onTogglePcrAlerts: (Boolean) -> Unit = {},
    onUpdatePcrThresholds: (Double, Double) -> Unit = { _, _ -> },
    onDismissPcrAlert: () -> Unit = {},
    onSimulatePcrAlert: (PcrAlertType) -> Unit = {},
    onSelectChartStock: (StockItem) -> Unit = {},
    onSelectChartTimeframe: (String) -> Unit = {},
    onRefreshChart: () -> Unit = {}
) {
    val gainers = stocks.filter { it.changePercent >= 0 }.sortedByDescending { it.changePercent }
    val losers = stocks.filter { it.changePercent < 0 }.sortedBy { it.changePercent }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MarketDarkBg),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Live Indian Market Session Status Bar
        item {
            Surface(
                color = Color(0xFF0C1322),
                border = androidx.compose.foundation.BorderStroke(1.dp, MarketCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(BullGreen.copy(alpha = pulseAlpha))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isGujarati) "NSE / BSE લાઈવ સેશન (09:15 - 15:30 IST)" else "NSE / BSE Live Session (09:15 - 15:30 IST)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BullGreen
                        )
                    }

                    Text(
                        text = if (isGujarati) "ઇન્ટ્રાડે સક્રિય" else "Active Phase",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = TechCyan
                    )
                }
            }
        }

        // 2. Real-time Stock Indices Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        tint = TechCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGujarati) "રિયલ-ટાઇમ ઇન્ડેક્સ (Real-Time Indices)" else "Real-Time Market Indices",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "${indices.size} ${if (isGujarati) "ઇન્ડેક્સ" else "indices"}",
                    fontSize = 11.sp,
                    color = TextTertiary
                )
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(indices) { index ->
                    RealtimeIndexCard(index = index, isGujarati = isGujarati)
                }
            }
        }

        // 2b. Visual Sentiment Gauge using Circular Progress Indicator (Gemini AI Daily News Analysis)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            NewsSentimentGaugeCard(
                sentimentResult = newsSentiment,
                isAnalyzing = isAnalyzingNews,
                isGujarati = isGujarati,
                onRefreshSentiment = onRefreshNewsSentiment
            )
        }

        // 3. Intraday Market Sentiment Summary (Mega Section)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            IntradaySentimentSummaryCard(
                sentiment = sentimentSummary,
                breadth = breadth,
                fiiDii = fiiDii,
                isGujarati = isGujarati,
                onNavigateToAi = onNavigateToAi,
                onNavigateToFO = onNavigateToFO
            )
        }

        // 3b. Open Interest (OI) Build-up Canvas Chart (Call vs Put Support & Resistance)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            OpenInterestCanvasChart(
                niftyFO = niftyFO,
                bankNiftyFO = bankNiftyFO,
                isGujarati = isGujarati
            )
        }

        // 3c. PCR Notification Trigger System (Overbought / Oversold Alert Monitor)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            PcrNotificationTriggerCard(
                currentPcr = niftyFO?.pcr ?: 1.18,
                settings = pcrSettings,
                activeAlert = activePcrAlert,
                isGujarati = isGujarati,
                onToggleEnabled = onTogglePcrAlerts,
                onUpdateThresholds = onUpdatePcrThresholds,
                onDismissAlert = onDismissPcrAlert,
                onSimulateAlert = onSimulatePcrAlert
            )
        }

        // 3d. Interactive Candlestick Chart Visualization (Canvas with Live Intraday Data)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            InteractiveCandlestickChartCard(
                stocks = stocks,
                selectedStock = selectedChartStock,
                candles = chartCandles,
                timeframe = chartTimeframe,
                isLoading = isChartLoading,
                isGujarati = isGujarati,
                onSelectStock = onSelectChartStock,
                onSelectTimeframe = onSelectChartTimeframe,
                onRefresh = onRefreshChart
            )
        }

        // 3e. Visual Volatility Heatmap Grid for Top Nifty 50 Stocks
        item {
            Spacer(modifier = Modifier.height(16.dp))
            VolatilityHeatmapCard(
                stocks = stocks,
                isGujarati = isGujarati,
                onStockClick = onStockClick
            )
        }

        // 3f. NSE Python Option Chain Pipeline Engine (Headers, Cookies & Pandas DF)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            NsePythonEngineCard(isGujarati = isGujarati)
        }

        // 4. Indian Brokers & SEBI Compliance Section
        item {
            Spacer(modifier = Modifier.height(16.dp))
            IndianBrokerAndSebiCard(
                brokers = brokers,
                isGujarati = isGujarati
            )
        }

        // 5. Sector Performance Heatmap
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QueryStats,
                        contentDescription = null,
                        tint = TechCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGujarati) "સેક્ટર હીટમેપ (Sector Radar)" else "Sectoral Heatmap & Strength",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(sectors) { sector ->
                    SectorTile(sector = sector, isGujarati = isGujarati)
                }
            }
        }

        // 6. Top Intraday Gainers (તેજી વાળા શેર)
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = BullGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGujarati) "આજના તેજીના શેર (Top Gainers)" else "Top Gainers (Bullish)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "${gainers.size} ${if (isGujarati) "શેર" else "stocks"}",
                    fontSize = 12.sp,
                    color = BullGreen
                )
            }
        }

        items(gainers.take(5)) { stock ->
            val isFav = watchlist.any { it.symbol == stock.symbol }
            StockRowItem(
                stock = stock,
                isFavorite = isFav,
                isGujarati = isGujarati,
                onClick = { onStockClick(stock) },
                onToggleWatchlist = { onToggleWatchlist(stock.symbol) }
            )
        }

        // 7. Top Intraday Losers (મંદી વાળા શેર)
        if (losers.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = BearRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isGujarati) "આજના મંદીના શેર (Top Losers)" else "Top Losers (Bearish)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "${losers.size} ${if (isGujarati) "શેર" else "stocks"}",
                        fontSize = 12.sp,
                        color = BearRed
                    )
                }
            }

            items(losers.take(4)) { stock ->
                val isFav = watchlist.any { it.symbol == stock.symbol }
                StockRowItem(
                    stock = stock,
                    isFavorite = isFav,
                    isGujarati = isGujarati,
                    onClick = { onStockClick(stock) },
                    onToggleWatchlist = { onToggleWatchlist(stock.symbol) }
                )
            }
        }
    }
}

/**
 * Real-time Index Card with live price, day range slider, and sparkline
 */
@Composable
fun RealtimeIndexCard(index: MarketIndex, isGujarati: Boolean) {
    val isPositive = index.change >= 0
    val color = if (isPositive) BullGreen else BearRed

    Card(
        modifier = Modifier
            .width(195.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MarketSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MarketCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isGujarati) index.nameGu else index.nameEn,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1
                )
                PriceChangeBadge(changePercent = index.changePercent)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = CurrencyFormatter.format(index.currentPrice),
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Text(
                text = "${if (isPositive) "+" else ""}${CurrencyFormatter.format(index.change)}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = color
            )

            Spacer(modifier = Modifier.height(8.dp))

            SparklineCanvas(
                points = index.sparkline,
                isPositive = isPositive,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Day's Range High/Low Slider
            val dayRange = if (index.highPrice - index.lowPrice == 0.0) 1.0 else (index.highPrice - index.lowPrice)
            val currentPos = ((index.currentPrice - index.lowPrice) / dayRange).coerceIn(0.0, 1.0).toFloat()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF223048))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(currentPos)
                        .height(4.dp)
                        .background(color)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "L: ${CurrencyFormatter.format(index.lowPrice)}",
                    fontSize = 9.sp,
                    color = TextTertiary
                )
                Text(
                    text = "H: ${CurrencyFormatter.format(index.highPrice)}",
                    fontSize = 9.sp,
                    color = TextTertiary
                )
            }
        }
    }
}

/**
 * Dedicated Intraday Market Sentiment Summary Card (ઇન્ટ્રાડે માર્કેટ સેન્ટિમેન્ટ સમરી)
 */
@Composable
fun IntradaySentimentSummaryCard(
    sentiment: IntradaySentimentSummary,
    breadth: MarketBreadth,
    fiiDii: FiiDiiActivity,
    isGujarati: Boolean,
    onNavigateToAi: () -> Unit,
    onNavigateToFO: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = MarketSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, TechCyan.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(TechCyanSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = TechCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isGujarati) "ઇન્ટ્રાડે માર્કેટ સેન્ટિમેન્ટ સમરી" else "Intraday Market Sentiment Summary",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isGujarati) "NSE/BSE રિયલ-ટાઇમ માર્કેટ મૂડ & પ્રવાહ" else "NSE/BSE Real-Time Market Mood & Flow",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Surface(
                    color = BullGreenSoft,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isGujarati) sentiment.overallSentimentGu else sentiment.overallSentimentEn,
                        color = BullGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sentiment Score Dial Gauge & Advances/Declines
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0C121E))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Dial Gauge Canvas
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SentimentGaugeCanvas(
                        score = sentiment.score,
                        modifier = Modifier.size(80.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${sentiment.score}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BullGreen
                        )
                        Text(
                            text = "/100",
                            fontSize = 9.sp,
                            color = TextTertiary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Market Breadth & Key Metrics
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isGujarati) "બજાર બ્રેડ્થ (Advances vs Declines):" else "Market Breadth (A/D):",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val total = breadth.advances + breadth.declines + breadth.unchanged
                    val advFraction = if (total > 0) breadth.advances.toFloat() / total else 0.5f

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(BearRed)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(advFraction)
                                .height(6.dp)
                                .background(BullGreen)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🟢 ${breadth.advances}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BullGreen
                        )
                        Text(
                            text = "⚪ ${breadth.unchanged}",
                            fontSize = 10.sp,
                            color = TextTertiary
                        )
                        Text(
                            text = "🔴 ${breadth.declines}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BearRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4-Block Matrix: FII/DII, VIX, Nifty PCR, Bank Nifty PCR
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // FII Inflow
                SentimentMetricBlock(
                    title = if (isGujarati) "FII રોકાણ પ્રવાહ" else "FII Net Cash",
                    value = "+₹${CurrencyFormatter.format(fiiDii.fiiNetBuyCr)} Cr",
                    subtitle = "Long: ${fiiDii.fiiFnoLongPercent}%",
                    color = BullGreen,
                    modifier = Modifier.weight(1f)
                )

                // DII Inflow
                SentimentMetricBlock(
                    title = if (isGujarati) "DII રોકાણ પ્રવાહ" else "DII Net Cash",
                    value = "+₹${CurrencyFormatter.format(fiiDii.diiNetBuyCr)} Cr",
                    subtitle = if (isGujarati) "સ્થાનિક ટેકો" else "Domestic Flow",
                    color = BullGreen,
                    modifier = Modifier.weight(1f)
                )

                // India VIX
                SentimentMetricBlock(
                    title = "INDIA VIX",
                    value = "${sentiment.vixValue}",
                    subtitle = if (isGujarati) "ઓછી વોલેટિલિટી" else "Low Volatility",
                    color = TechCyan,
                    modifier = Modifier.weight(1f)
                )

                // Multi-Strike PCR
                SentimentMetricBlock(
                    title = "NIFTY PCR",
                    value = "${sentiment.niftyPcr}",
                    subtitle = "BankNifty: ${sentiment.bankNiftyPcr}",
                    color = GoldAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // AI Actionable Intraday Verdict Callout
            Surface(
                color = TechCyanSoft.copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TechCyan.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAi() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = TechCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isGujarati) "AI ઇન્ટ્રાડે વ્યુ & વ્યૂહરચના" else "AI Intraday Actionable View",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TechCyan
                        )
                        Text(
                            text = if (isGujarati) sentiment.aiIntradayVerdictGu else sentiment.aiIntradayVerdictEn,
                            fontSize = 11.sp,
                            color = TextPrimary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Canvas Gauge Dial for Fear & Greed / Intraday Bull-Bear sentiment
 */
@Composable
fun SentimentGaugeCanvas(
    score: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2, h / 2)
        val strokeWidth = 8.dp.toPx()
        val radius = (minOf(w, h) - strokeWidth) / 2

        // Background arc (Bear Red to Bull Green gradient)
        val startAngle = 140f
        val sweepAngle = 260f

        // Draw segmented background arc
        drawArc(
            color = Color(0xFF1E2838),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        // Draw active score arc
        val activeSweep = (score / 100f) * sweepAngle
        val activeColor = when {
            score >= 70 -> BullGreen
            score >= 50 -> TechCyan
            score >= 40 -> GoldAmber
            else -> BearRed
        }

        drawArc(
            color = activeColor,
            startAngle = startAngle,
            sweepAngle = activeSweep,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )
    }
}

@Composable
fun SentimentMetricBlock(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MarketSurfaceVariant,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MarketCardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 9.sp, color = TextTertiary, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1)
            Spacer(modifier = Modifier.height(1.dp))
            Text(text = subtitle, fontSize = 8.sp, color = TextSecondary, maxLines = 1)
        }
    }
}

/**
 * Indian Brokers (Zerodha, Angel One, Groww, Upstox) & SEBI Compliance Card
 */
@Composable
fun IndianBrokerAndSebiCard(
    brokers: List<IndianBrokerStatus>,
    isGujarati: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MarketSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MarketCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = TechCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGujarati) "ભારતીય બ્રોકર્સ API & SEBI નિયમો" else "Indian Broker APIs & SEBI Compliance",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Surface(
                    color = Color(0xFF16253A),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "SEBI Compliance",
                        color = TechCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Broker Connection Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                brokers.forEach { broker ->
                    Surface(
                        color = Color(0xFF0C121E),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MarketCardBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = broker.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(BullGreen)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${broker.latencyMs}ms",
                                    fontSize = 8.sp,
                                    color = BullGreen
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // SEBI Educational Disclaimer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0D1420))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = GoldAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isGujarati)
                        "SEBI સંશોધન: F&O સેગમેન્ટમાં 90% ટ્રેડર્સને નુકસાન થાય છે. આ AI એજન્ટ માત્ર શૈક્ષણિક વિશ્લેષણ પૂરું પાડે છે."
                    else
                        "SEBI Study: 9 out of 10 traders incur net losses in F&O. This AI Agent provides educational quantitative research only.",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

@Composable
fun SectorTile(sector: SectorPerformance, isGujarati: Boolean) {
    val isPositive = sector.changePercent >= 0
    val color = if (isPositive) BullGreen else BearRed
    val bg = if (isPositive) BullGreenSoft else BearRedSoft

    Surface(
        color = bg,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        modifier = Modifier.width(135.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = if (isGujarati) sector.sectorNameGu else sector.sectorNameEn,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${PctFormatter.format(sector.changePercent)}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Leader: ${sector.leadingStock}",
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun StockRowItem(
    stock: StockItem,
    isFavorite: Boolean,
    isGujarati: Boolean,
    onClick: () -> Unit,
    onToggleWatchlist: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("stock_item_${stock.symbol}"),
        colors = CardDefaults.cardColors(containerColor = MarketSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MarketCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = onToggleWatchlist,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Watchlist",
                        tint = if (isFavorite) GoldAmber else TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stock.symbol,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        BuildupBadge(buildup = stock.buildup, isGujarati = isGujarati)
                    }
                    Text(
                        text = if (isGujarati) stock.nameGu else stock.nameEn,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${CurrencyFormatter.format(stock.currentPrice)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                PriceChangeBadge(changePercent = stock.changePercent)
            }
        }
    }
}
