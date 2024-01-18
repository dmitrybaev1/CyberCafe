package ru.shawarma.menu

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.databinding.ObservableBoolean
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.shawarma.core.data.entities.*
import ru.shawarma.core.data.repositories.MenuRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result
import ru.shawarma.core.data.repositories.OrderRepository
import ru.shawarma.menu.entities.CartMenuItem
import ru.shawarma.menu.entities.MenuElement
import ru.shawarma.menu.viewmodels.MenuUIState
import ru.shawarma.menu.viewmodels.MenuViewModel
import ru.shawarma.menu.viewmodels.MakeOrderUIState
import java.util.*

class MenuViewModelTest {

    private lateinit var viewModel: MenuViewModel

    private lateinit var menuRepository: MenuRepository

    private lateinit var orderRepository: OrderRepository

    private lateinit var menuItemResponse: MenuItemResponse

    private lateinit var orderResponse: OrderResponse

    @get:Rule
    val rule = MainDispatcherRule()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        menuRepository = mock()
        orderRepository = mock()
        menuItemResponse = MenuItemResponse(0,"",0,true)
        orderResponse = OrderResponse(0, listOf(),"", Date(), Date(),OrderStatus.IN_QUEUE,0)
        viewModel = MenuViewModel(menuRepository,orderRepository)
    }

    @Test
    fun `Get menu successfully`() = runTest {
        whenever(menuRepository.getMenu(anyInt(), anyInt())).thenReturn(Result.Success(listOf(menuItemResponse)))
        viewModel.getMenu()
        assertTrue(viewModel.menuState.value is MenuUIState.Success)
    }

    @Test
    fun `Get menu api error`() = runTest {
        whenever(menuRepository.getMenu(anyInt(), anyInt())).thenReturn(Result.Failure(""))
        viewModel.getMenu()
        assertTrue(viewModel.menuState.value is MenuUIState.Error)
    }

    @Test
    fun `Get menu token invalid error`() = runTest {
        whenever(menuRepository.getMenu(anyInt(), anyInt())).thenReturn(Result.Failure(Errors.REFRESH_TOKEN_ERROR))
        viewModel.getMenu()
        assertTrue(viewModel.menuState.value is MenuUIState.TokenInvalidError)
    }

    @Test
    fun `Get menu no internet error`() = runTest {
        whenever(menuRepository.getMenu(anyInt(), anyInt())).thenReturn(Result.Failure(Errors.NO_INTERNET_ERROR))
        viewModel.getMenu()
        assertTrue(viewModel.isDisconnectedToInternet.value!!)
    }

    @Test
    fun `Make order successfully`() = runTest {
        whenever(orderRepository.createOrder(any())).thenReturn(Result.Success(orderResponse))
        viewModel.makeOrder()
        assertTrue(viewModel.orderState.value is MakeOrderUIState.Success)
    }

    @Test
    fun `Make order api error`() = runTest{
        whenever(orderRepository.createOrder(any())).thenReturn(Result.Failure(""))
        viewModel.makeOrder()
        assertTrue(viewModel.orderState.value is MakeOrderUIState.Error)
    }

    @Test
    fun `Make order token invalid error`() = runTest{
        whenever(orderRepository.createOrder(any())).thenReturn(Result.Failure(Errors.REFRESH_TOKEN_ERROR))
        viewModel.makeOrder()
        assertTrue(viewModel.orderState.value is MakeOrderUIState.TokenInvalidError)
    }

    @Test
    fun `Make order no internet error`() = runTest{
        whenever(orderRepository.createOrder(any())).thenReturn(Result.Failure(Errors.NO_INTERNET_ERROR))
        viewModel.makeOrder()
        assertTrue(viewModel.isDisconnectedToInternet.value!!)
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