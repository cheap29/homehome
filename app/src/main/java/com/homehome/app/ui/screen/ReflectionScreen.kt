package com.homehome.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homehome.app.R
import com.homehome.app.data.db.entity.DailyPlanItemEntity
import com.homehome.app.data.db.entity.ReflectionResultEntity
import com.homehome.app.data.db.entity.TaskEntity
import com.homehome.app.ui.component.CharacterMessageCard
import com.homehome.app.ui.component.PraiseOverlay
import com.homehome.app.ui.viewmodel.ReflectionViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReflectionScreen(
    viewModel: ReflectionViewModel,
    onBack: () -> Unit,
    onComplete: (Long) -> Unit
) {
    val homeState by viewModel.homeState.collectAsState()
    val activeTasks by viewModel.activeTasks.collectAsState()
    val bonusItems by viewModel.bonusItems.collectAsState()
    val showBonusSheet by viewModel.showBonusSheet.collectAsState()
    val completedSessionId by viewModel.completedSessionId.collectAsState()

    var showPraise by remember { mutableStateOf(false) }
    var praiseKey by remember { mutableStateOf(0) }

    // ボーナスアイテムが増えたらほめほめ軍団
    var prevBonusSize by remember { mutableStateOf(bonusItems.size) }
    LaunchedEffect(bonusItems.size) {
        if (bonusItems.size > prevBonusSize) {
            praiseKey++
            showPraise = true
        }
        prevBonusSize = bonusItems.size
    }

    LaunchedEffect(showPraise) {
        if (showPraise) {
            delay(3000L)
            showPraise = false
        }
    }

    LaunchedEffect(completedSessionId) {
        completedSessionId?.let { onComplete(it) }
    }

    val triggerPraise = {
        praiseKey++
        showPraise = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("振り返り") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "もどる")
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    CharacterMessageCard(
                        imageRes = R.drawable.character_reflection,
                        message = "できたこと、いっしょに見よ",
                        subMessage = "今日やれたことを確認しよう"
                    )
                }

                item {
                    Text(
                        "今日の約束",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                if (homeState.planItems.isEmpty()) {
                    item {
                        Text(
                            "今日の約束はまだ決めていないよ",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                } else {
                    items(homeState.planItems) { item ->
                        PlanCheckRow(
                            item = item,
                            onToggle = { viewModel.toggleCheck(item) },
                            onPraise = triggerPraise
                        )
                    }
                }

                // これもやった！カード
                if (bonusItems.isNotEmpty()) {
                    item {
                        Text(
                            "これもやった！",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }
                    items(bonusItems) { result ->
                        BonusResultCard(result = result)
                    }
                }

                item {
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { viewModel.openBonusSheet() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("これもやった！を追加")
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.completeReflection() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(56.dp)
                    ) {
                        Text("今日を閉じる")
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "持ち越しは失敗じゃないよ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        PraiseOverlay(visible = showPraise, triggerKey = praiseKey)
    }

    if (showBonusSheet) {
        BonusBottomSheet(
            tasks = activeTasks,
            onSelectTask = { viewModel.addBonusFromTask(it) },
            onFreeInput = { viewModel.addBonusFree(it) },
            onDismiss = { viewModel.closeBonusSheet() }
        )
    }
}

@Composable
private fun PlanCheckRow(
    item: DailyPlanItemEntity,
    onToggle: () -> Unit,
    onPraise: () -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = { checked ->
                    onToggle()
                    if (checked) onPraise()
                }
            )
            Spacer(Modifier.width(8.dp))
            Text(item.titleSnapshot, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun BonusResultCard(result: ReflectionResultEntity) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(result.titleSnapshot, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BonusBottomSheet(
    tasks: List<TaskEntity>,
    onSelectTask: (TaskEntity) -> Unit,
    onFreeInput: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var freeText by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            CharacterMessageCard(
                imageRes = R.drawable.character_bonus,
                message = "それもやったん？天才やん",
                imageSize = 64.dp
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = freeText,
                onValueChange = { freeText = it },
                label = { Text("自由に入力") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    if (freeText.isNotBlank()) {
                        TextButton(onClick = { onFreeInput(freeText) }) { Text("追加") }
                    }
                }
            )
            if (tasks.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "やることBOXから選ぶ",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                tasks.forEach { task ->
                    TextButton(
                        onClick = { onSelectTask(task) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(task.title, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
