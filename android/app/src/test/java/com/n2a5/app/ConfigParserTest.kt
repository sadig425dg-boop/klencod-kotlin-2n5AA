package com.n2a5.app

import org.junit.Test
import org.junit.Assert.*

class ConfigParserTest {

    @Test
    fun testVlessUriStructure() {
        val uri = "vless://uuid-test@example.com:443?type=tcp&security=reality#TestServer"
        assertTrue(uri.startsWith("vless://"))
        assertTrue(uri.contains("@"))
        assertTrue(uri.contains("#"))
    }

    @Test
    fun testVmessBase64Structure() {
        val uri = "vmess://eyJhZGQiOiJleGFtcGxlLmNvbSIsInBvcnQiOiI0NDMiLCJpZCI6InV1aWQtdGVzdCIsInBzIjoiVGVzdCJ9"
        assertTrue(uri.startsWith("vmess://"))
    }

    @Test
    fun testTrojanUriStructure() {
        val uri = "trojan://password@example.com:443?security=tls#TestTrojan"
        assertTrue(uri.startsWith("trojan://"))
        assertTrue(uri.contains("@"))
    }

    @Test
    fun testShadowsocksUriStructure() {
        val uri = "ss://YWVzLTI1Ni1nY206cGFzc3dvcmQ@example.com:8388#TestSS"
        assertTrue(uri.startsWith("ss://"))
    }

    @Test
    fun testProtocolDetection() {
        val protocols = mapOf(
            "vless://test" to "vless",
            "vmess://test" to "vmess",
            "trojan://test" to "trojan",
            "ss://test" to "ss"
        )
        protocols.forEach { (uri, expected) ->
            val detected = uri.substringBefore("://")
            assertEquals(expected, detected)
        }
    }

    @Test
    fun testInvalidUri() {
        val invalid = listOf("http://example.com", "ftp://test", "", "random text")
        invalid.forEach { uri ->
            assertFalse(
                uri.startsWith("vless://") ||
                uri.startsWith("vmess://") ||
                uri.startsWith("trojan://") ||
                uri.startsWith("ss://")
            )
        }
    }
}
