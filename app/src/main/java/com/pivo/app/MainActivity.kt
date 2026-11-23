
package com.pivo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PivoApp()
        }
    }
}

private val Background = Color(0xFF09090F)
private val Purple = Color(0xFF9B5CFF)
private val TextWhite = Color(0xFFF5F3FF)
private val Muted = Color(0xFF92909F)

@Composable
fun PivoApp() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Background
        ) {
            WelcomeScreen()
        }
    }
}

@Composable
fun WelcomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF171027),
                        Background,
                        Background
                    )
                )
            )
            .padding(horizontal = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFB47AFF),
                            Color(0xFF6735D9)
                        )
                    ),
                    shape = RoundedCornerShape(30.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "P",
                fontSize = 58.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "PIVO",
            fontSize = 38.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 8.sp,
            color = TextWhite
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your World. Your People.",
            fontSize = 16.sp,
            color = Muted
        )

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "A world that brings people closer.",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Share moments, discover people,\nand be yourself.",
            fontSize = 15.sp,
            color = Muted,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(38.dp))

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple
            )
        ) {
            Text(
                text = "Get Started",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextWhite
            )
        ) {
            Text(
                text = "I already have an account",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "MADE IN INDIA 🇮🇳",
            color = Muted,
            fontSize = 12.sp,
            letterSpacing = 3.sp
        )

        Spacer(modifier = Modifier.height(22.dp))
    }
}
