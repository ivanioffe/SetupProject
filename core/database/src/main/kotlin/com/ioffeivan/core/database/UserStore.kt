package com.ioffeivan.core.database

import kotlinx.coroutines.flow.Flow

interface UserStore {
    val username: Flow<String?>

    suspend fun readUsername(): String?

    suspend fun saveUsername(username: String)

    suspend fun deleteUsername()
}
