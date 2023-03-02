package ru.shawarma.core.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import ru.shawarma.core.data.repositories.MainMenuRepository
import ru.shawarma.core.data.testdoubles.FakeMenuRemoteDataSource
import ru.shawarma.core.data.utils.Result

class MainMenuRepositoryTest {
    private lateinit var menuRepository: MainMenuRepository

    private lateinit var menuRemoteDataSource: FakeMenuRemoteDataSource
    @Before
    fun setup(){
        menuRemoteDataSource = FakeMenuRemoteDataSource()
        menuRepository = MainMenuRepository(menuRemoteDataSource)
    }

    @Test
    fun getMenuTest() = runTest {
        assertEquals(
            (menuRemoteDataSource.getMenu("",0,0) as Result.Success).data.size,
            (menuRepository.getMenu("",0,0) as Result.Success).data.size
        )
    }

    @Test
    fun getMenuItemTest() = runTest {
        assertEquals(
            (menuRemoteDataSource.getMenuItem("",0) as Result.Success).data,
            (menuRepository.getMenuItem("",0) as Result.Success).data
        )
    }

}