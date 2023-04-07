package ru.shawarma.order

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.entities.OrderStatus
import ru.shawarma.core.data.repositories.OrderRepository
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.order.viewmodels.OrderViewModel
import ru.shawarma.core.data.utils.Result
import ru.shawarma.order.viewmodels.OrderUIState
import java.util.*

class OrderViewModelTest {
    private lateinit var orderRepository: OrderRepository

    private lateinit var viewModel: OrderViewModel

    private val orderResponse =
        OrderResponse(0, listOf(),"", Date(), Date(),OrderStatus.IN_QUEUE,0)

    @get:Rule
    val rule = MainDispatcherRule()

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        orderRepository = mock()
        viewModel = OrderViewModel(orderRepository)
    }

    @Test
    fun `Get order successfully`() = runTest {
        whenever(orderRepository.getOrder(anyInt())).thenReturn(Result.Success(orderResponse))
        viewModel.getOrder(0)
        assertTrue(viewModel.orderState.value is OrderUIState.Success)
    }

    @Test
    fun `Get order api error`() = runTest {
        whenever(orderRepository.getOrder(anyInt())).thenReturn(Result.Failure(""))
        viewModel.getOrder(0)
        assertTrue(viewModel.orderState.value is OrderUIState.Error)
    }

    @Test
    fun `Get order invalid token error`() = runTest {
        whenever(orderRepository.getOrder(anyInt())).thenReturn(Result.Failure(Errors.REFRESH_TOKEN_ERROR))
        viewModel.getOrder(0)
        assertTrue(viewModel.orderState.value is OrderUIState.TokenInvalidError)
    }

    @Test
    fun `Get order no internet error`() = runTest {
        whenever(orderRepository.getOrder(anyInt())).thenReturn(Result.Failure(Errors.NO_INTERNET_ERROR))
        viewModel.getOrder(0)
        assertTrue(viewModel.isDisconnectedToInternet.value!!)
    }

    @Test
    fun `Get order keep state when error in refreshing`() = runTest {
        whenever(orderRepository.getOrder(anyInt())).thenReturn(Result.Failure(""))
        viewModel.getOrder(0, true)
        assertTrue(viewModel.orderState.value is OrderUIState.Loading)
    }

    @Test
    fun `Reset no internet state correctly`(){
        viewModel.resetNoInternetState()
        assertTrue(!viewModel.isDisconnectedToInternet.value!!)
    }

    @Test
    fun `Set order status correctly`(){
        viewModel.setStatus("Cooking")
        assertTrue(viewModel.orderStatus.value == "Cooking")
    }
}