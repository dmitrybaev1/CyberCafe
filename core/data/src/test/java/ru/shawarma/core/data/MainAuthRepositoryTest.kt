package ru.shawarma.core.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.TokensRequest
import ru.shawarma.core.data.entities.UserLoginRequest
import ru.shawarma.core.data.entities.UserRegisterRequest
import ru.shawarma.core.data.managers.InternetManager
import ru.shawarma.core.data.repositories.MainAuthRepository
import ru.shawarma.core.data.testdoubles.FakeAuthRemoteDataSource
import ru.shawarma.core.data.testdoubles.FakeOfflineInternetManager
import ru.shawarma.core.data.testdoubles.FakeOnlineInternetManager
import ru.shawarma.core.data.testdoubles.FakeTokenManager
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result

class MainAuthRepositoryTest {
    private lateinit var authRepository: MainAuthRepository

    private lateinit var authRemoteDataSource: FakeAuthRemoteDataSource

    private lateinit var tokenManager: FakeTokenManager

    private lateinit var internetManager: InternetManager

    private lateinit var userLoginRequest: UserLoginRequest

    private lateinit var tokensRequest: TokensRequest

    private lateinit var registerRequest: UserRegisterRequest

    @Before
    fun setup(){
        authRemoteDataSource = FakeAuthRemoteDataSource()
        tokenManager = FakeTokenManager()
        userLoginRequest = UserLoginRequest("","")
        tokensRequest = TokensRequest("","")
        registerRequest = UserRegisterRequest("","","")
    }

    @Test
    fun `Login success`() = runTest {
        internetManager = FakeOnlineInternetManager()
        authRepository = MainAuthRepository(authRemoteDataSource,tokenManager, internetManager)
        assertEquals(
            (authRemoteDataSource.login(userLoginRequest) as Result.Success<AuthData>).data,
            (authRepository.login(userLoginRequest) as Result.Success<AuthData>).data
        )
    }

    @Test
    fun `Login no internet error`() = runTest {
        internetManager = FakeOfflineInternetManager()
        authRepository = MainAuthRepository(authRemoteDataSource,tokenManager, internetManager)
        assertTrue(
            (authRepository.login(userLoginRequest) as Result.Failure).message == Errors.NO_INTERNET_ERROR
        )
    }

    @Test
    fun refreshTokenTest() = runTest {
        internetManager = FakeOnlineInternetManager()
        authRepository = MainAuthRepository(authRemoteDataSource,tokenManager, internetManager)
        assertEquals(
            tokenManager.getAuthData(),
            (authRepository.getActualAuthData() as Result.Success<AuthData>).data
        )
    }

    @Test
    fun `Register success`() = runTest {
        internetManager = FakeOnlineInternetManager()
        authRepository = MainAuthRepository(authRemoteDataSource,tokenManager, internetManager)
        assertEquals(
            (authRemoteDataSource.register(registerRequest) as Result.Success).data,
            (authRepository.register(registerRequest) as Result.Success).data
        )
    }

    @Test
    fun `Register no internet error`() = runTest {
        internetManager = FakeOfflineInternetManager()
        authRepository = MainAuthRepository(authRemoteDataSource,tokenManager, internetManager)
        assertTrue(
            (authRepository.register(registerRequest) as Result.Failure).message == Errors.NO_INTERNET_ERROR
        )
    }

    @Test
    fun `Get info success`() = runTest {
        internetManager = FakeOnlineInternetManager()
        authRepository = MainAuthRepository(authRemoteDataSource,tokenManager, internetManager)
        assertEquals(
            (authRemoteDataSource.getInfo("") as Result.Success).data,
            (authRepository.getInfo() as Result.Success).data
        )
    }

    @Test
    fun `Get info no internet error`() = runTest {
        internetManager = FakeOfflineInternetManager()
        authRepository = MainAuthRepository(authRemoteDataSource,tokenManager, internetManager)
        assertTrue(
            (authRepository.getInfo() as Result.Failure).message == Errors.NO_INTERNET_ERROR
        )
    }
}