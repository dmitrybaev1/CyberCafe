package ru.shawarma.core.data.entities

import com.google.gson.annotations.SerializedName

enum class OrderStatus(val value: String) {
    @SerializedName("InQueue")
    IN_QUEUE("InQueue"),

    @SerializedName("Cooking")
    COOKING("Cooking"),

    @SerializedName("Ready")
    READY("Ready"),

    @SerializedName("Closed")
    CLOSED("Closed"),

    @SerializedName("Canceled")
    CANCELED("Canceled")
}