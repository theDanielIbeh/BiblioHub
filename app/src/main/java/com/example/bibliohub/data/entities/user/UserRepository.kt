package com.example.bibliohub.data.entities.user

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insert(user: User)
    suspend fun getAllUsers(): Flow<List<User>>
    suspend fun getUser(email: String): User?
}

class OfflineUserRepository(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun insert(user: User) {
        userDao.insert(user = user)
    }

    override suspend fun getAllUsers(): Flow<List<User>> =
        userDao.getAllUsers()

    override suspend fun getUser(email: String): User? =
        userDao.getUser(email = email)
}