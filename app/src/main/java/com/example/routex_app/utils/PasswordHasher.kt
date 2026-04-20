package com.example.routex_app.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {

    // Genera un hash a partir de una contraseña en texto plano
    fun hashPassword(plainText: String): String {
        // gensalt() determina la complejidad (por defecto es 10)
        return BCrypt.hashpw(plainText, BCrypt.gensalt())
    }

    // Verifica si la contraseña escrita coincide con el hash guardado
    fun checkPassword(plainText: String, hashed: String): Boolean {
        return try {
            BCrypt.checkpw(plainText, hashed)
        } catch (e: Exception) {
            false
        }
    }
}