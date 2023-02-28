package ru.shawarma.core.data

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ru.shawarma.core.data.datasources.MainMenuRemoteDataSource
import ru.shawarma.core.data.testdoubles.FakeMenuService
import ru.shawarma.core.data.utils.Result

class MainMenuRemoteDataSourceTest {

    private lateinit var menuRemoteDataSource: MainMenuRemoteDataSource

    private lateinit var menuService: FakeMenuService

    @Before
    fun setup(){
        menuService = FakeMenuService()
    }

    @Test
    fun `menu response correctly wrapped into result`() = runTest {
        menuRemoteDataSource = MainMenuRemoteDataSource(menuService,
            StandardTestDispatcher(testScheduler))
        val expected = Result.Success(menuService.getMenu("",0,0))
        val actual = menuRemoteDataSource.getMenu("",0,0)
        Assert.assertTrue(expected.javaClass == actual.javaClass)
    }

    @Test
    fun `menu item response correctly wrapped into result`() = runTest {
        menuRemoteDataSource = MainMenuRemoteDataSource(menuService,
            StandardTestDispatcher(testScheduler))
        val expected = Result.Success(menuService.getMenuItem("",0))
        val actual = menuRemoteDataSource.getMenuItem("",0)
        Assert.assertTrue(expected.javaClass == actual.javaClass)
    }
}