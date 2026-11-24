package com.pivo.app

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

val VoidBlack = Color(0xFF04020A)
val DeepNebula = Color(0xFF0F0722)
val GlassSurface = Color(0xCC160C2E)
val GlassBorder = Color(0x55B565FF)
val NeonPurple = Color(0xFF9D4EDD)
val HyperViolet = Color(0xFF6A00F4)
val PlasmaPink = Color(0xFFFF2A85)
val SnapYellow = Color(0xFFFFFC00)
val LiveRed = Color(0xFFEF4444)
val CyberCyan = Color(0xFF38BDF8)
val StarlightWhite = Color(0xFFF9F7FF)
val MutedLavender = Color(0xFFA599C7)

data class UserProfileData(
    val name: String,
    val username: String,
    val pronouns: String = "",
    val bio: String,
    val website: String = "andexmml-gif.github.io/Pivo-web/",
    val category: String = "Software Company",
    val gender: String = "Prefer not to say",
    val isAiProfile: Boolean = false,
    val isVerified: Boolean = true,
    val isPrivate: Boolean = false,
    val snapScore: Int = 14820
)

data class PivoStory(
    val id: Int,
    val username: String,
    val caption: String,
    val timeAgo: String,
    val isLive: Boolean = false,
    val viewers: Int = 0,
    val gradient: List<Color>
)

data class PivoPost(
    val id: Int,
    val author: String,
    val location: String,
    val time: String,
    val caption: String,
    val likes: Int,
    val comments: MutableList<String>,
    val isVerified: Boolean,
    val cardGradient: List<Color>
)

data class PivoReelItem(
    val id: Int,
    val sharedBy: String,
    val caption: String,
    val embedUrl: String?,
    val likes: Int,
    val bgGradient: List<Color>
)

data class PivoChat(
    val name: String,
    val lastMsg: String,
    val time: String,
    val streakCount: Int,
    val hasUnopenedSnap: Boolean = false,
    val category: String = "Primary",
    val isGroup: Boolean = false,
    val isTyping: Boolean = false
)

data class ActiveCallSession(
    val peerName: String,
    val isVideoCall: Boolean
)

data class StarParticle(
    val xRatio: Float,
    val yRatio: Float,
    val radius: Float,
    val speed: Float,
    val phase: Float
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sharedText = if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)
        } else null

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = NeonPurple,
                    background = VoidBlack,
                    surface = GlassSurface,
                    onBackground = StarlightWhite,
                    onSurface = StarlightWhite
                )
            ) {
                PivoAppRoot(incomingSharedLink = sharedText)
            }
        }
    }
}

fun extractInstagramEmbedUrl(rawUrl: String): String? {
    val regex = Regex("""https?://(www\.)?instagram\.com/(reel|p|tv)/([A-Za-z0-9_-]+)""")
    val match = regex.find(rawUrl)
    return if (match != null) {
        "https://www.instagram.com/${match.groupValues[2]}/${match.groupValues[3]}/embed/"
    } else null
}

@Composable
fun LiveMovingCosmicCanvas(modifier: Modifier = Modifier, showHorizon: Boolean = false) {
    val stars = remember {
        val rng = Random(42)
        List(55) {
            StarParticle(
                xRatio = rng.nextFloat(),
                yRatio = rng.nextFloat(),
                radius = 1.5f + rng.nextFloat() * 3.2f,
                speed = 0.04f + rng.nextFloat() * 0.12f,
                phase = rng.nextFloat() * 2f * PI.toFloat()
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "cosmic_engine")
    val driftProgress by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(18000, easing = LinearEasing), RepeatMode.Restart),
        label = "drift"
    )
    val pulseAngle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing), RepeatMode.Restart),
        label = "pulse"
    )
    val meteorProgress by infiniteTransition.animateFloat(
        initialValue = -0.3f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(3600, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "meteor"
    )
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Restart),
        label = "ring"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val orb1X = w * (0.25f + 0.15f * cos(pulseAngle))
        val orb1Y = h * (0.22f + 0.08f * sin(pulseAngle))
        drawCircle(
            brush = Brush.radialGradient(listOf(HyperViolet.copy(alpha = 0.25f), Color.Transparent), Offset(orb1X, orb1Y), w * 0.65f),
            center = Offset(orb1X, orb1Y), radius = w * 0.65f
        )

        val orb2X = w * (0.78f - 0.15f * sin(pulseAngle))
        val orb2Y = h * (0.62f + 0.10f * cos(pulseAngle))
        drawCircle(
            brush = Brush.radialGradient(listOf(PlasmaPink.copy(alpha = 0.18f), Color.Transparent), Offset(orb2X, orb2Y), w * 0.58f),
            center = Offset(orb2X, orb2Y), radius = w * 0.58f
        )

        stars.forEach { star ->
            val animatedY = ((star.yRatio - driftProgress * star.speed * 4f) % 1f + 1f) % 1f
            val twinkleAlpha = (0.35f + 0.65f * ((sin(pulseAngle + star.phase) + 1f) / 2f)).coerceIn(0.15f, 1f)
            drawCircle(color = StarlightWhite.copy(alpha = twinkleAlpha), radius = star.radius, center = Offset(star.xRatio * w, animatedY * h))
        }

        if (meteorProgress in 0f..1.1f) {
            val startX = w * (1.1f - meteorProgress * 1.2f)
            val startY = h * (0.05f + meteorProgress * 0.45f)
            val tailX = startX + 140f
            val tailY = startY - 75f
            drawLine(
                brush = Brush.linearGradient(listOf(Color.White, CyberCyan, PlasmaPink.copy(alpha = 0.2f), Color.Transparent), Offset(startX, startY), Offset(tailX, tailY)),
                start = Offset(startX, startY), end = Offset(tailX, tailY), strokeWidth = 4f, cap = StrokeCap.Round
            )
            drawCircle(color = Color.White, radius = 4.5f, center = Offset(startX, startY))
        }

        if (showHorizon) {
            val horizonCenter = Offset(w / 2f, h * 1.16f)
            val horizonRadius = w * 1.32f
            drawCircle(brush = Brush.verticalGradient(listOf(Color(0xFF1D073E), VoidBlack)), center = horizonCenter, radius = horizonRadius)
            rotate(degrees = ringRotation * 0.15f, pivot = horizonCenter) {
                drawCircle(
                    brush = Brush.sweepGradient(listOf(PlasmaPink, NeonPurple, CyberCyan, PlasmaPink), horizonCenter),
                    radius = horizonRadius, center = horizonCenter, style = Stroke(width = 5f)
                )
            }
        }
    }
}

