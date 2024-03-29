package ru.shawarma.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.shawarma.core.data.datasources.*
import ru.shawarma.core.data.managers.InternetManager
import ru.shawarma.core.data.managers.MainInternetManager
import ru.shawarma.core.data.repositories.*
import ru.shawarma.core.data.managers.MainTokenManager
import ru.shawarma.core.data.managers.TokenManager

@InstallIn(SingletonComponent::class)
@Module
abstract class DataModuleBinder {

    @Binds
    abstract fun bindAuthRemoteDataSource(dataSource: MainAuthRemoteDataSource): AuthRemoteDataSource

    @Binds
    abstract fun bindMenuRemoteDataSource(dataSource: MainMenuRemoteDataSource): MenuRemoteDataSource

    @Binds
    abstract fun bindOrderRemoteDataSource(dataSource: MainOrderRemoteDataSource): OrderRemoteDataSource

    @Binds
    abstract fun bindAuthRepository(repository: MainAuthRepository): AuthRepository

    @Binds
    abstract fun bindMenuRepository(repository: MainMenuRepository): MenuRepository

    @Binds
    abstract fun bindOrderRepository(repository: MainOrderRepository): OrderRepository

    @Binds
    abstract fun bindTokenManager(tokenManager: MainTokenManager): TokenManager

    @Binds
    abstract fun bindInternetManager(internetManager: MainInternetManager): InternetManager

}
