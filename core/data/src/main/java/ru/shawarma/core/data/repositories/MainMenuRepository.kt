package ru.shawarma.core.data.repositories

import ru.shawarma.core.data.datasources.MenuRemoteDataSource
import ru.shawarma.core.data.entities.MenuItemResponse
import ru.shawarma.core.data.utils.Result
import javax.inject.Inject

class MainMenuRepository @Inject constructor(
    private val menuRemoteDataSource: MenuRemoteDataSource
): MenuRepository {
    override suspend fun getMenu(
        token: String,
        offset: Int,
        count: Int
    ): Result<List<MenuItemResponse>> =
        menuRemoteDataSource.getMenu(token, offset, count)

    override suspend fun getMenuItem(token: String, id: Long): Result<MenuItemResponse> =
        menuRemoteDataSource.getMenuItem(token, id)

}