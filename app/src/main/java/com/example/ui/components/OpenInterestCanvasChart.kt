package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FOAnalysisData
import com.example.data.model.OptionChainRow
import com.example.ui.theme.BearRed
import com.example.ui.theme.BearRedSoft
import com.example.ui.theme.BullGreen
import com.example.ui.theme.BullGreenSoft
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.MarketCardBorder
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TechCyanSoft
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun OpenInterestCanvasChart(
    niftyFO: FOAnalysisData?,
    bankNiftyFO: FOAnalysisData?,
    isGujarati: Boolean
) {
    var selectedIndex by remember { mutableStateOf("NIFTY") } // NIFTY or BANKNIFTY
    val activeFO = if (selectedIndex == "NIFTY") niftyFO else bankNiftyFO

    val optionChain = activeFO?.optionChain ?: emptyList()
    var selectedStrike by remember(activeFO) {
        mutableStateOf<OptionChainRow?>(optionChain.firstOrNull { it.isAtm } ?: optionChain.firstOrNull())
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .testTag("open_interest_canvas_chart_card"),
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
                            .background(TechCyanSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = TechCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isGujarati) "ઓપન ઇન્ટરેસ્ટ (OI) બિલ્ડઅપ ચાર્ટ" else "Open Interest (OI) Build-up",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isGujarati) "કોલ vs પુટ OI પરથી સપોર્ટ & રેઝિસ્ટન્સ" else "Visual Call vs Put OI Support & Resistance",
                            fontSize = 11.sp,
                            color = TechCyan
                        )
                    }
                }

                // Index Switcher
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilterChip(
                        selected = selectedIndex == "NIFTY",
                        onClick = { selectedIndex = "NIFTY" },
                        label = { Text("NIFTY", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TechCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = Color(0xFF0C121E),
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                    FilterChip(
                        selected = selectedIndex == "BANKNIFTY",
                        onClick = { selectedIndex = "BANKNIFTY" },
                        label = { Text("BANK", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TechCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = Color(0xFF0C121E),
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chart Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(BearRed, RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isGujarati) "Call OI (રેઝિસ્ટન્સ)" else "Call OI (Resistance)",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Box(modifier = Modifier.size(10.dp).background(BullGreen, RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isGujarati) "Put OI (સપોર્ટ)" else "Put OI (Support)",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    color = GoldAmber.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "ATM Spot: ${CurrencyFormatter.format(activeFO?.spotPrice ?: 0.0)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAmber,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Custom Canvas Open Interest Bar Chart
            val maxOi = (optionChain.maxOfOrNull { maxOf(it.callOi, it.putOi) } ?: 100000L).toFloat()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0A0F19))
                    .padding(horizontal = 8.dp, vertical = 12.dp)
                    .testTag("open_interest_canvas_chart")
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                    val w = size.width
                    val h = size.height
                    val n = optionChain.size
                    if (n == 0) return@Canvas

                    val strikeWidth = w / n
                    val barWidth = strikeWidth * 0.36f
                    val chartBottom = h - 22.dp.toPx()

                    // Horizontal grid guidelines
                    val gridLines = 3
                    for (i in 1..gridLines) {
                        val y = (chartBottom / gridLines) * i
                        drawLine(
                            color = Color(0xFF1E2838),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                    }

                    optionChain.forEachIndexed { i, row ->
                        val centerX = (i * strikeWidth) + (strikeWidth / 2)
                        val callBarHeight = if (maxOi > 0) (row.callOi / maxOi) * (chartBottom - 10.dp.toPx()) else 0f
                        val putBarHeight = if (maxOi > 0) (row.putOi / maxOi) * (chartBottom - 10.dp.toPx()) else 0f

                        val isHighestCall = row.strikePrice == activeFO?.highestCallStrike
                        val isHighestPut = row.strikePrice == activeFO?.highestPutStrike

                        // Call OI Bar (Left / Red)
                        drawRoundRect(
                            color = if (isHighestCall) Color(0xFFFF3B30) else BearRed.copy(alpha = 0.85f),
                            topLeft = Offset(centerX - barWidth - 1.dp.toPx(), chartBottom - callBarHeight),
                            size = Size(barWidth, callBarHeight),
                            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                        )

                        // Put OI Bar (Right / Green)
                        drawRoundRect(
                            color = if (isHighestPut) Color(0xFF00E676) else BullGreen.copy(alpha = 0.85f),
                            topLeft = Offset(centerX + 1.dp.toPx(), chartBottom - putBarHeight),
                            size = Size(barWidth, putBarHeight),
                            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
                        )

                        // ATM Marker line if ATM strike
                        if (row.isAtm) {
                            drawCircle(
                                color = GoldAmber,
                                radius = 3.dp.toPx(),
                                center = Offset(centerX, chartBottom + 5.dp.toPx())
                            )
                        }

                        // Resistance [R] or Support [S] indicator flags on top of peak bars
                        if (isHighestCall) {
                            drawCircle(
                                color = BearRed,
                                radius = 4.dp.toPx(),
                                center = Offset(centerX - (barWidth / 2), chartBottom - callBarHeight - 6.dp.toPx())
                            )
                        }
                        if (isHighestPut) {
                            drawCircle(
                                color = BullGreen,
                                radius = 4.dp.toPx(),
                                center = Offset(centerX + (barWidth / 2), chartBottom - putBarHeight - 6.dp.toPx())
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Strike labels row below canvas
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                optionChain.forEach { row ->
                    val isAtm = row.isAtm
                    val isPeakR = row.strikePrice == activeFO?.highestCallStrike
                    val isPeakS = row.strikePrice == activeFO?.highestPutStrike

                    val textColor = when {
                        isPeakR -> BearRed
                        isPeakS -> BullGreen
                        isAtm -> GoldAmber
                        else -> TextTertiary
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedStrike = row }
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = "${row.strikePrice.toInt()}",
                            fontSize = 8.sp,
                            fontWeight = if (isAtm || isPeakR || isPeakS) FontWeight.Bold else FontWeight.Normal,
                            color = textColor
                        )
                        if (isPeakR) {
                            Text(text = "R", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = BearRed)
                        } else if (isPeakS) {
                            Text(text = "S", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = BullGreen)
                        } else if (isAtm) {
                            Text(text = "ATM", fontSize = 7.sp, color = GoldAmber)
                        } else {
                            Text(text = " ", fontSize = 7.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Key Support & Resistance Level Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0C121E))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Major Support Callout
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(8.dp).clip(CircleShape).background(BullGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = if (isGujarati) "મહત્વનો સપોર્ટ (Put Wall)" else "Major Support (Put Wall)",
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "${activeFO?.highestPutStrike?.toInt() ?: 0} PE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BullGreen
                        )
                    }
                }

                // PCR Sentiment
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PCR: ${activeFO?.pcr ?: 1.18}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldAmber
                    )
                    Text(
                        text = if (isGujarati) (activeFO?.pcrSentimentGu ?: "તેજીનું વલણ") else (activeFO?.pcrSentimentEn ?: "Bullish Bias"),
                        fontSize = 9.sp,
                        color = BullGreen
                    )
                }

                // Major Resistance Callout
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (isGujarati) "મહત્વનો રેઝિસ્ટન્સ (Call Wall)" else "Major Resistance (Call Wall)",
                            fontSize = 9.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "${activeFO?.highestCallStrike?.toInt() ?: 0} CE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BearRed
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier.size(8.dp).clip(CircleShape).background(BearRed)
                    )
                }
            }

            // Selected Strike Inspector Detail Bar
            selectedStrike?.let { strike ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF141E30))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Strike: ${strike.strikePrice.toInt()}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TechCyan
                    )
                    Text(
                        text = "Call OI: ${strike.callOi / 1000}K (LTP: ₹${strike.callLtp})",
                        fontSize = 10.sp,
                        color = BearRed
                    )
                    Text(
                        text = "Put OI: ${strike.putOi / 1000}K (LTP: ₹${strike.putLtp})",
                        fontSize = 10.sp,
                        color = BullGreen
                    )
                }
            }
        }
    }
}
