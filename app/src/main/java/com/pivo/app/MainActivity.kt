package com.pivo.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Optional: Cloudinary credentials (अगर नहीं डालोगे तो भी Base64 से Firestore में परमानेंट सेव होगा)
const val CLOUDINARY_CLOUD_NAME = "YOUR_CLOUD_NAME"
const val CLOUDINARY_UPLOAD_PRESET = "YOUR_UNSIGNED_PRESET"

// केवल इन ऑफिशियल/एडमिन यूज़रनेम्स को डिफ़ॉल्ट Blue Tick मिलेगा
val OFFICIAL_VERIFIED_USERS = setOf("pivo.app", "pivo.devlopers", "rahulshankhwar421")

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
    val uid: String = "",
    val name: String = "",
    val username: String = "",
    val photoData: String = "", // HTTP URL या "data:image/jpeg;base64,..." दोनों सपोर्टेड
    val pronouns: String = "",
    val bio: String = "Made in India 🇮🇳",
    val website: String = "",
    val category: String = "",
    val gender: String = "Prefer not to say",
    val isAiProfile: Boolean = false,
    val isVerified: Boolean = false,
    val isPrivate: Boolean = false,
    val snapScore: Int = 0,
    val followers: List<String> = emptyList(),
    val following: List<String> = emptyList()
)

data class LiveStoryItem(
    val id: String = "",
    val uid: String = "",
    val username: String = "",
    val userPhoto: String = "",
    val caption: String = "",
    val imageData: String = "",
    val isLive: Boolean = false,
    val timestamp: Long = 0L
)

data class LivePostItem(
    val id: String = "",
    val authorUid: String = "",
    val authorUsername: String = "",
    val authorPhoto: String = "",
    val authorVerified: Boolean = false,
    val location: String = "",
    val caption: String = "",
    val imageData: String = "",
    val likedBy: List<String> = emptyList(),
    val comments: List<String> = emptyList(),
    val timestamp: Long = 0L
)

data class PivoReelItem(
    val id: String,
    val sharedBy: String,
    val caption: String,
    val embedUrl: String?,
    val bgGradient: List<Color>
)

data class LiveChatThread(
    val chatId: String = "",
    val peerUid: String = "",
    val peerUsername: String = "",
    val peerPhoto: String = "",
    val lastMsg: String = "",
    val isSnap: Boolean = false,
    val streakCount: Int = 0,
    val timestamp: Long = 0L
)

data class LiveMessageItem(
    val id: String = "",
    val senderUid: String = "",
    val senderName: String = "",
    val text: String = "",
    val imageData: String = "",
    val isSnap: Boolean = false,
    val timestamp: Long = 0L
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

// फोटो को कंप्रेस करके परमानेंट स्टोरेज (Cloudinary या Base64 Data URI) में बदलता है — कभी गायब नहीं होगी!
suspend fun processAndUploadImagePermanent(
    context: Context,
    imageUri: Uri,
    maxDimension: Int = 420,
    quality: Int = 75
): String? = withContext(Dispatchers.IO) {
    try {
        val inputStream = context.contentResolver.openInputStream(imageUri) ?: return@withContext null
        val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return@withContext null
        inputStream.close()

        val ratio = minOf(
            maxDimension.toFloat() / originalBitmap.width,
            maxDimension.toFloat() / originalBitmap.height,
            1f
        )
        val targetW = (originalBitmap.width * ratio).toInt().coerceAtLeast(1)
        val targetH = (originalBitmap.height * ratio).toInt().coerceAtLeast(1)
        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetW, targetH, true)

        val baos = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        val imageBytes = baos.toByteArray()

        if (CLOUDINARY_CLOUD_NAME != "YOUR_CLOUD_NAME" && CLOUDINARY_UPLOAD_PRESET != "YOUR_UNSIGNED_PRESET") {
            val boundary = "----PivoBoundary${System.currentTimeMillis()}"
            val url = URL("https://api.cloudinary.com/v1_1/$CLOUDINARY_CLOUD_NAME/image/upload")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            }
            DataOutputStream(conn.outputStream).use { out ->
                out.writeBytes("--$boundary\r\n")
                out.writeBytes("Content-Disposition: form-data; name=\"upload_preset\"\r\n\r\n")
                out.writeBytes("$CLOUDINARY_UPLOAD_PRESET\r\n")
                out.writeBytes("--$boundary\r\n")
                out.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"pivo_img.jpg\"\r\n")
                out.writeBytes("Content-Type: image/jpeg\r\n\r\n")
                out.write(imageBytes)
                out.writeBytes("\r\n--$boundary--\r\n")
            }
            if (conn.responseCode in 200..299) {
                val resp = conn.inputStream.bufferedReader().readText()
                val secureUrl = JSONObject(resp).optString("secure_url")
                if (secureUrl.isNotBlank()) return@withContext secureUrl
            }
        }

        // परमानेंट Base64 Data URI (Firestore में सीधे सेव होने के लिए)
        val base64Str = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
        "data:image/jpeg;base64,$base64Str"
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun saveUserProfileToFirestore(profile: UserProfileData) {
    if (profile.uid.isBlank()) return
    val data = hashMapOf(
        "uid" to profile.uid,
        "name" to profile.name,
        "username" to profile.username,
        "photoData" to profile.photoData,
        "pronouns" to profile.pronouns,
        "bio" to profile.bio,
        "website" to profile.website,
        "category" to profile.category,
        "gender" to profile.gender,
        "isAiProfile" to profile.isAiProfile,
        "isVerified" to profile.isVerified,
        "isPrivate" to profile.isPrivate,
        "snapScore" to profile.snapScore
    )
    FirebaseFirestore.getInstance()
        .collection("users")
        .document(profile.uid)
        .set(data, SetOptions.merge())
}

