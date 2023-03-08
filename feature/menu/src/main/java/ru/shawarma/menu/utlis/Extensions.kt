package ru.shawarma.menu.utlis

import ru.shawarma.core.data.entities.MenuItemResponse
import ru.shawarma.menu.entities.MenuElement

fun mapMenuItemResponseToMenuItem(menuItemsResponse: List<MenuItemResponse>): List<MenuElement.MenuItem>{
    val list = arrayListOf<MenuElement.MenuItem>()
    for(item in menuItemsResponse){
        list.add(
            MenuElement.MenuItem(
                item.id,
                item.name,
                item.price
            )
        )
    }
    return list
}

const val STANDARD_REQUEST_OFFSET = 10
const val MENU_FULL_SPAN_SIZE = 2
const val MENU_ITEM_SPAN_SIZE = 1

