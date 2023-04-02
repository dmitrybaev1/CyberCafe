package ru.shawarma.core.data.repositories

import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.data.entities.MenuItemResponse
import ru.shawarma.core.data.utils.Result

interface MenuRepository {

    suspend fun getMenu(offset: Int, count: Int): Result<List<MenuItemResponse>>

    suspend fun getMenuItem(id: Long): Result<MenuItemResponse>

}