package com.tureq.rickmorty.domain.usecase

import com.tureq.rickmorty.data.remote.PagedResult
import com.tureq.rickmorty.data.repo.CharacterRepository
import com.tureq.rickmorty.domain.model.Characters
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(
    private val repo: CharacterRepository
) {
    suspend operator fun invoke(page: Int, query: String): PagedResult<Characters> =
        repo.getCharacters(page, query)
}

