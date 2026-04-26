package com.example.routex_app.network

import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object NetworkClient {
    private const val HOST = "192.168.68.101"
    private const val PORT = 1234
    private const val ALGORITHM = "AES"
    private val secretKey = SecretKeySpec(KEY_STRING.toByteArray(), ALGORITHM)

    private const val KEY_STRING = "1234567812345678" // ¡Debe tener 16 caracteres exactos!

    fun xifrar(dades: ByteArray): ByteArray {
        val clauEspecif = SecretKeySpec(KEY_STRING.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, clauEspecif)
        return cipher.doFinal(dades)
    }

    fun desxifrar(dades: ByteArray): ByteArray {
        val clauEspecif = SecretKeySpec(KEY_STRING.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, clauEspecif)
        return cipher.doFinal(dades)
    }
    /*
    // --- PUJAR (UPLOAD) ---
    fun enviarDni(userId: String, bytes: ByteArray, fileName: String): String {
        val socket = Socket()
        return try {
            // 1. Establecer conexión con un timeout de 10 segundos
            socket.connect(InetSocketAddress(HOST, PORT), 10000)
            val output = DataOutputStream(socket.getOutputStream())

            // 2. Cifrar los bytes de la imagen antes de enviar
            val dadesXifrades = xifrar(bytes)

            // 3. Enviar metadatos (Comando, ID usuario, Nombre archivo)
            output.writeUTF("PUJAR")
            output.writeUTF(userId)
            output.writeUTF(fileName)

            // 4. ENVIAR TAMAÑO (Crucial para que el servidor no se quede bloqueado)
            output.writeLong(dadesXifrades.size.toLong())
            output.flush() // Aseguramos que los metadatos lleguen antes que el chorro de bytes

            // 5. Enviar el contenido del archivo cifrado
            output.write(dadesXifrades)
            output.flush()

            "✅ Pujada finalitzada"
        } catch (e: Exception) {
            Log.e("NetworkClient", "Error al subir: ${e.message}")
            "❌ Error al servidor: ${e.message}"
        } finally {
            // 6. Cerrar siempre el socket para liberar recursos
            try {
                socket.close()
            } catch (e: Exception) {
                Log.e("NetworkClient", "Error cerrando socket: ${e.message}")
            }
        }
    }
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

            val mida = input.readLong()
            val dadesRebudes = ByteArray(mida.toInt())

            // Usamos readFully para asegurar que leemos todos los bytes que prometió el servidor
            input.readFully(dadesRebudes)

            try {
                // Intentamos descifrar
                return desxifrar(dadesRebudes)
            } catch (e: Exception) {
                Log.e("NetworkClient", "Error descifrando: ${e.message}. ¿La clave es igual?")
                // Si falla el descifrado, devolvemos los bytes tal cual para diagnóstico
                return dadesRebudes
            }

        } catch (e: Exception) {
            Log.e("NetworkClient", "Error en red: ${e.message}")
            null
        } finally {
            socket.close()
        }
    }
    */

    // --- PUJAR (UPLOAD) SIN CIFRADO ---
    fun enviarDni(userId: String, bytes: ByteArray, fileName: String): String {
        val socket = Socket()
        return try {
            socket.connect(InetSocketAddress(HOST, PORT), 10000)
            val output = DataOutputStream(socket.getOutputStream())

            output.writeUTF("PUJAR")
            output.writeUTF(userId)
            output.writeUTF(fileName)
            output.writeLong(bytes.size.toLong()) // ← el servidor hace readLong()
            output.flush()

            output.write(bytes)
            output.flush()

            "✅ Pujada finalitzada"
        } catch (e: Exception) {
            Log.e("NetworkClient", "Error al subir: ${e.message}")
            "❌ Error al servidor: ${e.message}"
        } finally {
            try {
                socket.close()
            } catch (e: Exception) {
            }
        }
    }

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
            Log.d("BAIXAR", "¿Existe?: $existeix")
            if (!existeix) return null

            val mida = input.readLong()
            val dadesRebudes = ByteArray(mida.toInt())
            input.readFully(dadesRebudes)
            Log.d("BAIXAR", "Total recibido: ${dadesRebudes.size} bytes")
            dadesRebudes

        } catch (e: Exception) {
            Log.e("BAIXAR", "Error: ${e.javaClass.simpleName} - ${e.message}")
            null
        } finally {
            try { socket.close() } catch (e: Exception) { }
        }
    }
}