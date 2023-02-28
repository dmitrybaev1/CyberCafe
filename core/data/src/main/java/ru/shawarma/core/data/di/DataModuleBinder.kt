package ru.shawarma.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.shawarma.core.data.datasources.AuthRemoteDataSource
import ru.shawarma.core.data.datasources.MainAuthRemoteDataSource
import ru.shawarma.core.data.datasources.MainMenuRemoteDataSource
import ru.shawarma.core.data.datasources.MenuRemoteDataSource
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.repositories.MainAuthRepository
import ru.shawarma.core.data.repositories.MainMenuRepository
import ru.shawarma.core.data.repositories.MenuRepository
import ru.shawarma.core.data.utils.MainTokenManager
import ru.shawarma.core.data.utils.TokenManager

@InstallIn(SingletonComponent::class)
@Module
abstract class DataModuleBinder {

    @Binds
    abstract fun bindAuthRemoteDataSource(dataSource: MainAuthRemoteDataSource): AuthRemoteDataSource

    @Binds
    abstract fun bindMenuRemoteDataSource(dataSource: MainMenuRemoteDataSource): MenuRemoteDataSource

    @Binds
    abstract fun bindAuthRepository(repository: MainAuthRepository): AuthRepository

    @Binds
    abstract fun bindMenuRepository(repository: MainMenuRepository): MenuRepository

    @Binds
    abstract fun bindTokenManager(tokenManager: MainTokenManager): TokenManager

}
