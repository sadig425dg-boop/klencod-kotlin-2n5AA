package com.n2a5.app.data.db

import androidx.room.*
import com.n2a5.app.data.model.Subscription
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getById(id: Long): Subscription?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sub: Subscription): Long

    @Update
    suspend fun update(sub: Subscription)

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE subscriptions SET lastUpdated = :time, serverCount = :count WHERE id = :id")
    suspend fun updateStatus(id: Long, time: Long, count: Int)
}
