package com.n2a5.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.n2a5.app.ui.theme.*

enum class AppTab(val label: String, val icon: ImageVector) {
    Dashboard("الرئيسية", Icons.Default.Home),
    Servers("السيرفرات", Icons.Default.Dns),
    Subscriptions("الاشتراكات", Icons.Default.Bookmarks),
    Stats("الإحصائيات", Icons.Default.BarChart),
    Logs("السجلات", Icons.Default.Article),
    Settings("الإعدادات", Icons.Default.Settings)
}

enum class VpnStatus { Disconnected, Connecting, Connected, Error }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var currentTab by remember { mutableStateOf(AppTab.Dashboard) }
    var vpnStatus by remember { mutableStateOf(VpnStatus.Disconnected) }
    var hotspotActive by remember { mutableStateOf(false) }
    var serverName by remember { mutableStateOf("Fast-Node") }

    Scaffold(
        containerColor = BgPrimary,
        bottomBar = {
            BottomNavBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (currentTab) {
                AppTab.Dashboard -> DashboardContent(
                    vpnStatus = vpnStatus,
                    hotspotActive = hotspotActive,
                    serverName = serverName,
                    onToggleVpn = {
                        vpnStatus = when (vpnStatus) {
                            VpnStatus.Disconnected, VpnStatus.Error -> {
                                VpnStatus.Connecting
                            }
                            VpnStatus.Connected -> VpnStatus.Disconnected
                            VpnStatus.Connecting -> VpnStatus.Disconnected
                        }
                        if (vpnStatus == VpnStatus.Connecting) {
                            // Simulate connection
                        }
                    },
                    onToggleHotspot = { hotspotActive = !hotspotActive },
                    onOpenSettings = { currentTab = AppTab.Settings },
                    onSelectServer = { currentTab = AppTab.Servers }
                )
                AppTab.Servers -> ServersContent()
                AppTab.Subscriptions -> PlaceholderContent("الاشتراكات", "إدارة روابط الاشتراكات وتحديثها تلقائياً")
                AppTab.Stats -> PlaceholderContent("الإحصائيات", "عرض إحصائيات الاتصال وحركة البيانات")
                AppTab.Logs -> PlaceholderContent("السجلات", "سجلات VPN وXray والشبكة")
                AppTab.Settings -> SettingsContent()
            }
        }
    }
}

