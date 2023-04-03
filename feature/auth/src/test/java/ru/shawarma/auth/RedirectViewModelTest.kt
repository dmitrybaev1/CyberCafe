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
import ru.shawarma.auth.viewmodels.RedirectState
import ru.shawarma.auth.viewmodels.RedirectViewModel
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.data.managers.TokenManager

class RedirectViewModelTest {

    private lateinit var viewModel: RedirectViewModel

    private lateinit var authRepository: AuthRepository

    private lateinit var tokenManager: TokenManager

    private lateinit var authData: AuthData

    @get:Rule
    val rule = MainDispatcherRule()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        authRepository = mock()
        tokenManager = mock()
        viewModel = RedirectViewModel(authRepository, tokenManager,true)
        authData = AuthData("","","",0)
    }

    @Test
    fun `Get valid token and set success`() = runTest {
        whenever(tokenManager.getAuthData()).thenReturn(
            AuthData("","","",(System.currentTimeMillis() / 1000L)+100)
        )
        viewModel.tryToAuthIfValidData()
        assertTrue(viewModel.redirectState.value is RedirectState.TokenValid)
    }

    @Test
    fun `Get expired token and successful refresh`() = runTest {
        whenever(tokenManager.getAuthData()).thenReturn(
            AuthData("","","",(System.currentTimeMillis() / 1000L)-100)
        )
        whenever(authRepository.refreshToken(any())).thenReturn(Result.Success(authData))
        viewModel.tryToAuthIfValidData()
        assertTrue(viewModel.redirectState.value is RedirectState.TokenValid)
    }

    @Test
    fun `Get expired token and error refresh`() = runTest {
        whenever(tokenManager.getAuthData()).thenReturn(
            AuthData("","","",(System.currentTimeMillis() / 1000L)-100)
        )
        whenever(authRepository.refreshToken(any())).thenReturn(Result.Failure(""))
        viewModel.tryToAuthIfValidData()
        val state = viewModel.redirectState.value as RedirectState.RefreshError
        assertEquals(Errors.REFRESH_TOKEN_ERROR,state.message)
    }

    @Test
    fun `No existing auth data`() = runTest {
        whenever(tokenManager.getAuthData()).thenReturn(authData)
        viewModel.tryToAuthIfValidData()
        assertTrue(viewModel.redirectState.value is RedirectState.NoToken)
    }
}