package ru.shawarma.settings.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.utils.TokenManager
import ru.shawarma.settings.entities.InfoItem
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _infoState = MutableStateFlow<InfoUIState?>(null)

    val infoState = _infoState.asStateFlow()

    init {
        getInfo()
    }
    fun getInfo(){
        viewModelScope.launch {
            _infoState.value = null
            delay(2000)
           /* _infoState.value = InfoUIState.Success(
                listOf(
                    InfoItem("role","client"),
                    InfoItem("email","example@exmaple.com"),
                )
            )*/
            _infoState.value = InfoUIState.Error("Hardcoded error")
        }
    }
}
sealed interface InfoUIState{
    data class Success(val items: List<InfoItem>): InfoUIState
    class Error(val message: String): InfoUIState
    object TokenInvalidError: InfoUIState
}