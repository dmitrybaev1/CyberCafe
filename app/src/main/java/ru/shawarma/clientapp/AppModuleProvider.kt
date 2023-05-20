package ru.shawarma.clientapp

import android.app.PendingIntent
import android.content.Context
import androidx.navigation.NavDeepLinkBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.shawarma.core.data.OrderFragmentPendingIntentCreator

@InstallIn(SingletonComponent::class)
@Module
object AppModuleProvider {

    @Provides
    fun provideOrderFragmentPendingIntentCreator(@ApplicationContext context: Context)
    : OrderFragmentPendingIntentCreator =
        NavDeepLinkBuilderWrapper(NavDeepLinkBuilder(context)).setupOrderFragmentPendingIntent()
}