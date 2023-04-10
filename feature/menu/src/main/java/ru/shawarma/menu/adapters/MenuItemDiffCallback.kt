package ru.shawarma.menu.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.shawarma.menu.entities.MenuElement

class MenuItemDiffCallback: DiffUtil.ItemCallback<MenuElement>() {
    override fun areItemsTheSame(oldItem: MenuElement, newItem: MenuElement): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: MenuElement, newItem: MenuElement): Boolean =
        oldItem == newItem
}