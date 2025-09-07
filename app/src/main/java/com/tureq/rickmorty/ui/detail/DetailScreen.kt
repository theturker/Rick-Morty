package com.tureq.rickmorty.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tureq.rickmorty.domain.model.Characters
import com.tureq.rickmorty.ui.UiState
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    state: StateFlow<UiState<Characters>>,
    isFavorite: StateFlow<Boolean>,
    onToggleFavorite: () -> Unit
) {
    val uiState by state.collectAsStateWithLifecycle()
    val favorite by isFavorite.collectAsStateWithLifecycle()

    when (val s = uiState) {
        UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Hata: ${s.message}") }
        is UiState.Success -> {
            val ch = s.data
            Scaffold(
                topBar = { CenterAlignedTopAppBar(title = { Text(ch.name, maxLines = 1) }) },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = onToggleFavorite,
                        icon = {
                            Icon(if (favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, contentDescription = null)
                        },
                        text = { Text(if (favorite) "Favoriden Çıkar" else "Favoriye Ekle") }
                    )
                }
            ) { padding ->
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Hero görsel
                    Box(Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = ch.image,
                            contentDescription = ch.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                        )
                        // alttan gradient okunabilirlik için
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(0f to Color.Transparent, 1f to MaterialTheme.colorScheme.background)
                                )
                        )
                        Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                            Text(ch.name, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold))
                            Spacer(Modifier.height(4.dp))
                            AssistChip(onClick = {}, label = { Text(ch.status) })
                        }
                    }

                    // Bilgi kartları
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoCard(title = "Bölüm Sayısı", value = ch.episodeCount.toString(), modifier = Modifier.weight(1f))
                        InfoCard(title = "ID", value = "#${ch.id}", modifier = Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(8.dp))
                    ElevatedCard(modifier = Modifier.padding(16.dp).fillMaxWidth(), shape = MaterialTheme.shapes.extraLarge) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Detaylar", style = MaterialTheme.typography.titleMedium)
                            KeyValueRow("Durum", ch.status)
                            KeyValueRow("Tür", ch.species)}
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier, shape = MaterialTheme.shapes.extraLarge) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun KeyValueRow(key: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(key, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
