package ru.shawarma.settings.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.shawarma.settings.entities.OrderElement

class OrderItemDiffCallback: DiffUtil.ItemCallback<OrderElement>() {
    override fun areItemsTheSame(oldItem: OrderElement, newItem: OrderElement): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: OrderElement, newItem: OrderElement): Boolean =
        oldItem == newItem
}