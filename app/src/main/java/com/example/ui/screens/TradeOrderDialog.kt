package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.StockItem
import com.example.ui.components.CurrencyFormatter
import com.example.ui.theme.BearRed
import com.example.ui.theme.BearRedSoft
import com.example.ui.theme.BullGreen
import com.example.ui.theme.BullGreenSoft
import com.example.ui.theme.MarketCardBorder
import com.example.ui.theme.MarketSurface
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun TradeOrderDialog(
    stock: StockItem,
    isGujarati: Boolean,
    onDismiss: () -> Unit,
    onExecuteTrade: (StockItem, Boolean, Int, Double, Double) -> Unit
) {
    var isBuy by remember { mutableStateOf(true) }
    var quantityText by remember { mutableStateOf("10") }
    var targetText by remember {
        val t = if (isBuy) (stock.currentPrice * 1.02) else (stock.currentPrice * 0.98)
        mutableStateOf("${(t * 10).roundToInt() / 10.0}")
    }
    var stopLossText by remember {
        val sl = if (isBuy) (stock.currentPrice * 0.99) else (stock.currentPrice * 1.01)
        mutableStateOf("${(sl * 10).roundToInt() / 10.0}")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MarketSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MarketCardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("trade_order_dialog")
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "${if (isGujarati) "પેપર ટ્રેડ ઓર્ડર" else "Simulated Order"}: ${stock.symbol}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "LTP: ₹${CurrencyFormatter.format(stock.currentPrice)}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Buy vs Sell Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = isBuy,
                        onClick = { isBuy = true },
                        label = { Text("BUY (ખરીદી)", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BullGreen,
                            selectedLabelColor = Color.Black,
                            containerColor = Color(0xFF0C121E),
                            labelColor = BullGreen
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = !isBuy,
                        onClick = { isBuy = false },
                        label = { Text("SELL (વેચાણ)", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BearRed,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF0C121E),
                            labelColor = BearRed
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quantity Input
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text(if (isGujarati) "શેર સંખ્યા (Quantity)" else "Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TechCyan,
                        unfocusedBorderColor = MarketCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Target Price Input
                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text(if (isGujarati) "ટાર્ગેટ ભાવ (Target ₹)" else "Target Price ₹") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BullGreen,
                        unfocusedBorderColor = MarketCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stop Loss Price Input
                OutlinedTextField(
                    value = stopLossText,
                    onValueChange = { stopLossText = it },
                    label = { Text(if (isGujarati) "સ્ટોપ લોસ (Stop Loss ₹)" else "Stop Loss Price ₹") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = BearRed,
                        unfocusedBorderColor = MarketCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2838))
                    ) {
                        Text(text = if (isGujarati) "રદ કરો" else "Cancel", color = TextSecondary)
                    }

                    val qty = quantityText.toIntOrNull() ?: 1
                    val target = targetText.toDoubleOrNull() ?: (stock.currentPrice * 1.02)
                    val sl = stopLossText.toDoubleOrNull() ?: (stock.currentPrice * 0.99)
                    val actionColor = if (isBuy) BullGreen else BearRed

                    Button(
                        onClick = {
                            onExecuteTrade(stock, isBuy, qty, target, sl)
                        },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = actionColor,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = if (isBuy) (if (isGujarati) "ઓર્ડર લો (BUY)" else "Buy Order") else (if (isGujarati) "ઓર્ડર લો (SELL)" else "Sell Order"),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
