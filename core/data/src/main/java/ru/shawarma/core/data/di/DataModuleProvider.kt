package ru.shawarma.core.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ru.shawarma.core.data.managers.InternetManager
import ru.shawarma.core.data.services.AuthService
import ru.shawarma.core.data.services.MenuService
import ru.shawarma.core.data.services.OrderService
import ru.shawarma.core.data.utils.ApplicationRetrofit

@InstallIn(SingletonComponent::class)
@Module
object DataModuleProvider {

    @Provides
    fun provideDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideAuthService(): AuthService = ApplicationRetrofit.authService

    @Provides
    fun provideMenuService(): MenuService = ApplicationRetrofit.menuService

    @Provides
    fun provideOrderService(): OrderService = ApplicationRetrofit.orderService

    @Provides
    fun provideInternetManager(@ApplicationContext context: Context): InternetManager = InternetManager(context)
}