package ru.shawarma.core.data

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.shawarma.core.data.datasources.MainAuthRemoteDataSource
import ru.shawarma.core.data.entities.TokensRequest
import ru.shawarma.core.data.entities.UserLoginRequest
import ru.shawarma.core.data.entities.UserRegisterRequest
import ru.shawarma.core.data.testdoubles.FakeAuthService
import ru.shawarma.core.data.utils.Result

class MainAuthRemoteDataSourceTest {

    private lateinit var authRemoteDataSource: MainAuthRemoteDataSource

    private lateinit var authService: FakeAuthService

    private lateinit var userLoginRequest: UserLoginRequest

    private lateinit var tokensRequest: TokensRequest

    private lateinit var registerRequest: UserRegisterRequest


    @Before
    fun setup(){
        authService = FakeAuthService()
        userLoginRequest = UserLoginRequest("","")
        tokensRequest = TokensRequest("","")
        registerRequest = UserRegisterRequest("","","")
    }

    @Test
    fun `login response correctly wrapped into result`() = runTest {
        authRemoteDataSource = MainAuthRemoteDataSource(authService,
            StandardTestDispatcher(testScheduler))
        val expected = Result.Success(authService.login(userLoginRequest))
        val actual = authRemoteDataSource.login(userLoginRequest)
        assertTrue(expected.javaClass == actual.javaClass)
    }

    @Test
    fun `refresh token response correctly wrapped into result`() = runTest {
        authRemoteDataSource = MainAuthRemoteDataSource(authService,
            StandardTestDispatcher(testScheduler))
        val expected = Result.Success(authService.refreshToken(tokensRequest))
        val actual = authRemoteDataSource.refreshToken(tokensRequest)
        assertTrue(expected.javaClass == actual.javaClass)
    }

    @Test
    fun `register response correctly wrapped into result`() = runTest {
        authRemoteDataSource = MainAuthRemoteDataSource(authService,
            StandardTestDispatcher(testScheduler))
        val expected = Result.Success(authService.register(registerRequest))
        val actual = authRemoteDataSource.register(registerRequest)
        assertTrue(expected.javaClass == actual.javaClass)
    }

    @Test
    fun `get info response correctly wrapped into result`() = runTest {
        authRemoteDataSource = MainAuthRemoteDataSource(authService,
            StandardTestDispatcher(testScheduler))
        val expected = Result.Success(authService.getInfo(""))
        val actual = authRemoteDataSource.getInfo("")
        assertTrue(expected.javaClass == actual.javaClass)
    }
}