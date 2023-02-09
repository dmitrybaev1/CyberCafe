package ru.shawarma.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.shawarma.core.data.Errors
import ru.shawarma.core.data.Result
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.repositories.MainAuthRepository

class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel

    private lateinit var authData: AuthData

    @get:Rule
    val rule = MainDispatcherRule()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        viewModel = AuthViewModel()
        viewModel.authRepository = mock()
        authData = AuthData("","","",0)
    }

    @Test
    fun `Successful auth`() = runTest {
        whenever(viewModel.authRepository.login(any())).thenReturn(Result.Success(authData))
        viewModel.auth()
        assertTrue(viewModel.authState.value is AuthUIState.Success && !viewModel.isError.value!!)
    }

    @Test
    fun `Empty error when trying auth`() = runTest {
        viewModel.setEmptyInputError()
        val state = viewModel.authState.value as AuthUIState.Error
        assertEquals(Errors.emptyInputError,state.message)
        assertTrue(viewModel.isError.value!!)
    }

    @Test
    fun `Network error when trying auth`() = runTest {
        whenever(viewModel.authRepository.login(any())).thenReturn(Result.NetworkFailure)
        viewModel.auth()
        val state = viewModel.authState.value as AuthUIState.Error
        assertEquals(Errors.networkError, state.message)
        assertTrue(viewModel.isError.value!!)
    }

    @Test
    fun `Api error when trying auth`() = runTest {
        whenever(viewModel.authRepository.login(any())).thenReturn(Result.Failure(""))
        viewModel.auth()
        val state = viewModel.authState.value as AuthUIState.Error
        assertEquals("", state.message)
        assertTrue(viewModel.isError.value!!)
    }

    @Test
    fun `Navigation command to register was set`() = runTest {
        viewModel.goToRegister()
        assertEquals(NavigationCommand.ToRegister,viewModel.navCommand.value)
    }
}