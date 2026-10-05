package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StockItem
import com.example.ui.components.CandlestickChart
import com.example.ui.components.CurrencyFormatter
import com.example.ui.components.PctFormatter
import com.example.ui.theme.BearRed
import com.example.ui.theme.BullGreen
import com.example.ui.theme.BullGreenSoft
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.MarketCardBorder
import com.example.ui.theme.MarketDarkBg
import com.example.ui.theme.MarketSurface
import com.example.ui.theme.MarketSurfaceVariant
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockDetailSheet(
    stock: StockItem,
    isGujarati: Boolean,
    aiReport: String?,
    isAiLoading: Boolean,
    onDismiss: () -> Unit,
    onRequestAiAnalysis: () -> Unit,
    onOpenTradeSheet: (StockItem) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MarketSurface,
        dragHandle = null,
        modifier = Modifier.testTag("stock_detail_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Stock Name & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stock.symbol,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MarketSurfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = stock.sector,
                                fontSize = 11.sp,
                                color = TechCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = if (isGujarati) stock.nameGu else stock.nameEn,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Price & Change Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${CurrencyFormatter.format(stock.currentPrice)}",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                val isPositive = stock.change >= 0
                val color = if (isPositive) BullGreen else BearRed
                Text(
                    text = "${if (isPositive) "+" else ""}${CurrencyFormatter.format(stock.change)} (${PctFormatter.format(stock.changePercent)}%)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Candlestick Chart
            Text(
                text = if (isGujarati) "ઇન્ટ્રાડે કેન્ડલસ્ટિક ચાર્ટ & વોલ્યુમ" else "Intraday Candlestick Chart & Volume",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            CandlestickChart(
                candles = stock.candles,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0C121E))
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Technical Indicators 4-Grid
            Text(
                text = if (isGujarati) "ટેકનિકલ સૂચકાંકો (Technical Indicators)" else "Technical Indicators",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IndicatorCard(
                    title = "RSI (14)",
                    value = "${stock.rsi}",
                    subtitle = if (stock.rsi > 60) "Bullish" else "Neutral",
                    color = if (stock.rsi > 60) BullGreen else GoldAmber,
                    modifier = Modifier.weight(1f)
                )
                IndicatorCard(
                    title = "VWAP",
                    value = "₹${stock.vwap}",
                    subtitle = if (stock.currentPrice > stock.vwap) "Above (Bullish)" else "Below",
                    color = if (stock.currentPrice > stock.vwap) BullGreen else BearRed,
                    modifier = Modifier.weight(1f)
                )
                IndicatorCard(
                    title = "Support (S1)",
                    value = "₹${stock.support1}",
                    subtitle = "Pivot: ₹${stock.pivotPoint}",
                    color = BullGreen,
                    modifier = Modifier.weight(1f)
                )
                IndicatorCard(
                    title = "Resist (R1)",
                    value = "₹${stock.resistance1}",
                    subtitle = stock.macdStatus,
                    color = BearRed,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Stock Breakdown Button
            Button(
                onClick = onRequestAiAnalysis,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("request_ai_stock_analysis_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isAiLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isGujarati) "AI વિશ્લેષણ તૈયાર થઈ રહ્યું છે..." else "Generating AI Analysis...",
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isGujarati) "શેરબજાર AI એનાલિસિસ મેળવો (Gemini AI)" else "Get AI Technical Analysis (Gemini)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // AI Generated Report Content
            if (aiReport != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C121E)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TechCyan.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = TechCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isGujarati) "AI રિસર્ચ એનાલિસિસ રિપોર્ટ" else "AI Quantitative Research Report",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TechCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = aiReport,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Simulate Paper Trade Button
            Button(
                onClick = { onOpenTradeSheet(stock) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_paper_trade_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BullGreen,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isGujarati) "આ શેરમાં પેપર ટ્રેડ કરો" else "Simulate Paper Trade",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun IndicatorCard(
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
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 9.sp, color = TextTertiary, maxLines = 1)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1)
            Spacer(modifier = Modifier.height(1.dp))
            Text(text = subtitle, fontSize = 8.sp, color = TextSecondary, maxLines = 1)
        }
    }
}
