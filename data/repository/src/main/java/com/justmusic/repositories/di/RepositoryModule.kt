/*
Copyright 2022 Vasim Mansuri

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.justmusic.repositories.di

import com.justmusic.cache.AppDatabase
import com.justmusic.domain.repository.AlbumsRepository
import com.justmusic.domain.repository.MyFavoritesRepository
import com.justmusic.domain.repository.SearchRepository
import com.justmusic.domain.repository.SongsRepository
import com.justmusic.network.ApiService
import com.justmusic.repositories.data_source.AlbumsRepositoryImpl
import com.justmusic.repositories.data_source.MyFavoritesRepositoryImpl
import com.justmusic.repositories.data_source.SearchRepositoryImpl
import com.justmusic.repositories.data_source.SongsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository module to register repositories class in hilt dagger tree
 **/
@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
    @Singleton
    @Provides
    fun provideSongsRepository(
        apiService: ApiService,
        appDatabase: AppDatabase
    ): SongsRepository {
        return SongsRepositoryImpl(apiService, appDatabase)
    }

    @Singleton
    @Provides
    fun provideAlbumsRepository(
        apiService: ApiService,
        appDatabase: AppDatabase
    ): AlbumsRepository {
        return AlbumsRepositoryImpl(apiService, appDatabase)
    }

    @Singleton
    @Provides
    fun provideMyFavoritesRepository(
        apiService: ApiService,
        appDatabase: AppDatabase
    ): MyFavoritesRepository {
        return MyFavoritesRepositoryImpl(apiService, appDatabase)
    }

    @Singleton
    @Provides
    fun provideSearchRepository(
        apiService: ApiService,
        appDatabase: AppDatabase
    ): SearchRepository {
        return SearchRepositoryImpl(apiService, appDatabase)
    }
}
