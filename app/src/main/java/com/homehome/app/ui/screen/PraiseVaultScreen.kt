package com.homehome.app.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homehome.app.R
import com.homehome.app.data.db.entity.UserStatsEntity
import com.homehome.app.ui.viewmodel.PraiseVaultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PraiseVaultScreen(
    viewModel: PraiseVaultViewModel,
    onBack: () -> Unit,
    onNavigateToForest: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ほめほめビーカー") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Image(
                    painter = painterResource(R.drawable.character_vault),
                    contentDescription = "ほめほめビーカー",
                    modifier = Modifier.size(140.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "振り返りのたびにほめほめが貯まるよ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            item {
                PraiseCounterCard(stats = stats)
            }

            if (stats.praiseSuper > 0) {
                item {
                    Button(
                        onClick = { viewModel.tryEnterForest(onNavigateToForest) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Text(
                            "🌳 ほめほめの森へ（スーパーほめほめ×1消費）",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PraiseCounterCard(stats: UserStatsEntity) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PraiseRow(
                label = "ほめほめ",
                count = stats.praise,
                max = 10,
                color = Color(0xFF6650A4)
            )
            PraiseRow(
                label = "ほめほめ中",
                count = stats.praiseMedium,
                max = 10,
                color = Color(0xFF2196F3)
            )
            PraiseRow(
                label = "ほめほめ大",
                count = stats.praiseLarge,
                max = 10,
                color = Color(0xFFE91E63)
            )
            PraiseRow(
                label = "スーパーほめほめ",
                count = stats.praiseSuper,
                max = null,
                color = Color(0xFFFF9800)
            )
        }
    }
}

@Composable
private fun PraiseRow(label: String, count: Int, max: Int?, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(120.dp)
        )
        if (max != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(max) { i ->
                    val filled = i < count
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (filled) color else color.copy(alpha = 0.15f),
                        modifier = Modifier.size(18.dp)
                    ) {}
                }
            }
        } else {
            Text(
                "× $count",
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 20.sp
            )
        }
    }
}
