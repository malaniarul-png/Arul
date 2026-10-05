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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PaperTradeEntity
import com.example.data.local.WatchlistEntity
import com.example.data.model.StockItem
import com.example.ui.components.CurrencyFormatter
import com.example.ui.components.PctFormatter
import com.example.ui.theme.AppTheme
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun PaperTradingScreen(
    trades: List<PaperTradeEntity>,
    stocks: List<StockItem>,
    watchlist: List<WatchlistEntity>,
    isGujarati: Boolean,
    isLiveTrading: Boolean = false,
    connectedBroker: String = "Zerodha Kite",
    liveMargin: Double = 248500.0,
    onToggleLiveTrading: (Boolean) -> Unit = {},
    onSelectBroker: (String) -> Unit = {},
    onCloseTrade: (PaperTradeEntity) -> Unit,
    onResetTrades: () -> Unit,
    onStockClick: (StockItem) -> Unit,
    onToggleWatchlist: (String) -> Unit
) {
    var selectedSubTab by remember { mutableStateOf("POSITIONS") } // POSITIONS, CLOSED, WATCHLIST
    var showBrokerMenu by remember { mutableStateOf(false) }
    val colors = AppTheme.colors

    val openTrades = trades.filter { it.isOpen }
    val closedTrades = trades.filter { !it.isOpen }

    // Calculate Realized and Unrealized P&L
    val realizedPnL = closedTrades.sumOf { it.pnl }
    val unrealizedPnL = openTrades.sumOf { trade ->
        val currentLtp = stocks.find { it.symbol == trade.symbol }?.currentPrice ?: trade.buyPrice
        if (trade.isBuy) (currentLtp - trade.buyPrice) * trade.quantity
        else (trade.buyPrice - currentLtp) * trade.quantity
    }
    val totalPnL = realizedPnL + unrealizedPnL
    val initialCapital = 1000000.0 // 10 Lakhs
    val currentPortfolioValue = initialCapital + totalPnL

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Mode Switcher: Paper Trading vs Live Broker Trading
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = colors.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, colors.border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Paper Trading Tab
                    Surface(
                        color = if (!isLiveTrading) colors.cyanSoft else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleLiveTrading(false) }
                            .padding(vertical = 8.dp),
                        border = if (!isLiveTrading) androidx.compose.foundation.BorderStroke(1.dp, colors.cyan) else null
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isGujarati) "📊 પેપર ટ્રેડિંગ" else "📊 Paper Trading",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isLiveTrading) colors.cyan else colors.textSecondary
                            )
                            Text(
                                text = "₹10,00,000 વર્ચ્યુઅલ મૂડી",
                                fontSize = 9.sp,
                                color = colors.textTertiary
                            )
                        }
                    }

                    // Live Trading Tab
                    Surface(
                        color = if (isLiveTrading) BullGreenSoft else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleLiveTrading(true) }
                            .padding(vertical = 8.dp),
                        border = if (isLiveTrading) androidx.compose.foundation.BorderStroke(1.dp, BullGreen) else null
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(androidx.compose.foundation.shape.CircleShape).background(BullGreen))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isGujarati) "⚡ લાઈવ ટ્રેડિંગ" else "⚡ Live Trading",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLiveTrading) BullGreen else colors.textSecondary
                                )
                            }
                            Text(
                                text = "બ્રોકર કનેક્શન (API)",
                                fontSize = 9.sp,
                                color = colors.textTertiary
                            )
                        }
                    }
                }
            }
        }

        // Live Trading Terminal Card OR Paper Capital Card
        item {
            if (isLiveTrading) {
                // Live Broker Gateway Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BullGreen.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                        .background(BullGreenSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = null,
                                        tint = BullGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = connectedBroker,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.textPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = BullGreenSoft,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "ACTIVE 🟢",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = BullGreen,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "SEBI Registered Broker Gateway • 100ms Live Ticks",
                                        fontSize = 10.sp,
                                        color = colors.textSecondary
                                    )
                                }
                            }

                            // Switch Broker Button
                            Box {
                                OutlinedButton(
                                    onClick = { showBrokerMenu = true },
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Switch", fontSize = 10.sp, color = colors.cyan)
                                }

                                DropdownMenu(
                                    expanded = showBrokerMenu,
                                    onDismissRequest = { showBrokerMenu = false },
                                    modifier = Modifier.background(colors.surface)
                                ) {
                                    listOf("Zerodha Kite", "Angel One SmartAPI", "Groww", "Upstox Pro", "DhanHQ").forEach { b ->
                                        DropdownMenuItem(
                                            text = { Text(b, fontSize = 12.sp, color = if (b == connectedBroker) colors.cyan else colors.textPrimary) },
                                            onClick = {
                                                onSelectBroker(b)
                                                showBrokerMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Live Margin Details Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.surfaceVariant)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isGujarati) "ઉપલબ્ધ રોકડ માર્જિન" else "Available Cash Margin",
                                    fontSize = 10.sp,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "₹${CurrencyFormatter.format(liveMargin)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = colors.textPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isGujarati) "વપરાયેલ માર્જિન" else "Utilized Margin",
                                    fontSize = 10.sp,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "₹84,250.00",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAmber
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Regulatory Risk Notice
                        Surface(
                            color = colors.surfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isGujarati)
                                    "⚠️ SEBI સાવચેતી: ડેરિવેટિવ્ઝ (F&O) અને ઇન્ટ્રાડે ટ્રેડિંગમાં 90% ટ્રેડર્સ ખોટ કરે છે. યોગ્ય સ્ટોપલોસ સાથે જ ટ્રેડ કરો."
                                else
                                    "⚠️ SEBI Risk Disclosure: 9 out of 10 individual traders in equity F&O incur net losses. Always trade with strict stop-loss.",
                                fontSize = 9.sp,
                                color = colors.textTertiary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            } else {
                // Portfolio Capital Header Card (Paper Mode)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.border)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = colors.cyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isGujarati) "વર્ચ્યુઅલ મૂડી (Virtual Capital)" else "Virtual Trading Capital",
                                    fontSize = 13.sp,
                                    color = colors.textSecondary
                                )
                            }

                            IconButton(
                                onClick = onResetTrades,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reset",
                                    tint = colors.textTertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹${CurrencyFormatter.format(currentPortfolioValue)}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.surfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isGujarati) "કુલ નફો / નુકસાન" else "Total P&L",
                                    fontSize = 11.sp,
                                    color = colors.textSecondary
                                )
                                val pnlColor = if (totalPnL >= 0) BullGreen else BearRed
                                Text(
                                    text = "${if (totalPnL >= 0) "+" else ""}₹${CurrencyFormatter.format(totalPnL)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = pnlColor
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isGujarati) "સક્રિય પોઝિશન્સ" else "Open Trades",
                                    fontSize = 11.sp,
                                    color = colors.textSecondary
                                )
                                Text(
                                    text = "${openTrades.size} ${if (isGujarati) "ટ્રેડ" else "active"}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.cyan
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sub tabs: Open Positions, Closed Trades, Watchlist
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedSubTab == "POSITIONS",
                    onClick = { selectedSubTab = "POSITIONS" },
                    label = {
                        Text(
                            text = "${if (isGujarati) "સક્રિય પોઝિશન" else "Positions"} (${openTrades.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TechCyan,
                        selectedLabelColor = Color.Black,
                        containerColor = MarketSurface,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedSubTab == "CLOSED",
                    onClick = { selectedSubTab = "CLOSED" },
                    label = {
                        Text(
                            text = "${if (isGujarati) "બંધ કરેલ" else "History"} (${closedTrades.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TechCyan,
                        selectedLabelColor = Color.Black,
                        containerColor = MarketSurface,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedSubTab == "WATCHLIST",
                    onClick = { selectedSubTab = "WATCHLIST" },
                    label = {
                        Text(
                            text = "${if (isGujarati) "વોચલિસ્ટ" else "Watchlist"} (${watchlist.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TechCyan,
                        selectedLabelColor = Color.Black,
                        containerColor = MarketSurface,
                        labelColor = TextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Subtab Content
        when (selectedSubTab) {
            "POSITIONS" -> {
                if (openTrades.isEmpty()) {
                    item {
                        EmptyStateBox(
                            text = if (isGujarati)
                                "કોઈ સક્રિય ટ્રેડ નથી. ઇન્ટ્રાડે સ્કેનરમાંથી 'પેપર ટ્રેડ કરો' પર ક્લિક કરી ટ્રેડ લો!"
                            else
                                "No active trades. Tap 'Simulate Trade' from the Intraday Scanner to practice!"
                        )
                    }
                } else {
                    items(openTrades) { trade ->
                        val currentLtp = stocks.find { it.symbol == trade.symbol }?.currentPrice ?: trade.buyPrice
                        val pnl = if (trade.isBuy) (currentLtp - trade.buyPrice) * trade.quantity else (trade.buyPrice - currentLtp) * trade.quantity
                        val pnlPct = if (trade.buyPrice > 0) (pnl / (trade.buyPrice * trade.quantity)) * 100 else 0.0

                        OpenTradeCard(
                            trade = trade,
                            currentLtp = currentLtp,
                            pnl = pnl,
                            pnlPercent = pnlPct,
                            isGujarati = isGujarati,
                            onClose = { onCloseTrade(trade) }
                        )
                    }
                }
            }

            "CLOSED" -> {
                if (closedTrades.isEmpty()) {
                    item {
                        EmptyStateBox(
                            text = if (isGujarati) "કોઈ જૂના ટ્રેડ હિસ્ટ્રી નથી" else "No closed trade history"
                        )
                    }
                } else {
                    items(closedTrades) { trade ->
                        ClosedTradeCard(trade = trade, isGujarati = isGujarati)
                    }
                }
            }

            "WATCHLIST" -> {
                if (watchlist.isEmpty()) {
                    item {
                        EmptyStateBox(
                            text = if (isGujarati)
                                "વોચલિસ્ટ ખાલી છે. ડેશબોર્ડમાંથી શેરની બાજુમાં બુકમાર્ક આઇકોન પર ક્લિક કરો."
                            else
                                "Watchlist is empty. Tap the bookmark icon next to any stock on Dashboard."
                        )
                    }
                } else {
                    val favStocks = stocks.filter { s -> watchlist.any { it.symbol == s.symbol } }
                    items(favStocks) { stock ->
                        StockRowItem(
                            stock = stock,
                            isFavorite = true,
                            isGujarati = isGujarati,
                            onClick = { onStockClick(stock) },
                            onToggleWatchlist = { onToggleWatchlist(stock.symbol) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OpenTradeCard(
    trade: PaperTradeEntity,
    currentLtp: Double,
    pnl: Double,
    pnlPercent: Double,
    isGujarati: Boolean,
    onClose: () -> Unit
) {
    val pnlColor = if (pnl >= 0) BullGreen else BearRed
    val pnlBg = if (pnl >= 0) BullGreenSoft else BearRedSoft

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MarketSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, pnlColor.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = trade.symbol,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = if (trade.isBuy) BullGreenSoft else BearRedSoft,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (trade.isBuy) "BUY (ખરીદી)" else "SELL (શોર્ટ)",
                            color = if (trade.isBuy) BullGreen else BearRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Qty: ${trade.quantity}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    color = pnlBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${if (pnl >= 0) "+" else ""}₹${CurrencyFormatter.format(pnl)} (${PctFormatter.format(pnlPercent)}%)",
                        color = pnlColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${if (isGujarati) "ખરીદી ભાવ" else "Entry"}: ₹${CurrencyFormatter.format(trade.buyPrice)}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "${if (isGujarati) "હાલનો ભાવ" else "LTP"}: ₹${CurrencyFormatter.format(currentLtp)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Target: ₹${CurrencyFormatter.format(trade.targetPrice)}",
                    fontSize = 11.sp,
                    color = BullGreen
                )
                Text(
                    text = "Stop Loss: ₹${CurrencyFormatter.format(trade.stopLossPrice)}",
                    fontSize = 11.sp,
                    color = BearRed
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MarketSurfaceVariant)
            ) {
                Text(
                    text = if (isGujarati) "પોઝિશન સ્ક્વેર ઓફ કરો (Square Off)" else "Close Position",
                    fontSize = 12.sp,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
fun ClosedTradeCard(trade: PaperTradeEntity, isGujarati: Boolean) {
    val pnlColor = if (trade.pnl >= 0) BullGreen else BearRed
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        colors = CardDefaults.cardColors(containerColor = MarketSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MarketCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = trade.symbol, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    text = "${if (trade.isBuy) "BUY" else "SELL"} x ${trade.quantity} @ ₹${trade.buyPrice}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (trade.pnl >= 0) "+" else ""}₹${CurrencyFormatter.format(trade.pnl)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = pnlColor
                )
                Text(
                    text = "Exit: ₹${trade.exitPrice ?: "-"}",
                    fontSize = 11.sp,
                    color = TextTertiary
                )
            }
        }
    }
}

@Composable
fun EmptyStateBox(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
