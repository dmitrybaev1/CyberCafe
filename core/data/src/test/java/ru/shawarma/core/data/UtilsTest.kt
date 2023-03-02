package ru.shawarma.core.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.testdoubles.FakeErrorAuthRepository
import ru.shawarma.core.data.testdoubles.FakeSuccessAuthRepository
import ru.shawarma.core.data.testdoubles.FakeTokenManager
import ru.shawarma.core.data.utils.checkNotExpiresOrTryRefresh

class UtilsTest {

    private lateinit var authRepository: AuthRepository

    private lateinit var tokenManager: FakeTokenManager

    @Before
    fun setup(){
        tokenManager = FakeTokenManager()
    }

    @Test
    fun tokenValid() = runTest {
        authRepository = FakeSuccessAuthRepository()
        val authData = AuthData("","","",(System.currentTimeMillis() / 1000L) + 100)
        assertTrue(checkNotExpiresOrTryRefresh(authData,authRepository, tokenManager))
    }

    @Test
    fun tokenExpiresSuccessRefresh() = runTest {
        authRepository = FakeSuccessAuthRepository()
        val authData = AuthData("","","",(System.currentTimeMillis() / 1000L) - 100)
        assertTrue(checkNotExpiresOrTryRefresh(authData,authRepository, tokenManager))
    }

    @Test
    fun tokenExpiresErrorRefresh() = runTest {
        authRepository = FakeErrorAuthRepository()
        val authData = AuthData("","","",(System.currentTimeMillis() / 1000L) - 100)
        assertFalse(checkNotExpiresOrTryRefresh(authData,authRepository, tokenManager))
    }
}