package com.example.groupgo.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupgo.domain.model.AuthResult
import com.example.groupgo.domain.repository.AuthRepository
import com.example.groupgo.domain.repository.EmailSender
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val emailSender: EmailSender
) : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // ── Standard Auth States ────────────────────────────────────────────────
    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    // ── Registration Tiers & Verification States ────────────────────────────
    var tier by mutableStateOf("free")
        private set

    var enteredVerificationCode by mutableStateOf("")
        private set

    var generatedVerificationCode by mutableStateOf("")
        private set

    var isRegisterCodeSent by mutableStateOf(false)
        private set

    var isRegisterSending by mutableStateOf(false)
        private set

    // ── Forgot Password (Reset flow) States ─────────────────────────────────
    var isForgotPasswordMode by mutableStateOf(false)
        private set

    var forgotEmail by mutableStateOf("")
        private set

    var forgotGeneratedCode by mutableStateOf("")
        private set

    var forgotEnteredCode by mutableStateOf("")
        private set

    var forgotNewPassword by mutableStateOf("")
        private set

    var isForgotCodeSent by mutableStateOf(false)
        private set

    var isForgotSending by mutableStateOf(false)
        private set

    var isForgotVerifying by mutableStateOf(false)
        private set

    // ── Handlers & Input Setters ───────────────────────────────────────────
    fun onEmailChange(value: String) {
        email = value
        errorMessage = null
        successMessage = null
    }

    fun onPasswordChange(value: String) {
        password = value
        errorMessage = null
        successMessage = null
    }

    fun onTierChange(value: String) {
        tier = value
        errorMessage = null
        successMessage = null
        enteredVerificationCode = ""
        generatedVerificationCode = ""
        isRegisterCodeSent = false
        isRegisterSending = false
    }

    fun onEnteredVerificationCodeChange(value: String) {
        enteredVerificationCode = value
        errorMessage = null
    }

    fun onForgotPasswordModeChange(value: Boolean) {
        isForgotPasswordMode = value
        errorMessage = null
        successMessage = null
        forgotEmail = ""
        forgotGeneratedCode = ""
        forgotEnteredCode = ""
        forgotNewPassword = ""
        isForgotCodeSent = false
        isForgotSending = false
        isForgotVerifying = false
    }

    fun onForgotEmailChange(value: String) {
        forgotEmail = value
        errorMessage = null
    }

    fun onForgotEnteredCodeChange(value: String) {
        forgotEnteredCode = value
        errorMessage = null
    }

    fun onForgotNewPasswordChange(value: String) {
        forgotNewPassword = value
        errorMessage = null
    }

    // ── Verification Email Actions ──────────────────────────────────────────
    fun sendRegisterVerificationCode() {
        if (email.isBlank()) {
            errorMessage = "Please enter an email address first."
            return
        }
        if (password.length < 6) {
            errorMessage = "Please enter a password with at least 6 characters first."
            return
        }
        viewModelScope.launch {
            isRegisterSending = true
            errorMessage = null
            delay(800) // Aesthetic delay for user feedback

            val trimmedEmail = email.trim()
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null && currentUser.email != trimmedEmail) {
                firebaseAuth.signOut()
            }

            firebaseAuth.createUserWithEmailAndPassword(trimmedEmail, password)
                .addOnSuccessListener { result ->
                    result.user?.sendEmailVerification()
                        ?.addOnCompleteListener { verifyTask ->
                            isRegisterCodeSent = verifyTask.isSuccessful
                            generatedVerificationCode = if (verifyTask.isSuccessful) "firebase_email_verification" else ""
                            if (!verifyTask.isSuccessful) {
                                errorMessage = verifyTask.exception?.localizedMessage
                                    ?: "Unable to send verification email. Please try again."
                            }
                            isRegisterSending = false
                        }
                }
                .addOnFailureListener { ex ->
                    if (ex is FirebaseAuthUserCollisionException) {
                        firebaseAuth.signInWithEmailAndPassword(trimmedEmail, password)
                            .addOnSuccessListener { signInResult ->
                                signInResult.user?.sendEmailVerification()
                                    ?.addOnCompleteListener { verifyTask ->
                                        isRegisterCodeSent = verifyTask.isSuccessful
                                        generatedVerificationCode = if (verifyTask.isSuccessful) "firebase_email_verification" else ""
                                        if (!verifyTask.isSuccessful) {
                                            errorMessage = verifyTask.exception?.localizedMessage
                                                ?: "Unable to send verification email. Please try again."
                                        }
                                        isRegisterSending = false
                                    }
                            }
                            .addOnFailureListener {
                                errorMessage = "This email is already in Firebase with a different password."
                                isRegisterSending = false
                            }
                    } else {
                        errorMessage = ex.localizedMessage
                            ?: "Unable to send verification email. Please try again."
                        isRegisterSending = false
                    }
                }
        }
    }

    fun sendResetVerificationCode() {
        if (forgotEmail.isBlank()) {
            errorMessage = "Please enter your email address first."
            return
        }
        viewModelScope.launch {
            errorMessage = null
            isForgotSending = true
            delay(800)

            // Check if user is registered
            if (!authRepository.userExists(forgotEmail.trim())) {
                errorMessage = "No registered account found with this email."
                isForgotSending = false
                return@launch
            }

            val code = Random.nextInt(100000, 1_000_000).toString()
            forgotGeneratedCode = code

            val subject = "GroupGo Password Reset Verification"
            val html = """
                <div style="font-family: sans-serif; padding: 20px; color: #333;">
                    <h2 style="color: #14B8A6;">Reset Your GroupGo Password</h2>
                    <p>We received a request to reset your password. Please enter the following 6-digit verification code in the app to proceed:</p>
                    <div style="background-color: #f3f4f6; padding: 16px; border-radius: 8px; font-size: 24px; font-weight: bold; letter-spacing: 2px; text-align: center; margin: 20px 0; color: #8B5CF6;">
                        $code
                    </div>
                    <p style="font-size: 12px; color: #666;">If you did not request a password reset, please ignore this email.</p>
                </div>
            """.trimIndent()

            val success = emailSender.sendEmail(forgotEmail.trim(), subject, html)
            isForgotCodeSent = success
            if (!success) {
                forgotGeneratedCode = ""
                errorMessage = "Unable to send reset code. Please check your email settings and try again."
            }
            isForgotSending = false
        }
    }

    fun handleResetPassword() {
        if (forgotEnteredCode != forgotGeneratedCode) {
            errorMessage = "Incorrect verification code. Please check your email."
            return
        }
        if (forgotNewPassword.length < 6) {
            errorMessage = "Password must be at least 6 characters."
            return
        }

        viewModelScope.launch {
            isForgotVerifying = true
            delay(800)

            val success = authRepository.resetPassword(forgotEmail.trim(), forgotNewPassword)
            if (success) {
                successMessage = "Password reset successfully! Please sign in."
                // Return to Sign In mode
                isForgotPasswordMode = false
            } else {
                errorMessage = "Reset failed. Please try again."
            }
            isForgotVerifying = false
        }
    }

    // ── Authentication Actions ──────────────────────────────────────────────
    fun handleLogin(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null
            delay(600)

            when (authRepository.login(email.trim(), password)) {
                AuthResult.Success -> onSuccess()
                AuthResult.InvalidEmailFormat ->
                    errorMessage = "Please enter a valid email address."
                AuthResult.PasswordTooShort ->
                    errorMessage = "Password must be at least 6 characters."
                AuthResult.IncorrectCredentials ->
                    errorMessage = "Incorrect email or password. Try again."
                else ->
                    errorMessage = "An unexpected error occurred. Please try again."
            }

            isLoading = false
        }
    }

    fun handleRegister() {
        viewModelScope.launch {
            errorMessage = null
            successMessage = null

            // Free tier registration must be verified via code sent to entered email.
            if (tier == "free") {
                if (!isRegisterCodeSent) {
                    errorMessage = "Please send the Firebase verification email first."
                    return@launch
                }
                val currentUser = firebaseAuth.currentUser
                if (currentUser == null || currentUser.email != email.trim()) {
                    errorMessage = "Please request verification again for this email."
                    return@launch
                }
                try {
                    currentUser.reload().await()
                } catch (_: Exception) {
                    errorMessage = "Could not verify email status right now. Please try again."
                    return@launch
                }
                if (!currentUser.isEmailVerified) {
                    errorMessage = "Email not verified yet. Open your email and click the Firebase verification link."
                    return@launch
                }
            }

            isLoading = true
            delay(600)

            val codeParam = if (tier == "free") generatedVerificationCode else null
            when (authRepository.register(email.trim(), password, tier, codeParam)) {
                AuthResult.Success ->
                    successMessage = "Account created! You can now sign in."
                AuthResult.InvalidEmailFormat ->
                    errorMessage = "Please enter a valid email address."
                AuthResult.PasswordTooShort ->
                    errorMessage = "Password must be at least 6 characters."
                AuthResult.EmailAlreadyExists ->
                    errorMessage = "This email is already registered. Please sign in."
                else ->
                    errorMessage = "Registration failed. Please try again."
            }

            if (successMessage != null) {
                enteredVerificationCode = ""
                generatedVerificationCode = ""
                isRegisterCodeSent = false
            }
            isLoading = false
        }
    }

    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result)
            } else {
                continuation.resumeWithException(task.exception ?: IllegalStateException("Task failed"))
            }
        }
    }
}