// URL और Base64 दोनों तरह की फोटो दिखाने वाला यूनिवर्सल Avatar व्यू
@Composable
fun UniversalPhotoView(
    photoData: String,
    fallbackText: String,
    fontSizeSp: Int = 20,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    if (photoData.startsWith("data:image")) {
        val bitmap = remember(photoData) {
            try {
                val cleanBase64 = photoData.substringAfter("base64,")
                val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        } else {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                Text(fallbackText.firstOrNull()?.uppercase() ?: "P", fontSize = fontSizeSp.sp, fontWeight = FontWeight.Black, color = Color.White)
            }
        }
    } else if (photoData.isNotBlank()) {
        AsyncImage(
            model = photoData,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(fallbackText.firstOrNull()?.uppercase() ?: "P", fontSize = fontSizeSp.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
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
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    var currentScreen by remember {
        mutableStateOf(
            if (auth.currentUser != null || incomingSharedLink != null) "main" else "welcome"
        )
    }

    var currentUserProfile by remember {
        val u = auth.currentUser
        val uname = u?.email?.substringBefore("@")?.lowercase() ?: ""
        mutableStateOf(
            UserProfileData(
                uid = u?.uid ?: "",
                name = u?.displayName?.takeIf { it.isNotBlank() } ?: uname,
                username = uname,
                isVerified = uname in OFFICIAL_VERIFIED_USERS
            )
        )
    }

    // Real-time Firestore Profile Listener — DP और Profile कभी नहीं हटेगी!
    DisposableEffect(auth) {
        var docReg: com.google.firebase.firestore.ListenerRegistration? = null
        val authListener = FirebaseAuth.AuthStateListener { fa ->
            val user = fa.currentUser
            docReg?.remove()
            if (user != null) {
                currentScreen = "main"
                val defaultUsername = user.email?.substringBefore("@")?.lowercase() ?: "user"
                val isOfficial = defaultUsername in OFFICIAL_VERIFIED_USERS

                docReg = db.collection("users").document(user.uid)
                    .addSnapshotListener { doc, _ ->
                        if (doc != null && doc.exists()) {
                            val uname = doc.getString("username") ?: defaultUsername
                            currentUserProfile = UserProfileData(
                                uid = user.uid,
                                name = doc.getString("name") ?: uname,
                                username = uname,
                                photoData = doc.getString("photoData") ?: "",
                                pronouns = doc.getString("pronouns") ?: "",
                                bio = doc.getString("bio") ?: "Made in India 🇮🇳",
                                website = doc.getString("website") ?: "",
                                category = doc.getString("category") ?: "",
                                gender = doc.getString("gender") ?: "Prefer not to say",
                                isAiProfile = doc.getBoolean("isAiProfile") ?: false,
                                isVerified = (doc.getBoolean("isVerified") ?: false) || (uname in OFFICIAL_VERIFIED_USERS),
                                isPrivate = doc.getBoolean("isPrivate") ?: false,
                                snapScore = (doc.getLong("snapScore") ?: 0L).toInt(),
                                followers = (doc.get("followers") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                                following = (doc.get("following") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                            )
                        } else {
                            val freshProfile = UserProfileData(
                                uid = user.uid,
                                name = user.displayName?.takeIf { it.isNotBlank() } ?: defaultUsername,
                                username = defaultUsername,
                                isVerified = isOfficial
                            )
                            currentUserProfile = freshProfile
                            saveUserProfileToFirestore(freshProfile)
                        }
                    }
            } else {
                if (incomingSharedLink == null) currentScreen = "welcome"
            }
        }
        auth.addAuthStateListener(authListener)
        onDispose {
            docReg?.remove()
            auth.removeAuthStateListener(authListener)
        }
    }

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
                    onSuccess = { createdProfile ->
                        currentUserProfile = createdProfile
                        saveUserProfileToFirestore(createdProfile)
                        currentScreen = "main"
                    },
                    onSwitchMode = { currentScreen = "login" }
                )
                "login" -> AuthScreen(
                    isSignup = false,
                    onBack = { currentScreen = "welcome" },
                    onSuccess = { loggedProfile ->
                        currentUserProfile = loggedProfile
                        currentScreen = "main"
                    },
                    onSwitchMode = { currentScreen = "signup" }
                )
                "main" -> MainDashboardScreen(
                    currentUser = currentUserProfile,
                    incomingSharedLink = incomingSharedLink,
                    onUpdateProfile = { updated ->
                        currentUserProfile = updated
                        saveUserProfileToFirestore(updated)
                    },
                    onLogout = {
                        auth.signOut()
                        currentScreen = "welcome"
                    }
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
fun AuthScreen(
    isSignup: Boolean,
    onBack: () -> Unit,
    onSuccess: (UserProfileData) -> Unit,
    onSwitchMode: () -> Unit
) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }

    // Forgot Password Dialog (Firebase Password Reset Email)
    if (showForgotDialog) {
        var resetEmail by remember { mutableStateOf(email) }
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            containerColor = DeepNebula,
            title = { Text("Reset Password 🔑", color = StarlightWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Apni registered Gmail ID dalein, password reset link email par bhej diya jayega:", color = MutedLavender, fontSize = 13.sp)
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = resetEmail.trim()
                        if (target.isBlank()) {
                            Toast.makeText(context, "Email ID dalein", Toast.LENGTH_SHORT).show()
                        } else {
                            auth.sendPasswordResetEmail(target)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Reset link sent! Apna Gmail Inbox/Spam check karein ✅", Toast.LENGTH_LONG).show()
                                    showForgotDialog = false
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, e.localizedMessage ?: "Reset failed", Toast.LENGTH_LONG).show()
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) { Text("Send Link", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) { Text("Cancel", color = MutedLavender) }
            }
        )
    }

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
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(14.dp))
                }
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password (min 6 chars)") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

                if (!isSignup) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(onClick = { showForgotDialog = true }) {
                            Text("Forgot password?", color = CyberCyan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Button(
                    enabled = !isLoading,
                    onClick = {
                        val cleanEmail = email.trim()
                        val cleanPass = password.trim()
                        if (cleanEmail.isBlank() || cleanPass.length < 6) {
                            Toast.makeText(context, "Valid email aur 6+ character password dalein", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        if (isSignup) {
                            auth.createUserWithEmailAndPassword(cleanEmail, cleanPass)
                                .addOnSuccessListener { res ->
                                    isLoading = false
                                    val uid = res.user?.uid ?: ""
                                    val uname = cleanEmail.substringBefore("@").lowercase()
                                    val displayName = name.trim().ifBlank { uname }
                                    onSuccess(
                                        UserProfileData(
                                            uid = uid,
                                            name = displayName,
                                            username = uname,
                                            isVerified = uname in OFFICIAL_VERIFIED_USERS
                                        )
                                    )
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    Toast.makeText(context, e.localizedMessage ?: "Signup failed", Toast.LENGTH_LONG).show()
                                }
                        } else {
                            auth.signInWithEmailAndPassword(cleanEmail, cleanPass)
                                .addOnSuccessListener { res ->
                                    isLoading = false
                                    val uid = res.user?.uid ?: ""
                                    val uname = cleanEmail.substringBefore("@").lowercase()
                                    onSuccess(
                                        UserProfileData(
                                            uid = uid,
                                            name = uname,
                                            username = uname,
                                            isVerified = uname in OFFICIAL_VERIFIED_USERS
                                        )
                                    )
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    Toast.makeText(context, e.localizedMessage ?: "Login failed", Toast.LENGTH_LONG).show()
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(HyperViolet, PlasmaPink))), contentAlignment = Alignment.Center) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                        } else {
                            Text(if (isSignup) "Create Account 🚀" else "Log In ✦", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
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
    currentUser: UserProfileData,
    incomingSharedLink: String?,
    onUpdateProfile: (UserProfileData) -> Unit,
    onLogout: () -> Unit
) {
    val db = remember { FirebaseFirestore.getInstance() }
    var selectedTab by remember { mutableIntStateOf(if (incomingSharedLink != null) 1 else 0) }
    var subScreen by remember { mutableStateOf("none") }
    var activeStory by remember { mutableStateOf<LiveStoryItem?>(null) }
    var activeCall by remember { mutableStateOf<ActiveCallSession?>(null) }
    var isUserGoingLive by remember { mutableStateOf(false) }

    // 100% Real Firestore Data Lists (कोई डमी/फेक डेटा नहीं!)
    val livePosts = remember { mutableStateListOf<LivePostItem>() }
    val liveStories = remember { mutableStateListOf<LiveStoryItem>() }
    val allUsers = remember { mutableStateListOf<UserProfileData>() }
    val reels = remember {
        mutableStateListOf<PivoReelItem>().apply {
            if (incomingSharedLink != null) {
                add(PivoReelItem("shared_1", currentUser.username, "Shared Reel 🎬", extractInstagramEmbedUrl(incomingSharedLink), listOf(Color(0xFF3A0CA3), Color(0xFFFF2A85))))
            }
        }
    }

    // Real-time Firestore Listeners: Posts, Stories, Users
    DisposableEffect(currentUser.uid) {
        val postsReg = db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    livePosts.clear()
                    snap.documents.forEach { d ->
                        livePosts.add(
                            LivePostItem(
                                id = d.id,
                                authorUid = d.getString("authorUid") ?: "",
                                authorUsername = d.getString("authorUsername") ?: "user",
                                authorPhoto = d.getString("authorPhoto") ?: "",
                                authorVerified = d.getBoolean("authorVerified") ?: false,
                                location = d.getString("location") ?: "",
                                caption = d.getString("caption") ?: "",
                                imageData = d.getString("imageData") ?: "",
                                likedBy = (d.get("likedBy") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                                comments = (d.get("comments") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                                timestamp = d.getLong("timestamp") ?: 0L
                            )
                        )
                    }
                }
            }

        val cutoff24h = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
        val storiesReg = db.collection("stories")
            .whereGreaterThan("timestamp", cutoff24h)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    liveStories.clear()
                    snap.documents.forEach { d ->
                        liveStories.add(
                            LiveStoryItem(
                                id = d.id,
                                uid = d.getString("uid") ?: "",
                                username = d.getString("username") ?: "",
                                userPhoto = d.getString("userPhoto") ?: "",
                                caption = d.getString("caption") ?: "",
                                imageData = d.getString("imageData") ?: "",
                                isLive = d.getBoolean("isLive") ?: false,
                                timestamp = d.getLong("timestamp") ?: 0L
                            )
                        )
                    }
                }
            }

        val usersReg = db.collection("users")
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    allUsers.clear()
                    snap.documents.forEach { d ->
                        val uname = d.getString("username") ?: ""
                        allUsers.add(
                            UserProfileData(
                                uid = d.id,
                                name = d.getString("name") ?: uname,
                                username = uname,
                                photoData = d.getString("photoData") ?: "",
                                bio = d.getString("bio") ?: "",
                                isVerified = (d.getBoolean("isVerified") ?: false) || (uname in OFFICIAL_VERIFIED_USERS),
                                snapScore = (d.getLong("snapScore") ?: 0L).toInt(),
                                followers = (d.get("followers") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                                following = (d.get("following") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                            )
                        )
                    }
                }
            }

        onDispose {
            postsReg.remove()
            storiesReg.remove()
            usersReg.remove()
        }
    }

    if (activeCall != null) {
        CosmicCallOverlayScreen(session = activeCall!!, onEndCall = { activeCall = null })
        return
    }

    if (isUserGoingLive) {
        GoLiveStudioScreen(
            currentUser = currentUser,
            onEndLive = {
                db.collection("stories").document("live_${currentUser.uid}").delete()
                isUserGoingLive = false
            }
        )
        return
    }

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

                    // Center Real Camera & Filter Button
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .shadow(16.dp, CircleShape, spotColor = SnapYellow)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(SnapYellow, PlasmaPink)))
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { selectedTab = 2 },
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = VoidBlack, modifier = Modifier.size(26.dp)) }

                    BottomNavIcon(Icons.AutoMirrored.Filled.Send, selectedTab == 3) { selectedTab = 3 }
                    BottomNavIcon(Icons.Default.Person, selectedTab == 4) { selectedTab = 4 }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedTab) {
                    0 -> FeedTabScreen(
                        currentUser = currentUser,
                        stories = liveStories,
                        posts = livePosts,
                        allUsers = allUsers,
                        onOpenStory = { activeStory = it },
                        onGoLive = {
                            val liveStory = hashMapOf(
                                "uid" to currentUser.uid,
                                "username" to currentUser.username,
                                "userPhoto" to currentUser.photoData,
                                "caption" to "🔴 ${currentUser.username} is LIVE now!",
                                "isLive" to true,
                                "timestamp" to System.currentTimeMillis()
                            )
                            db.collection("stories").document("live_${currentUser.uid}").set(liveStory)
                            isUserGoingLive = true
                        },
                        onCreatePost = { caption, imageData, location ->
                            val postDoc = hashMapOf(
                                "authorUid" to currentUser.uid,
                                "authorUsername" to currentUser.username,
                                "authorPhoto" to currentUser.photoData,
                                "authorVerified" to currentUser.isVerified,
                                "location" to location,
                                "caption" to caption,
                                "imageData" to imageData,
                                "likedBy" to emptyList<String>(),
                                "comments" to emptyList<String>(),
                                "timestamp" to System.currentTimeMillis()
                            )
                            db.collection("posts").add(postDoc)
                        },
                        onToggleLike = { post ->
                            val ref = db.collection("posts").document(post.id)
                            if (currentUser.uid in post.likedBy) {
                                ref.update("likedBy", FieldValue.arrayRemove(currentUser.uid))
                            } else {
                                ref.update("likedBy", FieldValue.arrayUnion(currentUser.uid))
                            }
                        },
                        onAddComment = { post, commentText ->
                            db.collection("posts").document(post.id)
                                .update("comments", FieldValue.arrayUnion("${currentUser.username}: $commentText"))
                        },
                        onToggleFollow = { targetUser ->
                            toggleFollowInFirestore(db, currentUser.uid, targetUser.uid, targetUser.uid in currentUser.following)
                        }
                    )
                    1 -> ReelsTabScreen(reels = reels, onAddReelLink = { url, cap ->
                        reels.add(0, PivoReelItem("${System.currentTimeMillis()}", currentUser.username, cap.ifBlank { "PIVO Reel 🎬" }, extractInstagramEmbedUrl(url), listOf(Color(0xFF3A0CA3), Color(0xFFFF2A85))))
                    })
                    2 -> RealCameraAndFilterScreen(
                        currentUser = currentUser,
                        allUsers = allUsers,
                        onPostStory = { caption, imgData ->
                            val storyDoc = hashMapOf(
                                "uid" to currentUser.uid,
                                "username" to currentUser.username,
                                "userPhoto" to currentUser.photoData,
                                "caption" to caption,
                                "imageData" to imgData,
                                "isLive" to false,
                                "timestamp" to System.currentTimeMillis()
                            )
                            db.collection("stories").add(storyDoc)
                            db.collection("users").document(currentUser.uid).update("snapScore", FieldValue.increment(25))
                            selectedTab = 0
                        },
                        onSendSnapToUser = { peer, caption, imgData ->
                            sendRealMessageOrSnap(db, currentUser, peer, caption.ifBlank { "📸 Sent a Snap" }, imgData, isSnap = true)
                            db.collection("users").document(currentUser.uid).update("snapScore", FieldValue.increment(50))
                            selectedTab = 3
                        },
                        onGoLiveClick = { isUserGoingLive = true }
                    )
                    3 -> ChatsTabScreen(
                        currentUser = currentUser,
                        allUsers = allUsers,
                        onStartCall = { peerName, isVideo -> activeCall = ActiveCallSession(peerName, isVideo) }
                    )
                    4 -> FullProfileTabScreen(
                        profile = currentUser,
                        myPosts = livePosts.filter { it.authorUid == currentUser.uid },
                        onOpenEditProfile = { subScreen = "edit_profile" },
                        onOpenSettings = { subScreen = "settings" }
                    )
                }
            }
        }
    }
}

