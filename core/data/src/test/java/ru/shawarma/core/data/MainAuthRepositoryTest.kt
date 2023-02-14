package ru.shawarma.core.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.TokensRequest
import ru.shawarma.core.data.entities.UserLoginRequest
import ru.shawarma.core.data.entities.UserRegisterRequest
import ru.shawarma.core.data.repositories.MainAuthRepository
import ru.shawarma.core.data.testdoubles.FakeAuthRemoteDataSource
import ru.shawarma.core.data.utils.Result

class MainAuthRepositoryTest {
    private lateinit var mainAuthRepository: MainAuthRepository

    private lateinit var authRemoteDataSource: FakeAuthRemoteDataSource

    private lateinit var userLoginRequest: UserLoginRequest

    private lateinit var tokensRequest: TokensRequest

    private lateinit var registerRequest: UserRegisterRequest

    @Before
    fun setup(){
        authRemoteDataSource = FakeAuthRemoteDataSource()
        mainAuthRepository = MainAuthRepository(authRemoteDataSource)
        userLoginRequest = UserLoginRequest("","")
        tokensRequest = TokensRequest("","")
        registerRequest = UserRegisterRequest("","","")
    }

    @Test
    fun loginTest() = runTest {
        assertEquals(
            (authRemoteDataSource.login(userLoginRequest) as Result.Success<AuthData>).data,
            (mainAuthRepository.login(userLoginRequest) as Result.Success<AuthData>).data
        )
    }

    @Test
    fun refreshTokenTest() = runTest {
        assertEquals(
            (authRemoteDataSource.refreshToken(tokensRequest) as Result.Success<AuthData>).data,
            (mainAuthRepository.refreshToken(tokensRequest) as Result.Success<AuthData>).data
        )
    }

    @Test
    fun registerTest() = runTest {
        assertEquals(
            (authRemoteDataSource.register(registerRequest) as Result.Failure).message,
            (mainAuthRepository.register(registerRequest) as Result.Failure).message
        )
    }
}