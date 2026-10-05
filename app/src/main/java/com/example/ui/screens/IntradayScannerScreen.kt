package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IntradaySignal
import com.example.data.model.SignalType
import com.example.data.model.StockItem
import com.example.ui.components.CurrencyFormatter
import com.example.ui.theme.BearRed
import com.example.ui.theme.BearRedSoft
import com.example.ui.theme.BullGreen
import com.example.ui.theme.BullGreenSoft
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.MarketCardBorder
import com.example.ui.theme.MarketDarkBg
import com.example.ui.theme.MarketSurface
import com.example.ui.theme.MarketSurfaceVariant
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TechCyanSoft
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun IntradayScannerScreen(
    signals: List<IntradaySignal>,
    stocks: List<StockItem>,
    selectedFilter: String,
    isGujarati: Boolean,
    onFilterChange: (String) -> Unit,
    onStockClick: (StockItem) -> Unit,
    onTradeClick: (StockItem) -> Unit
) {
    val filters = listOf(
        "ALL" to (if (isGujarati) "બધા સિગ્નલ" else "All Signals"),
        "BUY" to (if (isGujarati) "માત્ર ખરીદી (Buy)" else "Bullish Only"),
        "SELL" to (if (isGujarati) "માત્ર વેચાણ (Short)" else "Bearish Only"),
        "VOLUME" to (if (isGujarati) "વોલ્યુમ શોકર્સ" else "Volume Surge")
    )

    val displayedSignals = signals.filter { signal ->
        when (selectedFilter) {
            "BUY" -> signal.signalType.isBullish
            "SELL" -> !signal.signalType.isBullish
            "VOLUME" -> signal.signalType == SignalType.VOLUME_SPIKE
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MarketDarkBg),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            // Header Info Bar
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = TechCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isGujarati) "ઇન્ટ્રાડે AI સ્કેનર & બ્રેકઆઉટ" else "Intraday AI Breakout Scanner",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isGujarati)
                        "રિયલ-ટાઇમ ૧૫-મિનિટ ORB બ્રેકઆઉટ, VWAP ક્રોસ અને વોલ્યુમ સર્જ આધારિત સચોટ સેટઅપ."
                    else
                        "Real-time institutional setups triggered by 15-min ORB, VWAP crosses, and volume spikes.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { (key, label) ->
                    val isSelected = selectedFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { onFilterChange(key) },
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TechCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = MarketSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) TechCyan else MarketCardBorder,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (displayedSignals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isGujarati) "હાલમાં આ ફિલ્ટરમાં કોઈ સક્રિય સિગ્નલ નથી" else "No active breakout signals matching filter",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        items(displayedSignals) { signal ->
            val matchedStock = stocks.find { it.symbol == signal.stockSymbol }
            IntradaySignalCard(
                signal = signal,
                stock = matchedStock,
                isGujarati = isGujarati,
                onViewChart = { matchedStock?.let { onStockClick(it) } },
                onTrade = { matchedStock?.let { onTradeClick(it) } }
            )
        }
    }
}

@Composable
fun IntradaySignalCard(
    signal: IntradaySignal,
    stock: StockItem?,
    isGujarati: Boolean,
    onViewChart: () -> Unit,
    onTrade: () -> Unit
) {
    val isBull = signal.signalType.isBullish
    val accentColor = if (isBull) BullGreen else BearRed
    val bgTint = if (isBull) BullGreenSoft else BearRedSoft

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .clip(RoundedCornerShape(12.dp))
            .testTag("signal_card_${signal.stockSymbol}"),
        colors = CardDefaults.cardColors(containerColor = MarketSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Signal Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = signal.stockSymbol,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = bgTint,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (isGujarati) signal.signalType.labelGu else signal.signalType.labelEn,
                            color = accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    color = MarketSurfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "AI Confidence: ${signal.confidence}%",
                        color = TechCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Trading Levels Matrix (Entry, Target 1, Target 2, SL)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0C121E))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isGujarati) "એન્ટ્રી ભાવ" else "Entry",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "₹${CurrencyFormatter.format(signal.entryPrice)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isGujarati) "ટાર્ગેટ ૧" else "Target 1",
                        fontSize = 10.sp,
                        color = BullGreen
                    )
                    Text(
                        text = "₹${CurrencyFormatter.format(signal.target1)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BullGreen
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isGujarati) "ટાર્ગેટ ૨" else "Target 2",
                        fontSize = 10.sp,
                        color = BullGreen
                    )
                    Text(
                        text = "₹${CurrencyFormatter.format(signal.target2)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BullGreen
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isGujarati) "સ્ટોપ લોસ" else "Stop Loss",
                        fontSize = 10.sp,
                        color = BearRed
                    )
                    Text(
                        text = "₹${CurrencyFormatter.format(signal.stopLoss)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = BearRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // AI Logic & Rationale
            Text(
                text = if (isGujarati) "🧠 AI તર્ક: ${signal.rationaleGu}" else "🧠 AI Rationale: ${signal.rationaleEn}",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Risk:Reward: ${signal.riskReward}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAmber
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "• ${signal.timestamp}",
                    fontSize = 11.sp,
                    color = TextTertiary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewChart,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TechCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TechCyan)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isGujarati) "ચાર્ટ જુઓ" else "Chart", fontSize = 12.sp)
                }

                Button(
                    onClick = onTrade,
                    modifier = Modifier.weight(1.3f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isGujarati) "પેપર ટ્રેડ કરો" else "Simulate Trade",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
