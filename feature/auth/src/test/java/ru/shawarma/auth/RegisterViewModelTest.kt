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
import ru.shawarma.auth.viewmodels.RegisterUIState
import ru.shawarma.auth.viewmodels.RegisterViewModel
import ru.shawarma.core.data.entities.RegisteredUser
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result

class RegisterViewModelTest {
    private lateinit var viewModel: RegisterViewModel

    private lateinit var authRepository: AuthRepository

    private lateinit var registeredUser: RegisteredUser

    @get:Rule
    val rule = MainDispatcherRule()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        authRepository = mock()
        viewModel = RegisterViewModel(authRepository)
        registeredUser = RegisteredUser("","","")
    }

    @Test
    fun `Successful register`() = runTest {
		whenever(authRepository.register(any())).thenReturn(Result.Success(registeredUser))
        viewModel.password.value = "12345678"
        viewModel.email.value = "example@example.com"
        viewModel.register()
        assertTrue(viewModel.registerState.value is RegisterUIState.Success && !viewModel.isError.value!!)
    }

    @Test
    fun `Password error when trying auth`() = runTest {
        viewModel.password.value = "1234567"
        viewModel.email.value = "example@example.com"
        viewModel.register()
        val state = viewModel.registerState.value as RegisterUIState.Error
        assertEquals(Errors.PASSWORD_ERROR,state.message)
        assertTrue(viewModel.isError.value!!)
    }

    @Test
    fun `Email error when trying auth`() = runTest {
        viewModel.password.value = "12345678"
        viewModel.email.value = "example@example"
        viewModel.register()
        val state = viewModel.registerState.value as RegisterUIState.Error
        assertEquals(Errors.EMAIL_ERROR,state.message)
        assertTrue(viewModel.isError.value!!)
    }

    @Test
    fun `Empty error when trying register`() = runTest {
        viewModel.setEmptyInputError()
        val state = viewModel.registerState.value as RegisterUIState.Error
        assertEquals(Errors.EMPTY_INPUT_ERROR,state.message)
        assertTrue(viewModel.isError.value!!)
    }

    @Test
    fun `Network error when trying register`() = runTest {
        whenever(authRepository.register(any())).thenReturn(Result.NetworkFailure)
        viewModel.password.value = "12345678"
        viewModel.email.value = "example@example.com"
        viewModel.register()
        val state = viewModel.registerState.value as RegisterUIState.Error
        assertEquals(Errors.NETWORK_ERROR, state.message)
        assertTrue(viewModel.isError.value!!)
    }

    @Test
    fun `Api error when trying auth`() = runTest {
        whenever(authRepository.register(any())).thenReturn(Result.Failure(""))
        viewModel.password.value = "12345678"
        viewModel.email.value = "example@example.com"
        viewModel.register()
        val state = viewModel.registerState.value as RegisterUIState.Error
        assertEquals("", state.message)
        assertTrue(viewModel.isError.value!!)
    }
}