package com.example.routex_app.network

import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


object NetworkClient {
    // Usamos el host que configuraste
    private const val HOST = "192.168.68.109"
    private const val PORT = 1234

    private const val ALGORITHM = "AES"
    private const val KEY_STRING = "1234567812345678"
    private val secretKey = SecretKeySpec(KEY_STRING.toByteArray(), ALGORITHM)

    // --- CIFRADO ---
    private fun xifrar(dades: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(dades)
    }

    private fun desxifrar(dades: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        return cipher.doFinal(dades)
    }

    // --- SUBIR (UPLOAD) ---
    fun enviarDni(userId: String, bytes: ByteArray, fileName: String): String {
        val socket = Socket()
        return try {
            socket.connect(InetSocketAddress(HOST, PORT), 10000)
            val output = DataOutputStream(socket.getOutputStream())

            // 1. Ciframos los datos
            val dadesXifrades = xifrar(bytes)

            // 2. Enviamos EXACTAMENTE lo que tu servidor lee con readUTF()
            output.writeUTF("PUJAR")
            output.writeUTF(userId)
            output.writeUTF(fileName)
            output.flush()
            // 4. Enviamos los bytes y forzamos la salida
            output.write(dadesXifrades)
            output.flush()

            "✅ Pujada finalitzada"
        } catch (e: Exception) {
            Log.e("NetworkClient", "Error: ${e.message}")
            "❌ Error al servidor"
        } finally {
            // Al cerrar el socket, el servidor recibe el "-1" en su bucle y termina de guardar
            try { socket.close() } catch (e: Exception) { }
        }
    }
    // --- BAJAR (DOWNLOAD) ---
    fun baixarDni(userId: String, fileName: String): ByteArray? {
        val socket = Socket()
        return try {
            socket.connect(InetSocketAddress(HOST, PORT), 10000)
            val output = DataOutputStream(socket.getOutputStream())
            val input = DataInputStream(socket.getInputStream())

            output.writeUTF("BAIXAR")
            output.writeUTF(userId)
            output.writeUTF(fileName)
            output.flush()

            val existeix = input.readBoolean()
            if (!existeix) return null

            // El servidor envía byte a byte sin prefijo de tamaño, leemos hasta EOF
            socket.getInputStream().readBytes()

        } catch (e: Exception) {
            Log.e("NetworkClient", "Error Download: ${e.message}")
            null
        } finally {
            try { socket.close() } catch (e: Exception) { }
        }
    }

    fun descargarFichero(userId: String, fileName: String): ByteArray? {
        val socket = Socket()
        return try {
            socket.connect(InetSocketAddress(HOST, PORT), 5000)
            val output = DataOutputStream(socket.getOutputStream())
            val input = DataInputStream(socket.getInputStream())

            // 1. Enviamos el comando de lectura
            output.writeUTF("BAIXAR")
            output.writeUTF(userId)
            output.writeUTF(fileName)

            // 2. Esperamos respuesta del servidor
            val existe = input.readBoolean()
            if (existe) {
                val mida = input.readLong()
                val dadesXifrades = ByteArray(mida.toInt())
                input.readFully(dadesXifrades)

                // Usamos el mismo método desxifrar que ya tienes para la subida
                desxifrar(dadesXifrades)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("NetworkClient", "Error descarregant: ${e.message}")
            null
        } finally {
            socket.close()
        }
    }
}