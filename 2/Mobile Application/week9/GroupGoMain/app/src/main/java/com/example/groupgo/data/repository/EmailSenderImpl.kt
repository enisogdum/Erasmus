package com.example.groupgo.data.repository

import com.example.groupgo.BuildConfig
import com.example.groupgo.domain.repository.EmailSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmailSenderImpl @Inject constructor(
    private val okHttpClient: OkHttpClient
) : EmailSender {

    override suspend fun sendEmail(to: String, subject: String, bodyHtml: String): Boolean = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.BREVO_API_KEY
        val senderEmail = BuildConfig.BREVO_SENDER_EMAIL

        if (apiKey.isBlank()) {
            // Graceful fallback to sandbox simulation when API key is unconfigured
            return@withContext false
        }

        // Clean escape formatting for JSON compatibility
        val escapedBody = bodyHtml
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "")

        val json = """
            {
              "sender": { "name": "GroupGo Verification", "email": "$senderEmail" },
              "to": [ { "email": "$to" } ],
              "subject": "$subject",
              "htmlContent": "$escapedBody"
            }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://api.brevo.com/v3/smtp/email")
            .header("api-key", apiKey)
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
