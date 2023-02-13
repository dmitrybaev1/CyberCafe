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
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result

class RegisterViewModelTest {
    private lateinit var viewModel: RegisterViewModel

    private lateinit var registeredUser: RegisteredUser

    @get:Rule
    val rule = MainDispatcherRule()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        viewModel = RegisterViewModel(mock())
        registeredUser = RegisteredUser("","","")
    }

    @Test
    fun `Successful register`() = runTest {
        whenever(viewModel.authRepository.register(any())).thenReturn(Result.Success(registeredUser))
        viewModel.register()
        assertTrue(viewModel.registerState.value is RegisterUIState.Success && !viewModel.isError.value!!)
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
        whenever(viewModel.authRepository.register(any())).thenReturn(Result.NetworkFailure)
        viewModel.register()
        val state = viewModel.registerState.value as RegisterUIState.Error
        assertEquals(Errors.NETWORK_ERROR, state.message)
        assertTrue(viewModel.isError.value!!)
    }

    @Test
    fun `Api error when trying auth`() = runTest {
        whenever(viewModel.authRepository.register(any())).thenReturn(Result.Failure(""))
        viewModel.register()
        val state = viewModel.registerState.value as RegisterUIState.Error
        assertEquals("", state.message)
        assertTrue(viewModel.isError.value!!)
    }
}