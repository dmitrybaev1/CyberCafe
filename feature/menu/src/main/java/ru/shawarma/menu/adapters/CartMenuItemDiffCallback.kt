package ru.shawarma.menu.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.shawarma.menu.entities.CartMenuItem
import ru.shawarma.menu.entities.MenuElement

class CartMenuItemDiffCallback: DiffUtil.ItemCallback<CartMenuItem>() {
    override fun areItemsTheSame(oldItem: CartMenuItem, newItem: CartMenuItem): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: CartMenuItem, newItem: CartMenuItem): Boolean =
        oldItem == newItem
}