fun toggleFollowInFirestore(db: FirebaseFirestore, myUid: String, targetUid: String, isCurrentlyFollowing: Boolean) {
    if (myUid.isBlank() || targetUid.isBlank() || myUid == targetUid) return
    val myRef = db.collection("users").document(myUid)
    val targetRef = db.collection("users").document(targetUid)
    if (isCurrentlyFollowing) {
        myRef.update("following", FieldValue.arrayRemove(targetUid))
        targetRef.update("followers", FieldValue.arrayRemove(myUid))
    } else {
        myRef.update("following", FieldValue.arrayUnion(targetUid))
        targetRef.update("followers", FieldValue.arrayUnion(myUid))
    }
}

fun getChatRoomId(uid1: String, uid2: String): String {
    return if (uid1 < uid2) "${uid1}_${uid2}" else "${uid2}_${uid1}"
}

fun sendRealMessageOrSnap(
    db: FirebaseFirestore,
    sender: UserProfileData,
    peer: UserProfileData,
    text: String,
    imageData: String = "",
    isSnap: Boolean = false
) {
    val roomId = getChatRoomId(sender.uid, peer.uid)
    val now = System.currentTimeMillis()
    val msgData = hashMapOf(
        "senderUid" to sender.uid,
        "senderName" to sender.username,
        "text" to text,
        "imageData" to imageData,
        "isSnap" to isSnap,
        "timestamp" to now
    )
    db.collection("chats").document(roomId).collection("messages").add(msgData)

    val roomMeta = hashMapOf(
        "participants" to listOf(sender.uid, peer.uid),
        "lastMsg" to if (isSnap) "🟥 Sent a Snap" else text,
        "isSnap" to isSnap,
        "timestamp" to now
    )
    db.collection("chats").document(roomId).set(roomMeta, SetOptions.merge())
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

// 100% Real Camera + Gallery + Live Color Filters Screen
@Composable
fun RealCameraAndFilterScreen(
    currentUser: UserProfileData,
    allUsers: List<UserProfileData>,
    onPostStory: (String, String) -> Unit,
    onSendSnapToUser: (UserProfileData, String, String) -> Unit,
    onGoLiveClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val filterPresets = listOf(
        "✨ Natural" to null,
        "🌌 Cosmic" to ColorMatrix(floatArrayOf(
            1.1f, 0f, 0.2f, 0f, 15f,
            0f, 0.9f, 0.2f, 0f, 0f,
            0.2f, 0.1f, 1.3f, 0f, 25f,
            0f, 0f, 0f, 1f, 0f
        )),
        "🔥 Warm Glow" to ColorMatrix(floatArrayOf(
            1.25f, 0.1f, 0f, 0f, 12f,
            0f, 1.05f, 0f, 0f, 5f,
            0f, 0f, 0.85f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )),
        "🖤 Noir B&W" to ColorMatrix().apply { setToSaturation(0f) },
        "💎 Cyber Cool" to ColorMatrix(floatArrayOf(
            0.85f, 0f, 0.1f, 0f, 0f,
            0f, 1.1f, 0.15f, 0f, 10f,
            0.1f, 0.15f, 1.3f, 0f, 20f,
            0f, 0f, 0f, 1f, 0f
        ))
    )

    var selectedFilterIdx by remember { mutableIntStateOf(0) }
    var capturedImageData by remember { mutableStateOf("") }
    var captionText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var showSendDialog by remember { mutableStateOf(false) }

    val tempCameraUri = remember {
        val file = File(context.cacheDir, "pivo_snap_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            isProcessing = true
            scope.launch {
                val data = processAndUploadImagePermanent(context, tempCameraUri, maxDimension = 540, quality = 75)
                isProcessing = false
                if (data != null) capturedImageData = data
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(tempCameraUri)
        else Toast.makeText(context, "Camera permission zaroori hai", Toast.LENGTH_SHORT).show()
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            isProcessing = true
            scope.launch {
                val data = processAndUploadImagePermanent(context, uri, maxDimension = 540, quality = 75)
                isProcessing = false
                if (data != null) capturedImageData = data
            }
        }
    }

    if (showSendDialog) {
        val otherUsers = allUsers.filter { it.uid != currentUser.uid }
        AlertDialog(
            onDismissRequest = { showSendDialog = false },
            containerColor = DeepNebula,
            title = { Text("Share Snap / Story 🔥", color = StarlightWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            onPostStory(captionText.ifBlank { "PIVO Story ✨" }, capturedImageData)
                            Toast.makeText(context, "Story live ho gayi! +25 Snap Score 🔥", Toast.LENGTH_SHORT).show()
                            showSendDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                    ) { Text("📢 Post to Your Story (24h)", color = Color.White, fontWeight = FontWeight.Bold) }

                    if (otherUsers.isEmpty()) {
                        Text("Abhi koi aur user registered nahi hai — apne doston ko invite karein!", color = MutedLavender, fontSize = 12.sp)
                    } else {
                        Text("Send Direct Snap to Friend:", color = MutedLavender, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        otherUsers.forEach { user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GlassSurface)
                                    .clickable {
                                        onSendSnapToUser(user, captionText, capturedImageData)
                                        Toast.makeText(context, "Snap sent to @${user.username}! 🔥", Toast.LENGTH_SHORT).show()
                                        showSendDialog = false
                                    }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("@${user.username}", color = StarlightWhite, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(VoidBlack).padding(16.dp)) {
        LiveMovingCosmicCanvas(showHorizon = false)

        // Top Bar
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("PIVO Camera 📸", color = StarlightWhite, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
            Button(
                onClick = onGoLiveClick,
                colors = ButtonDefaults.buttonColors(containerColor = LiveRed),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) { Text("🔴 GO LIVE", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp) }
        }

        // Center Viewfinder / Captured Photo Preview with Live Filter
        Column(modifier = Modifier.align(Alignment.Center).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .height(310.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(GlassSurface)
                    .border(2.dp, GlassBorder, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = PlasmaPink)
                } else if (capturedImageData.isNotBlank()) {
                    val bitmap = remember(capturedImageData) {
                        try {
                            val bytes = Base64.decode(capturedImageData.substringAfter("base64,"), Base64.DEFAULT)
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                        } catch (e: Exception) { null }
                    }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            colorFilter = filterPresets[selectedFilterIdx].second?.let { ColorFilter.colorMatrix(it) },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        UniversalPhotoView(photoData = capturedImageData, fallbackText = "P")
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(20.dp)) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = SnapYellow, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Real Camera & Gallery Studio", color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Neeche Camera shutter ya Gallery se real photo lein aur filter lagayein", color = MutedLavender, fontSize = 12.sp, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = captionText,
                onValueChange = { captionText = it },
                placeholder = { Text("Add caption for Story or Snap...") },
                modifier = Modifier.fillMaxWidth(0.88f),
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Bottom Filter Strip + Real Camera / Gallery / Send Controls
        Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterPresets.size) { idx ->
                    val active = idx == selectedFilterIdx
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (active) SnapYellow else GlassSurface)
                            .clickable { selectedFilterIdx = idx }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(filterPresets[idx].first, color = if (active) VoidBlack else Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                // Gallery Pick
                IconButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.size(50.dp).clip(CircleShape).background(GlassSurface)
                ) { Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = CyberCyan) }

                // Real Camera Capture Button
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f))
                        .border(4.dp, SnapYellow, CircleShape)
                        .clickable { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(Color.White))
                }

                // Send / Post Button
                IconButton(
                    enabled = capturedImageData.isNotBlank() || captionText.isNotBlank(),
                    onClick = { showSendDialog = true },
                    modifier = Modifier.size(50.dp).clip(CircleShape).background(if (capturedImageData.isNotBlank() || captionText.isNotBlank()) PlasmaPink else GlassSurface)
                ) { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White) }
            }
        }
    }
}

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
                    text = if (session.isVideoCall) "📹 PIVO Video Call" else "📞 PIVO Audio Call",
                    color = CyberCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                RotatingNeonRingBox(sizeDp = 120, borderWidth = 3.5f) {
                    Text(session.peerName.firstOrNull()?.uppercase() ?: "P", fontSize = 42.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(session.peerName, color = StarlightWhite, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                Text("Calling • $timeText", color = Color(0xFF4ADE80), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
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

// Real Firestore Live Stream Room
@Composable
fun GoLiveStudioScreen(currentUser: UserProfileData, onEndLive: () -> Unit) {
    val db = remember { FirebaseFirestore.getInstance() }
    val liveChat = remember { mutableStateListOf<String>() }
    var commentText by remember { mutableStateOf("") }

    DisposableEffect(currentUser.uid) {
        val reg = db.collection("live_rooms").document(currentUser.uid).collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    liveChat.clear()
                    snap.documents.forEach { d ->
                        liveChat.add("${d.getString("user") ?: ""}: ${d.getString("text") ?: ""}")
                    }
                }
            }
        onDispose { reg.remove() }
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF450A0A), VoidBlack))).statusBarsPadding().padding(16.dp)) {
        LiveMovingCosmicCanvas(showHorizon = false)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(LiveRed).padding(horizontal = 10.dp, vertical = 4.dp)) {
                Text("🔴 LIVE NOW", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
            Button(onClick = onEndLive, colors = ButtonDefaults.buttonColors(containerColor = GlassSurface)) {
                Text("End Live", color = PlasmaPink, fontWeight = FontWeight.Bold)
            }
        }

        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            RotatingNeonRingBox(sizeDp = 120, borderWidth = 3f, isLive = true) {
                UniversalPhotoView(photoData = currentUser.photoData, fallbackText = currentUser.username, fontSizeSp = 40)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("@${currentUser.username} is LIVE!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Column(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
            liveChat.takeLast(5).forEach { msg ->
                Box(modifier = Modifier.padding(vertical = 3.dp).clip(RoundedCornerShape(12.dp)).background(Color.Black.copy(alpha = 0.55f)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text(msg, color = Color.White, fontSize = 12.5.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(value = commentText, onValueChange = { commentText = it }, placeholder = { Text("Live chat...") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(20.dp))
                IconButton(onClick = {
                    if (commentText.isNotBlank()) {
                        db.collection("live_rooms").document(currentUser.uid).collection("comments").add(
                            hashMapOf("user" to currentUser.username, "text" to commentText.trim(), "timestamp" to System.currentTimeMillis())
                        )
                        commentText = ""
                    }
                }) { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = PlasmaPink) }
            }
        }
    }
}

@Composable
fun FullScreenStoryOrLiveViewer(story: LiveStoryItem, onClose: () -> Unit) {
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

    Box(modifier = Modifier.fillMaxSize().background(VoidBlack).statusBarsPadding().padding(18.dp)) {
        if (story.imageData.isNotBlank()) {
            UniversalPhotoView(photoData = story.imageData, fallbackText = story.username, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)))
        }
        Column {
            if (!story.isLive) {
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(3.dp), color = Color.White, trackColor = Color.White.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(12.dp))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (story.isLive) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(LiveRed).padding(horizontal = 8.dp, vertical = 3.dp)) {
                            Text("LIVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("@${story.username}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White) }
            }
        }
        if (story.caption.isNotBlank()) {
            Box(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 28.dp).clip(RoundedCornerShape(14.dp)).background(Color.Black.copy(alpha = 0.65f)).padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(story.caption, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTabScreen(
    currentUser: UserProfileData,
    stories: List<LiveStoryItem>,
    posts: List<LivePostItem>,
    allUsers: List<UserProfileData>,
    onOpenStory: (LiveStoryItem) -> Unit,
    onGoLive: () -> Unit,
    onCreatePost: (String, String, String) -> Unit,
    onToggleLike: (LivePostItem) -> Unit,
    onAddComment: (LivePostItem, String) -> Unit,
    onToggleFollow: (UserProfileData) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCreate by remember { mutableStateOf(false) }
    var showSearchUsers by remember { mutableStateOf(false) }
    var commentPost by remember { mutableStateOf<LivePostItem?>(null) }

    // Real Post Creator with Photo Picker
    if (showCreate) {
        var cap by remember { mutableStateOf("") }
        var loc by remember { mutableStateOf("") }
        var pickedPhotoData by remember { mutableStateOf("") }
        var uploading by remember { mutableStateOf(false) }

        val postPhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                uploading = true
                scope.launch {
                    val data = processAndUploadImagePermanent(context, uri, maxDimension = 640, quality = 78)
                    uploading = false
                    if (data != null) pickedPhotoData = data
                }
            }
        }

        AlertDialog(
            onDismissRequest = { showCreate = false },
            containerColor = DeepNebula,
            title = { Text("New Live Post ✦", color = StarlightWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { postPhotoPicker.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = GlassSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = CyberCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (uploading) "Processing photo..." else if (pickedPhotoData.isNotBlank()) "Photo Selected ✅" else "Select Photo from Gallery", color = StarlightWhite)
                    }
                    OutlinedTextField(value = cap, onValueChange = { cap = it }, placeholder = { Text("Write a caption...") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = loc, onValueChange = { loc = it }, placeholder = { Text("Location (optional)...") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    enabled = !uploading && (cap.isNotBlank() || pickedPhotoData.isNotBlank()),
                    onClick = {
                        onCreatePost(cap.trim(), pickedPhotoData, loc.trim())
                        showCreate = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PlasmaPink)
                ) { Text("Share Post", color = Color.White) }
            }
        )
    }

    // Real User Search & Follow Sheet
    if (showSearchUsers) {
        var query by remember { mutableStateOf("") }
        ModalBottomSheet(onDismissRequest = { showSearchUsers = false }, containerColor = DeepNebula) {
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                Text("Discover Real PIVO Users 🔍", color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(value = query, onValueChange = { query = it }, placeholder = { Text("Search username...") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp))
                Spacer(modifier = Modifier.height(12.dp))
                val filtered = allUsers.filter {
                    it.uid != currentUser.uid && (query.isBlank() || it.username.contains(query, ignoreCase = true) || it.name.contains(query, ignoreCase = true))
                }
                if (filtered.isEmpty()) {
                    Text("Koi user nahi mila. Doston ko PIVO join karne ke liye invite karein!", color = MutedLavender, fontSize = 13.sp)
                } else {
                    filtered.forEach { user ->
                        val isFollowing = user.uid in currentUser.following
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RotatingNeonRingBox(sizeDp = 46, borderWidth = 2f) {
                                    UniversalPhotoView(photoData = user.photoData, fallbackText = user.username, fontSizeSp = 18)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("@${user.username}", color = StarlightWhite, fontWeight = FontWeight.Bold)
                                        if (user.isVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.Verified, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(15.dp))
                                        }
                                    }
                                    Text(user.name, color = MutedLavender, fontSize = 12.sp)
                                }
                            }
                            Button(
                                onClick = { onToggleFollow(user) },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isFollowing) GlassSurface else NeonPurple),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                            ) {
                                Text(if (isFollowing) "Following" else "Follow", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (commentPost != null) {
        val liveTargetPost = posts.find { it.id == commentPost!!.id } ?: commentPost!!
        var newComment by remember { mutableStateOf("") }
        ModalBottomSheet(onDismissRequest = { commentPost = null }, containerColor = DeepNebula) {
            Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                Text("Comments (${liveTargetPost.comments.size})", color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(modifier = Modifier.height(10.dp))
                if (liveTargetPost.comments.isEmpty()) {
                    Text("No comments yet. Be the first!", color = MutedLavender, fontSize = 13.sp)
                } else {
                    liveTargetPost.comments.forEach { c -> Text("• $c", color = StarlightWhite, modifier = Modifier.padding(vertical = 4.dp)) }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = newComment, onValueChange = { newComment = it }, placeholder = { Text("Add a comment...") }, modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        if (newComment.isNotBlank()) {
                            onAddComment(liveTargetPost, newComment.trim())
                            newComment = ""
                        }
                    }) { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = PlasmaPink) }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row {
                    IconButton(onClick = { showCreate = true }) { Icon(Icons.Default.AddBox, contentDescription = "New Post", tint = StarlightWhite) }
                    IconButton(onClick = { showSearchUsers = true }) { Icon(Icons.Default.Search, contentDescription = "Search Users", tint = StarlightWhite) }
                }
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

            // Real Stories Row (केवल असली अपलोड की हुई स्टोरीज और लाइव स्ट्रीम्स)
            if (stories.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.padding(bottom = 14.dp)) {
                    items(stories, key = { it.id }) { story ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onOpenStory(story) }) {
                            Box(contentAlignment = Alignment.BottomCenter) {
                                RotatingNeonRingBox(sizeDp = 68, borderWidth = 2.5f, isLive = story.isLive) {
                                    UniversalPhotoView(photoData = story.userPhoto, fallbackText = story.username, fontSizeSp = 20)
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
        }

        if (posts.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp).clip(RoundedCornerShape(22.dp)).background(GlassSurface).border(1.dp, GlassBorder, RoundedCornerShape(22.dp)).padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FourPointStar(modifier = Modifier.size(36.dp), color = PlasmaPink)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Welcome to PIVO!", color = StarlightWhite, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Abhi feed fresh hai. Upar '+' icon par tap karke apni pehli real photo ya post share karein!", color = MutedLavender, fontSize = 13.5.sp, textAlign = TextAlign.Center)
                }
            }
        }

        items(posts, key = { it.id }) { post ->
            val liked = currentUser.uid in post.likedBy
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
                        UniversalPhotoView(photoData = post.authorPhoto, fallbackText = post.authorUsername, fontSizeSp = 16)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(post.authorUsername, color = StarlightWhite, fontWeight = FontWeight.Bold)
                            if (post.authorVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Verified, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(15.dp))
                            }
                        }
                        if (post.location.isNotBlank()) Text(post.location, color = MutedLavender, fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(if (post.imageData.isNotBlank()) 280.dp else 170.dp).clip(RoundedCornerShape(16.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF3C096C), Color(0xFF9D4EDD), Color(0xFFFF2A85))))
                        .pointerInput(post.id) {
                            detectTapGestures(onDoubleTap = {
                                if (!liked) onToggleLike(post)
                                showBigHeart = true
                            })
                        }
                ) {
                    if (post.imageData.isNotBlank()) {
                        UniversalPhotoView(photoData = post.imageData, fallbackText = post.authorUsername)
                    }
                    if (post.caption.isNotBlank()) {
                        Box(
                            modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth()
                                .background(if (post.imageData.isNotBlank()) Color.Black.copy(alpha = 0.55f) else Color.Transparent)
                                .padding(14.dp)
                        ) {
                            Text(post.caption, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    if (heartScale > 0.05f) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White.copy(alpha = 0.92f), modifier = Modifier.size(76.dp).align(Alignment.Center).scale(heartScale))
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onToggleLike(post); if (!liked) showBigHeart = true }) {
                            Icon(if (liked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = null, tint = if (liked) PlasmaPink else StarlightWhite)
                        }
                        Text("${post.likedBy.size}", color = StarlightWhite, fontSize = 13.sp)
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

// 100% Real Firestore 1-to-1 Chat & Snaps Screen
@Composable
fun ChatsTabScreen(
    currentUser: UserProfileData,
    allUsers: List<UserProfileData>,
    onStartCall: (String, Boolean) -> Unit
) {
    val db = remember { FirebaseFirestore.getInstance() }
    var activePeer by remember { mutableStateOf<UserProfileData?>(null) }
    var searchUserQuery by remember { mutableStateOf("") }

    if (activePeer != null) {
        val peer = activePeer!!
        val roomId = getChatRoomId(currentUser.uid, peer.uid)
        val messages = remember { mutableStateListOf<LiveMessageItem>() }
        var draft by remember { mutableStateOf("") }

        DisposableEffect(roomId) {
            val reg = db.collection("chats").document(roomId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snap, _ ->
                    if (snap != null) {
                        messages.clear()
                        snap.documents.forEach { d ->
                            messages.add(
                                LiveMessageItem(
                                    id = d.id,
                                    senderUid = d.getString("senderUid") ?: "",
                                    senderName = d.getString("senderName") ?: "",
                                    text = d.getString("text") ?: "",
                                    imageData = d.getString("imageData") ?: "",
                                    isSnap = d.getBoolean("isSnap") ?: false,
                                    timestamp = d.getLong("timestamp") ?: 0L
                                )
                            )
                        }
                    }
                }
            onDispose { reg.remove() }
        }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { activePeer = null }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = StarlightWhite) }
                    RotatingNeonRingBox(sizeDp = 40, borderWidth = 2f) {
                        UniversalPhotoView(photoData = peer.photoData, fallbackText = peer.username, fontSizeSp = 15)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("@${peer.username}", color = StarlightWhite, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
                Row {
                    IconButton(onClick = { onStartCall(peer.username, false) }) { Icon(Icons.Default.Call, contentDescription = "Audio Call", tint = Color(0xFF4ADE80)) }
                    IconButton(onClick = { onStartCall(peer.username, true) }) { Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = CyberCyan) }
                }
            }

            LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 8.dp)) {
                items(messages, key = { it.id }) { m ->
                    val isMine = m.senderUid == currentUser.uid
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        contentAlignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
                    ) {
                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isMine) NeonPurple else GlassSurface)
                                .padding(12.dp)
                        ) {
                            if (m.imageData.isNotBlank()) {
                                UniversalPhotoView(photoData = m.imageData, fallbackText = "S", modifier = Modifier.size(180.dp).clip(RoundedCornerShape(12.dp)))
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            if (m.text.isNotBlank()) Text(m.text, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(value = draft, onValueChange = { draft = it }, modifier = Modifier.weight(1f), placeholder = { Text("Message @${peer.username}...") }, shape = RoundedCornerShape(18.dp))
                IconButton(onClick = {
                    if (draft.isNotBlank()) {
                        sendRealMessageOrSnap(db, currentUser, peer, draft.trim())
                        draft = ""
                    }
                }) { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = PlasmaPink) }
            }
        }
        return
    }

    val realContacts = allUsers.filter {
        it.uid != currentUser.uid && (searchUserQuery.isBlank() || it.username.contains(searchUserQuery, ignoreCase = true))
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(currentUser.username, color = StarlightWhite, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp)
                Text("Direct Messages", color = MutedLavender, fontSize = 12.5.sp)
            }
            OutlinedTextField(
                value = searchUserQuery,
                onValueChange = { searchUserQuery = it },
                placeholder = { Text("Search users to chat...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MutedLavender) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(14.dp)
            )
        }

        if (realContacts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp).clip(RoundedCornerShape(18.dp)).background(GlassSurface).padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Abhi koi aur user online nahi hai. Jaise hi aapke dost PIVO par account banayenge, wo yahan live dikhenge!", color = MutedLavender, textAlign = TextAlign.Center, fontSize = 13.5.sp)
                }
            }
        }

        items(realContacts, key = { it.uid }) { user ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(GlassSurface)
                    .clickable { activePeer = user }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    RotatingNeonRingBox(sizeDp = 48, borderWidth = 2f) {
                        UniversalPhotoView(photoData = user.photoData, fallbackText = user.username, fontSizeSp = 18)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("@${user.username}", color = StarlightWhite, fontWeight = FontWeight.Bold)
                            if (user.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Verified, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(15.dp))
                            }
                        }
                        Text("Tap to chat or send Snap", color = MutedLavender, fontSize = 12.sp)
                    }
                }
                Row {
                    IconButton(onClick = { onStartCall(user.username, false) }) { Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF4ADE80), modifier = Modifier.size(20.dp)) }
                    IconButton(onClick = { onStartCall(user.username, true) }) { Icon(Icons.Default.Videocam, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(22.dp)) }
                }
            }
        }
    }
}

