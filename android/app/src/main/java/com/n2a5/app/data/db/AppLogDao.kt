package com.n2a5.app.data.db

import androidx.room.*
import com.n2a5.app.data.model.AppLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AppLogDao {
    @Query("SELECT * FROM app_logs ORDER BY createdAt DESC LIMIT :limit")
    fun getRecent(limit: Int = 200): Flow<List<AppLog>>

    @Query("SELECT * FROM app_logs WHERE category = :category ORDER BY createdAt DESC LIMIT :limit")
    fun getByCategory(category: String, limit: Int = 200): Flow<List<AppLog>>

    @Insert
    suspend fun insert(log: AppLog): Long

    @Query("DELETE FROM app_logs")
    suspend fun deleteAll()
}
