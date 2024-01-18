package ru.shawarma.menu.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.shawarma.menu.entities.MenuElement

class MenuItemDiffCallback: DiffUtil.ItemCallback<MenuElement.MenuItem>() {
    override fun areItemsTheSame(oldItem: MenuElement.MenuItem, newItem: MenuElement.MenuItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: MenuElement.MenuItem, newItem: MenuElement.MenuItem): Boolean =
        oldItem == newItem
}