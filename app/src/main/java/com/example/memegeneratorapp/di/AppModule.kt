package com.example.memegeneratorapp.di

import com.example.memegeneratorapp.data.repository.MemeRepositoryImpl
import com.example.memegeneratorapp.domain.repository.MemeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    abstract fun bindMemeRepository(
        impl: MemeRepositoryImpl
    ): MemeRepository
}