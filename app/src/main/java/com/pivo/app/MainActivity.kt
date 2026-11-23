package com.pivo.app

import android.os.Bundle
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Ultra-Deep Cosmic Palette
val VoidBlack = Color(0xFF04020A)
val DeepNebula = Color(0xFF0E061F)
val GlassSurface = Color(0xCC160C2E)
val GlassBorder = Color(0x44B565FF)
val NeonPurple = Color(0xFF9D4EDD)
val HyperViolet = Color(0xFF6A00F4)
val PlasmaPink = Color(0xFFFF2A85)
val StarlightWhite = Color(0xFFF9F7FF)
val MutedLavender = Color(0xFFA599C7)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                PivoAppRoot()
            }
        }
    }
}

@Composable
fun PivoAppRoot() {
    var currentScreen by remember { mutableStateOf("welcome") }
    var currentUserName by remember { mutableStateOf("Rahul") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
    ) {
        when (currentScreen) {
            "welcome" -> CosmicWelcomeScreen(
                onCreateAccountClick = { currentScreen = "signup" },
                onLoginClick = { currentScreen = "login" }
            )
            "signup" -> AuthScreen(
                isSignup = true,
                onBack = { currentScreen = "welcome" },
                onSuccess = { name ->
                    if (name.isNotBlank()) currentUserName = name
                    currentScreen = "main"
                },
                onSwitchMode = { currentScreen = "login" }
            )
            "login" -> AuthScreen(
                isSignup = false,
                onBack = { currentScreen = "welcome" },
                onSuccess = { name ->
                    if (name.isNotBlank()) currentUserName = name
                    currentScreen = "main"
                },
                onSwitchMode = { currentScreen = "signup" }
            )
            "main" -> MainDashboardScreen(
                userName = currentUserName,
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
fun CosmicWelcomeScreen(
    onCreateAccountClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cosmic_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF210747),
                        VoidBlack,
                        Color(0xFF170533),
                        VoidBlack
                    )
                )
            )
    ) {
        // Multi-layered Nebula Glow + 3D Cosmic Globe Horizon
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val globeCenter = Offset(w / 2f, h * 0.45f)
            val globeRadius = w * 0.36f

            // Outer Cosmic Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        PlasmaPink.copy(alpha = 0.28f * pulseAlpha),
                        NeonPurple.copy(alpha = 0.35f),
                        HyperViolet.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = globeCenter,
                    radius = w * 0.85f
                ),
                center = globeCenter,
                radius = w * 0.85f
            )

            // Dark Planet Core
            drawCircle(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3A0CA3),
                        Color(0xFF10002B),
                        VoidBlack
                    ),
                    startY = globeCenter.y - globeRadius,
                    endY = globeCenter.y + globeRadius
                ),
                center = globeCenter,
                radius = globeRadius
            )

            // Glowing Rim Light on Top of Globe
            drawArc(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, PlasmaPink, Color.White, PlasmaPink, Color.Transparent)
                ),
                startAngle = 195f,
                sweepAngle = 150f,
                useCenter = false,
                topLeft = Offset(globeCenter.x - globeRadius, globeCenter.y - globeRadius),
                size = Size(globeRadius * 2, globeRadius * 2),
                style = Stroke(width = 5f)
            )

            // 3D Latitude & Longitude Grid Lines inside Globe
            for (i in -2..2) {
                val factor = i * 0.32f
                drawOval(
                    color = NeonPurple.copy(alpha = 0.32f),
                    topLeft = Offset(globeCenter.x - globeRadius * (1f - kotlin.math.abs(factor) * 0.6f), globeCenter.y - globeRadius),
                    size = Size(globeRadius * 2 * (1f - kotlin.math.abs(factor) * 0.6f), globeRadius * 2),
                    style = Stroke(width = 1.5f)
                )
                drawOval(
                    color = PlasmaPink.copy(alpha = 0.22f),
                    topLeft = Offset(globeCenter.x - globeRadius, globeCenter.y - globeRadius * (0.25f + kotlin.math.abs(factor) * 0.45f)),
                    size = Size(globeRadius * 2, globeRadius * 2 * (0.25f + kotlin.math.abs(factor) * 0.45f)),
                    style = Stroke(width = 1.5f)
                )
            }

            // Starfield
            val starCoords = listOf(
                Triple(0.14f, 0.11f, 3.5f), Triple(0.84f, 0.13f, 4f),
                Triple(0.24f, 0.22f, 2.5f), Triple(0.75f, 0.24f, 3f),
                Triple(0.10f, 0.36f, 3f),   Triple(0.90f, 0.35f, 3.5f),
                Triple(0.18f, 0.56f, 2.5f), Triple(0.83f, 0.54f, 3f),
                Triple(0.50f, 0.08f, 3f),   Triple(0.35f, 0.16f, 2f)
            )
            starCoords.forEach { (sx, sy, r) ->
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = r,
                    center = Offset(w * sx, h * sy)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Glowing Emblem & Brand Title
            Column(
                modifier = Modifier.padding(top = 34.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(66.dp)
                        .shadow(24.dp, RoundedCornerShape(22.dp), ambientColor = PlasmaPink, spotColor = NeonPurple)
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(listOf(PlasmaPink, NeonPurple, HyperViolet))
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(22.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    FourPointStar(modifier = Modifier.size(34.dp), color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PIVO",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = StarlightWhite,
                    letterSpacing = 8.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "YOUR WORLD. YOUR PEOPLE.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE0AAFF),
                    letterSpacing = 3.sp
                )
            }

            // Spacer allowing the 3D Globe Horizon in center to shine
            Spacer(modifier = Modifier.height(180.dp))

            // Bottom Glass Panel + Hero Text + Glowing CTA Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "More than social.\nIt's your world.",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = StarlightWhite,
                    textAlign = TextAlign.Center,
                    lineHeight = 35.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Connect with friends, share your moments and discover people around your world.",
                    fontSize = 13.5.sp,
                    color = MutedLavender,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(26.dp))

                // Glowing Gradient Primary CTA
                Button(
                    onClick = onCreateAccountClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(20.dp, RoundedCornerShape(18.dp), spotColor = PlasmaPink),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(listOf(HyperViolet, NeonPurple, PlasmaPink))
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FourPointStar(modifier = Modifier.size(16.dp), color = Color.White)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Create your account",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Frosted Glass Log In Button
                OutlinedButton(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, GlassBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = GlassSurface
                    )
                ) {
                    Text(
                        text = "Log in",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StarlightWhite
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "MADE IN INDIA 🇮🇳",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MutedLavender,
                    letterSpacing = 2.5.sp
                )
            }
        }
    }
}

