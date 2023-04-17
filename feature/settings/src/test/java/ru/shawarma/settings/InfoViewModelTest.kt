package ru.shawarma.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.shawarma.core.data.entities.InfoResponse
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import ru.shawarma.settings.viewmodels.InfoUIState
import ru.shawarma.settings.viewmodels.InfoViewModel

class InfoViewModelTest {
    private lateinit var authRepository: AuthRepository

    private lateinit var viewModel: InfoViewModel

    @get:Rule
    val rule = MainDispatcherRule()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        authRepository = mock()
        viewModel = InfoViewModel(authRepository)
    }

    @Test
    fun `Get info successfully`() = runTest {
        whenever(authRepository.getInfo()).thenReturn(
            Result.Success(InfoResponse("","",""))
        )
        viewModel.getInfo()
        Assert.assertTrue(viewModel.infoState.value is InfoUIState.Success)
    }

    @Test
    fun `Get info api error`() = runTest {
        whenever(authRepository.getInfo()).thenReturn(
            Result.Failure("")
        )
        viewModel.getInfo()
        Assert.assertTrue(viewModel.infoState.value is InfoUIState.Error)
    }

    @Test
    fun `Get info invalid token error`() = runTest {
        whenever(authRepository.getInfo()).thenReturn(
            Result.Failure(Errors.REFRESH_TOKEN_ERROR)
        )
        viewModel.getInfo()
        Assert.assertTrue(viewModel.infoState.value is InfoUIState.TokenInvalidError)
    }

    @Test
    fun `Get info no internet error`() = runTest {
        whenever(authRepository.getInfo()).thenReturn(
            Result.Failure(Errors.NO_INTERNET_ERROR)
        )
        viewModel.getInfo()
        Assert.assertTrue(viewModel.isDisconnectedToInternet.value!!
                && viewModel.infoState.value == null
        )
    }

    @Test
    fun `Reset no internet state correctly`(){
        viewModel.resetNoInternetState()
        Assert.assertTrue(!viewModel.isDisconnectedToInternet.value!!)
    }
}