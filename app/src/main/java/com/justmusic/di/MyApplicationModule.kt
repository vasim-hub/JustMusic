package com.justmusic.di

import android.app.Application
import com.justmusic.JustMusicApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MyApplicationModule {

    @Singleton
    @Provides
    fun providesMyApplicationInstance(application: Application): JustMusicApplication = application as JustMusicApplication
}