@Composable
fun RotatingNeonRingBox(sizeDp: Int, borderWidth: Float = 2.5f, isLive: Boolean = false, content: @Composable BoxScope.() -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "ring_spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4500, easing = LinearEasing), RepeatMode.Restart),
        label = "spin"
    )
    val colors = if (isLive) listOf(LiveRed, PlasmaPink, SnapYellow, LiveRed)
    else listOf(PlasmaPink, NeonPurple, CyberCyan, HyperViolet, PlasmaPink)

    Box(modifier = Modifier.size(sizeDp.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize().rotate(angle)) {
            drawCircle(brush = Brush.sweepGradient(colors), style = Stroke(width = borderWidth.dp.toPx()))
        }
        Box(
            modifier = Modifier.size((sizeDp - 8).dp).clip(CircleShape).background(DeepNebula),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

@Composable
fun PivoAppRoot(incomingSharedLink: String? = null) {
    var currentScreen by remember { mutableStateOf(if (incomingSharedLink != null) "main" else "welcome") }

    val accounts = remember {
        mutableStateListOf(
            UserProfileData(
                name = "pivo™",
                username = "pivo.app",
                bio = "Your people, your moments.\nfollow @pivo.devlopers • Made in India 🇮🇳",
                category = "Software Company",
                isVerified = true,
                snapScore = 24950
            ),
            UserProfileData(
                name = "Rahul Arya",
                username = "rahul.arya.fzd",
                bio = "Founder @pivo.app 🚀 | Building in Public",
                category = "Entrepreneur",
                isVerified = true,
                snapScore = 18420
            )
        )
    }
    var activeAccountIndex by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize().background(VoidBlack)) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(250)) },
            label = "root_nav"
        ) { screen ->
            when (screen) {
                "welcome" -> CosmicWelcomeScreen(
                    onCreateAccountClick = { currentScreen = "signup" },
                    onLoginClick = { currentScreen = "login" }
                )
                "signup" -> AuthScreen(
                    isSignup = true,
                    onBack = { currentScreen = "welcome" },
                    onSuccess = { name ->
                        if (name.isNotBlank()) accounts[activeAccountIndex] = accounts[activeAccountIndex].copy(name = name)
                        currentScreen = "main"
                    },
                    onSwitchMode = { currentScreen = "login" }
                )
                "login" -> AuthScreen(
                    isSignup = false,
                    onBack = { currentScreen = "welcome" },
                    onSuccess = { currentScreen = "main" },
                    onSwitchMode = { currentScreen = "signup" }
                )
                "main" -> MainDashboardScreen(
                    accounts = accounts,
                    activeIndex = activeAccountIndex,
                    incomingSharedLink = incomingSharedLink,
                    onSwitchAccount = { activeAccountIndex = it },
                    onAddAccount = { accounts.add(it); activeAccountIndex = accounts.lastIndex },
                    onUpdateProfile = { accounts[activeAccountIndex] = it },
                    onLogout = { currentScreen = "welcome" }
                )
            }
        }
    }
}

@Composable
fun FourPointStar(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w / 2f, 0f)
            quadraticTo(w / 2f, h / 2f, w, h / 2f)
            quadraticTo(w / 2f, h / 2f, w / 2f, h)
            quadraticTo(w / 2f, h / 2f, 0f, h / 2f)
            quadraticTo(w / 2f, h / 2f, w / 2f, 0f)
            close()
        }
        drawPath(path, color = color)
    }
}

@Composable
fun CosmicWelcomeScreen(onCreateAccountClick: () -> Unit, onLoginClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -7f, targetValue = 7f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float"
    )

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF1F0744), VoidBlack, Color(0xFF160533), VoidBlack)))) {
        LiveMovingCosmicCanvas(showHorizon = true)
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(top = 42.dp).offset(y = floatOffset.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(78.dp).shadow(30.dp, RoundedCornerShape(26.dp), spotColor = PlasmaPink)
                        .clip(RoundedCornerShape(26.dp)).background(Brush.linearGradient(listOf(PlasmaPink, NeonPurple, HyperViolet))),
                    contentAlignment = Alignment.Center
                ) { FourPointStar(modifier = Modifier.size(40.dp), color = Color.White) }
                Spacer(modifier = Modifier.height(18.dp))
                Text("PIVO", fontSize = 44.sp, fontWeight = FontWeight.Black, color = StarlightWhite, letterSpacing = 10.sp)
                Text("SNAPS • LIVE • CALLS • SQUADS", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE0AAFF), letterSpacing = 2.5.sp)
            }

            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("More than social.\nIt's your world.", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = StarlightWhite, textAlign = TextAlign.Center, lineHeight = 37.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onCreateAccountClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(HyperViolet, NeonPurple, PlasmaPink))), contentAlignment = Alignment.Center) {
                        Text("Create your account 🚀", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, GlassBorder),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = GlassSurface)
                ) { Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = StarlightWhite) }
                Spacer(modifier = Modifier.height(16.dp))
                Text("MADE IN INDIA 🇮🇳", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MutedLavender, letterSpacing = 2.5.sp)
            }
        }
    }
}

