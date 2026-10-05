package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BuildupType
import com.example.data.model.CandleData
import com.example.data.model.MarketIndex
import com.example.ui.theme.AppTheme
import com.example.ui.theme.BearRed
import com.example.ui.theme.BearRedSoft
import com.example.ui.theme.BullGreen
import com.example.ui.theme.BullGreenSoft
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.MarketCardBorder
import com.example.ui.theme.MarketSurface
import com.example.ui.theme.MarketSurfaceVariant
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AppLanguage
import com.example.util.AppLocalization
import java.text.DecimalFormat

val CurrencyFormatter = DecimalFormat("#,##0.00")
val PctFormatter = DecimalFormat("+0.00;-0.00")

@Composable
fun TopMarketHeader(
    indices: List<MarketIndex>,
    isGujarati: Boolean = true,
    currentLanguage: AppLanguage = AppLanguage.GUJARATI,
    isDarkMode: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onToggleLanguage: () -> Unit = {},
    onSelectLanguage: (AppLanguage) -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    var showLanguageMenu by remember { mutableStateOf(false) }
    val colors = AppTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .border(width = 1.dp, color = colors.border)
    ) {
        // Main App Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(colors.cyan, colors.green)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "AI Agent",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = AppLocalization.appName(currentLanguage),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Live Pulse Dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(colors.green.copy(alpha = pulseAlpha))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = AppLocalization.live(currentLanguage),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.green
                        )
                    }
                    Text(
                        text = AppLocalization.tagline(currentLanguage),
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }
            }

            // Action Buttons: Theme Toggle & Language Dropdown
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Theme Toggle Button (Light/Dark Mode)
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(colors.surfaceVariant)
                        .testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkMode) "Light Mode" else "Dark Mode",
                        tint = if (isDarkMode) GoldAmber else Color(0xFF6366F1),
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Language Selector Button with Dropdown Menu
                Box {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = colors.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, colors.border),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { showLanguageMenu = true }
                            .testTag("lang_toggle_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = colors.cyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentLanguage.nativeName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.cyan
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showLanguageMenu,
                        onDismissRequest = { showLanguageMenu = false },
                        modifier = Modifier.background(colors.surface)
                    ) {
                        AppLanguage.values().forEach { lang ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(lang.flagEmoji, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = lang.nativeName,
                                                fontSize = 13.sp,
                                                fontWeight = if (lang == currentLanguage) FontWeight.Bold else FontWeight.Normal,
                                                color = if (lang == currentLanguage) colors.cyan else colors.textPrimary
                                            )
                                            Text(
                                                text = lang.displayName,
                                                fontSize = 10.sp,
                                                color = colors.textTertiary
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    onSelectLanguage(lang)
                                    showLanguageMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Live Ticker Marquee Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0C121E))
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            indices.forEach { index ->
                val isPositive = index.change >= 0
                val color = if (isPositive) BullGreen else BearRed

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 18.dp)
                ) {
                    Text(
                        text = if (isGujarati) index.nameGu else index.nameEn,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = CurrencyFormatter.format(index.currentPrice),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${PctFormatter.format(index.changePercent)}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = color
                    )
                }
            }
        }
    }
}

@Composable
fun SparklineCanvas(
    points: List<Float>,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    val lineColor = if (isPositive) BullGreen else BearRed
    val fillGradient = Brush.verticalGradient(
        colors = listOf(
            lineColor.copy(alpha = 0.28f),
            Color.Transparent
        )
    )

    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas
        val min = points.minOrNull() ?: 0f
        val max = points.maxOrNull() ?: 1f
        val range = if (max - min == 0f) 1f else max - min

        val w = size.width
        val h = size.height
        val stepX = w / (points.size - 1)

        val path = Path()
        val fillPath = Path()

        points.forEachIndexed { i, p ->
            val x = i * stepX
            val y = h - ((p - min) / range * h * 0.85f) - (h * 0.05f)
            if (i == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, h)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
            if (i == points.size - 1) {
                fillPath.lineTo(x, h)
                fillPath.close()
            }
        }

        drawPath(fillPath, brush = fillGradient)
        drawPath(path, color = lineColor, style = Stroke(width = 2.2f, cap = StrokeCap.Round))
    }
}

@Composable
fun CandlestickChart(
    candles: List<CandleData>,
    modifier: Modifier = Modifier
) {
    if (candles.isEmpty()) return

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val minPrice = candles.minOfOrNull { it.low } ?: 0f
            val maxPrice = candles.maxOfOrNull { it.high } ?: 1f
            val range = if (maxPrice - minPrice == 0f) 1f else maxPrice - minPrice

            val maxVolume = candles.maxOfOrNull { it.volume } ?: 1L

            val w = size.width
            val h = size.height
            val chartH = h * 0.75f
            val volH = h * 0.22f
            val volYStart = h

            val barSpacing = w / candles.size
            val candleBodyWidth = barSpacing * 0.55f

            // Draw horizontal grid lines
            for (g in 1..3) {
                val gridY = (chartH / 4) * g
                drawLine(
                    color = Color(0xFF223048),
                    start = Offset(0f, gridY),
                    end = Offset(w, gridY),
                    strokeWidth = 1f
                )
            }

            candles.forEachIndexed { i, c ->
                val centerX = i * barSpacing + (barSpacing / 2)
                val isBull = c.close >= c.open
                val color = if (isBull) BullGreen else BearRed

                // Price to Y coords
                val highY = chartH - ((c.high - minPrice) / range * chartH)
                val lowY = chartH - ((c.low - minPrice) / range * chartH)
                val openY = chartH - ((c.open - minPrice) / range * chartH)
                val closeY = chartH - ((c.close - minPrice) / range * chartH)

                val bodyTop = minOf(openY, closeY)
                val bodyBottom = maxOf(openY, closeY)
                val bodyHeight = maxOf(2f, bodyBottom - bodyTop)

                // Draw Wick
                drawLine(
                    color = color,
                    start = Offset(centerX, highY),
                    end = Offset(centerX, lowY),
                    strokeWidth = 1.8f
                )

                // Draw Body
                drawRect(
                    color = color,
                    topLeft = Offset(centerX - candleBodyWidth / 2, bodyTop),
                    size = Size(candleBodyWidth, bodyHeight)
                )

                // Volume Bar
                val barVolHeight = (c.volume.toFloat() / maxVolume) * volH
                drawRect(
                    color = color.copy(alpha = 0.45f),
                    topLeft = Offset(centerX - candleBodyWidth / 2, volYStart - barVolHeight),
                    size = Size(candleBodyWidth, barVolHeight)
                )
            }
        }

        // Time labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            candles.forEach { c ->
                Text(
                    text = c.timeLabel,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun PriceChangeBadge(changePercent: Double) {
    val isPositive = changePercent >= 0
    val color = if (isPositive) BullGreen else BearRed
    val bgColor = if (isPositive) BullGreenSoft else BearRedSoft

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "${PctFormatter.format(changePercent)}%",
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BuildupBadge(buildup: BuildupType, isGujarati: Boolean) {
    val color = if (buildup.isBullish) BullGreen else BearRed
    val bg = if (buildup.isBullish) BullGreenSoft else BearRedSoft

    Surface(
        color = bg,
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = if (isGujarati) buildup.labelGu else buildup.labelEn,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

fun BorderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)
