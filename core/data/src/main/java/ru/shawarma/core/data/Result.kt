package ru.shawarma.core.data

sealed interface Result<out R>{

    class Success<out T>(val data: T): Result<T>

    class Failure(val message: String): Result<Nothing>

    object NetworkFailure : Result<Nothing>
}