package ru.shawarma.core.data.testdoubles

import ru.shawarma.core.data.entities.MenuItemResponse
import ru.shawarma.core.data.services.MenuService

class FakeMenuService: MenuService {
    override suspend fun getMenu(token: String, offset: Int, count: Int): List<MenuItemResponse> =
        emptyList()

    override suspend fun getMenuItem(token: String, id: Long): MenuItemResponse =
        MenuItemResponse(0,"",0,true)
}