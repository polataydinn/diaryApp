package com.yahya.dailyflow.di

import android.content.Context
import androidx.room.Room
import com.yahya.dailyflow.data.database.ImagesDatabase
import com.yahya.dailyflow.util.Constants.IMAGE_DATABASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ImagesDatabase {
        return Room.databaseBuilder(
            context = context, klass = ImagesDatabase::class.java, name = IMAGE_DATABASE
        ).build()
    }

    @Provides
    @Singleton
    fun provideImageToUploadDao(database: ImagesDatabase) = database.imageToUploadDao()

    @Provides
    @Singleton
    fun provideImageToDeleteDao(database: ImagesDatabase) = database.imageToDeleteDao()
}