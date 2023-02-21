package ru.shawarma.menu

import ru.shawarma.core.data.entities.MenuItemResponse

fun mapMenuItemResponseToMenuItem(menuItemResponse: MenuItemResponse) =
    MenuElement.MenuItem(
        menuItemResponse.id,
        menuItemResponse.name,
        menuItemResponse.price
    )