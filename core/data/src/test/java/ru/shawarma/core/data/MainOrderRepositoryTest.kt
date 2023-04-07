package ru.shawarma.core.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.managers.InternetManager
import ru.shawarma.core.data.repositories.AuthRepository
import ru.shawarma.core.data.repositories.MainMenuRepository
import ru.shawarma.core.data.repositories.MainOrderRepository
import ru.shawarma.core.data.testdoubles.*
import ru.shawarma.core.data.utils.Errors
import ru.shawarma.core.data.utils.Result

class MainOrderRepositoryTest {
    private lateinit var orderRepository: MainOrderRepository

    private lateinit var orderRemoteDataSource: FakeOrderRemoteDataSource

    private lateinit var internetManager: InternetManager

    private lateinit var authRepository: AuthRepository

    private val createOrderRequest = CreateOrderRequest(listOf())

    @Before
    fun setup(){
        orderRemoteDataSource = FakeOrderRemoteDataSource()
    }

    @Test
    fun `Get orders successfully`() = runTest {
        internetManager = FakeOnlineInternetManager()
        authRepository = FakeSuccessAuthRepository()
        orderRepository = MainOrderRepository(orderRemoteDataSource,authRepository, internetManager)
        Assert.assertEquals(
            (orderRemoteDataSource.getOrders("",0,0) as Result.Success).data.size,
            (orderRepository.getOrders(0,0) as Result.Success).data.size
        )
    }

    @Test
    fun `Get orders no internet error`() = runTest {
        internetManager = FakeOfflineInternetManager()
        authRepository = FakeSuccessAuthRepository()
        orderRepository = MainOrderRepository(orderRemoteDataSource, authRepository, internetManager)
        Assert.assertTrue(
            (orderRepository.getOrders(0, 0) as Result.Failure).message == Errors.NO_INTERNET_ERROR
        )
    }

    @Test
    fun `Get orders auth error`() = runTest {
        internetManager = FakeOnlineInternetManager()
        authRepository = FakeErrorAuthRepository()
        orderRepository = MainOrderRepository(orderRemoteDataSource, authRepository, internetManager)
        Assert.assertTrue(
            (orderRepository.getOrders(0, 0) as Result.Failure).message == Errors.REFRESH_TOKEN_ERROR
        )
    }

    @Test
    fun `Get order successfully`() = runTest {
        internetManager = FakeOnlineInternetManager()
        authRepository = FakeSuccessAuthRepository()
        orderRepository = MainOrderRepository(orderRemoteDataSource,authRepository, internetManager)
        Assert.assertEquals(
            (orderRemoteDataSource.getOrder("",0) as Result.Success).data,
            (orderRepository.getOrder(0) as Result.Success).data
        )
    }

    @Test
    fun `Get order no internet error`() = runTest {
        internetManager = FakeOfflineInternetManager()
        authRepository = FakeSuccessAuthRepository()
        orderRepository = MainOrderRepository(orderRemoteDataSource, authRepository, internetManager)
        Assert.assertTrue(
            (orderRepository.getOrder( 0) as Result.Failure).message == Errors.NO_INTERNET_ERROR
        )
    }

    @Test
    fun `Get order auth error`() = runTest {
        internetManager = FakeOnlineInternetManager()
        authRepository = FakeErrorAuthRepository()
        orderRepository = MainOrderRepository(orderRemoteDataSource, authRepository, internetManager)
        Assert.assertTrue(
            (orderRepository.getOrder( 0) as Result.Failure).message == Errors.REFRESH_TOKEN_ERROR
        )
    }

    @Test
    fun `Create order successfully`() = runTest {
        internetManager = FakeOnlineInternetManager()
        authRepository = FakeSuccessAuthRepository()
        orderRepository = MainOrderRepository(orderRemoteDataSource,authRepository, internetManager)
        Assert.assertEquals(
            (orderRemoteDataSource.createOrder("", createOrderRequest) as Result.Success).data,
            (orderRepository.createOrder(createOrderRequest) as Result.Success).data
        )
    }

    @Test
    fun `Create order no internet error`() = runTest {
        internetManager = FakeOfflineInternetManager()
        authRepository = FakeSuccessAuthRepository()
        orderRepository = MainOrderRepository(orderRemoteDataSource, authRepository, internetManager)
        Assert.assertTrue(
            (orderRepository.createOrder(createOrderRequest) as Result.Failure).message == Errors.NO_INTERNET_ERROR
        )
    }

    @Test
    fun `Create order auth error`() = runTest {
        internetManager = FakeOnlineInternetManager()
        authRepository = FakeErrorAuthRepository()
        orderRepository = MainOrderRepository(orderRemoteDataSource, authRepository, internetManager)
        Assert.assertTrue(
            (orderRepository.createOrder( createOrderRequest) as Result.Failure).message == Errors.REFRESH_TOKEN_ERROR
        )
    }
}