@Composable
fun AuthScreen(isSignup: Boolean, onBack: () -> Unit, onSuccess: (String) -> Unit, onSwitchMode: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(VoidBlack)) {
        LiveMovingCosmicCanvas(showHorizon = false)
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp)) {
            IconButton(onClick = onBack, modifier = Modifier.clip(CircleShape).background(GlassSurface)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = StarlightWhite)
            }
            Spacer(modifier = Modifier.height(22.dp))
            Text(if (isSignup) "Join PIVO ✦" else "Welcome back ✦", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = StarlightWhite)
            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(GlassSurface).border(1.dp, GlassBorder, RoundedCornerShape(24.dp)).padding(20.dp)) {
                if (isSignup) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name / Username") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(14.dp))
                }
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onSuccess(name) },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(HyperViolet, PlasmaPink))), contentAlignment = Alignment.Center) {
                        Text(if (isSignup) "Create Account 🚀" else "Log In ✦", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            TextButton(onClick = onSwitchMode, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(if (isSignup) "Already have an account? Log in" else "New to PIVO? Create an account", color = Color(0xFFE0AAFF))
            }
        }
    }
}

@Composable
fun MainDashboardScreen(
    accounts: List<UserProfileData>,
    activeIndex: Int,
    incomingSharedLink: String?,
    onSwitchAccount: (Int) -> Unit,
    onAddAccount: (UserProfileData) -> Unit,
    onUpdateProfile: (UserProfileData) -> Unit,
    onLogout: () -> Unit
) {
    val currentUser = accounts[activeIndex]
    var selectedTab by remember { mutableIntStateOf(if (incomingSharedLink != null) 1 else 0) }
    var subScreen by remember { mutableStateOf("none") }
    var activeStory by remember { mutableStateOf<PivoStory?>(null) }
    var activeCall by remember { mutableStateOf<ActiveCallSession?>(null) }
    var isUserGoingLive by remember { mutableStateOf(false) }

    val stories = remember {
        mutableStateListOf(
            PivoStory(1, "pivo.devlopers", "🔴 LIVE: Building PIVO Beta v1.0 Q&A!", "LIVE", isLive = true, viewers = 428, gradient = listOf(Color(0xFF991B1B), Color(0xFF6A00F4))),
            PivoStory(2, "Your story", "Late night cosmic coding ✨🇮🇳", "12m", gradient = listOf(Color(0xFF6A00F4), Color(0xFFFF2A85))),
            PivoStory(3, "khushi260_", "Snapchat streaks + Insta vibes 🔥", "1h", gradient = listOf(Color(0xFF3A0CA3), Color(0xFF9D4EDD))),
            PivoStory(4, "vishu_07", "🔴 LIVE: Night Ride Firozabad 🏍️", "LIVE", isLive = true, viewers = 192, gradient = listOf(Color(0xFFEF4444), Color(0xFF1E1B4B)))
        )
    }

    val posts = remember {
        mutableStateListOf(
            PivoPost(1, "pivo.app", "New Delhi • HQ", "1h ago", "Instagram + Snapchat + Live Calls ab ek hi Made-in-India app mein! 🚀💜 #PIVO", 390, mutableListOf("Bhai ekdum fire UI hai 🔥", "Snap streaks mast hai!"), true, listOf(Color(0xFF3C096C), Color(0xFF9D4EDD), Color(0xFFFF2A85))),
            PivoPost(2, "pivo.app", "Made in India 🇮🇳", "3h ago", "Social Media Mein Bhi Fair System! No Random Suspensions, Creator Friendly ✦", 720, mutableListOf("Best Indian app 🇮🇳"), true, listOf(Color(0xFF10002B), Color(0xFF5A189A), Color(0xFFE056FD)))
        )
    }

    val reels = remember {
        mutableStateListOf<PivoReelItem>().apply {
            if (incomingSharedLink != null) {
                add(PivoReelItem(100, currentUser.username, "Shared from Instagram 🎬", extractInstagramEmbedUrl(incomingSharedLink), 12, listOf(Color(0xFF3A0CA3), Color(0xFFFF2A85))))
            }
            add(PivoReelItem(1, "pivo.app", "Paste any Instagram Reel link (+ Paste Link) to watch & scroll vertically! 🔥", null, 1840, listOf(Color(0xFF240046), Color(0xFF9D4EDD), Color(0xFFFF2A85))))
        }
    }

    val chats = remember {
        mutableStateListOf(
            PivoChat("GEN-Z Squad", "vishu sent a Snap 🟥", "now", streakCount = 48, hasUnopenedSnap = true, category = "Primary", isGroup = true, isTyping = true),
            PivoChat("HOMIES SQUAD 🥂", "Nehal Kharb: Video call aao 📹", "11m", streakCount = 32, hasUnopenedSnap = true, category = "Primary", isGroup = true),
            PivoChat("K 🍷", "Delivered Snap • 32m", "32m", streakCount = 19, hasUnopenedSnap = false, category = "Primary"),
            PivoChat("kashi group 🙏💙", "Seen by Rajat | Rao + 2", "1h", streakCount = 11, category = "Primary", isGroup = true),
            PivoChat("New Creator Collab", "Hey! Want to join PIVO live?", "2h", streakCount = 1, category = "Requests")
        )
    }

    // Active Audio/Video Call Overlay Screen
    if (activeCall != null) {
        CosmicCallOverlayScreen(session = activeCall!!, onEndCall = { activeCall = null })
        return
    }

    // User Going Live Camera Studio
    if (isUserGoingLive) {
        GoLiveStudioScreen(hostUsername = currentUser.username, onEndLive = { isUserGoingLive = false })
        return
    }

    // Story or Live Stream Viewer
    if (activeStory != null) {
        FullScreenStoryOrLiveViewer(story = activeStory!!, onClose = { activeStory = null })
        return
    }

    if (subScreen == "edit_profile") {
        FullEditProfileScreen(profile = currentUser, onSave = { onUpdateProfile(it); subScreen = "none" }, onBack = { subScreen = "none" })
        return
    }

    if (subScreen == "settings") {
        FullSettingsAndActivityScreen(profile = currentUser, onTogglePrivacy = { onUpdateProfile(currentUser.copy(isPrivate = it)) }, onBack = { subScreen = "none" }, onLogout = onLogout)
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(VoidBlack)) {
        LiveMovingCosmicCanvas(showHorizon = false)

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                // 5-Icon Bottom Bar: Home | Reels | Snap Camera (Center) | Chats & Calls | Profile
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .background(Color(0xEE090415))
                        .border(BorderStroke(0.5.dp, GlassBorder))
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavIcon(Icons.Default.Home, selectedTab == 0) { selectedTab = 0 }
                    BottomNavIcon(Icons.Default.PlayCircleOutline, selectedTab == 1) { selectedTab = 1 }

                    // Center Snapchat-Style Camera & AR Filter Button
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .shadow(16.dp, CircleShape, spotColor = SnapYellow)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(SnapYellow, PlasmaPink)))
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { selectedTab = 2 },
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.CameraAlt, contentDescription = "Snap Camera", tint = VoidBlack, modifier = Modifier.size(26.dp)) }

                    BottomNavIcon(Icons.AutoMirrored.Filled.Send, selectedTab == 3) { selectedTab = 3 }
                    BottomNavIcon(Icons.Default.Person, selectedTab == 4) { selectedTab = 4 }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedTab) {
                    0 -> FeedTabScreen(
                        stories = stories,
                        posts = posts,
                        onOpenStory = { activeStory = it },
                        onGoLive = { isUserGoingLive = true },
                        onAddPost = { cap ->
                            posts.add(0, PivoPost(posts.size + 1, currentUser.username, "India 🇮🇳", "Just now", cap, 1, mutableListOf(), currentUser.isVerified, listOf(Color(0xFF6A00F4), Color(0xFFFF2A85))))
                        }
                    )
                    1 -> ReelsTabScreen(reels = reels, onAddReelLink = { url, cap ->
                        reels.add(0, PivoReelItem(reels.size + 10, currentUser.username, cap.ifBlank { "Shared Reel 🎬" }, extractInstagramEmbedUrl(url), 1, listOf(Color(0xFF3A0CA3), Color(0xFFFF2A85))))
                    })
                    2 -> SnapchatCameraScreen(
                        userName = currentUser.username,
                        chats = chats,
                        onSendSnapToChat = { targetChatIndex, snapCaption ->
                            val old = chats[targetChatIndex]
                            chats[targetChatIndex] = old.copy(
                                lastMsg = "Sent Snap: $snapCaption 🟥",
                                time = "now",
                                streakCount = old.streakCount + 1
                            )
                            onUpdateProfile(currentUser.copy(snapScore = currentUser.snapScore + 50))
                            selectedTab = 3
                        },
                        onGoLiveClick = { isUserGoingLive = true }
                    )
                    3 -> ChatsTabScreen(
                        currentUser = currentUser,
                        chats = chats,
                        onStartCall = { peer, isVideo -> activeCall = ActiveCallSession(peer, isVideo) },
                        onCreateGroup = { gName ->
                            chats.add(0, PivoChat(gName, "Squad created! Invite friends 🚀", "now", streakCount = 1, category = "Primary", isGroup = true))
                        }
                    )
                    4 -> FullProfileTabScreen(
                        accounts = accounts,
                        activeIndex = activeIndex,
                        posts = posts,
                        onSwitchAccount = onSwitchAccount,
                        onAddAccount = onAddAccount,
                        onOpenEditProfile = { subScreen = "edit_profile" },
                        onOpenSettings = { subScreen = "settings" }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavIcon(icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "nav_scale"
    )
    IconButton(onClick = onClick) {
        Icon(imageVector = icon, contentDescription = null, tint = if (selected) PlasmaPink else MutedLavender, modifier = Modifier.size(26.dp).scale(scale))
    }
}

// Snapchat-Style AR Camera & Snap Sender
@Composable
fun SnapchatCameraScreen(
    userName: String,
    chats: List<PivoChat>,
    onSendSnapToChat: (Int, String) -> Unit,
    onGoLiveClick: () -> Unit
) {
    val context = LocalContext.current
    val lenses = listOf(
        "🌌 Cosmic Aura" to listOf(Color(0xFF3A0CA3), Color(0xFFFF2A85)),
        "⚡ Cyber Visor" to listOf(Color(0xFF0F172A), Color(0xFF38BDF8)),
        "🔥 Streak Flame" to listOf(Color(0xFF7C2D12), Color(0xFFF59E0B)),
        "👾 Neon Glitch" to listOf(Color(0xFF4C1D95), Color(0xFF10B981))
    )
    var selectedLensIndex by remember { mutableIntStateOf(0) }
    var snapText by remember { mutableStateOf("") }
    var snapTimer by remember { mutableIntStateOf(5) }
    var showSendPicker by remember { mutableStateOf(false) }

    if (showSendPicker) {
        AlertDialog(
            onDismissRequest = { showSendPicker = false },
            containerColor = DeepNebula,
            title = { Text("Send Snap To 🔥", color = StarlightWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    chats.forEachIndexed { idx, chat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(GlassSurface)
                                .clickable {
                                    onSendSnapToChat(idx, snapText.ifBlank { lenses[selectedLensIndex].first })
                                    Toast.makeText(context, "Snap sent! 🔥 +1 Streak", Toast.LENGTH_SHORT).show()
                                    showSendPicker = false
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(chat.name, color = StarlightWhite, fontWeight = FontWeight.SemiBold)
                            Text("🔥 ${chat.streakCount}", color = SnapYellow, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(lenses[selectedLensIndex].second))
            .padding(16.dp)
    ) {
        LiveMovingCosmicCanvas(showHorizon = false)

        // Top Camera Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { snapTimer = if (snapTimer == 5) 10 else if (snapTimer == 10) 3 else 5 }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("⏱ ${snapTimer}s Snap", color = SnapYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = onGoLiveClick,
                colors = ButtonDefaults.buttonColors(containerColor = LiveRed),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text("🔴 GO LIVE", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
            }
        }

        // Center AR Lens Viewfinder + Caption Overlay
        Column(
            modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RotatingNeonRingBox(sizeDp = 150, borderWidth = 3f) {
                Text(lenses[selectedLensIndex].first.take(2), fontSize = 48.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(lenses[selectedLensIndex].first, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(14.dp))
            OutlinedTextField(
                value = snapText,
                onValueChange = { snapText = it },
                placeholder = { Text("Add a Snap caption...", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.fillMaxWidth(0.85f),
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Bottom Lens Selector + Capture Snap Button
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(lenses.size) { idx ->
                    val active = idx == selectedLensIndex
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (active) SnapYellow else Color.Black.copy(alpha = 0.55f))
                            .clickable { selectedLensIndex = idx }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = lenses[idx].first,
                            color = if (active) VoidBlack else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                        .border(4.dp, SnapYellow, CircleShape)
                        .clickable { showSendPicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(54.dp).clip(CircleShape).background(Color.White))
                }
            }
            Text("Tap shutter to Send Snap & Keep Streak 🔥", color = Color.White, fontSize = 11.5.sp, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

// Full-Screen Audio & Video Call UI
@Composable
fun CosmicCallOverlayScreen(session: ActiveCallSession, onEndCall: () -> Unit) {
    var seconds by remember { mutableIntStateOf(0) }
    var muted by remember { mutableStateOf(false) }
    var speaker by remember { mutableStateOf(true) }

    LaunchedEffect(session) {
        while (true) {
            delay(1000L)
            seconds++
        }
    }

    val mins = seconds / 60
    val secs = seconds % 60
    val timeText = String.format("%02d:%02d", mins, secs)

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF280854), VoidBlack)))) {
        LiveMovingCosmicCanvas(showHorizon = false)

        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 30.dp)) {
                Text(
                    text = if (session.isVideoCall) "📹 PIVO Encrypted Video Call" else "📞 PIVO HD Audio Call",
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                RotatingNeonRingBox(sizeDp = 120, borderWidth = 3.5f) {
                    Text(session.peerName.first().uppercase(), fontSize = 42.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(session.peerName, color = StarlightWhite, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                Text("Connected • $timeText", color = Color(0xFF4ADE80), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { muted = !muted },
                    modifier = Modifier.size(58.dp).clip(CircleShape).background(if (muted) PlasmaPink else GlassSurface)
                ) { Icon(if (muted) Icons.Default.MicOff else Icons.Default.Mic, contentDescription = "Mute", tint = Color.White) }

                IconButton(
                    onClick = onEndCall,
                    modifier = Modifier.size(68.dp).clip(CircleShape).background(LiveRed)
                ) { Icon(Icons.Default.CallEnd, contentDescription = "End Call", tint = Color.White, modifier = Modifier.size(32.dp)) }

                IconButton(
                    onClick = { speaker = !speaker },
                    modifier = Modifier.size(58.dp).clip(CircleShape).background(if (speaker) NeonPurple else GlassSurface)
                ) { Icon(Icons.Default.VolumeUp, contentDescription = "Speaker", tint = Color.White) }
            }
        }
    }
}

// Host Go Live Studio Screen
@Composable
fun GoLiveStudioScreen(hostUsername: String, onEndLive: () -> Unit) {
    val liveChat = remember { mutableStateListOf("vishu_07: Bhai OP live stream! 🔥", "khushi260_: PIVO zindabad 🇮🇳💜", "kashi_boy: Shoutout dedo bhai!") }
    var commentText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF450A0A), VoidBlack))).statusBarsPadding().padding(16.dp)) {
        LiveMovingCosmicCanvas(showHorizon = false)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(LiveRed).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text("🔴 LIVE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("👁 514 watching", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Button(onClick = onEndLive, colors = ButtonDefaults.buttonColors(containerColor = GlassSurface)) {
                Text("End Live", color = PlasmaPink, fontWeight = FontWeight.Bold)
            }
        }

        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            RotatingNeonRingBox(sizeDp = 120, borderWidth = 3f, isLive = true) {
                Text(hostUsername.first().uppercase(), fontSize = 40.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("@$hostUsername is LIVE!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            liveChat.takeLast(4).forEach { msg ->
                Box(modifier = Modifier.padding(vertical = 3.dp).clip(RoundedCornerShape(12.dp)).background(Color.Black.copy(alpha = 0.55f)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text(msg, color = Color.White, fontSize = 12.5.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(value = commentText, onValueChange = { commentText = it }, placeholder = { Text("Say something live...") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(20.dp))
                IconButton(onClick = {
                    if (commentText.isNotBlank()) {
                        liveChat.add("$hostUsername: $commentText")
                        commentText = ""
                    }
                }) { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = PlasmaPink) }
            }
        }
    }
}

@Composable
fun FullScreenStoryOrLiveViewer(story: PivoStory, onClose: () -> Unit) {
    var progress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(story) {
        if (!story.isLive) {
            while (progress < 1f) {
                delay(50L)
                progress += 0.015f
            }
            onClose()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(story.gradient)).statusBarsPadding().padding(18.dp)) {
        Column {
            if (!story.isLive) {
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(3.dp), color = Color.White, trackColor = Color.White.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (story.isLive) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(LiveRed).padding(horizontal = 8.dp, vertical = 3.dp)) {
                            Text("LIVE • 👁 ${story.viewers}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(story.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White) }
            }
        }
        Text(
            text = story.caption,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center).padding(20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTabScreen(
    stories: List<PivoStory>,
    posts: List<PivoPost>,
    onOpenStory: (PivoStory) -> Unit,
    onGoLive: () -> Unit,
    onAddPost: (String) -> Unit
) {
    var showCreate by remember { mutableStateOf(false) }
    var commentPost by remember { mutableStateOf<PivoPost?>(null) }

    if (showCreate) {
        var cap by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreate = false }, containerColor = DeepNebula,
            title = { Text("New PIVO Post ✦", color = StarlightWhite) },
            text = { OutlinedTextField(value = cap, onValueChange = { cap = it }, placeholder = { Text("Share something...") }) },
            confirmButton = { Button(onClick = { if (cap.isNotBlank()) onAddPost(cap); showCreate = false }) { Text("Post") } }
        )
    }

    if (commentPost != null) {
        var newComment by remember { mutableStateOf("") }
        ModalBottomSheet(onDismissRequest = { commentPost = null }, containerColor = DeepNebula) {
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                Text("Comments (${commentPost!!.comments.size})", color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(modifier = Modifier.height(10.dp))
                commentPost!!.comments.forEach { c -> Text("• $c", color = StarlightWhite, modifier = Modifier.padding(vertical = 4.dp)) }
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = newComment, onValueChange = { newComment = it }, placeholder = { Text("Add a comment...") }, modifier = Modifier.weight(1f))
                    IconButton(onClick = { if (newComment.isNotBlank()) { commentPost!!.comments.add(newComment); newComment = "" } }) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = PlasmaPink)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showCreate = true }) { Icon(Icons.Default.Add, contentDescription = null, tint = StarlightWhite) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FourPointStar(modifier = Modifier.size(20.dp), color = PlasmaPink)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PIVO", fontSize = 24.sp, fontWeight = FontWeight.Black, color = StarlightWhite, letterSpacing = 4.sp)
                }
                Button(
                    onClick = onGoLive,
                    colors = ButtonDefaults.buttonColors(containerColor = LiveRed),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) { Text("🔴 LIVE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            }

            // Stories + Live Streams Row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.padding(bottom = 14.dp)) {
                items(stories) { story ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onOpenStory(story) }) {
                        Box(contentAlignment = Alignment.BottomCenter) {
                            RotatingNeonRingBox(sizeDp = 68, borderWidth = 2.5f, isLive = story.isLive) {
                                Text(story.username.first().uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                            if (story.isLive) {
                                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(LiveRed).padding(horizontal = 6.dp, vertical = 1.dp)) {
                                    Text("LIVE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(story.username, color = MutedLavender, fontSize = 11.sp, maxLines = 1)
                    }
                }
            }
        }

        items(posts, key = { it.id }) { post ->
            var liked by remember { mutableStateOf(false) }
            var saved by remember { mutableStateOf(false) }
            var showBigHeart by remember { mutableStateOf(false) }
            val heartScale by animateFloatAsState(
                targetValue = if (showBigHeart) 1.35f else 0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                finishedListener = { showBigHeart = false },
                label = "big_heart"
            )

            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp).clip(RoundedCornerShape(22.dp)).background(GlassSurface).border(1.dp, GlassBorder, RoundedCornerShape(22.dp)).padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RotatingNeonRingBox(sizeDp = 42, borderWidth = 2f) {
                        Text(post.author.first().uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(post.author, color = StarlightWhite, fontWeight = FontWeight.Bold)
                        Text(post.location, color = MutedLavender, fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(215.dp).clip(RoundedCornerShape(16.dp)).background(Brush.linearGradient(post.cardGradient))
                        .pointerInput(Unit) { detectTapGestures(onDoubleTap = { liked = true; showBigHeart = true }) }
                        .padding(16.dp)
                ) {
                    Text(post.caption, color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.BottomStart))
                    if (heartScale > 0.05f) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White.copy(alpha = 0.92f), modifier = Modifier.size(76.dp).align(Alignment.Center).scale(heartScale))
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { liked = !liked; if (liked) showBigHeart = true }) {
                            Icon(if (liked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = null, tint = if (liked) PlasmaPink else StarlightWhite)
                        }
                        Text("${post.likes + if (liked) 1 else 0}", color = StarlightWhite, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        IconButton(onClick = { commentPost = post }) { Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = StarlightWhite) }
                        Text("${post.comments.size}", color = StarlightWhite, fontSize = 13.sp)
                    }
                    IconButton(onClick = { saved = !saved }) { Icon(if (saved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder, contentDescription = null, tint = if (saved) PlasmaPink else StarlightWhite) }
                }
            }
        }
    }
}

@Composable
fun ChatsTabScreen(
    currentUser: UserProfileData,
    chats: List<PivoChat>,
    onStartCall: (String, Boolean) -> Unit,
    onCreateGroup: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("Primary") }
    var activeChat by remember { mutableStateOf<PivoChat?>(null) }
    var showNewGroup by remember { mutableStateOf(false) }
    var viewingSnapFrom by remember { mutableStateOf<String?>(null) }

    if (viewingSnapFrom != null) {
        LaunchedEffect(viewingSnapFrom) { delay(3000L); viewingSnapFrom = null }
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(HyperViolet, PlasmaPink))), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("👻 Snap from ${viewingSnapFrom}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Disappearing in 3 seconds... 🔥", color = SnapYellow, fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    if (showNewGroup) {
        var gName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showNewGroup = false }, containerColor = DeepNebula,
            title = { Text("Create Squad 👥", color = StarlightWhite) },
            text = { OutlinedTextField(value = gName, onValueChange = { gName = it }, placeholder = { Text("Squad Name...") }) },
            confirmButton = { Button(onClick = { if (gName.isNotBlank()) onCreateGroup(gName); showNewGroup = false }) { Text("Create") } }
        )
    }

    if (activeChat != null) {
        val msgs = remember { mutableStateListOf(activeChat!!.lastMsg) }
        var draft by remember { mutableStateOf("") }
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { activeChat = null }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = StarlightWhite) }
                    Column {
                        Text(activeChat!!.name, color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("🔥 ${activeChat!!.streakCount} Day Streak", color = SnapYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                // Audio Call & Video Call Buttons inside Chat
                Row {
                    IconButton(onClick = { onStartCall(activeChat!!.name, false) }) {
                        Icon(Icons.Default.Call, contentDescription = "Audio Call", tint = Color(0xFF4ADE80))
                    }
                    IconButton(onClick = { onStartCall(activeChat!!.name, true) }) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = CyberCyan)
                    }
                }
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(msgs) { m -> Box(modifier = Modifier.padding(vertical = 6.dp).clip(RoundedCornerShape(14.dp)).background(GlassSurface).padding(12.dp)) { Text(m, color = StarlightWhite) } }
            }
            Row {
                OutlinedTextField(value = draft, onValueChange = { draft = it }, modifier = Modifier.weight(1f), placeholder = { Text("Send chat or Snap...") })
                IconButton(onClick = { if (draft.isNotBlank()) { msgs.add(draft); draft = "" } }) { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = PlasmaPink) }
            }
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(currentUser.username, color = StarlightWhite, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp)
                IconButton(onClick = { showNewGroup = true }) { Icon(Icons.Outlined.Edit, contentDescription = "New Squad", tint = StarlightWhite) }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.padding(bottom = 14.dp)) {
                items(listOf("Your note", "K 🍷", "Amanraj", "Vishu")) { noteUser ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        RotatingNeonRingBox(sizeDp = 66, borderWidth = 2f) {
                            Text(noteUser.first().toString(), color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(noteUser, color = MutedLavender, fontSize = 11.sp)
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 12.dp)) {
                listOf("Primary", "Requests", "General").forEach { tab ->
                    val active = selectedFilter == tab
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(if (active) NeonPurple else GlassSurface).clickable { selectedFilter = tab }.padding(horizontal = 14.dp, vertical = 7.dp)
                    ) { Text(tab, color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
        items(chats.filter { it.category == selectedFilter || selectedFilter == "Primary" }) { chat ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassSurface)
                    .clickable {
                        if (chat.hasUnopenedSnap) viewingSnapFrom = chat.name
                        else activeChat = chat
                    }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(chat.name, color = StarlightWhite, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🔥 ${chat.streakCount}", color = SnapYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = if (chat.hasUnopenedSnap) "🟥 Tap to view Snap • ${chat.time}" else chat.lastMsg,
                        color = if (chat.hasUnopenedSnap) PlasmaPink else if (chat.isTyping) CyberCyan else MutedLavender,
                        fontSize = 12.5.sp,
                        fontWeight = if (chat.hasUnopenedSnap) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Row {
                    IconButton(onClick = { onStartCall(chat.name, false) }) { Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(20.dp)) }
                    IconButton(onClick = { onStartCall(chat.name, true) }) { Icon(Icons.Default.Videocam, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(22.dp)) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullProfileTabScreen(
    accounts: List<UserProfileData>,
    activeIndex: Int,
    posts: List<PivoPost>,
    onSwitchAccount: (Int) -> Unit,
    onAddAccount: (UserProfileData) -> Unit,
    onOpenEditProfile: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val profile = accounts[activeIndex]
    var profileSubTab by remember { mutableIntStateOf(0) }
    var showAccountSwitcher by remember { mutableStateOf(false) }

    if (showAccountSwitcher) {
        ModalBottomSheet(onDismissRequest = { showAccountSwitcher = false }, containerColor = DeepNebula) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Text("Switch Account", color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(14.dp))
                accounts.forEachIndexed { index, acc ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable { onSwitchAccount(index); showAccountSwitcher = false }.padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RotatingNeonRingBox(sizeDp = 46, borderWidth = 2f) {
                                Text(acc.username.first().uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(acc.username, color = StarlightWhite, fontWeight = FontWeight.Bold)
                                Text("👻 Snap Score: ${acc.snapScore}", color = SnapYellow, fontSize = 11.5.sp)
                            }
                        }
                        if (index == activeIndex) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberCyan)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable {
                        onAddAccount(UserProfileData("New Creator", "pivo.creator${accounts.size + 1}", bio = "New PIVO Account ✨"))
                        showAccountSwitcher = false
                    }.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(GlassSurface).border(1.dp, GlassBorder, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = StarlightWhite)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Add PIVO account", color = StarlightWhite, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.clickable { showAccountSwitcher = true }, verticalAlignment = Alignment.CenterVertically) {
                Text(profile.username, color = StarlightWhite, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = StarlightWhite)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(SnapYellow.copy(alpha = 0.2f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text("👻 ${profile.snapScore}", color = SnapYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                IconButton(onClick = onOpenSettings) { Icon(Icons.Default.Menu, contentDescription = "Settings", tint = StarlightWhite, modifier = Modifier.size(28.dp)) }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            RotatingNeonRingBox(sizeDp = 92, borderWidth = 3f) {
                Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(HyperViolet, NeonPurple, PlasmaPink))), contentAlignment = Alignment.Center) {
                    Text(profile.name.first().uppercase(), fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatColumn("${posts.size}", "posts")
                StatColumn("19", "followers")
                StatColumn("13", "following")
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(profile.name, color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                if (profile.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Verified, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                }
            }
            if (profile.category.isNotBlank()) Text(profile.category, color = MutedLavender, fontSize = 12.5.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(profile.bio, color = StarlightWhite, fontSize = 13.5.sp, lineHeight = 18.sp)
            if (profile.website.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("🔗 ${profile.website}", color = CyberCyan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = onOpenEditProfile, modifier = Modifier.weight(1f).height(38.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = GlassSurface), contentPadding = PaddingValues(0.dp)) {
                Text("Edit profile", color = StarlightWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Button(onClick = { Toast.makeText(context, "Profile link copied: pivo.app/${profile.username}", Toast.LENGTH_SHORT).show() }, modifier = Modifier.weight(1f).height(38.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = GlassSurface), contentPadding = PaddingValues(0.dp)) {
                Text("Share profile", color = StarlightWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp).border(BorderStroke(0.5.dp, GlassBorder)), horizontalArrangement = Arrangement.SpaceAround) {
            listOf(Icons.Default.GridOn, Icons.Default.PlayCircleOutline, Icons.Default.Repeat, Icons.Default.PersonPin).forEachIndexed { idx, ic ->
                IconButton(onClick = { profileSubTab = idx }) {
                    Icon(ic, contentDescription = null, tint = if (profileSubTab == idx) StarlightWhite else MutedLavender)
                }
            }
        }

        val rows = posts.chunked(3)
        rows.forEach { rowPosts ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                rowPosts.forEach { post ->
                    Box(modifier = Modifier.weight(1f).aspectRatio(0.85f).background(Brush.linearGradient(post.cardGradient)).padding(8.dp), contentAlignment = Alignment.BottomStart) {
                        Text(post.caption, color = Color.White, fontSize = 10.sp, maxLines = 3, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold)
                    }
                }
                repeat(3 - rowPosts.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
fun StatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = StarlightWhite, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
        Text(label, color = MutedLavender, fontSize = 12.5.sp)
    }
}

@Composable
fun FullEditProfileScreen(profile: UserProfileData, onSave: (UserProfileData) -> Unit, onBack: () -> Unit) {
    var name by remember { mutableStateOf(profile.name) }
    var username by remember { mutableStateOf(profile.username) }
    var pronouns by remember { mutableStateOf(profile.pronouns) }
    var bio by remember { mutableStateOf(profile.bio) }
    var website by remember { mutableStateOf(profile.website) }
    var category by remember { mutableStateOf(profile.category) }
    var gender by remember { mutableStateOf(profile.gender) }
    var isAi by remember { mutableStateOf(profile.isAiProfile) }

    Column(modifier = Modifier.fillMaxSize().background(VoidBlack).statusBarsPadding().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = StarlightWhite) }
                Text("Edit profile", color = StarlightWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = { onSave(profile.copy(name = name, username = username, pronouns = pronouns, bio = bio, website = website, category = category, gender = gender, isAiProfile = isAi)) }) {
                Text("Done", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = pronouns, onValueChange = { pronouns = it }, label = { Text("Pronouns") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = website, onValueChange = { website = it }, label = { Text("Links") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = gender, onValueChange = { gender = it }, label = { Text("Gender") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("AI-generated profile", color = StarlightWhite, fontWeight = FontWeight.SemiBold)
                Text("Add label if profile features AI-generated avatar", color = MutedLavender, fontSize = 12.sp)
            }
            Switch(checked = isAi, onCheckedChange = { isAi = it })
        }
    }
}

@Composable
fun FullSettingsAndActivityScreen(profile: UserProfileData, onTogglePrivacy: (Boolean) -> Unit, onBack: () -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(VoidBlack).statusBarsPadding().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = StarlightWhite) }
            Text("Settings and activity", color = StarlightWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it }, placeholder = { Text("Search settings...") }, leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MutedLavender) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))

        SettingsSectionHeader("How you use PIVO")
        SettingsRowItem(Icons.Outlined.BookmarkBorder, "Saved") { Toast.makeText(context, "Saved Posts", Toast.LENGTH_SHORT).show() }
        SettingsRowItem(Icons.Outlined.History, "Archive") { Toast.makeText(context, "Archive", Toast.LENGTH_SHORT).show() }
        SettingsRowItem(Icons.Outlined.Insights, "Your activity") {}
        SettingsRowItem(Icons.Outlined.Notifications, "Notifications") {}

        SettingsSectionHeader("Who can see your content")
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = StarlightWhite)
                Spacer(modifier = Modifier.width(14.dp))
                Text("Profile privacy", color = StarlightWhite, fontSize = 15.sp)
            }
            Switch(checked = profile.isPrivate, onCheckedChange = onTogglePrivacy)
        }
        SettingsRowItem(Icons.Outlined.StarBorder, "Close Friends", "0") {}
        SettingsRowItem(Icons.Outlined.Block, "Blocked", "0") {}

        SettingsSectionHeader("More info and support 🇮🇳")
        SettingsRowItem(Icons.Outlined.SupportAgent, "WhatsApp Support", "+91 7464963392") {
            val url = "https://wa.me/917464963392?text=Hi%20PIVO%20Support%2C%20mujhe%20help%20chahiye"
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
        SettingsRowItem(Icons.Outlined.Info, "About PIVO", "v1.0 Beta • Made in India") {}

        Spacer(modifier = Modifier.height(20.dp))
        TextButton(onClick = onLogout) { Text("Log out ${profile.username}", color = PlasmaPink, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(title, color = MutedLavender, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp, bottom = 6.dp))
}

@Composable
fun SettingsRowItem(icon: ImageVector, title: String, trailingText: String = "", onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = StarlightWhite, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(title, color = StarlightWhite, fontSize = 15.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingText.isNotBlank()) { Text(trailingText, color = MutedLavender, fontSize = 13.sp); Spacer(modifier = Modifier.width(6.dp)) }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedLavender, modifier = Modifier.size(20.dp))
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ReelsTabScreen(reels: List<PivoReelItem>, onAddReelLink: (String, String) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { reels.size })
    var showLinkDialog by remember { mutableStateOf(false) }

    if (showLinkDialog) {
        var link by remember { mutableStateOf("") }
        var cap by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showLinkDialog = false }, containerColor = DeepNebula,
            title = { Text("Paste Instagram Reel Link 🎬", color = StarlightWhite) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = link, onValueChange = { link = it }, placeholder = { Text("https://www.instagram.com/reel/...") })
                    OutlinedTextField(value = cap, onValueChange = { cap = it }, placeholder = { Text("Caption...") })
                }
            },
            confirmButton = { Button(onClick = { if (link.isNotBlank()) onAddReelLink(link, cap); showLinkDialog = false }) { Text("Load ▶") } }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        VerticalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val reel = reels[page]
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(reel.bgGradient)), contentAlignment = Alignment.Center) {
                if (reel.embedUrl != null) {
                    AndroidView(factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.cacheMode = WebSettings.LOAD_DEFAULT
                            webViewClient = WebViewClient()
                            loadUrl(reel.embedUrl)
                        }
                    }, modifier = Modifier.fillMaxSize())
                } else {
                    Text(reel.caption, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(24.dp))
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("PIVO Reels 🎬", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Button(onClick = { showLinkDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = PlasmaPink)) { Text("+ Paste Link", color = Color.White) }
        }
    }
}
