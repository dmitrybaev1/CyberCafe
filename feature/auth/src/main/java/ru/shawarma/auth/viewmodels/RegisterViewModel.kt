package ru.shawarma.auth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shawarma.auth.checkEmail
import ru.shawarma.auth.checkPassword
import ru.shawarma.core.data.entities.RegisteredUser
import ru.shawarma.core.data.entities.UserRegisterRequest
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterUIState?>(null)
    val registerState = _registerState.asStateFlow()

    val email = MutableLiveData("")
    val name = MutableLiveData("")
    val password = MutableLiveData("")

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError


    fun register(){
        if(!checkPassword(password.value!!)){
            _isError.value = true
            _registerState.value = RegisterUIState.Error(Errors.PASSWORD_ERROR)
            return
        }
        if(!checkEmail(email.value!!)){
            _isError.value = true
            _registerState.value = RegisterUIState.Error(Errors.EMAIL_ERROR)
            return
        }
        _isLoading.value = true
        val userRegisterRequest = UserRegisterRequest(name.value!!,email.value!!,password.value!!)
        viewModelScope.launch {
            val result = authRepository.register(userRegisterRequest)
            _isLoading.value = false
            when(result){
                is Result.Success -> {
                    _registerState.value = RegisterUIState.Success
                    _isError.value = false
                }
                is Result.Failure -> {
                    _registerState.value = RegisterUIState.Error(result.message)
                    val isNotInternetError = result.message != Errors.NO_INTERNET_ERROR
                    _isError.value = isNotInternetError
                    if(!isNotInternetError)
                        resetState()
                }
                is Result.NetworkFailure -> {
                    _registerState.value = RegisterUIState.Error(Errors.NETWORK_ERROR)
                    _isError.value = true
                }
            }
        }
    }

    private fun resetState(){
        _registerState.value = null
    }

    fun setEmptyInputError(){
        _isError.value = true
        _registerState.value = RegisterUIState.Error(Errors.EMPTY_INPUT_ERROR)
    }
}

sealed interface RegisterUIState{
    object Success: RegisterUIState
    class Error(val message: String): RegisterUIState
}