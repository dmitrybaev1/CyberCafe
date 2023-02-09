package ru.shawarma.core.data

sealed interface Result<out R>{

    class Success<out T>(val data: T): Result<T>{
        override fun equals(other: Any?): Boolean {
            return (other as Success<*>).data == this.data
        }
    }

    class Failure(val message: String): Result<Nothing>{
        override fun equals(other: Any?): Boolean {
            return (other as Failure).message == this.message
        }
    }

    object NetworkFailure : Result<Nothing>
}