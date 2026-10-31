package com.n2a5.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "connection_history")
data class ConnectionHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val serverId: Long? = null,
    val serverName: String? = null,
    val protocol: String? = null,
    val connectedAt: Long = System.currentTimeMillis(),
    val disconnectedAt: Long? = null,
    val durationSeconds: Int? = null,
    val downloadBytes: Long = 0,
    val uploadBytes: Long = 0,
    val success: Boolean = true,
    val errorMessage: String? = null
)

@Entity(tableName = "app_logs")
data class AppLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val level: String, // info, warn, error, debug
    val category: String, // vpn, xray, network, hotspot, subscriptions
    val message: String,
    val createdAt: Long = System.currentTimeMillis()
)
