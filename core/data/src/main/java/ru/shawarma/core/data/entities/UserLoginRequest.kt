package ru.shawarma.core.data.entities

data class UserLoginRequest(
    val email: String,
    val password: String
)