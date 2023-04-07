package ru.shawarma.core.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.shawarma.core.data.managers.InternetManager
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.repositories.MainMenuRepository
import ru.shawarma.core.data.testdoubles.*
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result

class MainMenuRepositoryTest {
    private lateinit var menuRepository: MainMenuRepository

    private lateinit var menuRemoteDataSource: FakeMenuRemoteDataSource

    private lateinit var internetManager: InternetManager

    private lateinit var authRepository: AuthRepository

    @Before
    fun setup(){
        menuRemoteDataSource = FakeMenuRemoteDataSource()
    }

    @Test
    fun `Get menu successfully`() = runTest {
        internetManager = FakeOnlineInternetManager()
        authRepository = FakeSuccessAuthRepository()
        menuRepository = MainMenuRepository(menuRemoteDataSource,authRepository, internetManager)
        assertEquals(
            (menuRemoteDataSource.getMenu("",0,0) as Result.Success).data.size,
            (menuRepository.getMenu(0,0) as Result.Success).data.size
        )
    }

    @Test
    fun `Get menu no internet error`() = runTest {
        internetManager = FakeOfflineInternetManager()
        authRepository = FakeSuccessAuthRepository()
        menuRepository = MainMenuRepository(menuRemoteDataSource, authRepository, internetManager)
        assertTrue(
            (menuRepository.getMenu(0, 0) as Result.Failure).message == Errors.NO_INTERNET_ERROR
        )
    }

    @Test
    fun `Get menu auth error`() = runTest {
        internetManager = FakeOnlineInternetManager()
        authRepository = FakeErrorAuthRepository()
        menuRepository = MainMenuRepository(menuRemoteDataSource, authRepository, internetManager)
        assertTrue(
            (menuRepository.getMenu(0, 0) as Result.Failure).message == Errors.REFRESH_TOKEN_ERROR
        )
    }

    @Test
    fun `Get menu item successfully`() = runTest {
        internetManager = FakeOnlineInternetManager()
        authRepository = FakeSuccessAuthRepository()
        menuRepository = MainMenuRepository(menuRemoteDataSource,authRepository, internetManager)
        assertEquals(
            (menuRemoteDataSource.getMenuItem("",0) as Result.Success).data,
            (menuRepository.getMenuItem(0) as Result.Success).data
        )
    }

    @Test
    fun `Get menu item no internet error`() = runTest {
        internetManager = FakeOfflineInternetManager()
        authRepository = FakeSuccessAuthRepository()
        menuRepository = MainMenuRepository(menuRemoteDataSource, authRepository, internetManager)
        assertTrue(
            (menuRepository.getMenuItem( 0) as Result.Failure).message == Errors.NO_INTERNET_ERROR
        )
    }

    @Test
    fun `Get menu item auth error`() = runTest {
        internetManager = FakeOnlineInternetManager()
        authRepository = FakeErrorAuthRepository()
        menuRepository = MainMenuRepository(menuRemoteDataSource, authRepository, internetManager)
        assertTrue(
            (menuRepository.getMenuItem( 0) as Result.Failure).message == Errors.REFRESH_TOKEN_ERROR
        )
    }

}