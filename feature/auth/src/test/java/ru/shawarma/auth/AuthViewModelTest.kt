package ru.shawarma.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.shawarma.auth.viewmodels.AuthUIState
import ru.shawarma.auth.viewmodels.AuthViewModel
import ru.shawarma.auth.viewmodels.RegisterUIState
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result

class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel

    private lateinit var authRepository: AuthRepository

    private lateinit var authData: AuthData

    @get:Rule
    val rule = MainDispatcherRule()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        authRepository = mock()
        viewModel = AuthViewModel(authRepository,mock())
        authData = AuthData("","","",0)
    }

    @Test
    fun `Successful auth`() = runTest {
        whenever(authRepository.login(any())).thenReturn(Result.Success(authData))
        viewModel.password.value = "12345678"
        viewModel.email.value = "example@example.com"
        viewModel.auth()
        assertTrue(viewModel.authState.value is AuthUIState.Success && !viewModel.isError.value!!)
    }

    @Test
    fun `Password error when trying auth`() = runTest {
        viewModel.password.value = "1234567"
        viewModel.email.value = "example@example.com"
        viewModel.auth()
        val state = viewModel.authState.value as AuthUIState.Error
        assertEquals(Errors.PASSWORD_ERROR,state.message)
        assertTrue(viewModel.isError.value!!)
    }

    @Test
    fun `Email error when trying auth`() = runTest {
        viewModel.password.value = "12345678"
        viewModel.email.value = "example@example"
        viewModel.auth()
        val state = viewModel.authState.value as AuthUIState.Error
        assertEquals(Errors.EMAIL_ERROR,state.message)
        assertTrue(viewModel.isError.value!!)
    }

    @Test
    fun `Empty error when trying auth`() = runTest {
        viewModel.setEmptyInputError()
        val state = viewModel.authState.value as AuthUIState.Error
        assertEquals(Errors.EMPTY_INPUT_ERROR,state.message)
        assertTrue(viewModel.isError.value!!)
    }

    @Test
    fun `Network error when trying auth`() = runTest {
        whenever(authRepository.login(any())).thenReturn(Result.NetworkFailure)
        viewModel.password.value = "12345678"
        viewModel.email.value = "example@example.com"
        viewModel.auth()
        val state = viewModel.authState.value as AuthUIState.Error
        assertEquals(Errors.NETWORK_ERROR, state.message)
        assertTrue(viewModel.isError.value!!)
    }

    @Test
    fun `Api error when trying auth`() = runTest {
        whenever(authRepository.login(any())).thenReturn(Result.Failure(""))
        viewModel.password.value = "12345678"
        viewModel.email.value = "example@example.com"
        viewModel.auth()
        val state = viewModel.authState.value as AuthUIState.Error
        assertEquals("", state.message)
        assertTrue(viewModel.isError.value!!)
    }

    @Test
    fun `Navigation command to register was set`() = runTest {
        viewModel.goToRegister()
        assertEquals(NavigationCommand.ToRegisterFragment,viewModel.navCommand.value)
    }


}