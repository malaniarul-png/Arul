package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockItem
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.MarketSurface
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TechCyanSoft
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun VolatilityHeatmapCard(
    stocks: List<StockItem>,
    isGujarati: Boolean,
    onStockClick: (StockItem) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, HIGH_VOL, GAINERS, LOSERS

    val filteredStocks = remember(stocks, selectedFilter) {
        val list = when (selectedFilter) {
            "HIGH_VOL" -> stocks.filter { stock ->
                val fluctuation = if (stock.dayLow > 0) ((stock.dayHigh - stock.dayLow) / stock.dayLow) * 100.0 else 0.0
                fluctuation >= 1.8
            }.sortedByDescending { if (it.dayLow > 0) ((it.dayHigh - it.dayLow) / it.dayLow) else 0.0 }
            "GAINERS" -> stocks.filter { it.changePercent > 0 }.sortedByDescending { it.changePercent }
            "LOSERS" -> stocks.filter { it.changePercent < 0 }.sortedBy { it.changePercent }
            else -> stocks
        }
        list.take(16) // Display top 16 active Nifty 50 stocks in grid
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .testTag("volatility_heatmap_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF261D00)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = GoldAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isGujarati) "વોલેટિલિટી હીટમેપ (Volatility Heatmap)" else "Intraday Volatility Heatmap",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isGujarati) "નિફ્ટી ૫૦ શેરોમાં ભાવની અસ્થિરતા & ફ્લકચ્યુએશન" else "Color-coded Intraday Price Swings (Nifty 50)",
                            fontSize = 11.sp,
                            color = GoldAmber
                        )
                    }
                }

                Surface(
                    color = GoldAmber.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = GoldAmber,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "India VIX: 13.4",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAmber
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text(if (isGujarati) "બધા શેરો" else "All Stocks", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TechCyan,
                        selectedLabelColor = Color.Black,
                        containerColor = Color(0xFF0C121E),
                        labelColor = TextSecondary
                    ),
                    modifier = Modifier.height(28.dp)
                )

                FilterChip(
                    selected = selectedFilter == "HIGH_VOL",
                    onClick = { selectedFilter = "HIGH_VOL" },
                    label = { Text(if (isGujarati) "હાઇ વોલેટિલિટી ⚡" else "High Vol ⚡", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldAmber,
                        selectedLabelColor = Color.Black,
                        containerColor = Color(0xFF0C121E),
                        labelColor = TextSecondary
                    ),
                    modifier = Modifier.height(28.dp)
                )

                FilterChip(
                    selected = selectedFilter == "GAINERS",
                    onClick = { selectedFilter = "GAINERS" },
                    label = { Text(if (isGujarati) "તેજી વાળા" else "Gainers", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BullGreen,
                        selectedLabelColor = Color.Black,
                        containerColor = Color(0xFF0C121E),
                        labelColor = TextSecondary
                    ),
                    modifier = Modifier.height(28.dp)
                )

                FilterChip(
                    selected = selectedFilter == "LOSERS",
                    onClick = { selectedFilter = "LOSERS" },
                    label = { Text(if (isGujarati) "મંદી વાળા" else "Losers", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BearRed,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF0C121E),
                        labelColor = TextSecondary
                    ),
                    modifier = Modifier.height(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Color Intensity Heatmap Grid (4 Columns x 4 Rows = 16 Tiles)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val chunkedStocks = filteredStocks.chunked(4)
                chunkedStocks.forEach { rowStocks ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowStocks.forEach { stock ->
                            Box(modifier = Modifier.weight(1f)) {
                                VolatilityHeatmapTile(
                                    stock = stock,
                                    onClick = { onStockClick(stock) }
                                )
                            }
                        }
                        // Padding empty spaces if row has fewer than 4 items
                        if (rowStocks.size < 4) {
                            for (i in 0 until (4 - rowStocks.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Heatmap Intensity Scale Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0C121E))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isGujarati) "તીવ્ર મંદી (-3%)" else "Bearish (-3%)",
                    fontSize = 9.sp,
                    color = Color(0xFFFF1744),
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Box(modifier = Modifier.size(14.dp, 8.dp).background(Color(0xFF8B0000)))
                    Box(modifier = Modifier.size(14.dp, 8.dp).background(Color(0xFFB71C1C)))
                    Box(modifier = Modifier.size(14.dp, 8.dp).background(Color(0xFF421010)))
                    Box(modifier = Modifier.size(14.dp, 8.dp).background(Color(0xFF1C2534)))
                    Box(modifier = Modifier.size(14.dp, 8.dp).background(Color(0xFF0A3319)))
                    Box(modifier = Modifier.size(14.dp, 8.dp).background(Color(0xFF1B5E20)))
                    Box(modifier = Modifier.size(14.dp, 8.dp).background(Color(0xFF00E676)))
                }

                Text(
                    text = if (isGujarati) "તીવ્ર તેજી (+3%)" else "Bullish (+3%)",
                    fontSize = 9.sp,
                    color = Color(0xFF00E676),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun VolatilityHeatmapTile(
    stock: StockItem,
    onClick: () -> Unit
) {
    val fluctuation = if (stock.dayLow > 0) ((stock.dayHigh - stock.dayLow) / stock.dayLow) * 100.0 else 0.0

    // Compute Heatmap tile background color based on % change and volatility intensity
    val tileBgColor = when {
        stock.changePercent >= 2.5 -> Color(0xFF00E676).copy(alpha = 0.85f)
        stock.changePercent in 1.2..2.5 -> Color(0xFF2E7D32)
        stock.changePercent in 0.3..1.2 -> Color(0xFF1B4323)
        stock.changePercent in -0.3..0.3 -> Color(0xFF1E2838)
        stock.changePercent in -1.2..-0.3 -> Color(0xFF4A1818)
        stock.changePercent in -2.5..-1.2 -> Color(0xFFB71C1C)
        else -> Color(0xFFFF1744).copy(alpha = 0.85f)
    }

    val textColor = if (stock.changePercent >= 2.5 || stock.changePercent <= -2.5) Color.White else TextPrimary
    val isHighVol = fluctuation >= 2.0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(tileBgColor)
            .border(
                width = if (isHighVol) 1.dp else 0.5.dp,
                color = if (isHighVol) GoldAmber.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stock.symbol,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isHighVol) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "⚡", fontSize = 7.sp)
                }
            }

            Text(
                text = "${if (stock.changePercent >= 0) "+" else ""}${String.format("%.1f", stock.changePercent)}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = textColor
            )

            Text(
                text = "₹${stock.currentPrice.toInt()}",
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                color = textColor.copy(alpha = 0.85f)
            )
        }
    }
}