@Composable
fun AuthScreen(
    isSignup: Boolean,
    onBack: () -> Unit,
    onSuccess: (String) -> Unit,
    onSwitchMode: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(DeepNebula, VoidBlack))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(GlassSurface)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = StarlightWhite)
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = if (isSignup) "Join PIVO ✦" else "Welcome back ✦",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = StarlightWhite
            )

            Text(
                text = if (isSignup) "Apna cosmic account banao aur duniya se judo."
                else "Apne PIVO world mein wapas swagat hai.",
                fontSize = 14.sp,
                color = MutedLavender,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(GlassSurface)
                    .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                if (isSignup) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name / Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onSuccess(name.ifBlank { "Rahul" }) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(listOf(HyperViolet, PlasmaPink))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isSignup) "Create Account 🚀" else "Log In ✦",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            TextButton(
                onClick = onSwitchMode,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (isSignup) "Already have an account? Log in"
                    else "New to PIVO? Create an account",
                    color = Color(0xFFE0AAFF),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

data class PivoPost(
    val id: Int,
    val author: String,
    val location: String,
    val time: String,
    val caption: String,
    val likes: Int,
    val comments: Int,
    val isVerified: Boolean,
    val cardGradient: List<Color>
)

data class PivoChat(
    val name: String,
    val lastMsg: String,
    val time: String,
    val unread: Int,
    val isOnline: Boolean = true,
    val isTyping: Boolean = false
)

@Composable
fun MainDashboardScreen(userName: String, onLogout: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val posts = remember {
        mutableStateListOf(
            PivoPost(
                1, "rahul_arya", "Agra • Sunset Point", "2h ago",
                "Chasing purple horizons after midnight coding ✨💜 #PIVO #CosmicVibes",
                2480, 142, true,
                listOf(Color(0xFF3C096C), Color(0xFF9D4EDD), Color(0xFFFF2A85))
            ),
            PivoPost(
                2, "pivo_creators", "New Delhi, India 🇮🇳", "5h ago",
                "India's own next-gen social platform is officially taking shape! 🚀",
                5390, 410, true,
                listOf(Color(0xFF10002B), Color(0xFF5A189A), Color(0xFFE056FD))
            )
        )
    }

    if (showCreateDialog) {
        var newCaption by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = DeepNebula,
            title = { Text("Create Cosmic Post ✦", color = StarlightWhite, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newCaption,
                    onValueChange = { newCaption = it },
                    placeholder = { Text("Share your moment with PIVO...") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCaption.isNotBlank()) {
                            posts.add(
                                0,
                                PivoPost(
                                    posts.size + 1,
                                    userName.lowercase(),
                                    "India 🇮🇳",
                                    "Just now",
                                    newCaption,
                                    1,
                                    0,
                                    true,
                                    listOf(Color(0xFF6A00F4), Color(0xFFFF2A85), Color(0xFF1A0836))
                                )
                            )
                        }
                        showCreateDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Text("Post 🚀", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = MutedLavender)
                }
            }
        )
    }

    Scaffold(
        containerColor = VoidBlack,
        bottomBar = {
            // Sleek Cosmic Bottom Bar with Center '+' Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(Color(0xEE0B0518))
                    .border(BorderStroke(0.5.dp, GlassBorder))
                    .padding(vertical = 10.dp, horizontal = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavIcon(Icons.Default.Home, selectedTab == 0) { selectedTab = 0 }
                BottomNavIcon(Icons.Default.Search, selectedTab == 1) { selectedTab = 1 }

                // Center Glowing Create Button (+)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .shadow(14.dp, CircleShape, spotColor = PlasmaPink)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(NeonPurple, PlasmaPink)))
                        .clickable { showCreateDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create", tint = Color.White, modifier = Modifier.size(26.dp))
                }

                BottomNavIcon(Icons.Default.ChatBubbleOutline, selectedTab == 2) { selectedTab = 2 }
                BottomNavIcon(Icons.Default.Person, selectedTab == 3) { selectedTab = 3 }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> FeedTabScreen(posts = posts)
                1 -> ExploreTabScreen()
                2 -> ChatsTabScreen()
                3 -> ProfileTabScreen(userName = userName, postCount = posts.size, onLogout = onLogout)
            }
        }
    }
}

