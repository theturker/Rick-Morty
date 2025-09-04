package com.tureq.rickmorty.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tureq.rickmorty.domain.model.Characters
import com.tureq.rickmorty.ui.UiState
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DetailScreen(
    state: StateFlow<UiState<Characters>>,
    isFavorite: StateFlow<Boolean>,
    onToggleFavorite: () -> Unit
) {
    val uiState by state.collectAsStateWithLifecycle()
    val favorite by isFavorite.collectAsStateWithLifecycle()

    when (val s = uiState) {
        UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is UiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Hata: ${s.message}")
        }
        is UiState.Success -> {
            val ch = s.data
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = ch.image,
                    contentDescription = ch.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )
                Text(ch.name, style = MaterialTheme.typography.headlineSmall)
                Text("${ch.species} • ${ch.status}", style = MaterialTheme.typography.bodyLarge)
                Text("Bölüm sayısı: ${ch.episodeCount}", style = MaterialTheme.typography.bodyMedium)
                FilledTonalButton(onClick = onToggleFavorite) {
                    Text(if (favorite) "Favoriden Çıkar" else "Favoriye Ekle")
                }
            }
        }
    }
}