package com.n2a5.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val url: String,
    val isActive: Boolean = true,
    val autoUpdate: Boolean = true,
    val updateIntervalHours: Int = 24,
    val lastUpdated: Long? = null,
    val serverCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
