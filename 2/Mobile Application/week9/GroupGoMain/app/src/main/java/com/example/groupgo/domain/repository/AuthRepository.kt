package com.example.groupgo.domain.repository

import com.example.groupgo.domain.model.AuthResult

interface AuthRepository {
    /** Register a new account with the given email, password, tier and optional googleCode */
    fun register(email: String, password: String, tier: String = "free", googleCode: String? = null): AuthResult

    /** Attempt to log in with existing credentials */
    fun login(email: String, password: String): AuthResult

    /** Returns true if a session user is currently stored */
    fun isLoggedIn(): Boolean

    /** Returns the email of the currently logged-in user, or null */
    fun getSessionUser(): String?

    /** Clear the current session (logout) */
    fun logout()

    /** Retrieve the registered account tier (e.g. "free" or "premium") for the user */
    fun getUserTier(email: String): String

    /** Reset the user's password securely */
    fun resetPassword(email: String, newPassword: String): Boolean

    /** Returns true if the user email is already registered */
    fun userExists(email: String): Boolean
}
