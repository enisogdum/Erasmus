package com.example.groupgo.domain.repository

interface EmailSender {
    /**
     * Sends an HTML email using the configured provider.
     * @param to The recipient's email address
     * @param subject The email subject line
     * @param bodyHtml The HTML content of the email
     * @return true if successfully sent, false otherwise
     */
    suspend fun sendEmail(to: String, subject: String, bodyHtml: String): Boolean
}
