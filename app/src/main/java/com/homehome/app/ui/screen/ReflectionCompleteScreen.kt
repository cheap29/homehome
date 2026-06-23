package com.homehome.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.homehome.app.R
import com.homehome.app.ui.component.CharacterMessageCard
import com.homehome.app.ui.viewmodel.ReflectionCompleteState
import com.homehome.app.ui.viewmodel.ReflectionCompleteViewModel

@Composable
fun ReflectionCompleteScreen(
    viewModel: ReflectionCompleteViewModel,
    onNavigateToSelectThree: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    val imageRes = resolveCharacterImage(state)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        CharacterMessageCard(
            imageRes = imageRes,
            message = state.praiseText,
            imageSize = 160.dp
        )

        Spacer(Modifier.height(24.dp))

        // スコアカード
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScoreRow("約束できたこと", state.checkedCount)
                ScoreRow("ボーナス", state.bonusCount)
                HorizontalDivider()
                ScoreRow("合計できたこと", state.totalCount, highlight = true)
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            "今日もちゃんと前に進んだね",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onNavigateToSelectThree,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("次の3つを決める")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onNavigateToHome) {
            Text("ホームへもどる")
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ScoreRow(label: String, count: Int, highlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = if (highlight) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyMedium,
            color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Text(
            "$count",
            style = if (highlight) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyLarge,
            color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun resolveCharacterImage(state: ReflectionCompleteState): Int {
    val plannedTotal = state.plannedResults.size
    val checked = state.checkedCount
    val bonus = state.bonusCount
    return when {
        checked >= plannedTotal && plannedTotal > 0 && bonus > 0 -> R.drawable.character_complete_great
        checked >= plannedTotal && plannedTotal > 0 -> R.drawable.character_complete_great
        checked in 1 until plannedTotal -> R.drawable.character_complete_normal
        checked == 0 && bonus > 0 -> R.drawable.character_complete_bonus
        else -> R.drawable.character_complete_soft
    }
}
