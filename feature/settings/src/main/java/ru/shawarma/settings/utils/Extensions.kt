package ru.shawarma.settings.utils

import ru.shawarma.core.data.entities.InfoResponse
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.settings.entities.InfoItem
import ru.shawarma.settings.entities.OrderItem


fun mapOrderResponseToOrderItems(orderResponse: OrderResponse): OrderItem =
    OrderItem(
        orderResponse.id,
        orderResponse.createdDate,
        orderResponse.status
    )

fun mapInfoResponseToInfoItems(infoResponse: InfoResponse): List<InfoItem> =
    listOf(
        InfoItem("name",infoResponse.name),
        InfoItem("id",infoResponse.id),
        InfoItem("email",infoResponse.email)
    )

const val ORDERS_REQUEST_OFFSET = 10