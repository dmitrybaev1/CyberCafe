package ru.shawarma.core.data

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ru.shawarma.core.data.datasources.MainMenuRemoteDataSource
import ru.shawarma.core.data.datasources.MainOrderRemoteDataSource
import ru.shawarma.core.data.entities.CreateOrderRequest
import ru.shawarma.core.data.testdoubles.FakeMenuService
import ru.shawarma.core.data.testdoubles.FakeOrderService
import ru.shawarma.core.data.utils.Result

class MainOrderRemoteDataSourceTest {
    private lateinit var orderRemoteDataSource: MainOrderRemoteDataSource

    private lateinit var orderService: FakeOrderService

    @Before
    fun setup(){
        orderService = FakeOrderService()
    }

    @Test
    fun `get orders response correctly wrapped into result`() = runTest {
        orderRemoteDataSource = MainOrderRemoteDataSource(orderService,
            StandardTestDispatcher(testScheduler)
        )
        val expected = Result.Success(orderService.getOrders("",0,0))
        val actual = orderRemoteDataSource.getOrders("",0,0)
        Assert.assertTrue(expected.javaClass == actual.javaClass)
    }

    @Test
    fun `get order response correctly wrapped into result`() = runTest {
        orderRemoteDataSource = MainOrderRemoteDataSource(orderService,
            StandardTestDispatcher(testScheduler)
        )
        val expected = Result.Success(orderService.getOrder("",0))
        val actual = orderRemoteDataSource.getOrder("",0)
        Assert.assertTrue(expected.javaClass == actual.javaClass)
    }

    @Test
    fun `create order response correctly wrapped into result`() = runTest {
        orderRemoteDataSource = MainOrderRemoteDataSource(orderService,
            StandardTestDispatcher(testScheduler)
        )
        val expected = Result.Success(orderService.createOrder("",CreateOrderRequest(listOf())))
        val actual = orderRemoteDataSource.createOrder("",CreateOrderRequest(listOf()))
        Assert.assertTrue(expected.javaClass == actual.javaClass)
    }
}