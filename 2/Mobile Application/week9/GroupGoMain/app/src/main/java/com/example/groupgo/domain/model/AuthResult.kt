package com.example.groupgo.domain.model

sealed class AuthResult {
    /** Login or registration completed successfully */
    object Success : AuthResult()

    /** Registration rejected because the email is already taken */
    object EmailAlreadyExists : AuthResult()

    /** The email string does not match a valid email pattern */
    object InvalidEmailFormat : AuthResult()

    /** The password is fewer than 6 characters */
    object PasswordTooShort : AuthResult()

    /** Login failed — wrong password or the account does not exist */
    object IncorrectCredentials : AuthResult()
}
