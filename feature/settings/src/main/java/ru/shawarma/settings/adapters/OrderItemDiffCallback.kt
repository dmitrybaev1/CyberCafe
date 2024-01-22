package ru.shawarma.settings.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.shawarma.settings.entities.OrderItem

class OrderItemDiffCallback: DiffUtil.ItemCallback<OrderItem>() {
    override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean =
        oldItem == newItem
}