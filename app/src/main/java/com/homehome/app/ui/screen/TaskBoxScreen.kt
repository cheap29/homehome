package com.homehome.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homehome.app.data.db.entity.TaskEntity
import com.homehome.app.ui.viewmodel.TaskBoxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBoxScreen(
    viewModel: TaskBoxViewModel,
    onBack: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("やることBOX") },
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
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "思いついたことをどんどん入れよう",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onEdit = { editingTask = task },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        TaskEditDialog(
            title = "",
            memo = "",
            onConfirm = { title, memo ->
                viewModel.addTask(title, memo)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    editingTask?.let { task ->
        TaskEditDialog(
            title = task.title,
            memo = task.memo ?: "",
            onConfirm = { title, memo ->
                viewModel.updateTask(task, title, memo)
                editingTask = null
            },
            onDismiss = { editingTask = null }
        )
    }
}

@Composable
private fun TaskItem(
    task: TaskEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.bodyLarge)
                if (!task.memo.isNullOrBlank()) {
                    Text(
                        task.memo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "編集", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "削除", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun TaskEditDialog(
    title: String,
    memo: String,
    onConfirm: (String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var titleState by remember { mutableStateOf(title) }
    var memoState by remember { mutableStateOf(memo) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (title.isEmpty()) "追加" else "編集") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titleState,
                    onValueChange = { titleState = it },
                    label = { Text("タイトル") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = memoState,
                    onValueChange = { memoState = it },
                    label = { Text("メモ（任意）") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (titleState.isNotBlank()) onConfirm(titleState, memoState) },
                enabled = titleState.isNotBlank()
            ) { Text("保存") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("やめる") }
        }
    )
}
