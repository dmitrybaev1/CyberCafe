package ru.shawarma.menu.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.shawarma.menu.entities.CartMenuItem

class CartMenuItemDiffCallback: DiffUtil.ItemCallback<CartMenuItem>() {
    override fun areItemsTheSame(oldItem: CartMenuItem, newItem: CartMenuItem): Boolean =
        oldItem.menuItem.id == newItem.menuItem.id && oldItem.count == newItem.count

    override fun areContentsTheSame(oldItem: CartMenuItem, newItem: CartMenuItem): Boolean =
        oldItem == newItem
}