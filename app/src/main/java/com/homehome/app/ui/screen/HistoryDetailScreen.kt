package com.homehome.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homehome.app.data.db.entity.ReflectionResultEntity
import com.homehome.app.ui.viewmodel.HistoryDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    viewModel: HistoryDetailViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.JAPAN)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        state.session?.closedAt?.let { dateFormat.format(Date(it)) } ?: "詳細"
                    )
                },
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ほめコメント
            state.session?.praiseText?.let { praise ->
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            praise,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            // 今日の約束
            item {
                Text(
                    "今日の約束",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            if (state.plannedResults.isEmpty()) {
                item { Text("約束なし", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                items(state.plannedResults) { result ->
                    ResultRow(result)
                }
            }

            // ボーナス
            if (state.bonusResults.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "ボーナスでできたこと",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                items(state.bonusResults) { result ->
                    ResultRow(result)
                }
            }
        }
    }
}

@Composable
private fun ResultRow(result: ReflectionResultEntity) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (result.isPlanned) {
                Text(
                    if (result.isCompleted) "✓ " else "○ ",
                    color = if (result.isCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text("★ ", color = MaterialTheme.colorScheme.tertiary)
            }
            Text(
                result.titleSnapshot,
                style = MaterialTheme.typography.bodyLarge,
                color = if (result.isPlanned && !result.isCompleted)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
