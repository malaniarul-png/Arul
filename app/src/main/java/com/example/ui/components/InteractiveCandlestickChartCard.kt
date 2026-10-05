package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CandleData
import com.example.data.model.StockItem
import com.example.util.RsiCalculator
import com.example.ui.theme.BearRed
import com.example.ui.theme.BearRedSoft
import com.example.ui.theme.BullGreen
import com.example.ui.theme.BullGreenSoft
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun InteractiveCandlestickChartCard(
    stocks: List<StockItem>,
    selectedStock: StockItem?,
    candles: List<CandleData>,
    timeframe: String,
    isLoading: Boolean,
    isGujarati: Boolean,
    onSelectStock: (StockItem) -> Unit,
    onSelectTimeframe: (String) -> Unit,
    onRefresh: () -> Unit
) {
    var showMa by remember { mutableStateOf(true) }
    var showVolume by remember { mutableStateOf(true) }
    var showRsi by remember { mutableStateOf(true) }
    var selectedCandleIndex by remember(candles) { mutableStateOf<Int?>(null) }

    val activeStock = selectedStock ?: stocks.firstOrNull()
    val activeCandles = if (candles.isNotEmpty()) candles else (activeStock?.candles ?: emptyList())
    val rsiSeries = remember(activeCandles) { RsiCalculator.calculateRsiSeries(activeCandles, period = 14) }

    val inspectedCandle = selectedCandleIndex?.let { idx ->
        if (idx in activeCandles.indices) activeCandles[idx] else null
    } ?: activeCandles.lastOrNull()

    val currentRsi = selectedCandleIndex?.let { idx ->
        if (idx in rsiSeries.indices) rsiSeries[idx] else null
    } ?: rsiSeries.lastOrNull()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .testTag("interactive_candlestick_chart_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101726)),
        border = androidx.compose.foundation.BorderStroke(1.dp, TechCyan.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Stock Info & Live Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0C2433)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CandlestickChart,
                            contentDescription = null,
                            tint = TechCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeStock?.symbol ?: "NIFTY 50",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = if ((activeStock?.changePercent ?: 0.0) >= 0) BullGreenSoft else BearRedSoft,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "${if ((activeStock?.changePercent ?: 0.0) >= 0) "+" else ""}${String.format("%.2f", activeStock?.changePercent ?: 0.0)}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if ((activeStock?.changePercent ?: 0.0) >= 0) BullGreen else BearRed,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isGujarati) "ઇન્ટ્રાડે કેન્ડલસ્ટિક ચાર્ટ (Canvas એન્જિન)" else "Intraday Candlestick Chart (Canvas)",
                            fontSize = 11.sp,
                            color = TechCyan
                        )
                    }
                }

                // Price and Refresh Action
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${String.format("%.2f", activeStock?.currentPrice ?: 0.0)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "H: ₹${activeStock?.dayHigh?.toInt() ?: 0}  L: ₹${activeStock?.dayLow?.toInt() ?: 0}",
                            fontSize = 9.sp,
                            color = TextTertiary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.size(32.dp).testTag("refresh_chart_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = TechCyan,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = TechCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stock Quick-Picker Horizontal Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                stocks.take(10).forEach { stock ->
                    val isSelected = stock.symbol == activeStock?.symbol
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectStock(stock) },
                        label = { Text(stock.symbol, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TechCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = Color(0xFF0B101B),
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Timeframe & Indicator Controls Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timeframes
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("5M", "15M", "1H", "1D").forEach { tf ->
                        val isSelected = tf == timeframe
                        Surface(
                            color = if (isSelected) Color(0xFF1E2F4A) else Color(0xFF0C1322),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = if (isSelected) TechCyan else Color(0xFF1F2B3E)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onSelectTimeframe(tf) }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = tf,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TechCyan else TextSecondary
                            )
                        }
                    }
                }

                // Overlay Toggles: MA20 & Volume
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (showMa) GoldAmber.copy(alpha = 0.2f) else Color(0xFF0C1322),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 0.5.dp,
                            color = if (showMa) GoldAmber else Color(0xFF1F2B3E)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { showMa = !showMa }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "MA20",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showMa) GoldAmber else TextTertiary
                        )
                    }

                    Surface(
                        color = if (showVolume) TechCyan.copy(alpha = 0.2f) else Color(0xFF0C1322),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 0.5.dp,
                            color = if (showVolume) TechCyan else Color(0xFF1F2B3E)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { showVolume = !showVolume }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "VOL",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showVolume) TechCyan else TextTertiary
                        )
                    }

                    Surface(
                        color = if (showRsi) Color(0xFFA855F7).copy(alpha = 0.22f) else Color(0xFF0C1322),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 0.5.dp,
                            color = if (showRsi) Color(0xFFA855F7) else Color(0xFF1F2B3E)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { showRsi = !showRsi }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "RSI(14)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showRsi) Color(0xFFA855F7) else TextTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic OHLC & RSI Floating HUD Bar (Updates on Drag / Tap)
            inspectedCandle?.let { c ->
                val isBull = c.close >= c.open
                val candleChgPct = if (c.open > 0) ((c.close - c.open) / c.open) * 100f else 0f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0A0F1A))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = c.timeLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechCyan
                    )
                    Text(
                        text = "O:${String.format("%.1f", c.open)}",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "H:${String.format("%.1f", c.high)}",
                        fontSize = 10.sp,
                        color = BullGreen
                    )
                    Text(
                        text = "L:${String.format("%.1f", c.low)}",
                        fontSize = 10.sp,
                        color = BearRed
                    )
                    Text(
                        text = "C:${String.format("%.1f", c.close)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isBull) BullGreen else BearRed
                    )
                    Text(
                        text = "${if (candleChgPct >= 0) "+" else ""}${String.format("%.1f", candleChgPct)}%",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isBull) BullGreen else BearRed
                    )
                    // RSI Badge in HUD
                    currentRsi?.let { rsiVal ->
                        Surface(
                            color = when {
                                rsiVal >= 70f -> BearRedSoft
                                rsiVal <= 30f -> BullGreenSoft
                                else -> Color(0xFF192336)
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "RSI ${String.format("%.1f", rsiVal)} ${when {
                                    rsiVal >= 70f -> "🚨"
                                    rsiVal <= 30f -> "🟢"
                                    else -> ""
                                }}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    rsiVal >= 70f -> BearRed
                                    rsiVal <= 30f -> BullGreen
                                    else -> Color(0xFFA855F7)
                                },
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive High-Performance Canvas Candlestick Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF070B13))
                    .testTag("candlestick_interactive_canvas")
            ) {
                if (activeCandles.isNotEmpty()) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .pointerInput(activeCandles) {
                                detectTapGestures(
                                    onTap = { offset ->
                                        val totalCandles = activeCandles.size
                                        if (totalCandles > 0) {
                                            val candleWidth = size.width / totalCandles
                                            val idx = (offset.x / candleWidth).toInt().coerceIn(0, totalCandles - 1)
                                            selectedCandleIndex = idx
                                        }
                                    }
                                )
                            }
                            .pointerInput(activeCandles) {
                                detectDragGestures(
                                    onDrag = { change, _ ->
                                        change.consume()
                                        val totalCandles = activeCandles.size
                                        if (totalCandles > 0) {
                                            val candleWidth = size.width / totalCandles
                                            val idx = (change.position.x / candleWidth).toInt().coerceIn(0, totalCandles - 1)
                                            selectedCandleIndex = idx
                                        }
                                    },
                                    onDragEnd = {
                                        // Keep inspected candle visible
                                    }
                                )
                            }
                    ) {
                        val w = size.width
                        val h = size.height
                        val totalCandles = activeCandles.size
                        if (totalCandles == 0) return@Canvas

                        val priceChartH = if (showVolume) h * 0.76f else h * 0.90f
                        val volChartH = h * 0.20f
                        val volYBase = h - 18.dp.toPx()

                        val rawMinPrice = activeCandles.minOfOrNull { it.low } ?: 0f
                        val rawMaxPrice = activeCandles.maxOfOrNull { it.high } ?: 1f
                        val pricePadding = (rawMaxPrice - rawMinPrice) * 0.08f
                        val minPrice = rawMinPrice - pricePadding
                        val maxPrice = rawMaxPrice + pricePadding
                        val priceRange = if (maxPrice - minPrice == 0f) 1f else maxPrice - minPrice

                        val maxVol = (activeCandles.maxOfOrNull { it.volume } ?: 1L).toFloat().coerceAtLeast(1f)

                        val candleStep = w / totalCandles
                        val bodyWidth = (candleStep * 0.60f).coerceIn(4.dp.toPx(), 24.dp.toPx())

                        // 1. Horizontal Guidelines with Price Labels
                        val gridCount = 4
                        for (i in 0..gridCount) {
                            val gridY = (priceChartH / gridCount) * i
                            val priceVal = maxPrice - (i.toFloat() / gridCount * priceRange)

                            // Guideline
                            drawLine(
                                color = Color(0xFF141F30),
                                start = Offset(0f, gridY),
                                end = Offset(w, gridY),
                                strokeWidth = 1f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                            )
                        }

                        // 2. Draw Candlesticks & Volume
                        activeCandles.forEachIndexed { i, c ->
                            val centerX = (i * candleStep) + (candleStep / 2f)
                            val isBull = c.close >= c.open
                            val candleColor = if (isBull) BullGreen else BearRed

                            val highY = priceChartH - ((c.high - minPrice) / priceRange * priceChartH)
                            val lowY = priceChartH - ((c.low - minPrice) / priceRange * priceChartH)
                            val openY = priceChartH - ((c.open - minPrice) / priceRange * priceChartH)
                            val closeY = priceChartH - ((c.close - minPrice) / priceRange * priceChartH)

                            val bodyTop = minOf(openY, closeY)
                            val bodyBottom = maxOf(openY, closeY)
                            val bodyH = maxOf(2.5f, bodyBottom - bodyTop)

                            // Volume Bar (if enabled)
                            if (showVolume) {
                                val vBarH = (c.volume / maxVol) * volChartH
                                drawRoundRect(
                                    color = candleColor.copy(alpha = 0.35f),
                                    topLeft = Offset(centerX - (bodyWidth / 2f), volYBase - vBarH),
                                    size = Size(bodyWidth, vBarH),
                                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                                )
                            }

                            // Wick (Upper & Lower)
                            drawLine(
                                color = candleColor,
                                start = Offset(centerX, highY),
                                end = Offset(centerX, lowY),
                                strokeWidth = 1.8.dp.toPx(),
                                cap = StrokeCap.Round
                            )

                            // Body
                            drawRoundRect(
                                color = candleColor,
                                topLeft = Offset(centerX - (bodyWidth / 2f), bodyTop),
                                size = Size(bodyWidth, bodyH),
                                cornerRadius = CornerRadius(2.5.dp.toPx(), 2.5.dp.toPx())
                            )
                        }

                        // 3. Draw Moving Average (MA20) Curve
                        if (showMa && totalCandles >= 2) {
                            val maPath = Path()
                            val period = minOf(5, totalCandles)
                            var firstPoint = true

                            for (i in 0 until totalCandles) {
                                val windowStart = maxOf(0, i - period + 1)
                                val sublist = activeCandles.subList(windowStart, i + 1)
                                val maPrice = sublist.map { it.close }.average().toFloat()

                                val x = (i * candleStep) + (candleStep / 2f)
                                val y = priceChartH - ((maPrice - minPrice) / priceRange * priceChartH)

                                if (firstPoint) {
                                    maPath.moveTo(x, y)
                                    firstPoint = false
                                } else {
                                    maPath.lineTo(x, y)
                                }
                            }

                            drawPath(
                                path = maPath,
                                color = GoldAmber,
                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        // 4. Interactive Crosshair Overlay
                        selectedCandleIndex?.let { selIdx ->
                            if (selIdx in 0 until totalCandles) {
                                val selCandle = activeCandles[selIdx]
                                val crossX = (selIdx * candleStep) + (candleStep / 2f)
                                val crossY = priceChartH - ((selCandle.close - minPrice) / priceRange * priceChartH)

                                // Vertical crosshair dashed line
                                drawLine(
                                    color = TechCyan.copy(alpha = 0.75f),
                                    start = Offset(crossX, 0f),
                                    end = Offset(crossX, h),
                                    strokeWidth = 1.2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                                )

                                // Horizontal crosshair dashed line
                                drawLine(
                                    color = TechCyan.copy(alpha = 0.75f),
                                    start = Offset(0f, crossY),
                                    end = Offset(w, crossY),
                                    strokeWidth = 1.2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                                )

                                // Center highlight dot
                                drawCircle(
                                    color = TechCyan,
                                    radius = 4.dp.toPx(),
                                    center = Offset(crossX, crossY)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 2.dp.toPx(),
                                    center = Offset(crossX, crossY)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // X-Axis Time Labels Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val step = maxOf(1, activeCandles.size / 5)
                for (i in 0 until activeCandles.size step step) {
                    Text(
                        text = activeCandles[i].timeLabel,
                        fontSize = 9.sp,
                        color = TextTertiary
                    )
                }
                if (activeCandles.isNotEmpty()) {
                    Text(
                        text = activeCandles.last().timeLabel,
                        fontSize = 9.sp,
                        color = TextTertiary
                    )
                }
            }

            // RSI (Relative Strength Index) Dedicated Oscillator Sub-pane
            AnimatedVisibility(visible = showRsi) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    // RSI Status Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "RSI (14)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA855F7)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            currentRsi?.let { rsiVal ->
                                Text(
                                    text = String.format("%.1f", rsiVal),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when {
                                        rsiVal >= 70f -> BearRed
                                        rsiVal <= 30f -> BullGreen
                                        else -> TextPrimary
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when {
                                        rsiVal >= 70f -> if (isGujarati) "🚨 અતિ તેજી / Overbought (≥70)" else "🚨 Overbought Zone (≥70)"
                                        rsiVal <= 30f -> if (isGujarati) "🟢 અતિ મંદી / Oversold (≤30)" else "🟢 Oversold Zone (≤30)"
                                        else -> if (isGujarati) "સામાન્ય રેન્જ (30-70)" else "Normal Neutral Zone"
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        rsiVal >= 70f -> BearRed
                                        rsiVal <= 30f -> BullGreen
                                        else -> TextTertiary
                                    }
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "70 OB", fontSize = 8.sp, color = BearRed, fontWeight = FontWeight.Bold)
                            Text(text = "50 MID", fontSize = 8.sp, color = TextTertiary)
                            Text(text = "30 OS", fontSize = 8.sp, color = BullGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // RSI Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(82.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF080D17))
                            .border(0.5.dp, Color(0xFF1E293B), RoundedCornerShape(6.dp))
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(activeCandles) {
                                    detectTapGestures { offset ->
                                        val totalCandles = activeCandles.size
                                        if (totalCandles > 0) {
                                            val candleWidth = size.width / totalCandles
                                            selectedCandleIndex = (offset.x / candleWidth).toInt().coerceIn(0, totalCandles - 1)
                                        }
                                    }
                                }
                                .pointerInput(activeCandles) {
                                    detectDragGestures { change, _ ->
                                        change.consume()
                                        val totalCandles = activeCandles.size
                                        if (totalCandles > 0) {
                                            val candleWidth = size.width / totalCandles
                                            selectedCandleIndex = (change.position.x / candleWidth).toInt().coerceIn(0, totalCandles - 1)
                                        }
                                    }
                                }
                        ) {
                            val w = size.width
                            val h = size.height
                            val totalCandles = rsiSeries.size
                            if (totalCandles == 0) return@Canvas

                            val y70 = h - (0.70f * h)
                            val y50 = h - (0.50f * h)
                            val y30 = h - (0.30f * h)

                            // 1. Shaded Overbought zone (70 to 100)
                            drawRect(
                                color = BearRed.copy(alpha = 0.08f),
                                topLeft = Offset(0f, 0f),
                                size = Size(w, y70)
                            )

                            // 2. Shaded Oversold zone (0 to 30)
                            drawRect(
                                color = BullGreen.copy(alpha = 0.08f),
                                topLeft = Offset(0f, y30),
                                size = Size(w, h - y30)
                            )

                            // 3. Threshold Guidelines
                            // Overbought 70 dashed line
                            drawLine(
                                color = BearRed.copy(alpha = 0.55f),
                                start = Offset(0f, y70),
                                end = Offset(w, y70),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                            )
                            // Mid 50 dashed line
                            drawLine(
                                color = Color(0xFF28364E),
                                start = Offset(0f, y50),
                                end = Offset(w, y50),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                            )
                            // Oversold 30 dashed line
                            drawLine(
                                color = BullGreen.copy(alpha = 0.55f),
                                start = Offset(0f, y30),
                                end = Offset(w, y30),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                            )

                            // 4. Plot RSI Curve
                            val candleStep = w / totalCandles
                            val rsiPath = Path()
                            var firstPoint = true

                            for (i in 0 until totalCandles) {
                                val rsiVal = rsiSeries[i].coerceIn(0f, 100f)
                                val x = (i * candleStep) + (candleStep / 2f)
                                val y = h - ((rsiVal / 100f) * h)

                                if (firstPoint) {
                                    rsiPath.moveTo(x, y)
                                    firstPoint = false
                                } else {
                                    rsiPath.lineTo(x, y)
                                }
                            }

                            drawPath(
                                path = rsiPath,
                                color = Color(0xFFA855F7),
                                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // 5. Crosshair synchronization on RSI
                            selectedCandleIndex?.let { selIdx ->
                                if (selIdx in 0 until totalCandles) {
                                    val selRsi = rsiSeries[selIdx].coerceIn(0f, 100f)
                                    val crossX = (selIdx * candleStep) + (candleStep / 2f)
                                    val crossY = h - ((selRsi / 100f) * h)

                                    // Vertical sync crosshair
                                    drawLine(
                                        color = TechCyan.copy(alpha = 0.65f),
                                        start = Offset(crossX, 0f),
                                        end = Offset(crossX, h),
                                        strokeWidth = 1.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                                    )

                                    // RSI point highlight dot
                                    val dotColor = when {
                                        selRsi >= 70f -> BearRed
                                        selRsi <= 30f -> BullGreen
                                        else -> Color(0xFFA855F7)
                                    }
                                    drawCircle(color = dotColor, radius = 4.dp.toPx(), center = Offset(crossX, crossY))
                                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(crossX, crossY))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Legend Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(BullGreen, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isGujarati) "તેજી કેન્ડલ" else "Bullish", fontSize = 10.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.size(8.dp).background(BearRed, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isGujarati) "મંદી કેન્ડલ" else "Bearish", fontSize = 10.sp, color = TextSecondary)
                    if (showMa) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.size(8.dp).background(GoldAmber, CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "MA20", fontSize = 10.sp, color = TextSecondary)
                    }
                    if (showRsi) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFFA855F7), CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "RSI(14)", fontSize = 10.sp, color = TextSecondary)
                    }
                }

                Text(
                    text = if (isGujarati) "સ્ક્રોલ/ડ્રેગ કરીને તપાસો 👆" else "Tap/Drag to Inspect 👆",
                    fontSize = 9.sp,
                    color = TechCyan
                )
            }
        }
    }
}
