package ru.shawarma.menu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.shawarma.menu.MenuController
import ru.shawarma.menu.R
import ru.shawarma.menu.databinding.CartItemBinding
import ru.shawarma.menu.entities.CartMenuItem

class CartAdapter(
    private val menuController: MenuController
) : RecyclerView.Adapter<CartAdapter.CartItemViewHolder>() {

    private var list: List<CartMenuItem>? = null

    fun setList(list: List<CartMenuItem>){
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding = DataBindingUtil.inflate<CartItemBinding>(
            LayoutInflater.from(parent.context), R.layout.cart_item,parent,false
        )
        return CartItemViewHolder(binding,menuController)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val cartItem = list?.get(position)
        cartItem?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = list?.size ?: 0

    class CartItemViewHolder(
        private val binding: CartItemBinding,
        private val menuController: MenuController
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(cartItem: CartMenuItem){
            binding.cartItem = cartItem
            binding.cartCartQuantityControlView.count = cartItem.count
            binding.cartCartQuantityControlView.setOnMinusClickListener {
                menuController.removeFromCart(cartItem.menuItem)
            }
            binding.cartCartQuantityControlView.setOnPlusClickListener {
                menuController.addToCart(cartItem.menuItem)
            }
        }
    }
}