@Composable
fun FullProfileTabScreen(
    profile: UserProfileData,
    myPosts: List<LivePostItem>,
    onOpenEditProfile: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    var profileSubTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(profile.username, color = StarlightWhite, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                if (profile.isVerified) {
                    Spacer(modifier = Modifier.width(5.dp))
                    Icon(Icons.Default.Verified, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(SnapYellow.copy(alpha = 0.2f)).padding(horizontal = 10.dp, vertical = 4.dp)) {
                    Text("👻 ${profile.snapScore}", color = SnapYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                IconButton(onClick = onOpenSettings) { Icon(Icons.Default.Menu, contentDescription = "Settings", tint = StarlightWhite, modifier = Modifier.size(28.dp)) }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.clickable { onOpenEditProfile() }) {
                RotatingNeonRingBox(sizeDp = 92, borderWidth = 3f) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(HyperViolet, NeonPurple, PlasmaPink))), contentAlignment = Alignment.Center) {
                        UniversalPhotoView(photoData = profile.photoData, fallbackText = profile.name, fontSizeSp = 34)
                    }
                }
            }
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatColumn("${myPosts.size}", "posts")
                StatColumn("${profile.followers.size}", "followers")
                StatColumn("${profile.following.size}", "following")
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
            if (profile.bio.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(profile.bio, color = StarlightWhite, fontSize = 13.5.sp, lineHeight = 18.sp)
            }
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

        if (myPosts.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("No posts yet. Share your first moment from Home!", color = MutedLavender, fontSize = 13.sp)
            }
        } else {
            val rows = myPosts.chunked(3)
            rows.forEach { rowPosts ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    rowPosts.forEach { post ->
                        Box(
                            modifier = Modifier.weight(1f).aspectRatio(0.85f).background(Brush.linearGradient(listOf(Color(0xFF3C096C), Color(0xFFFF2A85)))),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            if (post.imageData.isNotBlank()) {
                                UniversalPhotoView(photoData = post.imageData, fallbackText = profile.username)
                            } else {
                                Text(post.caption, color = Color.White, fontSize = 10.sp, maxLines = 3, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(8.dp))
                            }
                        }
                    }
                    repeat(3 - rowPosts.size) { Spacer(modifier = Modifier.weight(1f)) }
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(profile.name) }
    var username by remember { mutableStateOf(profile.username) }
    var photoData by remember { mutableStateOf(profile.photoData) }
    var isUploadingDp by remember { mutableStateOf(false) }
    var pronouns by remember { mutableStateOf(profile.pronouns) }
    var bio by remember { mutableStateOf(profile.bio) }
    var website by remember { mutableStateOf(profile.website) }
    var category by remember { mutableStateOf(profile.category) }
    var gender by remember { mutableStateOf(profile.gender) }
    var isAi by remember { mutableStateOf(profile.isAiProfile) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isUploadingDp = true
            scope.launch {
                val permanentData = processAndUploadImagePermanent(context, uri, maxDimension = 360, quality = 75)
                isUploadingDp = false
                if (permanentData != null) {
                    photoData = permanentData
                    // चुनते ही तुरंत Firestore में सेव भी कर दे ताकि कभी न हटे!
                    onSave(
                        profile.copy(
                            name = name,
                            username = username,
                            photoData = permanentData,
                            pronouns = pronouns,
                            bio = bio,
                            website = website,
                            category = category,
                            gender = gender,
                            isAiProfile = isAi
                        )
                    )
                    Toast.makeText(context, "DP permanently saved! ✅", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Photo load fail hui, dubara try karein", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(VoidBlack).statusBarsPadding().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = StarlightWhite) }
                Text("Edit profile", color = StarlightWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            TextButton(
                enabled = !isUploadingDp,
                onClick = {
                    onSave(
                        profile.copy(
                            name = name,
                            username = username,
                            photoData = photoData,
                            pronouns = pronouns,
                            bio = bio,
                            website = website,
                            category = category,
                            gender = gender,
                            isAiProfile = isAi
                        )
                    )
                }
            ) {
                Text("Done", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier.fillMaxWidth().clickable { imagePickerLauncher.launch("image/*") },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RotatingNeonRingBox(sizeDp = 96, borderWidth = 3f) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(HyperViolet, PlasmaPink))),
                    contentAlignment = Alignment.Center
                ) {
                    if (isUploadingDp) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(28.dp))
                    } else {
                        UniversalPhotoView(photoData = photoData, fallbackText = name, fontSizeSp = 36)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isUploadingDp) "Saving DP permanently..." else "Change profile photo (DP) 📸",
                color = CyberCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp
            )
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
        SettingsRowItem(Icons.Outlined.Info, "About PIVO", "v1.0 Production • Made in India") {}

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
    val pagerState = rememberPagerState(pageCount = { maxOf(reels.size, 1) })
    var showLinkDialog by remember { mutableStateOf(false) }

    if (showLinkDialog) {
        var link by remember { mutableStateOf("") }
        var cap by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showLinkDialog = false }, containerColor = DeepNebula,
            title = { Text("Paste Reel Link 🎬", color = StarlightWhite) },
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
        if (reels.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(28.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.PlayCircleOutline, contentDescription = null, tint = PlasmaPink, modifier = Modifier.size(56.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("PIVO Reels", color = StarlightWhite, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                    Text("Upar '+ Paste Link' par tap karke koi bhi Reel link dalein aur full-screen scroll karein!", color = MutedLavender, textAlign = TextAlign.Center, fontSize = 13.sp)
                }
            }
        } else {
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
        }
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("PIVO Reels 🎬", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Button(onClick = { showLinkDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = PlasmaPink)) { Text("+ Paste Link", color = Color.White) }
        }
    }
}
