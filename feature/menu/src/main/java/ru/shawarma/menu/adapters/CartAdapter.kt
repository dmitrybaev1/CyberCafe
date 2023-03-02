package ru.shawarma.menu.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.shawarma.menu.entities.CartMenuItem
import ru.shawarma.menu.MenuController
import ru.shawarma.menu.databinding.CartItemBinding

class CartAdapter(
    private val list: List<CartMenuItem>,
    private val menuController: MenuController
) : RecyclerView.Adapter<CartAdapter.CartItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding = DataBindingUtil.inflate<CartItemBinding>(
            LayoutInflater.from(parent.context),viewType,parent,false
        )
        return CartItemViewHolder(binding,menuController)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val cartItem = list[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int = list.size

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