package ru.shawarma.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shawarma.core.data.AppRetrofit
import ru.shawarma.core.data.Errors
import ru.shawarma.core.data.Result
import ru.shawarma.core.data.datasources.MainAuthRemoteDataSource
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.RegisteredUser
import ru.shawarma.core.data.entities.UserRegisterRequest
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.repositories.MainAuthRepository

class RegisterViewModel : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterUIState?>(null)
    val registerState = _registerState.asStateFlow()

    val email = MutableLiveData("")
    val name = MutableLiveData("")
    val password = MutableLiveData("")

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    var authRepository: AuthRepository = MainAuthRepository(
        MainAuthRemoteDataSource(
            AppRetrofit.authService,
            Dispatchers.IO)
    )

    fun register(){
        val userRegisterRequest = UserRegisterRequest(name.value!!,email.value!!,password.value!!)
        viewModelScope.launch {
            when(val result = authRepository.register(userRegisterRequest)){
                is Result.Success<RegisteredUser> -> { _registerState.value = RegisterUIState.Success(result.data); _isError.value = false }
                is Result.Failure -> { _registerState.value = RegisterUIState.Error(result.message); _isError.value = true }
                is Result.NetworkFailure -> { _registerState.value = RegisterUIState.Error(Errors.networkError); _isError.value = true }
            }
        }
    }

    fun setEmptyInputError(){
        _isError.value = true
        _registerState.value = RegisterUIState.Error(Errors.emptyInputError)
    }
}

sealed interface RegisterUIState{
    data class Success(val user: RegisteredUser): RegisterUIState
    data class Error(val message: String): RegisterUIState
}