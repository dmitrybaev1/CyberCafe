package ru.shawarma.menu.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.shawarma.menu.entities.MenuItem

class MenuItemDiffCallback: DiffUtil.ItemCallback<MenuItem>() {
    override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean =
        oldItem == newItem
}