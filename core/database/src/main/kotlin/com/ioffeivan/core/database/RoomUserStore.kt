package com.ioffeivan.core.database

import javax.inject.Inject

class RoomUserStore @Inject constructor(
    private val userDao: UserDao,
) : UserStore {
    override val username = userDao.observeUsername()

    override suspend fun readUsername() = userDao.getUsername()

    override suspend fun saveUsername(username: String) = userDao.upsert(UserEntity(username = username))

    override suspend fun deleteUsername() = userDao.deleteUser()
}
