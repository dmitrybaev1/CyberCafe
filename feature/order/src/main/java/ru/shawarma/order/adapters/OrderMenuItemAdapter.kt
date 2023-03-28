package ru.shawarma.order.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.shawarma.order.R
import ru.shawarma.order.databinding.OrderMenuItemBinding
import ru.shawarma.order.entities.OrderMenuItem

class OrderMenuItemAdapter(
    private val list: List<OrderMenuItem>
) : RecyclerView.Adapter<OrderMenuItemAdapter.OrderMenuItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderMenuItemViewHolder {
        val binding = DataBindingUtil.inflate<OrderMenuItemBinding>(
            LayoutInflater.from(parent.context), R.layout.order_menu_item,parent,false)
        return OrderMenuItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderMenuItemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    class OrderMenuItemViewHolder(private val binding: OrderMenuItemBinding): ViewHolder(binding.root){
        fun bind(orderMenuItem: OrderMenuItem){
            binding.orderMenuItem = orderMenuItem
        }
    }
}