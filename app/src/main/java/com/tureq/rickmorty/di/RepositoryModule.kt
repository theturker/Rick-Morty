package com.tureq.rickmorty.di


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tureq.rickmorty.data.remote.RmApi
import com.tureq.rickmorty.data.repo.CharacterRepository
import com.tureq.rickmorty.data.repo.CharacterRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides @Singleton
    fun provideCharacterRepository(
        api: RmApi,
        dataStore: DataStore<Preferences>
    ): CharacterRepository = CharacterRepositoryImpl(api, dataStore)
}
