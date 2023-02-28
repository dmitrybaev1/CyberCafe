package ru.shawarma.core.data.testdoubles

import ru.shawarma.core.data.datasources.MenuRemoteDataSource
import ru.shawarma.core.data.entities.MenuItemResponse
import ru.shawarma.core.data.utils.Result

class FakeMenuRemoteDataSource: MenuRemoteDataSource {
    override suspend fun getMenu(
        token: String,
        offset: Int,
        count: Int
    ): Result<List<MenuItemResponse>> = Result.Success(emptyList())

    override suspend fun getMenuItem(token: String, id: Long): Result<MenuItemResponse> =
        Result.Success(MenuItemResponse(1,"",1,true))
}