package com.n2a5.app.data.db

import androidx.room.*
import com.n2a5.app.data.model.Server
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {
    @Query("SELECT * FROM servers ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllServers(): Flow<List<Server>>

    @Query("SELECT * FROM servers WHERE id = :id")
    suspend fun getServerById(id: Long): Server?

    @Query("SELECT * FROM servers WHERE name LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%'")
    fun searchServers(query: String): Flow<List<Server>>

    @Query("SELECT * FROM servers WHERE protocol = :protocol ORDER BY isFavorite DESC, createdAt DESC")
    fun getServersByProtocol(protocol: String): Flow<List<Server>>

    @Query("SELECT * FROM servers WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteServers(): Flow<List<Server>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(server: Server): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(servers: List<Server>)

    @Update
    suspend fun update(server: Server)

    @Delete
    suspend fun delete(server: Server)

    @Query("DELETE FROM servers WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM servers WHERE subscriptionId = :subId")
    suspend fun deleteBySubscriptionId(subId: Long)

    @Query("UPDATE servers SET isFavorite = :fav WHERE id = :id")
    suspend fun setFavorite(id: Long, fav: Boolean)

    @Query("UPDATE servers SET pingMs = :ping WHERE id = :id")
    suspend fun updatePing(id: Long, ping: Int?)

    @Query("UPDATE servers SET lastUsed = :time WHERE id = :id")
    suspend fun updateLastUsed(id: Long, time: Long)

    @Query("SELECT COUNT(*) FROM servers")
    suspend fun getCount(): Int
}
