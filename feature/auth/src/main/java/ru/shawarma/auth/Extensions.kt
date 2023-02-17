package ru.shawarma.auth

fun checkPassword(password: String) = password.length >= 8

fun checkEmail(email: String): Boolean{
    val regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$".toRegex()
    return regex.containsMatchIn(email)
}