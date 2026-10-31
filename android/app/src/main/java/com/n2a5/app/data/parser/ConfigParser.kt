package com.n2a5.app.data.parser

import android.net.Uri
import android.util.Base64
import com.n2a5.app.data.model.Server
import org.json.JSONObject

object ConfigParser {

    fun parse(rawUri: String): Server? {
        val trimmed = rawUri.trim()
        return when {
            trimmed.startsWith("vless://") -> parseVless(trimmed)
            trimmed.startsWith("vmess://") -> parseVmess(trimmed)
            trimmed.startsWith("trojan://") -> parseTrojan(trimmed)
            trimmed.startsWith("ss://") -> parseShadowsocks(trimmed)
            else -> null
        }
    }

    fun parseMultiple(text: String): List<Server> {
        val content = tryBase64Decode(text) ?: text
        return content.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapNotNull { parse(it) }
    }

    private fun tryBase64Decode(text: String): String? {
        return try {
            val decoded = String(Base64.decode(text.trim(), Base64.DEFAULT), Charsets.UTF_8)
            if (decoded.contains("://")) decoded else null
        } catch (_: Exception) { null }
    }

    private fun parseVless(raw: String): Server? = runCatching {
        val uri = Uri.parse(raw)
        val params = uri.queryParameterNames.associateWith { uri.getQueryParameter(it).orEmpty() }
        Server(
            name = Uri.decode(uri.fragment ?: "VLESS Server"),
            protocol = "vless",
            address = uri.host.orEmpty(),
            port = uri.port.takeIf { it > 0 } ?: 443,
            uuid = uri.userInfo,
            security = params["security"] ?: "none",
            network = params["type"] ?: "tcp",
            sni = params["sni"],
            fingerprint = params["fp"],
            flow = params["flow"],
            publicKey = params["pbk"],
            shortId = params["sid"],
            host = params["host"],
            path = params["path"],
            serviceName = params["serviceName"],
            alpn = params["alpn"],
            rawConfig = raw
        )
    }.getOrNull()

    private fun parseVmess(raw: String): Server? = runCatching {
        val b64 = raw.removePrefix("vmess://")
        val json = JSONObject(String(Base64.decode(b64, Base64.DEFAULT), Charsets.UTF_8))
        Server(
            name = json.optString("ps", "VMess Server"),
            protocol = "vmess",
            address = json.optString("add"),
            port = json.optString("port", "443").toIntOrNull() ?: 443,
            uuid = json.optString("id"),
            security = json.optString("tls", "none"),
            network = json.optString("net", "tcp"),
            sni = json.optString("sni"),
            host = json.optString("host"),
            path = json.optString("path"),
            encryption = json.optString("scy", "auto"),
            rawConfig = raw
        )
    }.getOrNull()

    private fun parseTrojan(raw: String): Server? = runCatching {
        val uri = Uri.parse(raw)
        val params = uri.queryParameterNames.associateWith { uri.getQueryParameter(it).orEmpty() }
        Server(
            name = Uri.decode(uri.fragment ?: "Trojan Server"),
            protocol = "trojan",
            address = uri.host.orEmpty(),
            port = uri.port.takeIf { it > 0 } ?: 443,
            password = uri.userInfo,
            security = params["security"] ?: "tls",
            network = params["type"] ?: "tcp",
            sni = params["sni"],
            host = params["host"],
            path = params["path"],
            rawConfig = raw
        )
    }.getOrNull()

    private fun parseShadowsocks(raw: String): Server? = runCatching {
        val withoutScheme = raw.removePrefix("ss://")
        val hashIdx = withoutScheme.indexOf('#')
        val name = if (hashIdx >= 0) Uri.decode(withoutScheme.substring(hashIdx + 1)) else "Shadowsocks"
        val main = if (hashIdx >= 0) withoutScheme.substring(0, hashIdx) else withoutScheme
        val atIdx = main.lastIndexOf('@')
        val serverPart = main.substring(atIdx + 1)
        val colonIdx = serverPart.lastIndexOf(':')
        val address = serverPart.substring(0, colonIdx)
        val port = serverPart.substring(colonIdx + 1).toIntOrNull() ?: 8388
        val decoded = String(Base64.decode(main.substring(0, atIdx), Base64.URL_SAFE or Base64.NO_PADDING), Charsets.UTF_8)
        val parts = decoded.split(":", limit = 2)
        Server(
            name = name,
            protocol = "ss",
            address = address,
            port = port,
            method = parts.getOrNull(0),
            password = parts.getOrNull(1),
            rawConfig = raw
        )
    }.getOrNull()
}
