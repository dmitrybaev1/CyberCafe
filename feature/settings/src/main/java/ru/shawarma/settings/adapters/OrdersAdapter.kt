package ru.shawarma.settings.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.shawarma.core.data.entities.OrderStatus
import ru.shawarma.settings.R
import ru.shawarma.settings.SettingsController
import ru.shawarma.settings.databinding.OrderItemBinding
import ru.shawarma.settings.entities.OrderItem

class OrdersAdapter(
    private val settingsController: SettingsController
) : PagingDataAdapter<OrderItem, OrdersAdapter.OrderItemViewHolder>(OrderItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = OrderItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OrderItemViewHolder(binding,settingsController)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val orderItem = getItem(position)
        if (orderItem != null) {
            holder.bind(orderItem)
        }
    }


    class OrderItemViewHolder(
        private val binding: OrderItemBinding,
        private val settingsController: SettingsController
    ) : ViewHolder(binding.root){
        fun bind(orderItem: OrderItem){
            binding.orderItem = orderItem
            val context = binding.root.context
            when(orderItem.status){
                OrderStatus.IN_QUEUE -> {
                    binding.orderItemStatusTextView.text = context.getString(R.string.in_queue)
                    binding.orderItemIndicatorImageView.setImageResource(
                        ru.shawarma.core.ui.R.drawable.status_indicator_yellow
                    )
                }
                OrderStatus.COOKING -> {
                    binding.orderItemStatusTextView.text = context.getString(R.string.cooking)
                    binding.orderItemIndicatorImageView.setImageResource(
                        ru.shawarma.core.ui.R.drawable.status_indicator_yellow
                    )
                }
                OrderStatus.READY -> {
                    binding.orderItemStatusTextView.text = context.getString(R.string.ready)
                    binding.orderItemIndicatorImageView.setImageResource(
                        ru.shawarma.core.ui.R.drawable.status_indicator_green
                    )
                }
                OrderStatus.CLOSED -> {
                    binding.orderItemStatusTextView.text = context.getString(R.string.closed)
                    binding.orderItemIndicatorImageView.setImageResource(
                        ru.shawarma.core.ui.R.drawable.status_indicator_green
                    )
                }
                OrderStatus.CANCELED -> {
                    binding.orderItemStatusTextView.text = context.getString(R.string.canceled)
                    binding.orderItemIndicatorImageView.setImageResource(
                        ru.shawarma.core.ui.R.drawable.status_indicator_red
                    )
                }
            }
            binding.root.setOnClickListener {
                settingsController.goToOrder(orderItem.id)
            }
        }
    }
}