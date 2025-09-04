package com.tureq.rickmorty.data.repo


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tureq.rickmorty.data.local.FavoritesStore
import com.tureq.rickmorty.data.remote.PagedResult
import com.tureq.rickmorty.data.remote.RmApi
import com.tureq.rickmorty.data.remote.toDomain
import com.tureq.rickmorty.domain.model.Characters
import kotlinx.coroutines.flow.Flow

class CharacterRepositoryImpl(
    private val api: RmApi,
    dataStore: DataStore<Preferences>
) : CharacterRepository {

    private val favStore = FavoritesStore(dataStore)

    override suspend fun getCharacters(page: Int, query: String): PagedResult<Characters> {
        val resp = if (query.isBlank()) {
            api.getCharacters(page = page)
        } else {
            api.getCharacters(page = page, name = query)
        }
        return PagedResult(
            items = resp.results.map { it.toDomain() },
            hasNext = resp.info.next != null
        )
    }

    override suspend fun getCharacter(id: Int): Characters = api.getCharacter(id).toDomain()
    override fun favorites(): Flow<Set<String>> = favStore.favoritesFlow
    override suspend fun toggleFavorite(id: Int) = favStore.toggle(id)
}

