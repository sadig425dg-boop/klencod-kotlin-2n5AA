package com.n2a5.app.data.db

import androidx.room.*
import com.n2a5.app.data.model.ConnectionHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface ConnectionHistoryDao {
    @Query("SELECT * FROM connection_history ORDER BY connectedAt DESC LIMIT :limit")
    fun getRecent(limit: Int = 20): Flow<List<ConnectionHistory>>

    @Insert
    suspend fun insert(history: ConnectionHistory): Long

    @Query("DELETE FROM connection_history")
    suspend fun deleteAll()
}
