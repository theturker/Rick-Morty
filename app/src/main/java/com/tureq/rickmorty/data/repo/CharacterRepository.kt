package com.tureq.rickmorty.data.repo


import com.tureq.rickmorty.data.remote.PagedResult
import com.tureq.rickmorty.domain.model.Characters
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    suspend fun getCharacters(page: Int, query: String): PagedResult<Characters>
    suspend fun getCharacter(id: Int): Characters
    fun favorites(): Flow<Set<String>>
    suspend fun toggleFavorite(id: Int)
}
