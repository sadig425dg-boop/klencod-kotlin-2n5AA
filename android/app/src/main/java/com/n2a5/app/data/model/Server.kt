package com.n2a5.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers")
data class Server(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val protocol: String, // vless, vmess, trojan, ss
    val address: String,
    val port: Int,
    val uuid: String? = null,
    val password: String? = null,
    val encryption: String? = null,
    val security: String? = null, // tls, reality, none
    val network: String? = null, // tcp, ws, grpc
    val host: String? = null,
    val path: String? = null,
    val sni: String? = null,
    val fingerprint: String? = null,
    val flow: String? = null,
    val alpn: String? = null,
    val publicKey: String? = null,
    val shortId: String? = null,
    val serviceName: String? = null,
    val method: String? = null,
    val rawConfig: String? = null,
    val isFavorite: Boolean = false,
    val subscriptionId: Long? = null,
    val lastUsed: Long? = null,
    val pingMs: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
