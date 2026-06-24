package com.homehome.app.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val praiseWords = listOf(
    "スゴイ！", "天才！", "さすが！", "えらい！", "やったね！",
    "最高！", "かっこいい！", "完璧！", "すばらしい！", "神！",
    "やるじゃん！", "できてる！", "輝いてる！", "どんどん成長してる！", "尊い！",
    "そうそう！", "ばっちり！", "上出来！", "花マル！", "100点！",
    "まじで偉い！", "やりきった！", "継続力すごい！", "本物だ！", "勝ち確！",
    "すごすぎる！", "感動した！", "ありがとう！", "頼もしい！", "誇りだよ！"
)

@Composable
fun PraiseOverlay(visible: Boolean, triggerKey: Int = 0, modifier: Modifier = Modifier) {
    val word = remember(triggerKey) { praiseWords.random() }
    val bubbleColor = MaterialTheme.colorScheme.primaryContainer
    val textColor = MaterialTheme.colorScheme.onPrimaryContainer

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(700)),
        modifier = modifier.fillMaxSize()
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            // キャラクターアイコン（80dp、画面中央）の右横に吹き出し表示
            val x = maxWidth / 2 + 44.dp
            val y = 102.dp

            Row(
                modifier = Modifier.offset(x = x, y = y),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 吹き出しのしっぽ（左向き三角）
                Canvas(modifier = Modifier.size(8.dp, 14.dp)) {
                    val path = Path().apply {
                        moveTo(size.width, 0f)
                        lineTo(0f, size.height / 2f)
                        lineTo(size.width, size.height)
                        close()
                    }
                    drawPath(path, color = bubbleColor)
                }
                // 吹き出し本体
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = bubbleColor,
                    shadowElevation = 3.dp
                ) {
                    Text(
                        text = word,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }
    }
}
