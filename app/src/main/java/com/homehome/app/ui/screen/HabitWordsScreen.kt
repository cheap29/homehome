package com.homehome.app.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.homehome.app.data.db.entity.HabitWordEntity
import com.homehome.app.ui.viewmodel.HabitWordsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitWordsScreen(
    viewModel: HabitWordsViewModel,
    onBack: () -> Unit
) {
    val words by viewModel.habitWords.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingWord by remember { mutableStateOf<HabitWordEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("毎日の単語帳") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "もどる")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, "追加")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(words, key = { it.id }) { word ->
                NotebookCard(
                    word = word,
                    onEdit = { editingWord = word },
                    onDelete = { viewModel.deleteWord(word) }
                )
            }
        }
    }

    if (showAddDialog) {
        WordEditDialog(
            initial = "",
            onConfirm = { title ->
                viewModel.addWord(title)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    editingWord?.let { word ->
        WordEditDialog(
            initial = word.title,
            onConfirm = { title ->
                viewModel.updateWord(word, title)
                editingWord = null
            },
            onDismiss = { editingWord = null }
        )
    }
}

@Composable
private fun NotebookCard(
    word: HabitWordEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // パンチ穴エリア
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .heightIn(min = 56.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFEDE7C8)),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(18.dp)) {
                    drawCircle(color = Color.White, radius = size.minDimension / 2)
                    drawCircle(
                        color = Color(0xFFC8BB87),
                        radius = size.minDimension / 2,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }
            // コンテンツ
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    word.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF4A3B1F),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "編集", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "削除", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun WordEditDialog(
    initial: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initial) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial.isEmpty()) "単語を追加" else "単語を編集") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("単語") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (text.isNotBlank()) onConfirm(text) },
                enabled = text.isNotBlank()
            ) { Text("保存") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("やめる") }
        }
    )
}
