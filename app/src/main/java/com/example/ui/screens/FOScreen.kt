package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QueryStats
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FOAnalysisData
import com.example.data.model.OptionChainRow
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
fun FOScreen(
    niftyFO: FOAnalysisData?,
    bankNiftyFO: FOAnalysisData?,
    isGujarati: Boolean
) {
    var selectedIndex by remember { mutableStateOf("NIFTY") }
    val currentData = if (selectedIndex == "NIFTY") niftyFO else bankNiftyFO

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MarketDarkBg),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            // Screen Header
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QueryStats,
                        contentDescription = null,
                        tint = TechCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isGujarati) "ફ્યુચર્સ & ઓપ્શન્સ (F&O) એનાલિસિસ" else "Futures & Options (F&O) Analysis",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isGujarati)
                        "ઓપ્શન ચેઇન, પુટ-કોલ રેશિયો (PCR), ઓપન ઇન્ટરેસ્ટ (OI) અને મેક્સ પેઇન સ્ટ્રાઇકનું સચોટ વિશ્લેષણ."
                    else
                        "Live option chain, Put-Call Ratio (PCR), Open Interest (OI) buildup, and Max Pain levels.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // Index Switcher Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = selectedIndex == "NIFTY",
                    onClick = { selectedIndex = "NIFTY" },
                    label = { Text("NIFTY 50", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TechCyan,
                        selectedLabelColor = Color.Black,
                        containerColor = MarketSurface,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedIndex == "BANKNIFTY",
                    onClick = { selectedIndex = "BANKNIFTY" },
                    label = { Text("BANK NIFTY", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TechCyan,
                        selectedLabelColor = Color.Black,
                        containerColor = MarketSurface,
                        labelColor = TextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        if (currentData != null) {
            // Derivatives Highlights Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
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
                            Column {
                                Text(
                                    text = "${currentData.underlyingIndex} ${if (isGujarati) "સ્પોટ ભાવ" else "Spot"}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = CurrencyFormatter.format(currentData.spotPrice),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "PCR (Put/Call)",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Surface(
                                    color = if (currentData.pcr >= 1.0) BullGreenSoft else BearRedSoft,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${currentData.pcr} (${if (isGujarati) currentData.pcrSentimentGu else currentData.pcrSentimentEn})",
                                        color = if (currentData.pcr >= 1.0) BullGreen else BearRed,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Key Levels Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0C121E))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = if (isGujarati) "મેક્સ પેઇન" else "Max Pain", fontSize = 10.sp, color = TextSecondary)
                                Text(
                                    text = "${currentData.maxPain.toInt()}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAmber
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = if (isGujarati) "મહત્વનો સપોર્ટ (PE)" else "Major Support", fontSize = 10.sp, color = BullGreen)
                                Text(
                                    text = "${currentData.highestPutStrike.toInt()}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BullGreen
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = if (isGujarati) "મહત્વનો રેઝિસ્ટન્સ (CE)" else "Major Resist", fontSize = 10.sp, color = BearRed)
                                Text(
                                    text = "${currentData.highestCallStrike.toInt()}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BearRed
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // OI Balance Bar
                        val totalOi = currentData.totalCallOi + currentData.totalPutOi
                        val putOiFraction = if (totalOi > 0) currentData.totalPutOi.toFloat() / totalOi else 0.5f

                        Text(
                            text = if (isGujarati) "ઓપન ઇન્ટરેસ્ટ (OI) સંતુલન:" else "Open Interest (OI) Distribution:",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(BearRed)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(putOiFraction)
                                    .height(8.dp)
                                    .background(BullGreen)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Put OI (Bulls): ${(currentData.totalPutOi / 1000)}k",
                                fontSize = 10.sp,
                                color = BullGreen
                            )
                            Text(
                                text = "Call OI (Bears): ${(currentData.totalCallOi / 1000)}k",
                                fontSize = 10.sp,
                                color = BearRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                // Option Chain Table Header
                Text(
                    text = if (isGujarati) "ઓપ્શન ચેઇન (Option Chain)" else "Live Option Chain",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                // Table Columns Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(Color(0xFF141D2E))
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CALL OI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BearRed,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "CALL LTP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BearRed,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "STRIKE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TechCyan,
                        modifier = Modifier.weight(1.1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "PUT LTP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BullGreen,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "PUT OI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BullGreen,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Option Chain Rows
            items(currentData.optionChain) { row ->
                OptionChainTableRow(row = row)
            }
        }
    }
}

@Composable
fun OptionChainTableRow(row: OptionChainRow) {
    val bg = if (row.isAtm) Color(0xFF1F2B42) else MarketSurface
    val borderModifier = if (row.isAtm) Modifier.border(1.dp, TechCyan.copy(alpha = 0.5f)) else Modifier

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .then(borderModifier)
            .background(bg)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${(row.callOi / 1000)}k",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "₹${row.callLtp}",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        // Strike Cell
        Surface(
            color = if (row.isAtm) TechCyanSoft else Color.Transparent,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.weight(1.1f)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${row.strikePrice.toInt()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (row.isAtm) TechCyan else TextPrimary,
                    textAlign = TextAlign.Center
                )
                if (row.isAtm) {
                    Text(
                        text = "ATM",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechCyan
                    )
                }
            }
        }
        Text(
            text = "₹${row.putLtp}",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "${(row.putOi / 1000)}k",
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}
