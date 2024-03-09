package com.example.bibliohub.data.entities.user

import androidx.lifecycle.LiveData

interface UserRepository {
    suspend fun insert(user: User)
    suspend fun getAllUsers(): LiveData<List<User>>
    suspend fun getUser(email: String): User?
    suspend fun getUserById(userId: Int): User?
}

class OfflineUserRepository(
    private val userDao: UserDao
) : UserRepository {
    override suspend fun insert(user: User) {
        userDao.insert(user = user)
    }

    override suspend fun getAllUsers(): LiveData<List<User>> =
        userDao.getAllUsers()

    override suspend fun getUser(email: String): User? =
        userDao.getUser(email = email)

    override suspend fun getUserById(userId: Int): User? =
        userDao.getUserById(userId = userId)
}