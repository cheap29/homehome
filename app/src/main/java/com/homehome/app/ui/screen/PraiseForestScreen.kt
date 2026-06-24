package com.homehome.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val forestMessages = listOf(
    "スーパーほめほめ、達成！\nここまで積み上げてきたあなたは\n本当にすごい。",
    "これだけのことを\nやり続けた証拠がここにある。\nえらすぎる。本当に。",
    "毎日コツコツ続けてきたんだよ。\nそれってね、誰にでも\nできることじゃないよ。",
    "ほめほめの森へようこそ。\nここはあなたのためだけの場所。\nゆっくりしていってね。",
    "積み上げてきたものは\n絶対に消えない。\nあなたは確実に前に進んでいる。",
    "毎日の小さな一歩が\nこんなに大きくなった。\nあなたの努力がここに結晶している。",
    "できないことより\nできたことに目を向けてきた。\nそれがあなたの強さ。",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PraiseForestScreen(onBack: () -> Unit) {
    val message = remember { forestMessages.random() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF388E3C))
                )
            )
    ) {
        TopAppBar(
            title = { Text("ほめほめの森", color = Color.White) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "もどる", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            modifier = Modifier.align(Alignment.TopStart)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🌳",
                fontSize = 64.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))
            Text(
                text = message,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp
            )
            Spacer(Modifier.height(48.dp))
            Text(
                text = "— ほめほめの森より",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
