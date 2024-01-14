package ru.shawarma.menu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.shawarma.core.ui.loadImage
import ru.shawarma.menu.MenuController
import ru.shawarma.menu.R
import ru.shawarma.menu.databinding.CartItemBinding
import ru.shawarma.menu.entities.CartMenuItem

class CartAdapter(
    private val menuController: MenuController
) : ListAdapter<CartMenuItem, CartAdapter.CartItemViewHolder>(CartMenuItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding = DataBindingUtil.inflate<CartItemBinding>(
            LayoutInflater.from(parent.context), R.layout.cart_item,parent,false
        )
        return CartItemViewHolder(binding,menuController)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val cartItem = getItem(position)
        cartItem?.let { holder.bind(it) }
    }

    class CartItemViewHolder(
        private val binding: CartItemBinding,
        private val menuController: MenuController
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(cartItem: CartMenuItem){
            binding.cartItem = cartItem
            binding.cartCartQuantityControlView.count = cartItem.count
            cartItem.menuItem.imageUrl?.let { binding.cartItemImageView.loadImage(it,binding.root.context) }
            binding.cartCartQuantityControlView.setOnMinusClickListener {
                menuController.removeFromCart(cartItem.menuItem)
            }
            binding.cartCartQuantityControlView.setOnPlusClickListener {
                menuController.addToCart(cartItem.menuItem)
            }
        }
    }
}