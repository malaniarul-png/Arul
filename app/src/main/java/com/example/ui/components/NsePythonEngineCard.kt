package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BearRed
import com.example.ui.theme.BearRedSoft
import com.example.ui.theme.BullGreen
import com.example.ui.theme.BullGreenSoft
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.MarketCardBorder
import com.example.ui.theme.MarketSurface
import com.example.ui.theme.TechCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun NsePythonEngineCard(isGujarati: Boolean) {
    var selectedTab by remember { mutableStateOf("UNPACK_CODE") } // UNPACK_CODE, PCR_CODE, AUTO_LOOP, DF_VIEW
    val context = LocalContext.current

    val automatedPipelineCode = """
# ============================================================
# NSE Option Chain PCR Notification Trigger System (Every 3 Min)
# ============================================================
import time
import requests
import pandas as pd
from datetime import datetime

# ૧. નોટિફિકેશન થ્રેશોલ્ડ વ્યાખ્યાયિત કરવા (Defined Thresholds)
OVERBOUGHT_THRESHOLD = 1.35  # અતિ તેજી ચેતવણી (સંભવિત રિવર્સલ / નફો બુક કરવો)
OVERSOLD_THRESHOLD = 0.65    # અતિ મંદી ચેતવણી (સંભવિત શોર્ટ કવરિંગ બાઉન્સ)

HEADERS = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    'Accept-Language': 'en-US,en;q=0.9',
    'Accept-Encoding': 'gzip, deflate, br',
    'Referer': 'https://www.nseindia.com/'
}

def send_alert_notification(alert_type, pcr, support, resistance):
    time_str = datetime.now().strftime('%H:%M:%S')
    if alert_type == "OVERBOUGHT":
        title = f"🚨 NIFTY OVERBOUGHT ALERT (PCR: {pcr:.2f} >= {OVERBOUGHT_THRESHOLD})"
        message = f"[{time_str}] બજાર અતિ તેજી ઝોનમાં છે! Major Resistance: {resistance}. લોંગ સોદામાં નફો બુક કરવો."
    else:
        title = f"🟢 NIFTY OVERSOLD ALERT (PCR: {pcr:.2f} <= {OVERSOLD_THRESHOLD})"
        message = f"[{time_str}] બજાર અતિ મંદી ઝોનમાં છે! Major Support: {support}. સપોર્ટથી તીવ્ર શોર્ટ કવરિંગ શક્ય."

    print("\n" + "="*65)
    print(title)
    print(message)
    print("="*65 + "\n")
    # Webhook / Telegram notification call:
    # requests.post(WEBHOOK_URL, json={'title': title, 'text': message})

def run_pcr_notification_monitor(interval_sec=180):
    print("🚀 PCR નોટિફિકેશન મોનિટરિંગ સિસ્ટમ શરૂ થઈ રહી છે...")
    session = requests.Session()
    session.headers.update(HEADERS)
    session.get('https://www.nseindia.com', timeout=10) # Cookies મેળવવી
    
    last_alert_type = None

    while True:
        try:
            url = "https://www.nseindia.com/api/option-chain-indices?symbol=NIFTY"
            resp = session.get(url, timeout=10)
            raw = resp.json()['records']['data']

            # ૨. ડિક્શનરી અનપેકિંગ અને ક્લીન ડેટાફ્રેમ
            rows = []
            for item in raw:
                strike = item.get('strikePrice')
                ce = item.get('CE', {}) if isinstance(item.get('CE'), dict) else {}
                pe = item.get('PE', {}) if isinstance(item.get('PE'), dict) else {}
                rows.append({
                    'Strike': strike,
                    'CE_OI': ce.get('openInterest', 0),
                    'PE_OI': pe.get('openInterest', 0)
                })

            df = pd.DataFrame(rows).dropna(subset=['Strike'])
            tot_ce = df['CE_OI'].sum()
            tot_pe = df['PE_OI'].sum()
            pcr = tot_pe / tot_ce if tot_ce > 0 else 1.0

            support = df.loc[df['PE_OI'].idxmax()]['Strike']
            resistance = df.loc[df['CE_OI'].idxmax()]['Strike']

            now_str = datetime.now().strftime('%H:%M:%S')
            print(f"[{now_str}] NIFTY PCR: {pcr:.2f} | Support: {support} | Resistance: {resistance}")

            # ૩. ઓટોમેટેડ નોટિફિકેશન ટ્રિગર
            if pcr >= OVERBOUGHT_THRESHOLD:
                if last_alert_type != "OVERBOUGHT":
                    send_alert_notification("OVERBOUGHT", pcr, support, resistance)
                    last_alert_type = "OVERBOUGHT"
            elif pcr <= OVERSOLD_THRESHOLD:
                if last_alert_type != "OVERSOLD":
                    send_alert_notification("OVERSOLD", pcr, support, resistance)
                    last_alert_type = "OVERSOLD"
            else:
                last_alert_type = None # નોર્મલ રેન્જમાં રીસેટ કરવું

        except Exception as e:
            print(f"⚠️ કનેક્શન એરર ({e}), નવું સેશન બનાવી રહ્યા છીએ...")
            session = requests.Session()
            session.headers.update(HEADERS)
            session.get('https://www.nseindia.com', timeout=10)

        time.sleep(interval_sec)

if __name__ == '__main__':
    run_pcr_notification_monitor(interval_sec=180)
""".trimIndent()

    val pcrSentimentCode = """
# ============================================================
# NIFTY Current Expiry PCR Calculation & Market Sentiment
# ============================================================
import pandas as pd

# 1. Total Call OI અને Total Put OI નો સરવાળો
total_put_oi = df['PE_OI'].sum()
total_call_oi = df['CE_OI'].sum()

# 2. પ્રમાણિત પુટ-કોલ રેશિયો સૂત્ર:
# PCR (OI) = Total Put Open Interest / Total Call Open Interest
pcr = total_put_oi / total_call_oi

# 3. PCR ના આધારે માર્કેટ સેન્ટિમેન્ટ (બજારનો મૂડ):
if pcr >= 1.35:
    sentiment = "અતિ તેજી / Overbought (સાવચેતી - નફો બુક કરવો)"
    bias = "BEARISH_REVERSAL"
elif 1.05 <= pcr < 1.35:
    sentiment = "મજબૂત તેજીનું વલણ / Bullish (Buy on Dips)"
    bias = "BULLISH"
elif 0.85 <= pcr < 1.05:
    sentiment = "સંતુલિત / Neutral Range-bound (સાઈડવેઝ)"
    bias = "NEUTRAL"
elif 0.65 <= pcr < 0.85:
    sentiment = "મંદીનું દબાણ / Bearish (Sell on Rise)"
    bias = "BEARISH"
else:
    sentiment = "અતિ મંદી / Oversold (શોર્ટ કવરિંગ બાઉન્સ શક્ય)"
    bias = "BULLISH_BOUNCE"

print(f"Total Call OI: {total_call_oi:,} contracts")
print(f"Total Put OI:  {total_put_oi:,} contracts")
print(f"PCR (OI):      {pcr:.2f}")
print(f"Market Sentiment: {sentiment}")
""".trimIndent()

    val dictionaryUnpackCode = """
# ============================================================
# NSE CE & PE Dictionary Unpacking & Support/Resistance Finder
# ============================================================
import pandas as pd
import requests

# 1. Headers & Cookies Handshake to avoid NSE block
headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    'Referer': 'https://www.nseindia.com/'
}
session = requests.Session()
session.get('https://www.nseindia.com', headers=headers, timeout=10)

url = 'https://www.nseindia.com/api/option-chain-indices?symbol=NIFTY'
response = session.get(url, headers=headers, timeout=10)
raw_data = response.json()
records = raw_data['records']['data']

# 2. CE અને PE ડિક્શનરીમાંથી ડેટા કાઢીને અલગ-અલગ કોલમમાં ગોઠવવો
unpacked_rows = []
for row in records:
    strike = row.get('strikePrice')
    # CE અને PE કોલમ્સ ડિક્શનરી (JSON) ફોર્મેટમાં હોય છે
    ce_dict = row.get('CE') if isinstance(row.get('CE'), dict) else {}
    pe_dict = row.get('PE') if isinstance(row.get('PE'), dict) else {}

    unpacked_rows.append({
        # Call (CE) Metrics
        'CE_OI': ce_dict.get('openInterest', 0),
        'CE_Change_OI': ce_dict.get('changeinOpenInterest', 0),
        'CE_Volume': ce_dict.get('totalTradedVolume', 0),
        'CE_IV': ce_dict.get('impliedVolatility', 0.0),
        'CE_LTP': ce_dict.get('lastPrice', 0.0),
        
        # Strike Price
        'Strike_Price': strike,
        
        # Put (PE) Metrics
        'PE_LTP': pe_dict.get('lastPrice', 0.0),
        'PE_IV': pe_dict.get('impliedVolatility', 0.0),
        'PE_Volume': pe_dict.get('totalTradedVolume', 0),
        'PE_Change_OI': pe_dict.get('changeinOpenInterest', 0),
        'PE_OI': pe_dict.get('openInterest', 0)
    })

# 3. સ્વચ્છ અને સુવ્યવસ્થિત Pandas DataFrame
df = pd.DataFrame(unpacked_rows).dropna(subset=['Strike_Price'])

# 4. સપોર્ટ અને રેઝિસ્ટન્સ શોધવું:
# સૌથી વધુ Call OI = Major Resistance (કોલ રાઇટર્સનું દબાણ)
major_resistance = df.loc[df['CE_OI'].idxmax()]['Strike_Price']
max_call_oi = df['CE_OI'].max()

# સૌથી વધુ Put OI = Major Support (પુટ રાઇટર્સનો મજબૂત ટેકો)
major_support = df.loc[df['PE_OI'].idxmax()]['Strike_Price']
max_put_oi = df['PE_OI'].max()

# 5. પુટ-કોલ રેશિયો (PCR) ગણતરી:
pcr = df['PE_OI'].sum() / df['CE_OI'].sum()

print(f"મહત્વનો સપોર્ટ (Support Strike): {major_support} (Put OI: {max_put_oi})")
print(f"મહત્વનો રેઝિસ્ટન્સ (Resistance Strike): {major_resistance} (Call OI: {max_call_oi})")
print(f"Put-Call Ratio (PCR): {pcr:.2f}")
""".trimIndent()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .testTag("nse_python_engine_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAmber.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Title Row
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
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = GoldAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isGujarati) "NSE CE/PE ડિક્શનરી અનપેકિંગ (Python)" else "NSE Option Chain Dictionary Unpacker",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isGujarati) "OI, Volume & LTP ને અલગ કોલમમાં ગોઠવી સપોર્ટ/રેઝિસ્ટન્સ શોધવું" else "Unpack CE/PE dicts into clean columns for Support & Resistance",
                            fontSize = 11.sp,
                            color = GoldAmber
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val codeToCopy = when (selectedTab) {
                            "AUTO_LOOP" -> automatedPipelineCode
                            "PCR_CODE" -> pcrSentimentCode
                            else -> dictionaryUnpackCode
                        }
                        val clip = ClipData.newPlainText("NSE Python Code", codeToCopy)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, if (isGujarati) "પાયથોન કોડ કૉપિ થયો!" else "Python Code Copied!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("copy_python_code_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = GoldAmber,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isGujarati)
                    "NSE ના રો JSON માં CE અને PE ડેટા ડિક્શનરી ફોર્મેટમાં હોય છે. તેને અનપેક કરીને Total Put OI / Total Call OI સૂત્રથી દર 3 મિનિટે રિયલ-ટાઇમ સેન્ટિમેન્ટ ઓટોમેટિક અપડેટ થાય છે."
                else
                    "Unpacks nested CE & PE dictionaries into clean DataFrame columns, calculating PCR every 3 minutes automatically in a scheduled background loop.",
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedTab == "UNPACK_CODE",
                    onClick = { selectedTab = "UNPACK_CODE" },
                    label = { Text(if (isGujarati) "ડિક્શનરી અનપેકિંગ કોડ" else "Unpacking Code", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldAmber,
                        selectedLabelColor = Color.Black,
                        containerColor = Color(0xFF0C121E),
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedTab == "PCR_CODE",
                    onClick = { selectedTab = "PCR_CODE" },
                    label = { Text(if (isGujarati) "PCR સેન્ટિમેન્ટ કોડ" else "PCR Sentiment Code", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldAmber,
                        selectedLabelColor = Color.Black,
                        containerColor = Color(0xFF0C121E),
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedTab == "AUTO_LOOP",
                    onClick = { selectedTab = "AUTO_LOOP" },
                    label = { Text(if (isGujarati) "ઓટોમેટેડ 3-Min લૂપ 🚀" else "Auto 3-Min Loop 🚀", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldAmber,
                        selectedLabelColor = Color.Black,
                        containerColor = Color(0xFF0C121E),
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedTab == "DF_VIEW",
                    onClick = { selectedTab = "DF_VIEW" },
                    label = { Text(if (isGujarati) "અલગ થયેલ કોલમ્સ (DF)" else "Clean Columns (DF)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldAmber,
                        selectedLabelColor = Color.Black,
                        containerColor = Color(0xFF0C121E),
                        labelColor = TextSecondary
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (selectedTab) {
                "UNPACK_CODE" -> {
                    // Python Code Box
                    Surface(
                        color = Color(0xFF070B12),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223048)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = dictionaryUnpackCode,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TechCyan,
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                "AUTO_LOOP" -> {
                    // Complete Automated 3-min Loop Code Box
                    Surface(
                        color = Color(0xFF070B12),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223048)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = automatedPipelineCode,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = GoldAmber,
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                "PCR_CODE" -> {
                    // PCR Sentiment Code Box
                    Surface(
                        color = Color(0xFF070B12),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223048)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = pcrSentimentCode,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = BullGreen,
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                "DF_VIEW" -> {
                    // Clean Converted DataFrame View with Support/Resistance Badges
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0C121E))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "# Unpacked DataFrame (CE & PE Separated Columns)",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = GoldAmber
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Table row header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF141D2E))
                                .padding(vertical = 5.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "CE OI", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BearRed, modifier = Modifier.weight(1f))
                            Text(text = "CE LTP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(0.9f))
                            Text(text = "STRIKE", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = TechCyan, modifier = Modifier.weight(1.1f))
                            Text(text = "PE LTP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(0.9f))
                            Text(text = "PE OI", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BullGreen, modifier = Modifier.weight(1f))
                        }

                        // Sample unpacked rows with Support & Resistance highlights
                        val sampleRows = listOf(
                            UnpackedRow("24,900", "38,400", "225.0", "182,500", "32.0", isSupport = false, isResistance = false),
                            UnpackedRow("24,950", "54,200", "182.4", "215,800", "44.2", isSupport = false, isResistance = false),
                            UnpackedRow("25,000", "82,100", "148.0", "268,400", "65.8", isSupport = true, isResistance = false), // Major Support!
                            UnpackedRow("25,050", "125,800", "116.5", "142,300", "92.0", isSupport = false, isResistance = false),
                            UnpackedRow("25,100", "285,400", "88.2", "88,200", "125.4", isSupport = false, isResistance = true), // Major Resistance!
                            UnpackedRow("25,150", "148,200", "62.0", "42,100", "168.0", isSupport = false, isResistance = false)
                        )

                        sampleRows.forEach { r ->
                            val rowBg = when {
                                r.isSupport -> BullGreenSoft.copy(alpha = 0.4f)
                                r.isResistance -> BearRedSoft.copy(alpha = 0.4f)
                                else -> Color.Transparent
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(rowBg)
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = r.ceOi, fontSize = 9.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                                Text(text = "₹${r.ceLtp}", fontSize = 9.sp, color = TextPrimary, modifier = Modifier.weight(0.9f))
                                
                                Row(
                                    modifier = Modifier.weight(1.1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = r.strike,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (r.isSupport) BullGreen else if (r.isResistance) BearRed else TechCyan
                                    )
                                    if (r.isSupport) {
                                        Text(text = " [S]", fontSize = 8.sp, color = BullGreen, fontWeight = FontWeight.Bold)
                                    } else if (r.isResistance) {
                                        Text(text = " [R]", fontSize = 8.sp, color = BearRed, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text(text = "₹${r.peLtp}", fontSize = 9.sp, color = TextPrimary, modifier = Modifier.weight(0.9f))
                                Text(text = r.peOi, fontSize = 9.sp, color = TextSecondary, modifier = Modifier.weight(1f))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Key Technical Derivations Box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF141D2E))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = if (isGujarati) "મહત્વનો સપોર્ટ" else "Major Support", fontSize = 9.sp, color = BullGreen)
                                Text(text = "25,000 PE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BullGreen)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = if (isGujarati) "મહત્વનો રેઝિસ્ટન્સ" else "Major Resistance", fontSize = 9.sp, color = BearRed)
                                Text(text = "25,100 CE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BearRed)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "PCR", fontSize = 9.sp, color = GoldAmber)
                                Text(text = "1.18 (Bullish)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldAmber)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class UnpackedRow(
    val strike: String,
    val ceOi: String,
    val ceLtp: String,
    val peOi: String,
    val peLtp: String,
    val isSupport: Boolean,
    val isResistance: Boolean
)
