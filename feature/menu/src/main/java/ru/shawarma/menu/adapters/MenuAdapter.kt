package ru.shawarma.menu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.shawarma.core.ui.loadImage
import ru.shawarma.menu.MenuController
import ru.shawarma.menu.R
import ru.shawarma.menu.databinding.MenuErrorBinding
import ru.shawarma.menu.databinding.MenuHeaderBinding
import ru.shawarma.menu.databinding.MenuItemBinding
import ru.shawarma.menu.databinding.MenuLoadingBinding
import ru.shawarma.menu.entities.MenuElement

class MenuAdapter(
    private val menuController: MenuController
) : ListAdapter<MenuElement, ViewHolder>(MenuItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(viewType){
            R.layout.menu_item -> {
                val binding = DataBindingUtil.inflate<MenuItemBinding>(
                    LayoutInflater.from(parent.context),viewType,parent,false
                )
                MenuItemViewHolder(binding,menuController)
            }
            R.layout.menu_header -> {
                val binding = DataBindingUtil.inflate<MenuHeaderBinding>(
                    LayoutInflater.from(parent.context),viewType,parent,false
                )
                MenuHeaderViewHolder(binding)
            }
            R.layout.menu_error -> {
                val binding = DataBindingUtil.inflate<MenuErrorBinding>(
                    LayoutInflater.from(parent.context),viewType,parent,false
                )
                MenuErrorViewHolder(binding,menuController)
            }
            R.layout.menu_loading -> {
                val binding = DataBindingUtil.inflate<MenuLoadingBinding>(
                    LayoutInflater.from(parent.context),viewType,parent,false
                )
                MenuLoadingViewHolder(binding)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(holder){
            is MenuItemViewHolder -> {
                val menuItem = getItem(position) as MenuElement.MenuItem
                holder.bind(menuItem)
            }
            is MenuHeaderViewHolder -> {
                val headerItem = getItem(position) as MenuElement.Header
                holder.bind(headerItem)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is MenuElement.MenuItem -> R.layout.menu_item
            is MenuElement.Header -> R.layout.menu_header
            is MenuElement.Error -> R.layout.menu_error
            is MenuElement.Loading -> R.layout.menu_loading
            else -> throw IllegalStateException()
        }
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
            binding.menuCartQuantityControlView.setOnMinusClickListener { value ->
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

    class MenuHeaderViewHolder(private val binding: MenuHeaderBinding): ViewHolder(binding.root){
        fun bind(headerItem: MenuElement.Header){
            binding.headerItem = headerItem
        }
    }

    class MenuErrorViewHolder(binding: MenuErrorBinding,menuController: MenuController
    ): ViewHolder(binding.root){
        init {
            binding.menuRetryButton.setOnClickListener { menuController.reloadMenu() }
        }
    }

    class MenuLoadingViewHolder(binding: MenuLoadingBinding): ViewHolder(binding.root)
}