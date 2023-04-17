package ru.shawarma.settings.utils

import ru.shawarma.core.data.entities.InfoResponse
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.settings.entities.InfoItem
import ru.shawarma.settings.entities.OrderElement


fun mapOrderResponseToOrderItems(ordersResponse: List<OrderResponse>): List<OrderElement.OrderItem> {
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

fun mapInfoResponseToInfoItems(infoResponse: InfoResponse): List<InfoItem> =
    listOf(
        InfoItem("name",infoResponse.name),
        InfoItem("id",infoResponse.id),
        InfoItem("email",infoResponse.email)
    )

const val STANDARD_REQUEST_OFFSET = 10