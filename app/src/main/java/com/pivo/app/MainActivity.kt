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
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

val VoidBlack = Color(0xFF05030C)
val DeepNebula = Color(0xFF100822)
val GlassSurface = Color(0xCC170E2E)
val GlassBorder = Color(0x44B565FF)
val NeonPurple = Color(0xFF9D4EDD)
val HyperViolet = Color(0xFF6A00F4)
val PlasmaPink = Color(0xFFFF2A85)
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
    val isPrivate: Boolean = false
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
fun PivoAppRoot(incomingSharedLink: String? = null) {
    var currentScreen by remember { mutableStateOf(if (incomingSharedLink != null) "main" else "welcome") }

    val accounts = remember {
        mutableStateListOf(
            UserProfileData(
                name = "pivo™",
                username = "pivo.app",
                bio = "Your people, your moments.\nfollow @pivo.devlopers • Made in India 🇮🇳",
                category = "Software Company",
                isVerified = true
            ),
            UserProfileData(
                name = "Rahul Arya",
                username = "rahul.arya.fzd",
                bio = "Founder @pivo.app 🚀 | Building in Public",
                category = "Entrepreneur",
                isVerified = true
            )
        )
    }
    var activeAccountIndex by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize().background(VoidBlack)) {
        when (currentScreen) {
            "welcome" -> CosmicWelcomeScreen(
                onCreateAccountClick = { currentScreen = "signup" },
                onLoginClick = { currentScreen = "login" }
            )
            "signup" -> AuthScreen(
                isSignup = true,
                onBack = { currentScreen = "welcome" },
                onSuccess = { name ->
                    if (name.isNotBlank()) {
                        accounts[activeAccountIndex] = accounts[activeAccountIndex].copy(name = name)
                    }
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
                onAddAccount = { newProfile ->
                    accounts.add(newProfile)
                    activeAccountIndex = accounts.lastIndex
                },
                onUpdateProfile = { updated -> accounts[activeAccountIndex] = updated },
                onLogout = { currentScreen = "welcome" }
            )
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
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.55f, targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(2800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF23084C), VoidBlack, Color(0xFF190638), VoidBlack))
        )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val horizonCenter = Offset(w / 2f, h * 1.18f)
            val horizonRadius = w * 1.35f
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(PlasmaPink.copy(alpha = 0.32f * glowPulse), NeonPurple.copy(alpha = 0.28f), Color.Transparent),
                    center = horizonCenter, radius = horizonRadius * 1.12f
                ),
                center = horizonCenter, radius = horizonRadius * 1.12f
            )
            drawCircle(brush = Brush.verticalGradient(listOf(Color(0xFF1E0840), VoidBlack)), center = horizonCenter, radius = horizonRadius)
            drawArc(
                brush = Brush.horizontalGradient(listOf(Color.Transparent, NeonPurple, PlasmaPink, Color.White, PlasmaPink, NeonPurple, Color.Transparent)),
                startAngle = 225f, sweepAngle = 90f, useCenter = false,
                topLeft = Offset(horizonCenter.x - horizonRadius, horizonCenter.y - horizonRadius),
                size = Size(horizonRadius * 2, horizonRadius * 2), style = Stroke(width = 4.5f)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(top = 44.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(72.dp).shadow(28.dp, RoundedCornerShape(24.dp), spotColor = NeonPurple)
                        .clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(listOf(PlasmaPink, NeonPurple, HyperViolet))),
                    contentAlignment = Alignment.Center
                ) { FourPointStar(modifier = Modifier.size(38.dp), color = Color.White) }
                Spacer(modifier = Modifier.height(18.dp))
                Text("PIVO", fontSize = 42.sp, fontWeight = FontWeight.Black, color = StarlightWhite, letterSpacing = 9.sp)
                Text("YOUR WORLD. YOUR PEOPLE.", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE0AAFF), letterSpacing = 3.sp)
            }

            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("More than social.\nIt's your world.", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = StarlightWhite, textAlign = TextAlign.Center, lineHeight = 37.sp)
                Spacer(modifier = Modifier.height(26.dp))
                Button(
                    onClick = onCreateAccountClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(HyperViolet, NeonPurple, PlasmaPink))), contentAlignment = Alignment.Center) {
                        Text("Create your account", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
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

    Column(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(DeepNebula, VoidBlack))).statusBarsPadding().padding(24.dp)) {
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

data class PivoPost(val id: Int, val author: String, val location: String, val time: String, val caption: String, val likes: Int, val comments: Int, val isVerified: Boolean, val cardGradient: List<Color>)
data class PivoReelItem(val id: Int, val sharedBy: String, val caption: String, val embedUrl: String?, val likes: Int, val bgGradient: List<Color>)
data class PivoChat(val name: String, val lastMsg: String, val time: String, val unread: Int, val category: String = "Primary", val isGroup: Boolean = false, val isTyping: Boolean = false)

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
    var selectedTab by remember { mutableIntStateOf(if (incomingSharedLink != null) 2 else 0) }
    var subScreen by remember { mutableStateOf("none") } // "none", "edit_profile", "settings"

    val posts = remember {
        mutableStateListOf(
            PivoPost(1, "pivo.app", "New Delhi • HQ", "1h ago", "Project Fail Ho Gaya? Koi Baat Nahi! Kabhi Har Nahi Manni Chahiye! 🚀💜 #PIVO", 390, 42, true, listOf(Color(0xFF3C096C), Color(0xFF9D4EDD), Color(0xFFFF2A85))),
            PivoPost(2, "pivo.app", "Made in India 🇮🇳", "3h ago", "Social Media Mein Bhi Fair System! No Random Suspensions, Creator Friendly ✦", 720, 68, true, listOf(Color(0xFF10002B), Color(0xFF5A189A), Color(0xFFE056FD))),
            PivoPost(3, "pivo.app", "Cosmic Lab", "1d ago", "PIVO — Built to Connect. Made in India 🇮🇳🔥", 930, 95, true, listOf(Color(0xFF240046), Color(0xFFFF2A85), Color(0xFF3A0CA3))),
            PivoPost(4, "pivo.app", "Beta Preview", "2d ago", "More than social. It's your world. Coming Soon 2026 ✨", 1280, 140, true, listOf(Color(0xFF1A0836), Color(0xFF6A00F4), Color(0xFF38BDF8)))
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
            PivoChat("GEN-Z Squad", "vishu is typing...", "now", 2, category = "Primary", isGroup = true, isTyping = true),
            PivoChat("HOMIES SQUAD 🥂", "Nehal Kharb: Hlw • 11m", "11m", 1, category = "Primary", isGroup = true),
            PivoChat("kashi group 🙏💙", "Seen by Rajat | Rao + 2", "1h", 0, category = "Primary", isGroup = true),
            PivoChat("New Creator Collab", "Hey! Want to join PIVO beta?", "2h", 1, category = "Requests")
        )
    }

    if (subScreen == "edit_profile") {
        FullEditProfileScreen(
            profile = currentUser,
            onSave = { updated ->
                onUpdateProfile(updated)
                subScreen = "none"
            },
            onBack = { subScreen = "none" }
        )
        return
    }

    if (subScreen == "settings") {
        FullSettingsAndActivityScreen(
            profile = currentUser,
            onTogglePrivacy = { onUpdateProfile(currentUser.copy(isPrivate = it)) },
            onBack = { subScreen = "none" },
            onLogout = onLogout
        )
        return
    }

    Scaffold(
        containerColor = VoidBlack,
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().navigationBarsPadding().background(Color(0xEE0B0518)).border(BorderStroke(0.5.dp, GlassBorder)).padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavIcon(Icons.Default.Home, selectedTab == 0) { selectedTab = 0 }
                BottomNavIcon(Icons.Default.PlayCircleOutline, selectedTab == 1) { selectedTab = 1 }
                Box(
                    modifier = Modifier.size(44.dp).shadow(14.dp, CircleShape, spotColor = PlasmaPink).clip(CircleShape).background(Brush.linearGradient(listOf(NeonPurple, PlasmaPink))).clickable { selectedTab = 2 },
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Chats", tint = Color.White, modifier = Modifier.size(22.dp)) }
                BottomNavIcon(Icons.Default.Search, selectedTab == 3) { selectedTab = 3 }
                BottomNavIcon(Icons.Default.Person, selectedTab == 4) { selectedTab = 4 }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> FeedTabScreen(posts = posts, onAddPost = { cap ->
                    posts.add(0, PivoPost(posts.size + 1, currentUser.username, "India 🇮🇳", "Just now", cap, 1, 0, currentUser.isVerified, listOf(Color(0xFF6A00F4), Color(0xFFFF2A85))))
                })
                1 -> ReelsTabScreen(reels = reels, onAddReelLink = { url, cap ->
                    reels.add(0, PivoReelItem(reels.size + 10, currentUser.username, cap.ifBlank { "Shared Reel 🎬" }, extractInstagramEmbedUrl(url), 1, listOf(Color(0xFF3A0CA3), Color(0xFFFF2A85))))
                })
                2 -> ChatsTabScreen(currentUser = currentUser, chats = chats, onCreateGroup = { gName ->
                    chats.add(0, PivoChat(gName, "Squad created! Invite friends 🚀", "now", 1, category = "Primary", isGroup = true))
                })
                3 -> ExploreTabScreen()
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

@Composable
fun BottomNavIcon(icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(imageVector = icon, contentDescription = null, tint = if (selected) PlasmaPink else MutedLavender, modifier = Modifier.size(26.dp))
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
    var profileSubTab by remember { mutableIntStateOf(0) } // 0: Grid, 1: Reels, 2: Reposts, 3: Tagged
    var showAccountSwitcher by remember { mutableStateOf(false) }

    if (showAccountSwitcher) {
        ModalBottomSheet(onDismissRequest = { showAccountSwitcher = false }, containerColor = DeepNebula) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Text("Switch Account", color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(14.dp))
                accounts.forEachIndexed { index, acc ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable {
                            onSwitchAccount(index)
                            showAccountSwitcher = false
                        }.padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(Brush.linearGradient(listOf(NeonPurple, PlasmaPink))), contentAlignment = Alignment.Center) {
                                Text(acc.username.first().uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(acc.username, color = StarlightWhite, fontWeight = FontWeight.Bold)
                                Text(acc.name, color = MutedLavender, fontSize = 12.sp)
                            }
                        }
                        if (index == activeIndex) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF38BDF8))
                        }
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
        // Top Header (Username switcher + Menu)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.clickable { showAccountSwitcher = true }, verticalAlignment = Alignment.CenterVertically) {
                if (profile.isPrivate) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = StarlightWhite, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(profile.username, color = StarlightWhite, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = StarlightWhite)
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Default.Menu, contentDescription = "Settings and Activity", tint = StarlightWhite, modifier = Modifier.size(28.dp))
            }
        }

        // Avatar + Stats Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.size(84.dp).clip(CircleShape).background(Brush.linearGradient(listOf(HyperViolet, NeonPurple, PlasmaPink))).border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) { Text(profile.name.first().uppercase(), fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color.White) }

            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatColumn("${posts.size}", "posts")
                StatColumn("19", "followers")
                StatColumn("13", "following")
            }
        }

        // Name, Category, Bio & Website Link
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(profile.name, color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                if (profile.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                }
                if (profile.pronouns.isNotBlank()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(profile.pronouns, color = MutedLavender, fontSize = 13.sp)
                }
            }
            if (profile.category.isNotBlank()) {
                Text(profile.category, color = MutedLavender, fontSize = 12.5.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(profile.bio, color = StarlightWhite, fontSize = 13.5.sp, lineHeight = 18.sp)
            if (profile.website.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("🔗 ${profile.website}", color = Color(0xFF7DD3FC), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Professional Dashboard Card
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clip(RoundedCornerShape(14.dp)).background(GlassSurface).border(1.dp, GlassBorder, RoundedCornerShape(14.dp)).padding(14.dp)
        ) {
            Text("Your dashboard", color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
            Text("↗ 323 views in the last 30 days.", color = MutedLavender, fontSize = 12.sp)
        }

        // Edit Profile & Share Profile Buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onOpenEditProfile,
                modifier = Modifier.weight(1f).height(38.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlassSurface),
                contentPadding = PaddingValues(0.dp)
            ) { Text("Edit profile", color = StarlightWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }

            Button(
                onClick = { Toast.makeText(context, "Profile link copied: pivo.app/${profile.username}", Toast.LENGTH_SHORT).show() },
                modifier = Modifier.weight(1f).height(38.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlassSurface),
                contentPadding = PaddingValues(0.dp)
            ) { Text("Share profile", color = StarlightWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
        }

        // 4 Profile Sub-Tabs (Grid, Reels, Reposts, Tagged)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).border(BorderStroke(0.5.dp, GlassBorder)),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf(Icons.Default.GridOn, Icons.Default.PlayCircleOutline, Icons.Default.Repeat, Icons.Default.PersonPin).forEachIndexed { idx, ic ->
                IconButton(onClick = { profileSubTab = idx }) {
                    Icon(ic, contentDescription = null, tint = if (profileSubTab == idx) StarlightWhite else MutedLavender)
                }
            }
        }

        // 3-Column Post Grid
        val rows = posts.chunked(3)
        rows.forEach { rowPosts ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                rowPosts.forEach { post ->
                    Box(
                        modifier = Modifier.weight(1f).aspectRatio(0.85f).background(Brush.linearGradient(post.cardGradient)).padding(8.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
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
            TextButton(onClick = {
                onSave(profile.copy(name = name, username = username, pronouns = pronouns, bio = bio, website = website, category = category, gender = gender, isAiProfile = isAi))
            }) { Text("Done", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 16.sp) }
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
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (e.g. Software Company)") }, modifier = Modifier.fillMaxWidth())
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
        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            placeholder = { Text("Search settings...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MutedLavender) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)
        )

        SettingsSectionHeader("How you use PIVO")
        SettingsRowItem(Icons.Outlined.BookmarkBorder, "Saved") { Toast.makeText(context, "Saved Posts", Toast.LENGTH_SHORT).show() }
        SettingsRowItem(Icons.Outlined.History, "Archive") { Toast.makeText(context, "Story & Post Archive", Toast.LENGTH_SHORT).show() }
        SettingsRowItem(Icons.Outlined.Insights, "Your activity") {}
        SettingsRowItem(Icons.Outlined.Notifications, "Notifications") {}
        SettingsRowItem(Icons.Outlined.Schedule, "Time management") {}

        SettingsSectionHeader("Your insights and tools")
        SettingsRowItem(Icons.Outlined.BarChart, "Professional dashboard", "323 views") {}
        SettingsRowItem(Icons.Outlined.Verified, "PIVO Verified", if (profile.isVerified) "Active" else "Apply") {}

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

        SettingsSectionHeader("How others can interact with you")
        SettingsRowItem(Icons.Outlined.ChatBubbleOutline, "Messages and story replies") {}
        SettingsRowItem(Icons.Outlined.AlternateEmail, "Tags and mentions") {}
        SettingsRowItem(Icons.Outlined.TextFields, "Hidden Words") {}

        SettingsSectionHeader("More info and support 🇮🇳")
        SettingsRowItem(Icons.Outlined.SupportAgent, "WhatsApp Support", "+91 7464963392") {
            val url = "https://wa.me/917464963392?text=Hi%20PIVO%20Support%2C%20mujhe%20help%20chahiye"
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
        SettingsRowItem(Icons.Outlined.PrivacyTip, "Privacy Center") {}
        SettingsRowItem(Icons.Outlined.Info, "About PIVO", "v1.0 Beta • Made in India") {}

        Spacer(modifier = Modifier.height(20.dp))
        TextButton(onClick = onLogout) {
            Text("Log out ${profile.username}", color = PlasmaPink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(title, color = MutedLavender, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp, bottom = 6.dp))
}

@Composable
fun SettingsRowItem(icon: ImageVector, title: String, trailingText: String = "", onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = StarlightWhite, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(title, color = StarlightWhite, fontSize = 15.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingText.isNotBlank()) {
                Text(trailingText, color = MutedLavender, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MutedLavender, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun FeedTabScreen(posts: List<PivoPost>, onAddPost: (String) -> Unit) {
    var showCreate by remember { mutableStateOf(false) }
    if (showCreate) {
        var cap by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreate = false }, containerColor = DeepNebula,
            title = { Text("New PIVO Post ✦", color = StarlightWhite) },
            text = { OutlinedTextField(value = cap, onValueChange = { cap = it }, placeholder = { Text("Share something...") }) },
            confirmButton = { Button(onClick = { if (cap.isNotBlank()) onAddPost(cap); showCreate = false }) { Text("Post") } }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showCreate = true }) { Icon(Icons.Default.Add, contentDescription = null, tint = StarlightWhite) }
                Text("PIVO", fontSize = 24.sp, fontWeight = FontWeight.Black, color = StarlightWhite, letterSpacing = 4.sp)
                Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, tint = StarlightWhite)
            }
        }
        items(posts, key = { it.id }) { post ->
            var liked by remember { mutableStateOf(false) }
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp).clip(RoundedCornerShape(22.dp)).background(GlassSurface).border(1.dp, GlassBorder, RoundedCornerShape(22.dp)).padding(14.dp)) {
                Text(post.author, color = StarlightWhite, fontWeight = FontWeight.Bold)
                Text(post.location, color = MutedLavender, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(210.dp).clip(RoundedCornerShape(16.dp)).background(Brush.linearGradient(post.cardGradient)).clickable { liked = !liked }.padding(16.dp),
                    contentAlignment = Alignment.BottomStart
                ) { Text(post.caption, color = Color.White, fontWeight = FontWeight.SemiBold) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { liked = !liked }) { Icon(if (liked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = null, tint = if (liked) PlasmaPink else StarlightWhite) }
                    Text("${post.likes + if (liked) 1 else 0}", color = StarlightWhite)
                }
            }
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

@Composable
fun ChatsTabScreen(currentUser: UserProfileData, chats: List<PivoChat>, onCreateGroup: (String) -> Unit) {
    var selectedFilter by remember { mutableStateOf("Primary") }
    var activeChat by remember { mutableStateOf<PivoChat?>(null) }
    var showNewGroup by remember { mutableStateOf(false) }

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { activeChat = null }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = StarlightWhite) }
                Text(activeChat!!.name, color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(msgs) { m -> Box(modifier = Modifier.padding(vertical = 6.dp).clip(RoundedCornerShape(14.dp)).background(GlassSurface).padding(12.dp)) { Text(m, color = StarlightWhite) } }
            }
            Row {
                OutlinedTextField(value = draft, onValueChange = { draft = it }, modifier = Modifier.weight(1f), placeholder = { Text("Message...") })
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
            // Notes Row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.padding(bottom = 14.dp)) {
                items(listOf("Your note", "K 🍷", "Amanraj", "Vishu")) { noteUser ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(62.dp).clip(CircleShape).background(GlassSurface).border(1.5.dp, NeonPurple, CircleShape), contentAlignment = Alignment.Center) {
                            Text(noteUser.first().toString(), color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(noteUser, color = MutedLavender, fontSize = 11.sp)
                    }
                }
            }
            // Filter Pills: Primary | Requests | General
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
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clip(RoundedCornerShape(16.dp)).background(GlassSurface).clickable { activeChat = chat }.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(chat.name, color = StarlightWhite, fontWeight = FontWeight.Bold)
                    Text(chat.lastMsg, color = if (chat.isTyping) PlasmaPink else MutedLavender, fontSize = 12.5.sp)
                }
                Text(chat.time, color = MutedLavender, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun ExploreTabScreen() {
    var q by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Explore ✦", color = StarlightWhite, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(value = q, onValueChange = { q = it }, placeholder = { Text("Search PIVO...") }, modifier = Modifier.fillMaxWidth())
    }
}
