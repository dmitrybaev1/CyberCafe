package ru.shawarma.core.data.entities

data class TokensRequest(
    val refreshToken: String,
    val accessToken: String
)