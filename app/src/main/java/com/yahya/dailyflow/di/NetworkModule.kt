package com.yahya.dailyflow.di

import android.content.Context
import com.yahya.dailyflow.connectivity.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideNetworkConnectivityObserver(@ApplicationContext context: Context) =
        NetworkConnectivityObserver(context = context)

}