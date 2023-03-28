package ru.shawarma.order

import ru.shawarma.core.data.entities.OrderMenuItemResponse
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.order.entities.Order
import ru.shawarma.order.entities.OrderMenuItem

fun mapOrderMenuItemResponseToOrderMenuItem(
    itemResponses: List<OrderMenuItemResponse>
): List<OrderMenuItem>{
    val list = arrayListOf<OrderMenuItem>()
    for(itemResponse in itemResponses){
        list.add(
            OrderMenuItem(
            itemResponse.id,
            itemResponse.name,
            itemResponse.price,
            itemResponse.amount
        )
        )
    }
    return list
}

fun mapOrderResponseToOrder(orderResponse: OrderResponse): Order =
    Order(
        orderResponse.id,
        mapOrderMenuItemResponseToOrderMenuItem(orderResponse.menuItems),
        orderResponse.createdDate,
        orderResponse.closeDate,
        orderResponse.status,
        orderResponse.totalPrice
    )
