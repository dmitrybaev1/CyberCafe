package ru.shawarma.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.shawarma.core.data.repositories.OrderRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.settings.viewmodels.OrdersViewModel
import ru.shawarma.core.data.utils.Result
import ru.shawarma.settings.viewmodels.OrdersUIState

class OrdersViewModelTest {
    private lateinit var orderRepository: OrderRepository

    private lateinit var viewModel: OrdersViewModel

    @get:Rule
    val rule = MainDispatcherRule()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        orderRepository = mock()
        viewModel = OrdersViewModel(orderRepository)
    }

    @Test
    fun `Get orders successfully`() = runTest {
        whenever(orderRepository.getOrders(anyInt(), anyInt())).thenReturn(Result.Success(listOf()))
        viewModel.getOrders()
        assertTrue(viewModel.ordersState.value is OrdersUIState.Success)
    }

    @Test
    fun `Get orders api error`() = runTest {
        whenever(orderRepository.getOrders(anyInt(), anyInt())).thenReturn(Result.Failure(""))
        viewModel.getOrders()
        assertTrue(viewModel.ordersState.value is OrdersUIState.Error)
    }

    @Test
    fun `Get orders invalid token error`() = runTest {
        whenever(orderRepository.getOrders(anyInt(), anyInt())).thenReturn(Result.Failure(Errors.REFRESH_TOKEN_ERROR))
        viewModel.getOrders()
        assertTrue(viewModel.ordersState.value is OrdersUIState.TokenInvalidError)
    }

    @Test
    fun `Get orders no internet error`() = runTest {
        whenever(orderRepository.getOrders(anyInt(), anyInt())).thenReturn(Result.Failure(Errors.NO_INTERNET_ERROR))
        viewModel.getOrders()
        assertTrue(viewModel.isDisconnectedToInternet.value!!)
    }

    @Test
    fun `Reset no internet state correctly`(){
        viewModel.resetNoInternetState()
        assertTrue(!viewModel.isDisconnectedToInternet.value!!)
    }

    @Test
    fun `Go to order correctly works`(){
        viewModel.goToOrder(0)
        assertTrue(viewModel.navCommand.value is NavigationCommand.ToOrderModule)
    }
}