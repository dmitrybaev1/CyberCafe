package ru.shawarma.core.data.utils

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.*
import retrofit2.HttpException
import ru.shawarma.core.data.entities.ApiError
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.TokensRequest
import ru.shawarma.core.data.repositories.AuthRepository

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "authData")

object Errors {
    const val EMPTY_INPUT_ERROR = "EmptyInputError"
    const val PASSWORD_ERROR = "PasswordError"
    const val EMAIL_ERROR = "EmailError"
    const val NETWORK_ERROR = "NetworkError"
    const val REFRESH_TOKEN_ERROR = "RefreshTokenError"
    const val UNAUTHORIZED_ERROR = "401Error"
    const val FORBIDDEN_ERROR = "403Error"
    const val NOT_FOUND_ERROR = "404Error"
}

fun checkExpires(expiresIn: Long): Boolean {
    return expiresIn <= (System.currentTimeMillis() / 1000L) - 60L //Sub one minute to guarantee correct timings and refresh
}

suspend fun checkNotExpiresOrTryRefresh(
    authData: AuthData,
    authRepository: AuthRepository,
    tokenManager: TokenManager,
): Boolean =
    if(!checkExpires(authData.expiresIn)) {
        true
    }
    else{
        val tokensRequest = TokensRequest(authData.refreshToken,authData.accessToken)
        when(val result = authRepository.refreshToken(tokensRequest)){
            is Result.Success<AuthData> -> {
                val authData = result.data
                tokenManager.update(authData)
                true
            }
            else ->
                false
        }
    }

internal fun parseError(httpException: HttpException): ApiError {
    val converter = AppRetrofit.getInstance().responseBodyConverter<ApiError>(
        ApiError::class.java,
        arrayOf<Annotation>())
    val apiError: ApiError
    try{
        apiError = httpException.response()?.errorBody()?.let { converter.convert(it) }!!
    }
    catch (e: Exception){
        Log.d("parseError","${e.message}")
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
            e.printStackTrace()
            when(e){
                is HttpException -> {
                    when(e.code()){
                        401 -> Result.Failure(Errors.UNAUTHORIZED_ERROR)
                        403 -> Result.Failure(Errors.FORBIDDEN_ERROR)
                        404 -> Result.Failure(Errors.NOT_FOUND_ERROR)
                        else -> {
                            val errorBody = parseError(e)
                            Result.Failure(errorBody.message)
                        }
                    }
                }
                else -> Result.NetworkFailure
            }
        }
    }