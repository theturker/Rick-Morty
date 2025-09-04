package com.tureq.rickmorty.domain.usecase


import com.tureq.rickmorty.data.repo.CharacterRepository
import com.tureq.rickmorty.domain.model.Characters
import javax.inject.Inject

class GetCharacterDetailUseCase @Inject constructor(
    private val repo: CharacterRepository
) {
    suspend operator fun invoke(id: Int): Characters = repo.getCharacter(id)
}
