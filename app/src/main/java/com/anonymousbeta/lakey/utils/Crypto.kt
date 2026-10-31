package com.anonymousbeta.lakey.utils

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Crypto {

    fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        val iv = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key.copyOf(32), "AES"), IvParameterSpec(iv))
        return iv + cipher.doFinal(data)
    }

    fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        val iv = data.copyOfRange(0, 16)
        val encrypted = data.copyOfRange(16, data.size)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key.copyOf(32), "AES"), IvParameterSpec(iv))
        return cipher.doFinal(encrypted)
    }
}
