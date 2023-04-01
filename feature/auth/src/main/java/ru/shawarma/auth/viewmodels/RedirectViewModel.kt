package ru.shawarma.auth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.TokenManager
import ru.shawarma.core.data.utils.checkNotExpiresOrTryRefresh
import javax.inject.Inject

@HiltViewModel
class RedirectViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager,
    isManualAuthorization: Boolean
) : ViewModel() {

    @Inject constructor(
        authRepository: AuthRepository,
        tokenManager: TokenManager
    ) : this(authRepository,tokenManager,false)

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
                if(checkNotExpiresOrTryRefresh(authData, authRepository, tokenManager))
                    _redirectState.value = RedirectState.TokenValid
                else
                    _redirectState.value = RedirectState.RefreshError(Errors.REFRESH_TOKEN_ERROR)
            }
            else
                _redirectState.value = RedirectState.NoToken
        }
    }
}

sealed interface RedirectState{
    object TokenValid: RedirectState
    data class RefreshError(val message: String): RedirectState
    object NoToken: RedirectState
}