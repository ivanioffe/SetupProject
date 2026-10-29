package com.ioffeivan.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT username FROM users WHERE id = 1")
    fun observeUsername(): Flow<String?>

    @Query("SELECT username FROM users WHERE id = 1")
    suspend fun getUsername(): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)

    @Query("DELETE FROM users WHERE id = 1")
    suspend fun deleteUser()
}
