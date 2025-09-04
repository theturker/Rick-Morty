package com.tureq.rickmorty.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tureq.rickmorty.data.repo.CharacterRepository
import com.tureq.rickmorty.domain.model.Characters
import com.tureq.rickmorty.domain.usecase.GetCharactersUseCase
import com.tureq.rickmorty.domain.usecase.ToggleFavoriteUseCase
import com.tureq.rickmorty.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val getCharacters: GetCharactersUseCase,
    private val toggleFavoriteUc: ToggleFavoriteUseCase,
    repo: CharacterRepository
) : ViewModel() {

    // Gösterilecek liste
    private val _items = MutableStateFlow<List<Characters>>(emptyList())
    val items: StateFlow<List<Characters>> = _items.asStateFlow()

    // Ekran genel state’i (ilk yükleme / hata)
    private val _state = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val state: StateFlow<UiState<Unit>> = _state.asStateFlow()

    // Ek sayfa yükleniyor mu?
    private val _isAppending = MutableStateFlow(false)
    val isAppending: StateFlow<Boolean> = _isAppending.asStateFlow()

    val favorites: StateFlow<Set<String>> =
        repo.favorites().stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    private var page = 1
    private var hasNext = true
    private var isLoading = false
    private val _query = MutableStateFlow("")

    init { refresh() }

    fun onQueryChange(q: String) {
        _query.value = q
        // debounce basit
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(350)
            refresh()
        }
    }

    private var searchJob: Job? = null

    fun refresh() {
        page = 1
        hasNext = true
        isLoading = false
        _items.value = emptyList()
        loadPage(initial = true)
    }

    fun loadNext() {
        if (!hasNext || isLoading) return
        page += 1
        loadPage(initial = false)
    }

    private fun loadPage(initial: Boolean) {
        viewModelScope.launch {
            try {
                isLoading = true
                if (initial) _state.value = UiState.Loading else _isAppending.value = true
                val res = getCharacters(page, _query.value)
                hasNext = res.hasNext
                _items.value = if (initial) res.items else _items.value + res.items
                if (initial) _state.value = UiState.Success(Unit)
            } catch (t: Throwable) {
                if (initial) _state.value = UiState.Error(t.message ?: "Hata")
                else { // ek yükleme hatası -> sayfayı geri sar
                    page -= 1
                }
            } finally {
                isLoading = false
                _isAppending.value = false
            }
        }
    }

    fun retry() = refresh()

    fun toggleFavorite(id: Int) {
        viewModelScope.launch { toggleFavoriteUc(id) }
    }
}
