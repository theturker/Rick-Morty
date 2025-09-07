package com.tureq.rickmorty.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.tureq.rickmorty.domain.model.Characters
import com.tureq.rickmorty.ui.UiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    state: StateFlow<UiState<Unit>>,
    items: StateFlow<List<Characters>>,
    favorites: StateFlow<Set<String>>,
    isAppending: StateFlow<Boolean>,
    onQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onOpenDetail: (Int) -> Unit,
    onLoadNext: () -> Unit
) {
    val uiState by state.collectAsStateWithLifecycle()
    val data by items.collectAsStateWithLifecycle()
    val favs by favorites.collectAsStateWithLifecycle()
    val appending by isAppending.collectAsStateWithLifecycle()

    var query by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Sonsuza yakın kaydırma
    LaunchedEffect(listState, data.size) {
        snapshotFlow {
            val li = listState.layoutInfo
            val lastVisible = li.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible to li.totalItemsCount
        }.distinctUntilChanged().collect { (lastVisible, total) ->
            if (total > 0 && lastVisible >= total - 4) onLoadNext()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text("Rick & Morty", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            })
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    onQueryChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                label = { Text("Ara (ör. rick)") },
                singleLine = true,
                shape = MaterialTheme.shapes.large
            )
            Spacer(Modifier.height(12.dp))

            when (uiState) {
                UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                is UiState.Error -> ErrorState((uiState as UiState.Error).message, onRetry)

                is UiState.Success -> {
                    if (data.isEmpty()) {
                        EmptyState()
                    } else {
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(data, key = { it.id }) { ch ->
                                CharacterRow(
                                    ch = ch,
                                    isFav = favs.contains(ch.id.toString()),
                                    onToggleFavorite = { onToggleFavorite(ch.id) },
                                    onClick = { onOpenDetail(ch.id) }
                                )
                            }
                            if (appending) {
                                item {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) { CircularProgressIndicator() }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bir şeyler ters gitti", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(10.dp))
        Button(onClick = onRetry) { Text("Tekrar Dene") }
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Kayıt bulunamadı", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun CharacterRow(
    ch: Characters,
    isFav: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ch.image,
                contentDescription = ch.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp)
                    .clip(MaterialTheme.shapes.large)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(ch.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${ch.species} • ${ch.status}", style = MaterialTheme.typography.bodyMedium)
                Text("Bölüm: ${ch.episodeCount}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFav) Icons.Filled.Star else Icons.Default.Add,
                    contentDescription = "Favori"
                )
            }
        }
    }
}