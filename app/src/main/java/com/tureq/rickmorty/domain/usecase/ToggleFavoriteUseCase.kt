package com.tureq.rickmorty.domain.usecase


import com.tureq.rickmorty.data.repo.CharacterRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repo: CharacterRepository
) {
    suspend operator fun invoke(id: Int) = repo.toggleFavorite(id)
}
