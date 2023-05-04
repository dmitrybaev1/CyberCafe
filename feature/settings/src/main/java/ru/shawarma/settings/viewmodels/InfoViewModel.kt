package ru.shawarma.settings.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shawarma.core.data.entities.InfoResponse
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.settings.entities.InfoItem
import javax.inject.Inject
import ru.shawarma.core.data.utils.Result
import ru.shawarma.settings.utils.mapInfoResponseToInfoItems

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _infoState = MutableStateFlow<InfoUIState?>(null)

    val infoState = _infoState.asStateFlow()

    private val _userName = MutableLiveData<String>()

    val userName: LiveData<String> = _userName

    private val _isDisconnectedToInternet = MutableLiveData<Boolean>()

    val isDisconnectedToInternet: LiveData<Boolean> = _isDisconnectedToInternet

    init {
        getInfo()
    }

    fun getInfo(){
        viewModelScope.launch {
            _infoState.value = null
           when(val result = authRepository.getInfo()){
               is Result.Success<InfoResponse> -> {
                   val list = mapInfoResponseToInfoItems(result.data)
                   _userName.value = list[0].value
                   _infoState.value = InfoUIState.Success(list.subList(1,list.size))
               }
               is Result.Failure -> {
                   if(result.message == Errors.UNAUTHORIZED_ERROR || result.message == Errors.REFRESH_TOKEN_ERROR)
                       _infoState.value = InfoUIState.TokenInvalidError
                   else {
                       if(result.message == Errors.NO_INTERNET_ERROR) {
                           _isDisconnectedToInternet.value = true
                           resetNoInternetState()
                       }
                       _infoState.value = InfoUIState.Error(result.message)
                   }
               }
               is Result.NetworkFailure -> _infoState.value = InfoUIState.Error(Errors.NETWORK_ERROR)
           }
        }
    }

    fun resetNoInternetState(){
        _isDisconnectedToInternet.value = false
    }
}
sealed interface InfoUIState{
    data class Success(val items: List<InfoItem>): InfoUIState
    class Error(val message: String): InfoUIState
    object TokenInvalidError: InfoUIState
}