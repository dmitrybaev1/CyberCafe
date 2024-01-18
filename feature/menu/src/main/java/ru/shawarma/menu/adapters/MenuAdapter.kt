package ru.shawarma.menu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.shawarma.core.ui.loadImage
import ru.shawarma.menu.MenuController
import ru.shawarma.menu.databinding.MenuItemBinding
import ru.shawarma.menu.entities.MenuElement

class MenuAdapter(
    private val menuController: MenuController
) : PagingDataAdapter<MenuElement.MenuItem, MenuAdapter.MenuItemViewHolder>(MenuItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val binding = DataBindingUtil.inflate<MenuItemBinding>(
            LayoutInflater.from(parent.context),viewType,parent,false
        )
        return MenuItemViewHolder(binding,menuController)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val menuItem = getItem(position) as MenuElement.MenuItem
        holder.bind(menuItem)
    }

    class MenuItemViewHolder(
        private val binding: MenuItemBinding,
        private val menuController: MenuController
    ) : ViewHolder(binding.root){
        fun bind(menuItem: MenuElement.MenuItem){
            binding.menuItem = menuItem
            binding.menuCartQuantityControlView.count = menuController.getMenuItemCount(menuItem)
            menuItem.imageUrl?.let { binding.menuItemImageView.loadImage(it,binding.root.context) }
            binding.menuAddToCartButton.setOnClickListener {
                menuController.addToCart(menuItem)
                binding.menuCartQuantityControlView.count = menuController.getMenuItemCount(menuItem)
            }
            binding.menuCartQuantityControlView.setOnMinusClickListener {
                menuController.removeFromCart(menuItem)
                binding.menuCartQuantityControlView.count = menuController.getMenuItemCount(menuItem)
            }
            binding.menuCartQuantityControlView.setOnPlusClickListener {
                menuController.addToCart(menuItem)
                binding.menuCartQuantityControlView.count = menuController.getMenuItemCount(menuItem)
            }
            binding.root.setOnClickListener {
                menuController.goToMenuItemFragment(menuItem,binding.menuCartQuantityControlView.count)
            }
        }
    }
}