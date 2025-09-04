package com.tureq.rickmorty.ui.detail


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tureq.rickmorty.data.repo.CharacterRepository
import com.tureq.rickmorty.domain.model.Characters
import com.tureq.rickmorty.domain.usecase.GetCharacterDetailUseCase
import com.tureq.rickmorty.domain.usecase.ToggleFavoriteUseCase
import com.tureq.rickmorty.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getDetail: GetCharacterDetailUseCase,
    private val toggleFavoriteUc: ToggleFavoriteUseCase,
    repo: CharacterRepository
) : ViewModel() {

    private val id: Int = checkNotNull(savedStateHandle["id"])

    private val _state = MutableStateFlow<UiState<Characters>>(UiState.Loading)
    val state: StateFlow<UiState<Characters>> = _state.asStateFlow()

    val isFavorite: StateFlow<Boolean> =
        repo.favorites().map { it.contains(id.toString()) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init { load() }

    private fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching { getDetail(id) }
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.message ?: "Hata") }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch { toggleFavoriteUc(id) }
    }
}
