package ru.shawarma.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.shawarma.core.data.Errors
import ru.shawarma.core.data.Result
import ru.shawarma.core.data.entities.RegisteredUser

class RegisterViewModelTest {
    private lateinit var viewModel: RegisterViewModel

    private lateinit var registeredUser: RegisteredUser

    @get:Rule
    val rule = MainDispatcherRule()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        viewModel = RegisterViewModel()
        viewModel.authRepository = mock()
        registeredUser = RegisteredUser("","","")
    }

    @Test
    fun `Successful register`() = runTest {
        whenever(viewModel.authRepository.register(any())).thenReturn(Result.Success(registeredUser))
        viewModel.register()
        Assert.assertTrue(viewModel.registerState.value is RegisterUIState.Success && !viewModel.isError.value!!)
    }

    @Test
    fun `Empty error when trying register`() = runTest {
        viewModel.setEmptyInputError()
        val state = viewModel.registerState.value as RegisterUIState.Error
        Assert.assertEquals(Errors.emptyInputError,state.message)
        Assert.assertTrue(viewModel.isError.value!!)
    }

    @Test
    fun `Network error when trying register`() = runTest {
        whenever(viewModel.authRepository.register(any())).thenReturn(Result.NetworkFailure)
        viewModel.register()
        val state = viewModel.registerState.value as RegisterUIState.Error
        Assert.assertEquals(Errors.networkError, state.message)
        Assert.assertTrue(viewModel.isError.value!!)
    }

    @Test
    fun `Api error when trying auth`() = runTest {
        whenever(viewModel.authRepository.register(any())).thenReturn(Result.Failure(""))
        viewModel.register()
        val state = viewModel.registerState.value as RegisterUIState.Error
        Assert.assertEquals("", state.message)
        Assert.assertTrue(viewModel.isError.value!!)
    }
}