@Composable
fun BottomNavIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) PlasmaPink else MutedLavender,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun FeedTabScreen(posts: List<PivoPost>) {
    var selectedFilter by remember { mutableStateOf("For You") }
    val filters = listOf("For You", "Following", "Nearby")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FourPointStar(modifier = Modifier.size(22.dp), color = PlasmaPink)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PIVO",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Black,
                        color = StarlightWhite,
                        letterSpacing = 4.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(GlassSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = StarlightWhite)
                }
            }

            // Filter Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filters.forEach { tab ->
                    val active = selectedFilter == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                if (active) Brush.horizontalGradient(listOf(HyperViolet, PlasmaPink))
                                else Brush.horizontalGradient(listOf(GlassSurface, GlassSurface))
                            )
                            .border(
                                1.dp,
                                if (active) Color.White.copy(alpha = 0.4f) else GlassBorder,
                                RoundedCornerShape(22.dp)
                            )
                            .clickable { selectedFilter = tab }
                            .padding(horizontal = 18.dp, vertical = 9.dp)
                    ) {
                        Text(
                            text = tab,
                            color = if (active) Color.White else MutedLavender,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        items(posts, key = { it.id }) { post ->
            var liked by remember { mutableStateOf(false) }
            var saved by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(GlassSurface)
                    .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(NeonPurple, PlasmaPink))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = post.author.first().uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(post.author, fontWeight = FontWeight.Bold, color = StarlightWhite, fontSize = 14.5.sp)
                                if (post.isVerified) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(15.dp))
                                }
                            }
                            Text("${post.location} • ${post.time}", color = MutedLavender, fontSize = 11.sp)
                        }
                    }
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = MutedLavender)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scenic Cosmic Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(235.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Brush.linearGradient(post.cardGradient))
                        .clickable { liked = !liked }
                ) {
                    // Decorative Moon / Glow Orb inside card
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.35f), Color.Transparent),
                                center = Offset(size.width * 0.78f, size.height * 0.25f),
                                radius = 140f
                            ),
                            center = Offset(size.width * 0.78f, size.height * 0.25f),
                            radius = 140f
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f))
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Text(
                            text = post.caption,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.5.sp,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { liked = !liked }) {
                            Icon(
                                imageVector = if (liked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (liked) PlasmaPink else StarlightWhite
                            )
                        }
                        Text("${post.likes + if (liked) 1 else 0}", color = StarlightWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment", tint = StarlightWhite)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${post.comments}", color = StarlightWhite, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Share", tint = StarlightWhite)
                    }

                    IconButton(onClick = { saved = !saved }) {
                        Icon(
                            imageVector = if (saved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (saved) PlasmaPink else StarlightWhite
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExploreTabScreen() {
    var query by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Explore World ✦", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = StarlightWhite)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search people, nearby vibes, reels...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PlasmaPink) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(22.dp))
        Text("Trending Across India 🇮🇳", fontWeight = FontWeight.Bold, color = Color(0xFFE0AAFF))
        Spacer(modifier = Modifier.height(10.dp))
        listOf(
            "#PIVOLaunch" to "14.2K moments",
            "#BuildInPublic" to "8.9K builders",
            "#MadeInIndia" to "32.1K creators",
            "#CosmicNights" to "5.4K reels"
        ).forEach { (tag, count) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassSurface)
                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(tag, color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(count, color = MutedLavender, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ChatsTabScreen() {
    var activeChatUser by remember { mutableStateOf<String?>(null) }
    val stories = listOf("Your story", "Aditi", "Kunal", "Neha", "Aarav")
    val chats = listOf(
        PivoChat("Aditi Sharma", "Hey! Kaise ho? 🔥", "2m", 2, isOnline = true),
        PivoChat("Kunal Verma", "Sketch bhej do bro", "10m", 0, isOnline = true),
        PivoChat("Pivo Team", "Welcome to PIVO ✦", "30m", 1, isOnline = true),
        PivoChat("Neha Singh", "Typing...", "now", 0, isOnline = true, isTyping = true),
        PivoChat("Creators Hub", "New update is live! 🚀", "1h", 3, isOnline = false)
    )

    if (activeChatUser != null) {
        val messages = remember {
            mutableStateListOf(
                "Hey! Welcome to PIVO Cosmic Chat ✨",
                "Bhai UI ekdum next-level lag raha hai! 🔥"
            )
        }
        var draft by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { activeChatUser = null }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = StarlightWhite)
                }
                Text(activeChatUser!!, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = StarlightWhite)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(messages) { msg ->
                    Box(
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(GlassSurface)
                            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Text(msg, color = StarlightWhite)
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    placeholder = { Text("Message ${activeChatUser}...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (draft.isNotBlank()) {
                            messages.add(draft)
                            draft = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PlasmaPink)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Text(
                text = "Chats",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = StarlightWhite,
                modifier = Modifier.padding(vertical = 14.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.padding(bottom = 18.dp)
            ) {
                items(stories) { person ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(62.dp)
                                .clip(CircleShape)
                                .background(GlassSurface)
                                .border(
                                    2.dp,
                                    Brush.linearGradient(listOf(NeonPurple, PlasmaPink)),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (person == "Your story") "+" else person.first().toString(),
                                color = StarlightWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(person, color = MutedLavender, fontSize = 11.sp)
                    }
                }
            }
        }

        items(chats) { chat ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(GlassSurface)
                    .border(1.dp, GlassBorder, RoundedCornerShape(18.dp))
                    .clickable { activeChatUser = chat.name }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(HyperViolet, NeonPurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(chat.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        if (chat.isOnline) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                                    .border(2.dp, VoidBlack, CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(chat.name, color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = chat.lastMsg,
                            color = if (chat.isTyping) PlasmaPink else MutedLavender,
                            fontSize = 13.sp,
                            fontWeight = if (chat.isTyping) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(chat.time, color = MutedLavender, fontSize = 11.sp)
                    if (chat.unread > 0) {
                        Spacer(modifier = Modifier.height(5.dp))
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(PlasmaPink),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${chat.unread}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTabScreen(userName: String, postCount: Int, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(96.dp)
                .shadow(20.dp, CircleShape, spotColor = PlasmaPink)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(HyperViolet, NeonPurple, PlasmaPink)))
                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(userName.first().uppercase(), fontSize = 38.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(userName, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, color = StarlightWhite)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
        }
        Text("@${userName.lowercase()}_pivo • Founder ✦", color = Color(0xFFE0AAFF), fontSize = 13.sp)
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(GlassSurface)
                .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$postCount", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = StarlightWhite)
                Text("Posts", color = MutedLavender, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("1.4K", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = StarlightWhite)
                Text("Followers", color = MutedLavender, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("180", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = StarlightWhite)
                Text("Following", color = MutedLavender, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, PlasmaPink.copy(alpha = 0.6f))
        ) {
            Text("Log Out", color = PlasmaPink, fontWeight = FontWeight.Bold)
        }
    }
}
