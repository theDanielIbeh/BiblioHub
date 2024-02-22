package com.example.bibliohub.data.entities.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Query(
        "SELECT * FROM user"
    )
    fun getAllUsers(): Flow<List<User>>

    @Query(
        "SELECT * FROM user where email = :email"
    )
    fun getUser(email: String): User?
}