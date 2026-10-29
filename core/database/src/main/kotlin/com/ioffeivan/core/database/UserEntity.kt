package com.ioffeivan.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    val username: String,
    @PrimaryKey val id: Int = LOCAL_USER_ID,
) {
    companion object {
        const val LOCAL_USER_ID = 1
    }
}
