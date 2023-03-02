package ru.shawarma.core.data.datasources

import ru.shawarma.core.data.entities.MenuItemResponse
import ru.shawarma.core.data.utils.Result

interface MenuRemoteDataSource {

    suspend fun getMenu(token: String, offset: Int, count: Int): Result<List<MenuItemResponse>>

    suspend fun getMenuItem(token: String, id: Long): Result<MenuItemResponse>

}