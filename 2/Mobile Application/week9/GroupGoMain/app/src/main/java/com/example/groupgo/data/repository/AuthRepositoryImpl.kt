package com.example.groupgo.data.repository

import android.content.Context
import android.util.Patterns
import com.example.groupgo.domain.model.AuthResult
import com.example.groupgo.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stores registered user credentials persistently using two SharedPreferences files:
 *
 *  - "groupgo_users"   — key = email, value = hashed password
 *  - "groupgo_session" — key = "current_user", value = logged-in email (or absent)
 *
 * Passwords are stored as a simple deterministic hash so that plain-text passwords
 * are never written to disk, while still being verifiable without a backend.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AuthRepository {

    // SharedPreferences file that persists all registered accounts.
    private val usersPrefs by lazy {
        context.getSharedPreferences("groupgo_users", Context.MODE_PRIVATE)
    }

    // SharedPreferences file that persists the current session.
    private val sessionPrefs by lazy {
        context.getSharedPreferences("groupgo_session", Context.MODE_PRIVATE)
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    override fun register(email: String, password: String, tier: String, googleCode: String?): AuthResult {
        val trimmedEmail = email.trim().lowercase()

        if (!isValidEmail(trimmedEmail)) return AuthResult.InvalidEmailFormat
        if (password.length < 6) return AuthResult.PasswordTooShort
        if (usersPrefs.contains(trimmedEmail)) return AuthResult.EmailAlreadyExists

        usersPrefs.edit()
            .putString(trimmedEmail, hashPassword(password))
            .putString(trimmedEmail + "_tier", tier)
            .apply()

        return AuthResult.Success
    }

    override fun login(email: String, password: String): AuthResult {
        val trimmedEmail = email.trim().lowercase()

        if (!isValidEmail(trimmedEmail)) return AuthResult.InvalidEmailFormat
        if (password.length < 6) return AuthResult.PasswordTooShort

        val storedHash = usersPrefs.getString(trimmedEmail, null)
            ?: return AuthResult.IncorrectCredentials

        if (storedHash != hashPassword(password)) return AuthResult.IncorrectCredentials

        // Persist the session so the app can remember the user across restarts
        sessionPrefs.edit()
            .putString("current_user", trimmedEmail)
            .apply()

        return AuthResult.Success
    }

    override fun isLoggedIn(): Boolean =
        sessionPrefs.getString("current_user", null) != null

    override fun getSessionUser(): String? =
        sessionPrefs.getString("current_user", null)

    override fun logout() {
        sessionPrefs.edit().remove("current_user").apply()
    }

    override fun getUserTier(email: String): String {
        val trimmedEmail = email.trim().lowercase()
        return usersPrefs.getString(trimmedEmail + "_tier", "free") ?: "free"
    }

    override fun resetPassword(email: String, newPassword: String): Boolean {
        val trimmedEmail = email.trim().lowercase()
        if (!usersPrefs.contains(trimmedEmail)) return false

        usersPrefs.edit()
            .putString(trimmedEmail, hashPassword(newPassword))
            .apply()
        return true
    }

    override fun userExists(email: String): Boolean {
        val trimmedEmail = email.trim().lowercase()
        return usersPrefs.contains(trimmedEmail)
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /** Simple deterministic hash — avoids storing plain-text passwords. */
    private fun hashPassword(password: String): String {
        var hash = 17L
        for (ch in password) {
            hash = hash * 31 + ch.code
        }
        return hash.toString(16)
    }

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
