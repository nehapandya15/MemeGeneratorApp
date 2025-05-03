package com.example.memegeneratorapp.di

import android.content.Context
import com.example.memegeneratorapp.data.repository.MemeRepositoryImpl
import com.example.memegeneratorapp.domain.repository.MemeRepository
import com.example.memegeneratorapp.domain.usecase.SaveMemeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideMemeRepository(@ApplicationContext context: Context): MemeRepository =
        MemeRepositoryImpl(context)

    @Provides
    fun provideSaveMemeUseCase(repo: MemeRepository): SaveMemeUseCase =
        SaveMemeUseCase(repo)
}