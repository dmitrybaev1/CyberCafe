package ru.shawarma.settings.utils

import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.settings.entities.OrderElement


fun mapOrderResponseToOrderItem(ordersResponse: List<OrderResponse>): List<OrderElement.OrderItem> {
    val list = arrayListOf<OrderElement.OrderItem>()
    for(order in ordersResponse){
        list.add(
            OrderElement.OrderItem(
                order.id,
                order.createdDate,
                order.status
            )
        )
    }
    return list
}

const val STANDARD_REQUEST_OFFSET = 10