package com.tureq.rickmorty.data.local


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesStore(private val dataStore: DataStore<Preferences>) {
    private val KEY = stringSetPreferencesKey("favorite_ids")

    val favoritesFlow: Flow<Set<String>> =
        dataStore.data.map { prefs -> prefs[KEY] ?: emptySet() }

    suspend fun toggle(id: Int) {
        dataStore.edit { prefs ->
            val current = prefs[KEY]?.toMutableSet() ?: mutableSetOf()
            val key = id.toString()
            if (current.contains(key)) current.remove(key) else current.add(key)
            prefs[KEY] = current
        }
    }
}
