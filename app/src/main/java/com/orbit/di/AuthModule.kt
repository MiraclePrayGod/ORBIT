package com.orbit.di

import android.content.Context
import com.orbit.data.auth.MicrosoftAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideMicrosoftAuthRepository(
        @ApplicationContext context: Context
    ): MicrosoftAuthRepository {
        return MicrosoftAuthRepository(context)
    }
}
