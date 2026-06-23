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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homehome.app.R
import com.homehome.app.data.db.entity.ReflectionSessionEntity
import com.homehome.app.ui.component.CharacterMessageCard
import com.homehome.app.ui.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onBack: () -> Unit,
    onDetail: (Long) -> Unit
) {
    val sessions by viewModel.sessions.collectAsState()
    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.JAPAN)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("履歴") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "もどる")
                    }
                }
            )
        }
    ) { padding ->
        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CharacterMessageCard(
                    imageRes = R.drawable.character_empty,
                    message = "ちゃんと積み重なってるで",
                    subMessage = "振り返りを完了すると、ここに記録されるよ"
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
                item {
                    CharacterMessageCard(
                        imageRes = R.drawable.character_history,
                        message = "ちゃんと積み重なってるで",
                        imageSize = 64.dp
                    )
                }
                items(sessions, key = { it.id }) { session ->
                    HistoryCard(
                        session = session,
                        dateText = session.closedAt?.let { dateFormat.format(Date(it)) } ?: "",
                        onClick = { onDetail(session.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(
    session: ReflectionSessionEntity,
    dateText: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                dateText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                session.praiseText ?: "",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