@Composable
fun DashboardContent(
    vpnStatus: VpnStatus,
    hotspotActive: Boolean,
    serverName: String,
    onToggleVpn: () -> Unit,
    onToggleHotspot: () -> Unit,
    onOpenSettings: () -> Unit,
    onSelectServer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Default.Settings, "الإعدادات", tint = White70)
            }

            if (hotspotActive) {
                Surface(
                    color = CardBg,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, White10)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("تم تفعيل مشاركة نقطة\nالاتصال عبر Shizzi",
                            color = Color.White, fontSize = 11.sp,
                            textAlign = TextAlign.Center, lineHeight = 16.sp)
                        Box(
                            Modifier.size(6.dp).clip(CircleShape).background(PinkPrimary)
                        )
                    }
                }
            } else {
                Spacer(Modifier.weight(1f))
            }

            Surface(
                onClick = onSelectServer,
                color = CardBg,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, White10)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(serverName, color = White70, fontSize = 13.sp, maxLines = 1)
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = White40, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // VPN Orb
        VpnOrbComposable(vpnStatus)

        Spacer(Modifier.height(16.dp))

        // Status Text
        Text(
            text = when (vpnStatus) {
                VpnStatus.Disconnected -> "غير متصل"
                VpnStatus.Connecting -> "جارٍ الاتصال..."
                VpnStatus.Connected -> "متصل"
                VpnStatus.Error -> "فشل الاتصال"
            },
            color = when (vpnStatus) {
                VpnStatus.Disconnected -> White40
                VpnStatus.Connecting -> PurplePrimary
                VpnStatus.Connected -> GreenStatus
                VpnStatus.Error -> RedStatus
            },
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.weight(1f))

        // Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // VPN Button
            Button(
                onClick = onToggleVpn,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PinkPrimary
                )
            ) {
                Text(
                    if (vpnStatus == VpnStatus.Connected) "إيقاف VPN" else "تشغيل VPN",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Hotspot Button
            OutlinedButton(
                onClick = onToggleHotspot,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    Brush.linearGradient(listOf(PurplePrimary, PinkPrimary))
                )
            ) {
                Icon(Icons.Default.Share, null, tint = PurplePrimary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (hotspotActive) "إيقاف مشاركة الشبكة" else "تشغيل مشاركة الشبكة",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurplePrimary
                )
            }
        }

        // Bottom status bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("V1.0.0", color = White10, fontSize = 11.sp)
            Text(
                if (hotspotActive) "STATUS SHARING ACTIVE" else "STATUS IDLE",
                color = White10,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun VpnOrbComposable(status: VpnStatus) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val outerColor = when (status) {
        VpnStatus.Disconnected -> Color.Gray.copy(alpha = 0.3f)
        VpnStatus.Connecting -> PurplePrimary.copy(alpha = pulse)
        VpnStatus.Connected -> PurplePrimary
        VpnStatus.Error -> RedStatus.copy(alpha = 0.5f)
    }
    val innerColor = when (status) {
        VpnStatus.Disconnected -> Color.Gray.copy(alpha = 0.4f)
        VpnStatus.Connecting -> PinkPrimary.copy(alpha = pulse)
        VpnStatus.Connected -> PinkPrimary
        VpnStatus.Error -> RedStatus
    }

    Canvas(modifier = Modifier.size(220.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val outerRadius = size.minDimension / 2 - 10
        val midRadius = outerRadius * 0.72f
        val innerRadius = outerRadius * 0.45f
        val coreRadius = outerRadius * 0.18f

        // Outer ring
        drawArc(
            color = outerColor,
            startAngle = rotation,
            sweepAngle = 240f,
            useCenter = false,
            style = Stroke(width = 8f, cap = StrokeCap.Round),
            topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
            size = androidx.compose.ui.geometry.Size(outerRadius * 2, outerRadius * 2)
        )
        // Middle ring
        drawArc(
            color = innerColor,
            startAngle = -rotation * 0.8f,
            sweepAngle = 200f,
            useCenter = false,
            style = Stroke(width = 8f, cap = StrokeCap.Round),
            topLeft = Offset(center.x - midRadius, center.y - midRadius),
            size = androidx.compose.ui.geometry.Size(midRadius * 2, midRadius * 2)
        )
        // Inner ring
        drawArc(
            color = outerColor.copy(alpha = 0.7f),
            startAngle = rotation * 0.6f,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(width = 6f, cap = StrokeCap.Round),
            topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
            size = androidx.compose.ui.geometry.Size(innerRadius * 2, innerRadius * 2)
        )
        // Core dot
        drawCircle(color = innerColor, radius = coreRadius, center = center)
        drawCircle(color = Color.White.copy(alpha = 0.3f), radius = coreRadius * 0.5f, center = center)
    }
}

@Composable
fun ServersContent() {
    val sampleServers = remember {
        listOf(
            Triple("DE-Xray-Reality-01 (VLESS)", "vless", 42),
            Triple("NL-VMess-WS-Stream (VMess)", "vmess", 58),
            Triple("Thmanyah-Fast-Node (VLESS)", "vless", 35),
            Triple("US-Shadowsocks-Aes (SS)", "ss", 120),
            Triple("SG-Trojan-TLS (Trojan)", "trojan", 85),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "السيرفرات",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(sampleServers) { (name, protocol, ping) ->
                Surface(
                    color = CardBg,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, White10)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = when (protocol) {
                                "vless" -> PurplePrimary.copy(alpha = 0.2f)
                                "vmess" -> Color(0xFF2196F3).copy(alpha = 0.2f)
                                "trojan" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                else -> GreenStatus.copy(alpha = 0.2f)
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                protocol.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (protocol) {
                                    "vless" -> PurplePrimary
                                    "vmess" -> Color(0xFF64B5F6)
                                    "trojan" -> Color(0xFFFFB74D)
                                    else -> GreenStatus
                                }
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            name,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "$ping ms",
                            color = when {
                                ping < 100 -> GreenStatus
                                ping < 200 -> Color(0xFFFFEB3B)
                                else -> Color(0xFFFF9800)
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("الإعدادات", color = Color.White, fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
        }

        // V2Ray code section
        item {
            Surface(color = CardBg, shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, White10)) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("كود اتصال V2Ray", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("VLESS / VMess / Trojan / SS", color = White40, fontSize = 11.sp)
                        }
                        Text("إدراج كود تجريبي", color = PinkPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(12.dp))
                    Surface(color = White10.copy(alpha = 0.05f), shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, White10)) {
                        Text("vless://9a8b7c6d-1122...",
                            color = White40, fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp).fillMaxWidth())
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("سيرفرات سريعة محفوظة:", color = White40, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    listOf("DE-Xray-Reality-01 (VLESS)" to 42, "NL-VMess-WS-Stream (VMess)" to 58).forEach { (n, p) ->
                        Surface(
                            color = if (p == 42) PurplePrimary.copy(alpha = 0.1f) else White10.copy(alpha = 0.03f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp,
                                if (p == 42) PurplePrimary.copy(alpha = 0.4f) else White10),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
                                Text(n, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f))
                                Text("$p ms", color = GreenStatus, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Shizzi settings
        item {
            Surface(color = CardBg, shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, White10)) {
                Column(Modifier.padding(16.dp)) {
                    Text("إعدادات Shizzi (بث نقطة الاتصال عبر Shizuku)", color = Color.White,
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    Surface(color = White10.copy(alpha = 0.03f), shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, White10)) {
                        Column(Modifier.padding(12.dp)) {
                            Text("وضع توجيه VPN في Shizzi", color = White40, fontSize = 11.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("ربط تلقائي مع V2Ray (Follow VPN)", color = Color.White, fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Surface(color = White10.copy(alpha = 0.03f), shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, White10)) {
                        Row(Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("صلاحية Shizuku", color = White40, fontSize = 11.sp)
                                Text("متصل ومفعّل (UID 2000)", color = GreenStatus, fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold)
                            }
                            Surface(onClick = {}, color = CardBg, shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, White10)) {
                                Text("فحص", color = Color.White, fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                            }
                        }
                    }
                }
            }
        }

        // About
        item {
            Surface(color = CardBg, shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, White10)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        color = PinkPrimary,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("2", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Column {
                        Text("2n5a", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("الإصدار 1.0.0", color = White40, fontSize = 12.sp)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
fun PlaceholderContent(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(description, color = White40, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun BottomNavBar(currentTab: AppTab, onTabSelected: (AppTab) -> Unit) {
    NavigationBar(
        containerColor = BgPrimary.copy(alpha = 0.95f),
        tonalElevation = 0.dp
    ) {
        AppTab.entries.forEach { tab ->
            val selected = currentTab == tab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        tab.icon,
                        contentDescription = tab.label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(tab.label, fontSize = 10.sp, maxLines = 1)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PinkPrimary,
                    selectedTextColor = PinkPrimary,
                    unselectedIconColor = White40,
                    unselectedTextColor = White40,
                    indicatorColor = PinkPrimary.copy(alpha = 0.12f)
                )
            )
        }
    }
}
