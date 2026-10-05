package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PcrAlertSettings
import com.example.data.model.PcrAlertType
import com.example.data.model.PcrNotificationEvent
import com.example.ui.theme.BearRed
import com.example.ui.theme.BearRedSoft
import com.example.ui.theme.BullGreen
import com.example.ui.theme.BullGreenSoft
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TechCyanSoft
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun PcrNotificationTriggerCard(
    currentPcr: Double,
    settings: PcrAlertSettings,
    activeAlert: PcrNotificationEvent?,
    isGujarati: Boolean,
    onToggleEnabled: (Boolean) -> Unit,
    onUpdateThresholds: (overbought: Double, oversold: Double) -> Unit,
    onDismissAlert: () -> Unit,
    onSimulateAlert: (PcrAlertType) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("pcr_notification_trigger_card")
    ) {
        // Active In-App Notification Banner if threshold is breached
        AnimatedVisibility(
            visible = activeAlert != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            activeAlert?.let { alert ->
                val isOverbought = alert.alertType == PcrAlertType.OVERBOUGHT
                val bannerBorder = if (isOverbought) BearRed else BullGreen
                val bannerBg = if (isOverbought) BearRedSoft else BullGreenSoft

                Surface(
                    color = bannerBg,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, bannerBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = bannerBorder,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isGujarati) alert.titleGu else alert.titleEn,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isGujarati) alert.messageGu else alert.messageEn,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "PCR: ${alert.pcrValue} • ${alert.timestamp}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = bannerBorder
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismissAlert,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Main PCR Alert Trigger Control Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAmber.copy(alpha = 0.45f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header with Toggle
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
                                .background(Color(0xFF332700)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = GoldAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isGujarati) "PCR નોટિફિકેશન ટ્રિગર સિસ્ટમ" else "PCR Notification Trigger System",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isGujarati) "Overbought / Oversold થ્રેશોલ્ડ મોનિટર" else "Real-time Overbought/Oversold Thresholds",
                                fontSize = 11.sp,
                                color = GoldAmber
                            )
                        }
                    }

                    Switch(
                        checked = settings.isEnabled,
                        onCheckedChange = onToggleEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GoldAmber,
                            checkedTrackColor = Color(0xFF332700),
                            uncheckedThumbColor = TextTertiary,
                            uncheckedTrackColor = Color(0xFF1E2838)
                        ),
                        modifier = Modifier.testTag("pcr_alert_toggle")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Real-time PCR Current Position Gauge Bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0C121E))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isGujarati) "વર્તમાન PCR લેવલ:" else "Current PCR Level:",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = String.format("%.2f", currentPcr),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = when {
                                    currentPcr >= settings.overboughtThreshold -> BearRed
                                    currentPcr <= settings.oversoldThreshold -> BullGreen
                                    else -> GoldAmber
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = when {
                                    currentPcr >= settings.overboughtThreshold -> BearRedSoft
                                    currentPcr <= settings.oversoldThreshold -> BullGreenSoft
                                    else -> Color(0xFF1C283C)
                                },
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = when {
                                        currentPcr >= settings.overboughtThreshold -> if (isGujarati) "અતિ તેજી (Overbought)" else "Overbought"
                                        currentPcr <= settings.oversoldThreshold -> if (isGujarati) "અતિ મંદી (Oversold)" else "Oversold"
                                        else -> if (isGujarati) "સામાન્ય રેન્જ" else "Normal Range"
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        currentPcr >= settings.overboughtThreshold -> BearRed
                                        currentPcr <= settings.oversoldThreshold -> BullGreen
                                        else -> TechCyan
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tri-zone visual gauge bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    ) {
                        // Oversold Zone (0.0 to oversold)
                        Box(
                            modifier = Modifier
                                .weight((settings.oversoldThreshold / 2.0).toFloat())
                                .height(8.dp)
                                .background(BullGreen)
                        )
                        // Normal Zone (oversold to overbought)
                        Box(
                            modifier = Modifier
                                .weight(((settings.overboughtThreshold - settings.oversoldThreshold) / 2.0).toFloat())
                                .height(8.dp)
                                .background(Color(0xFF2E3E56))
                        )
                        // Overbought Zone (overbought to 2.0)
                        Box(
                            modifier = Modifier
                                .weight(((2.0 - settings.overboughtThreshold) / 2.0).toFloat().coerceAtLeast(0.1f))
                                .height(8.dp)
                                .background(BearRed)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Oversold (≤${settings.oversoldThreshold})", fontSize = 8.sp, color = BullGreen)
                        Text(text = "Normal (0.65 - 1.35)", fontSize = 8.sp, color = TextTertiary)
                        Text(text = "Overbought (≥${settings.overboughtThreshold})", fontSize = 8.sp, color = BearRed)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Configurable Threshold Steppers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Overbought Stepper
                    ThresholdStepperBox(
                        title = if (isGujarati) "અતિ તેજી (Overbought)" else "Overbought Limit",
                        value = settings.overboughtThreshold,
                        color = BearRed,
                        modifier = Modifier.weight(1f),
                        onIncrease = {
                            val newOverbought = (settings.overboughtThreshold + 0.05).coerceIn(1.10, 1.80)
                            onUpdateThresholds(newOverbought, settings.oversoldThreshold)
                        },
                        onDecrease = {
                            val newOverbought = (settings.overboughtThreshold - 0.05).coerceIn(1.10, 1.80)
                            onUpdateThresholds(newOverbought, settings.oversoldThreshold)
                        }
                    )

                    // Oversold Stepper
                    ThresholdStepperBox(
                        title = if (isGujarati) "અતિ મંદી (Oversold)" else "Oversold Limit",
                        value = settings.oversoldThreshold,
                        color = BullGreen,
                        modifier = Modifier.weight(1f),
                        onIncrease = {
                            val newOversold = (settings.oversoldThreshold + 0.05).coerceIn(0.40, 0.90)
                            onUpdateThresholds(settings.overboughtThreshold, newOversold)
                        },
                        onDecrease = {
                            val newOversold = (settings.oversoldThreshold - 0.05).coerceIn(0.40, 0.90)
                            onUpdateThresholds(settings.overboughtThreshold, newOversold)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action / Test Notification Triggers Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onSimulateAlert(PcrAlertType.OVERBOUGHT)
                            Toast.makeText(context, if (isGujarati) "🚨 Overbought એલર્ટ ટ્રિગર થયું!" else "🚨 Overbought Alert Triggered!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BearRed),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BearRed.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_overbought_alert_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isGujarati) "ટેસ્ટ Overbought 🚨" else "Test Overbought 🚨",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onSimulateAlert(PcrAlertType.OVERSOLD)
                            Toast.makeText(context, if (isGujarati) "🟢 Oversold એલર્ટ ટ્રિગર થયું!" else "🟢 Oversold Alert Triggered!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BullGreen),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BullGreen.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_oversold_alert_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isGujarati) "ટેસ્ટ Oversold 🟢" else "Test Oversold 🟢",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThresholdStepperBox(
    title: String,
    value: Double,
    color: Color,
    modifier: Modifier = Modifier,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Surface(
        color = Color(0xFF0C121E),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 10.sp, color = TextSecondary, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = onDecrease,
                    modifier = Modifier.size(24.dp)
                ) {
                    Text(text = "-", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = String.format("%.2f", value),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = onIncrease,
                    modifier = Modifier.size(24.dp)
                ) {
                    Text(text = "+", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = color)
                }
            }
        }
    }
}
