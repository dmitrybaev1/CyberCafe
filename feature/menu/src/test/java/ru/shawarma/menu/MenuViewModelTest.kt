package ru.shawarma.menu

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.databinding.ObservableBoolean
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.MenuItemResponse
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.repositories.MenuRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.data.managers.TokenManager
import ru.shawarma.menu.entities.CartMenuItem
import ru.shawarma.menu.entities.MenuElement
import ru.shawarma.menu.viewmodels.MenuUIState
import ru.shawarma.menu.viewmodels.MenuViewModel

class MenuViewModelTest {

    private lateinit var viewModel: MenuViewModel

    private lateinit var menuRepository: MenuRepository

    private lateinit var authRepository: AuthRepository

    private lateinit var tokenManager: TokenManager

    private lateinit var authData: AuthData

    private lateinit var menuItemResponse: MenuItemResponse

    @get:Rule
    val rule = MainDispatcherRule()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        menuRepository = mock()
        authRepository = mock()
        tokenManager = mock()
        menuItemResponse = MenuItemResponse(0,"",0,true)
        viewModel = MenuViewModel(menuRepository,authRepository,tokenManager)
    }

    @Test
    fun `Get menu successfully`() = runTest {
        authData = AuthData("","","",System.currentTimeMillis() / 1000L + 100)
        viewModel.setToken(authData)
        whenever(tokenManager.getAuthData()).thenReturn(authData)
        whenever(authRepository.refreshToken(any())).thenReturn(Result.Success(authData))
        whenever(menuRepository.getMenu(anyString(),anyInt(), anyInt())).thenReturn(Result.Success(listOf(menuItemResponse)))
        viewModel.getMenu()
        assertTrue(viewModel.menuState.value is MenuUIState.Success)
    }

    @Test
    fun `Get menu api error`() = runTest {
        authData = AuthData("","","",System.currentTimeMillis() / 1000L + 100)
        viewModel.setToken(authData)
        whenever(tokenManager.getAuthData()).thenReturn(authData)
        whenever(authRepository.refreshToken(any())).thenReturn(Result.Success(authData))
        whenever(menuRepository.getMenu(anyString(),anyInt(), anyInt())).thenReturn(Result.Failure(""))
        viewModel.getMenu()
        assertTrue(viewModel.menuState.value is MenuUIState.Error)
    }

    @Test
    fun `Token expired but successful refresh and get menu`() = runTest {
        authData = AuthData("","","",System.currentTimeMillis() / 1000L - 100)
        viewModel.setToken(authData)
        whenever(authRepository.refreshToken(any())).thenReturn(Result.Success(authData))
        whenever(menuRepository.getMenu(anyString(),anyInt(), anyInt())).thenReturn(Result.Success(listOf(menuItemResponse)))
        viewModel.getMenu()
        assertTrue(viewModel.menuState.value is MenuUIState.Success)
    }

    @Test
    fun `Get menu token invalid error`() = runTest {
        authData = AuthData("","","",System.currentTimeMillis() / 1000L - 100)
        viewModel.setToken(authData)
        whenever(authRepository.refreshToken(any())).thenReturn(Result.Failure(Errors.REFRESH_TOKEN_ERROR))
        viewModel.getMenu()
        assertTrue(viewModel.menuState.value is MenuUIState.TokenInvalidError)
    }

    @Test
    fun `Cart list operations correctly work`(){
        val menuItem = MenuElement.MenuItem(0,"",0, ObservableBoolean(false))
        repeat(3){
            viewModel.addToCart(menuItem)
        }
        viewModel.removeFromCart(menuItem)
        val expectedCartItemList = listOf(CartMenuItem(menuItem,2))
        assert(viewModel.cartListLiveData.value == expectedCartItemList)
    }

    @Test
    fun `Total price correctly calculated`(){
        val menuItem = MenuElement.MenuItem(0,"",120, ObservableBoolean(false))
        repeat(5){
            viewModel.addToCart(menuItem)
        }
        repeat(2){
            viewModel.removeFromCart(menuItem)
        }
        repeat(3){
            viewModel.addToCart(menuItem)
        }
        assert(viewModel.totalCurrentCartPrice == 720)
    }
}