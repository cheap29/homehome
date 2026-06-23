package com.homehome.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homehome.app.R
import com.homehome.app.ui.component.CharacterMessageCard
import com.homehome.app.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSelectThree: () -> Unit,
    onNavigateToReflection: () -> Unit,
    onNavigateToTaskBox: () -> Unit,
    onNavigateToHabitWords: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val homeState by viewModel.homeState.collectAsState()
    val planItems = homeState.planItems

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("自分ほめほめ", style = MaterialTheme.typography.headlineSmall) },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "履歴")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // キャラクター
            item {
                CharacterMessageCard(
                    imageRes = R.drawable.character_home,
                    message = "今日の約束は3つだけでええよ",
                    subMessage = "できた分だけ、自分ほめほめしよ",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 今日の3つ
            item {
                Text(
                    text = "今日の約束",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            if (planItems.isEmpty()) {
                item {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "次の自分への約束を3つだけ決めよう",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(16.dp))
                            FilledTonalButton(onClick = onNavigateToSelectThree) {
                                Text("今日の3つを選ぶ")
                            }
                        }
                    }
                }
            } else {
                items(planItems) { item ->
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
                                onCheckedChange = { viewModel.toggleCheck(item) }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = item.titleSnapshot,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 振り返りボタン（大・FilledButton）
            item {
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onNavigateToReflection,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(56.dp)
                ) {
                    Text("振り返る", style = MaterialTheme.typography.labelLarge)
                }
            }

            // サブナビ（フカツさん案：小さいTextButton 3つ）
            item {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onNavigateToTaskBox) {
                        Text("やることBOX")
                    }
                    TextButton(onClick = onNavigateToHabitWords) {
                        Text("単語帳")
                    }
                    TextButton(onClick = onNavigateToHistory) {
                        Text("履歴")
                    }
                }
            }
        }
    }
}
