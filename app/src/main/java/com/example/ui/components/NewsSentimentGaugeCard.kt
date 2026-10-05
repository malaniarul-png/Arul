package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NewsImpact
import com.example.data.model.NewsSentimentResult
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
fun NewsSentimentGaugeCard(
    sentimentResult: NewsSentimentResult,
    isAnalyzing: Boolean,
    isGujarati: Boolean,
    onRefreshSentiment: () -> Unit
) {
    // Smooth animated circular progress
    val animatedProgress by animateFloatAsState(
        targetValue = (sentimentResult.overallScore / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1200),
        label = "market_sentiment_progress"
    )

    // Dynamic color: Green for Bullish (>=60), Amber for Neutral (41-59), Red for Bearish (<=40)
    val targetColor = when {
        sentimentResult.overallScore >= 60 -> BullGreen
        sentimentResult.overallScore <= 40 -> BearRed
        else -> GoldAmber
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 800),
        label = "market_sentiment_color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .testTag("news_sentiment_gauge_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, animatedColor.copy(alpha = 0.45f))
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
                            imageVector = Icons.Default.Newspaper,
                            contentDescription = null,
                            tint = TechCyan,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isGujarati) "માર્કેટ સેન્ટિમેન્ટ (Market Sentiment)" else "Market Sentiment Indicator",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isGujarati) "Gemini AI દૈનિક ન્યૂઝ એનાલિસિસ" else "Gemini AI Daily News Analysis",
                            fontSize = 11.sp,
                            color = TechCyan
                        )
                    }
                }

                IconButton(
                    onClick = onRefreshSentiment,
                    enabled = !isAnalyzing,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("refresh_news_sentiment_button")
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = TechCyan,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = animatedColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Visual 'Market Sentiment' Circular Progress Indicator Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0C121E))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Circular Progress Indicator Gauge
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .testTag("market_sentiment_circular_progress"),
                    contentAlignment = Alignment.Center
                ) {
                    // Track background circle
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(100.dp),
                        color = Color(0xFF1E2838),
                        strokeWidth = 9.dp,
                        trackColor = Color.Transparent
                    )

                    // Active progress ring with animated color and value
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(100.dp),
                        color = animatedColor,
                        strokeWidth = 9.dp,
                        strokeCap = StrokeCap.Round,
                        trackColor = Color.Transparent
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${sentimentResult.overallScore}%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (sentimentResult.isBullish) (if (isGujarati) "તેજી (BULL)" else "BULLISH") else (if (isGujarati) "મંદી (BEAR)" else "BEARISH"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = animatedColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Sentiment Status & Breakdown
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = if (sentimentResult.isBullish) BullGreenSoft else BearRedSoft,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isGujarati) sentimentResult.sentimentLabelGu else sentimentResult.sentimentLabelEn,
                            color = animatedColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isGujarati) sentimentResult.summaryGu else sentimentResult.summaryEn,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Proportion Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF223048))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(sentimentResult.bullishPercentage / 100f)
                                .height(6.dp)
                                .background(BullGreen)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${if (isGujarati) "તેજી" else "Bull"}: ${sentimentResult.bullishPercentage}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BullGreen
                        )
                        Text(
                            text = "${if (isGujarati) "મંદી" else "Bear"}: ${sentimentResult.bearishPercentage}%",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BearRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Actionable Takeaway
            Surface(
                color = TechCyanSoft.copy(alpha = 0.35f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TechCyan.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = TechCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isGujarati) sentimentResult.keyTakeawayGu else sentimentResult.keyTakeawayEn,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Trigger AI Headline Analysis Button
            Button(
                onClick = onRefreshSentiment,
                enabled = !isAnalyzing,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("analyze_headlines_with_gemini_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = animatedColor,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isGujarati) "Gemini AI વિશ્લેષણ કરી રહ્યું છે..." else "Analyzing Headlines with Gemini AI...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGujarati) "સમાચારનું AI સેન્ટિમેન્ટ અપડેટ કરો" else "Update Market Sentiment via Gemini AI",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Analyzed Daily News Headlines
            Text(
                text = if (isGujarati) "વિશ્લેષણ કરાયેલ મુખ્ય સમાચાર (Daily Headlines):" else "Analyzed Daily Headlines:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            sentimentResult.headlines.take(3).forEach { news ->
                NewsRowItem(news = news, isGujarati = isGujarati)
            }
        }
    }
}

@Composable
fun NewsRowItem(news: com.example.data.model.NewsItem, isGujarati: Boolean) {
    val impactColor = when (news.impact) {
        NewsImpact.BULLISH -> BullGreen
        NewsImpact.BEARISH -> BearRed
        NewsImpact.NEUTRAL -> GoldAmber
    }
    val impactBg = when (news.impact) {
        NewsImpact.BULLISH -> BullGreenSoft
        NewsImpact.BEARISH -> BearRedSoft
        NewsImpact.NEUTRAL -> Color(0xFF2B2005)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isGujarati) news.headlineGu else news.headlineEn,
                fontSize = 11.sp,
                color = TextPrimary,
                lineHeight = 15.sp,
                maxLines = 2
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = news.source,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TechCyan
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• ${news.timeAgo}",
                    fontSize = 9.sp,
                    color = TextTertiary
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            color = impactBg,
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = if (isGujarati) news.impact.labelGu else news.impact.labelEn,
                color = impactColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
