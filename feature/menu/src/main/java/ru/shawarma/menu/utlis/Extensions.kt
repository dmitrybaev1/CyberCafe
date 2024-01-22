package ru.shawarma.menu.utlis

import ru.shawarma.core.data.entities.MenuItemResponse
import ru.shawarma.core.data.entities.OrderMenuItemRequest
import ru.shawarma.menu.entities.CartMenuItem
import ru.shawarma.menu.entities.MenuItem

fun mapMenuItemResponseToMenuItem(menuItemResponse: MenuItemResponse): MenuItem =
    MenuItem(
        menuItemResponse.id,
        menuItemResponse.name,
        menuItemResponse.price,
        menuItemResponse.imageUrl,
        menuItemResponse.description
    )

fun mapCartListToOrderMenuItemRequest(cartList: List<CartMenuItem>): List<OrderMenuItemRequest>{
    val list = arrayListOf<OrderMenuItemRequest>()
    for(cartItem in cartList){
        list.add(
            OrderMenuItemRequest(cartItem.menuItem.id,cartItem.count)
        )
    }
    return list
}
const val MENU_REQUEST_OFFSET = 10
const val MENU_FULL_SPAN_SIZE = 2
const val MENU_ITEM_SPAN_SIZE = 1

