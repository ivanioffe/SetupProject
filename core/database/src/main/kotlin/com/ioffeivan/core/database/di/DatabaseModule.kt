package com.ioffeivan.core.database.di

import android.content.Context
import androidx.room.Room
import com.ioffeivan.core.database.RoomUserStore
import com.ioffeivan.core.database.UserDao
import com.ioffeivan.core.database.UserDatabase
import com.ioffeivan.core.database.UserStore
import dagger.Binds
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
        @ApplicationContext context: Context,
    ): UserDatabase =
        Room.databaseBuilder(context, UserDatabase::class.java, "users.db").build()

    @Provides
    // CI-DEMO: пропущен пробел после двоеточия для ktlint.
    fun provideUserDao(database:UserDatabase): UserDao = database.userDao()
}

@Module
@InstallIn(SingletonComponent::class)
// CI-DEMO: абстрактный класс вместо интерфейса для Detekt.
abstract class UserStoreModule {
    @Binds
    @Singleton
    abstract fun bindUserStore(store: RoomUserStore): UserStore
}
