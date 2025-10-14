package com.orbit.data.auth

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MicrosoftAuthRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
    
    suspend fun authenticateWithMicrosoft(email: String, password: String): Result<User> {
        return try {
            if (!isInstitutionalEmail(email)) {
                return Result.failure(Exception("Debe ser un correo institucional (.edu)"))
            }
            
            // Obtener token de acceso
            val accessToken = getAccessToken(email, password)
            
            if (accessToken != null) {
                // Verificar que el usuario existe y obtener información
                val userInfo = getUserInfo(accessToken)
                
                if (userInfo != null) {
                    val user = User(
                        email = email,
                        name = userInfo.displayName ?: extractName(email)
                    )
                    saveUserSession(user, accessToken)
                    Result.success(user)
                } else {
                    Result.failure(Exception("No se pudo obtener información del usuario"))
                }
            } else {
                Result.failure(Exception("Credenciales incorrectas o servicio no disponible"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun getAccessToken(email: String, password: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.post("https://login.microsoftonline.com/common/oauth2/v2.0/token") {
                    contentType(ContentType.Application.FormUrlEncoded)
                    setBody(Parameters.build {
                        append("client_id", "00000000-0000-0000-0000-000000000000") // Configurar con tu Client ID real
                        append("scope", "https://graph.microsoft.com/User.Read")
                        append("username", email)
                        append("password", password)
                        append("grant_type", "password")
                    })
                }
                
                if (response.status.isSuccess()) {
                    val tokenResponse = response.body<TokenResponse>()
                    tokenResponse.access_token
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    private suspend fun getUserInfo(accessToken: String): UserInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val response = httpClient.get("https://graph.microsoft.com/v1.0/me") {
                    headers {
                        append("Authorization", "Bearer $accessToken")
                    }
                }
                
                if (response.status.isSuccess()) {
                    response.body<UserInfo>()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    private fun isInstitutionalEmail(email: String): Boolean {
        val institutionalDomains = listOf(
            ".edu",
            ".edu.pe",
            "upeu.edu.pe"
        )
        return institutionalDomains.any { domain ->
            email.endsWith(domain, ignoreCase = true)
        }
    }
    
    private fun saveUserSession(user: User, accessToken: String) {
        val sharedPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putString("user_email", user.email)
            .putString("user_name", user.name)
            .putString("access_token", accessToken)
            .putBoolean("is_logged_in", true)
            .apply()
    }
    
    fun getCurrentUser(): User? {
        val sharedPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val email = sharedPrefs.getString("user_email", null)
        val name = sharedPrefs.getString("user_name", null)
        
        return if (email != null && name != null) {
            User(email = email, name = name)
        } else null
    }
    
    fun signOut() {
        val sharedPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()
    }
    
    private fun extractName(email: String): String {
        val name = email.substringBefore("@")
        return name.replace(".", " ").replace("_", " ").split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    }
}

@Serializable
data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)

@Serializable
data class UserInfo(
    val displayName: String?,
    val mail: String?,
    val userPrincipalName: String?
)

data class User(
    val email: String,
    val name: String
)
