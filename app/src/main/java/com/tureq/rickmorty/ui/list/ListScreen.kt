package com.tureq.rickmorty.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.saveable.rememberSaveable
import com.tureq.rickmorty.domain.model.Characters
import com.tureq.rickmorty.ui.UiState
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged

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

    // Liste sonuna yaklaşınca tetikle
    LaunchedEffect(listState, data.size) {
        snapshotFlow {
            val li = listState.layoutInfo
            val lastVisible = li.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible to li.totalItemsCount
        }.distinctUntilChanged()
            .collect { (lastVisible, total) ->
                if (total > 0 && lastVisible >= total - 5) onLoadNext()
            }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                onQueryChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Ara (ör. rick)") }
        )
        Spacer(Modifier.height(12.dp))

        when (uiState) {
            UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            is UiState.Error -> Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Hata:  ${(uiState as UiState.Error).message}")
                Spacer(Modifier.height(8.dp))
                Button(onClick = onRetry) { Text("Tekrar Dene") }
            }
            is UiState.Success -> {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(data, key = { it.id }) { ch ->
                        CharacterItem(
                            ch = ch,
                            isFav = favs.contains(ch.id.toString()),
                            onToggleFavorite = { onToggleFavorite(ch.id) },
                            onClick = { onOpenDetail(ch.id) }
                        )
                    }
                    // Footer: ek sayfa yüklenirken progress
                    if (appending) {
                        item {
                            Box(
                                Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterItem(
    ch: Characters,
    isFav: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ch.image,
                contentDescription = ch.name,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(ch.name, style = MaterialTheme.typography.titleMedium)
                Text("${ch.species} • ${ch.status}", style = MaterialTheme.typography.bodyMedium)
                Text("Bölüm: ${ch.episodeCount}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFav) Icons.Default.Star else Icons.Outlined.StarBorder,
                    contentDescription = "fav"
                )
            }
        }
    }
}

// basit ikon importları
private object Icons {
    object Default {
        val Star = androidx.compose.material.icons.Icons.Default.Star
    }
    object Outlined {
        val StarBorder = androidx.compose.material.icons.Icons.Outlined.Add
    }
}