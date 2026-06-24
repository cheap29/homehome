package com.homehome.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homehome.app.R
import com.homehome.app.ui.component.CharacterMessageCard
import com.homehome.app.ui.component.PraiseOverlay
import com.homehome.app.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSelectThree: () -> Unit,
    onNavigateToReflection: () -> Unit,
    onNavigateToTaskBox: () -> Unit,
    onNavigateToHabitWords: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToPraiseVault: () -> Unit
) {
    val homeState by viewModel.homeState.collectAsState()
    val planItems = homeState.planItems

    var showPraise by remember { mutableStateOf(false) }
    var praiseKey by remember { mutableStateOf(0) }

    LaunchedEffect(showPraise) {
        if (showPraise) {
            delay(3000L)
            showPraise = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "自分ほめほめ",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    actions = {
                        IconButton(onClick = onNavigateToHistory) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = "履歴",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    CharacterMessageCard(
                        imageRes = R.drawable.character_home,
                        message = "今日の約束は3つだけでええよ",
                        subMessage = "できた分だけ、自分ほめほめしよ",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "今日の約束",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (planItems.isNotEmpty()) {
                            TextButton(
                                onClick = { viewModel.resetPlan() },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Text(
                                    "選び直す",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (planItems.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .clip(MaterialTheme.shapes.large)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            MaterialTheme.colorScheme.secondaryContainer
                                        )
                                    )
                                )
                                .padding(24.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "次の自分への約束を3つだけ決めよう",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(Modifier.height(16.dp))
                                Button(
                                    onClick = onNavigateToSelectThree,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text("今日の3つを選ぶ")
                                }
                            }
                        }
                    }
                } else {
                    items(planItems) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 4.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(
                                containerColor = if (item.isChecked)
                                    MaterialTheme.colorScheme.surfaceVariant
                                else
                                    MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (item.isChecked) 0.dp else 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = item.isChecked,
                                    onCheckedChange = { checked ->
                                        viewModel.toggleCheck(item)
                                        if (checked) {
                                            praiseKey++
                                            showPraise = true
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = item.titleSnapshot,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
                                    ),
                                    color = if (item.isChecked)
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    else
                                        MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onNavigateToReflection,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("振り返る", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }

                item {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(
                            "やることBOX" to onNavigateToTaskBox,
                            "単語帳" to onNavigateToHabitWords,
                            "履歴" to onNavigateToHistory,
                            "ビーカー" to onNavigateToPraiseVault
                        ).forEach { (label, onClick) ->
                            TextButton(onClick = onClick) {
                                Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        PraiseOverlay(visible = showPraise, triggerKey = praiseKey)
    }
}
