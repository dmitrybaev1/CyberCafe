package ru.shawarma.auth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.TokensRequest
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.data.utils.TokenManager
import ru.shawarma.core.data.utils.checkExpires

class RedirectViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    isManualAuthorization: Boolean = false // especially for tests
) : ViewModel() {

    private val _redirectState = MutableStateFlow<RedirectState?>(null)
    val redirectState = _redirectState.asStateFlow()

    init{
        if(!isManualAuthorization)
            tryToAuthIfValidData()
    }

    fun tryToAuthIfValidData(){
        viewModelScope.launch {
            val authData = tokenManager.getAuthData()
            if(!authData.isEmpty()){
                if(!checkExpires(authData.expiresIn))
                    _redirectState.value = RedirectState.TokenValid(authData)
                else{
                    refreshToken(TokensRequest(authData.refreshToken, authData.accessToken))
                }
            }
            else
                _redirectState.value = RedirectState.NoToken
        }
    }

    private suspend fun refreshToken(tokensRequest: TokensRequest){
        when(val result = authRepository.refreshToken(tokensRequest)){
            is Result.Success<AuthData> -> {
                val authData = result.data
                tokenManager.update(authData)
                _redirectState.value = RedirectState.TokenValid(authData)
            }
            else -> _redirectState.value = RedirectState.RefreshError(Errors.REFRESH_TOKEN_ERROR)
        }
    }
}

sealed interface RedirectState{
    data class TokenValid(val authData: AuthData): RedirectState
    data class RefreshError(val message: String): RedirectState
    object NoToken: RedirectState
}