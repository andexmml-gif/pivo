package com.pivo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// PIVO Cosmic Color Palette
val CosmicBg = Color(0xFF07040F)
val CosmicCard = Color(0xFF130B24)
val CosmicGlass = Color(0x332A1654)
val NeonPurple = Color(0xFF9D4EDD)
val ElectricViolet = Color(0xFF7B2CBF)
val GlowPink = Color(0xFFE056FD)
val TextPrimary = Color(0xFFF8F7FF)
val TextSecondary = Color(0xFFA99EC4)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = NeonPurple,
                    background = CosmicBg,
                    surface = CosmicCard,
                    onBackground = TextPrimary,
                    onSurface = TextPrimary
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
            .background(CosmicBg)
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
fun FourPointStar(modifier: Modifier = Modifier, color: Color = GlowPink) {
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A0836),
                        CosmicBg,
                        Color(0xFF120626),
                        CosmicBg
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.44f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x669D4EDD),
                        Color(0x227B2CBF),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width * 0.72f
                ),
                center = center,
                radius = size.width * 0.72f
            )
            val stars = listOf(
                Offset(size.width * 0.18f, size.height * 0.14f),
                Offset(size.width * 0.82f, size.height * 0.18f),
                Offset(size.width * 0.12f, size.height * 0.34f),
                Offset(size.width * 0.88f, size.height * 0.38f),
                Offset(size.width * 0.25f, size.height * 0.52f),
                Offset(size.width * 0.76f, size.height * 0.50f)
            )
            stars.forEach { pos ->
                drawCircle(color = Color.White.copy(alpha = 0.75f), radius = 3.5f, center = pos)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(top = 42.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(listOf(NeonPurple, ElectricViolet))
                        )
                        .border(1.dp, GlowPink.copy(alpha = 0.6f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    FourPointStar(modifier = Modifier.size(34.dp), color = Color.White)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "P I V O",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    letterSpacing = 6.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "YOUR WORLD. YOUR PEOPLE.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GlowPink,
                    letterSpacing = 2.5.sp
                )
            }

            Box(
                modifier = Modifier
                    .size(210.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                GlowPink.copy(alpha = 0.45f),
                                NeonPurple.copy(alpha = 0.28f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(1.dp, NeonPurple.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(74.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "✦ Cosmic Network",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Column(
                modifier = Modifier.padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "More than social.\nIt's your world.",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 33.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Connect with friends, share your moments and discover people around your world.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(26.dp))

                Button(
                    onClick = onCreateAccountClick,
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
                            .background(
                                Brush.horizontalGradient(listOf(NeonPurple, GlowPink))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✦  Create your account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        NeonPurple.copy(alpha = 0.7f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CosmicGlass
                    )
                ) {
                    Text(
                        text = "Log in",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "MADE IN INDIA 🇮🇳",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    letterSpacing = 2.sp
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicBg)
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isSignup) "Join PIVO ✦" else "Welcome back ✦",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Text(
            text = if (isSignup) "Apna cosmic profile banao aur duniya se judo."
            else "Apne PIVO world mein wapas swagat hai.",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

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

        Spacer(modifier = Modifier.height(26.dp))

        Button(
            onClick = { onSuccess(name.ifBlank { "Rahul" }) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
        ) {
            Text(
                text = if (isSignup) "Create Account 🚀" else "Log In ✦",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onSwitchMode,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = if (isSignup) "Already have an account? Log in"
                else "New to PIVO? Create an account",
                color = GlowPink
            )
        }
    }
}

data class PivoPost(
    val id: Int,
    val author: String,
    val handle: String,
    val time: String,
    val caption: String,
    val likes: Int,
    val comments: Int,
    val gradientColors: List<Color>
)

data class PivoChat(
    val name: String,
    val lastMsg: String,
    val time: String,
    val unread: Int,
    val isTyping: Boolean = false
)

@Composable
fun MainDashboardScreen(userName: String, onLogout: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = CosmicBg,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0E081C),
                contentColor = TextPrimary
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Feed") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Explore") },
                    label = { Text("Explore") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Chats") },
                    label = { Text("Chats") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> FeedTabScreen()
                1 -> ExploreTabScreen()
                2 -> ChatsTabScreen()
                3 -> ProfileTabScreen(userName = userName, onLogout = onLogout)
            }
        }
    }
}

@Composable
fun FeedTabScreen() {
    var selectedFilter by remember { mutableStateOf("For You") }
    val filters = listOf("For You", "Following", "Nearby")

    val posts = remember {
        listOf(
            PivoPost(
                1, "rahul_arya", "@rahul_arya", "2h ago",
                "Late night sunset vibes over the city skyline ✨💜 #PIVO #Cosmic",
                2400, 128,
                listOf(Color(0xFF5A189A), Color(0xFFE056FD), Color(0xFF240046))
            ),
            PivoPost(
                2, "pivo_creators", "@pivo_creators", "5h ago",
                "India's own next-gen social space is officially taking shape! 🚀🇮🇳",
                5120, 340,
                listOf(Color(0xFF3C096C), Color(0xFF9D4EDD), Color(0xFF10002B))
            )
        )
    }

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
                    FourPointStar(modifier = Modifier.size(22.dp), color = GlowPink)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PIVO",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = 3.sp
                    )
                }
                Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = TextPrimary)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filters.forEach { tab ->
                    val active = selectedFilter == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (active) NeonPurple else CosmicCard)
                            .border(
                                1.dp,
                                if (active) GlowPink else NeonPurple.copy(alpha = 0.25f),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedFilter = tab }
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tab,
                            color = if (active) Color.White else TextSecondary,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        items(posts) { post ->
            var liked by remember { mutableStateOf(false) }
            var saved by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(CosmicCard)
                    .border(1.dp, NeonPurple.copy(alpha = 0.25f), RoundedCornerShape(22.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonPurple, GlowPink))),
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
                        Text(post.author, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                        Text(post.time, color = TextSecondary, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(post.gradientColors)),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = post.caption,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

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
                                tint = if (liked) GlowPink else TextPrimary
                            )
                        }
                        Text(
                            text = "${post.likes + if (liked) 1 else 0}",
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment", tint = TextPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${post.comments}", color = TextPrimary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(14.dp))
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Share", tint = TextPrimary)
                    }

                    IconButton(onClick = { saved = !saved }) {
                        Icon(
                            imageVector = if (saved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (saved) GlowPink else TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExploreTabScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Explore World ✦", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search people, nearby vibes, reels...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text("Trending in India 🇮🇳", fontWeight = FontWeight.SemiBold, color = GlowPink)
        Spacer(modifier = Modifier.height(10.dp))
        listOf("#PIVOLaunch", "#BuildInPublic", "#MadeInIndia", "#CosmicVibes").forEach { tag ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CosmicCard)
                    .padding(16.dp)
            ) {
                Text(tag, color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ChatsTabScreen() {
    val stories = listOf("Your story", "Aditi", "Kunal", "Neha", "Aarav")
    val chats = listOf(
        PivoChat("Aditi Sharma", "Hey! Kaise ho? 🔥", "2m", 2),
        PivoChat("Kunal Verma", "Sketch bhej do bro", "10m", 0),
        PivoChat("Pivo Team", "Welcome to PIVO ✦", "30m", 1),
        PivoChat("Neha Singh", "Typing...", "now", 0, isTyping = true),
        PivoChat("Creators Hub", "New update is live! 🚀", "1h", 3)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Text(
                text = "Chats",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
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
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(CosmicCard)
                                .border(2.dp, GlowPink, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (person == "Your story") "+" else person.first().toString(),
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(person, color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        items(chats) { chat ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CosmicCard)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonPurple, ElectricViolet))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(chat.name.first().toString(), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(chat.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = chat.lastMsg,
                            color = if (chat.isTyping) GlowPink else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(chat.time, color = TextSecondary, fontSize = 11.sp)
                    if (chat.unread > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(GlowPink),
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
fun ProfileTabScreen(userName: String, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .size(92.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(NeonPurple, GlowPink))),
            contentAlignment = Alignment.Center
        ) {
            Text(userName.first().uppercase(), fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(userName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("@${userName.lowercase()}_pivo • Creator ✦", color = GlowPink, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CosmicCard)
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("12", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                Text("Posts", color = TextSecondary, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("1.4K", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                Text("Followers", color = TextSecondary, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("180", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                Text("Following", color = TextSecondary, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Log Out", color = GlowPink, fontWeight = FontWeight.Bold)
        }
    }
}
