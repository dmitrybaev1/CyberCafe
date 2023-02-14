package ru.shawarma.core.data.entities

data class UserRegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
