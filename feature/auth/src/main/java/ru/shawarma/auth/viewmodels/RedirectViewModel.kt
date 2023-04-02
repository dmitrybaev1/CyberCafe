package ru.shawarma.auth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.exceptions.NoTokenException
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import javax.inject.Inject

@HiltViewModel
class RedirectViewModel(
    private val authRepository: AuthRepository,
    isManualAuthorization: Boolean
) : ViewModel() {

    @Inject constructor(
        authRepository: AuthRepository
    ) : this(authRepository,false)

    private val _redirectState = MutableStateFlow<RedirectState?>(null)
    val redirectState = _redirectState.asStateFlow()

    init{
        if(!isManualAuthorization)
            tryToAuthIfValidData()
    }

    fun tryToAuthIfValidData(){
        viewModelScope.launch {
            try{
                when(authRepository.getActualAuthData()){
                    is Result.Success -> _redirectState.value = RedirectState.TokenValid
                    else -> _redirectState.value = RedirectState.RefreshError
                }
            }
            catch(e: NoTokenException) {
                _redirectState.value = RedirectState.NoToken
            }
        }
    }
}

sealed interface RedirectState{
    object TokenValid: RedirectState
    object RefreshError: RedirectState
    object NoToken: RedirectState
}