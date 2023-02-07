package ru.shawarma.core.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.shawarma.core.data.entities.ApiError


object Errors {
    const val emptyInputError = "EmptyInputError"
    const val networkError = "NetworkError"
}

internal fun parseError(httpException: HttpException): ApiError {
    val converter = AppRetrofit.instance.responseBodyConverter<ApiError>(
        ApiError::class.java,
        arrayOf<Annotation>())
    val apiError: ApiError
    try{
        apiError = httpException.response()?.errorBody()?.let { converter.convert(it) }!!
    }
    catch (e: Exception){
        return ApiError("Unknown error")
    }
    return apiError
}

internal suspend fun <T> safeServiceCall(dispatcher: CoroutineDispatcher, call: suspend () -> T): Result<T> =
    withContext(dispatcher){
        try {
            val result = call.invoke()
            Result.Success(result)
        }
        catch(e: Exception){
            when(e){
                is HttpException -> {
                    val errorBody = parseError(e)
                    Result.Failure(errorBody.message)
                }
                else -> Result.NetworkFailure
            }
        }
    }