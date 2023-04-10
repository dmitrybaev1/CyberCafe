package ru.shawarma.settings.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.shawarma.core.data.entities.OrderStatus
import ru.shawarma.settings.R
import ru.shawarma.settings.SettingsController
import ru.shawarma.settings.databinding.OrderErrorBinding
import ru.shawarma.settings.databinding.OrderItemBinding
import ru.shawarma.settings.databinding.OrderLoadingBinding
import ru.shawarma.settings.entities.OrderElement

class OrdersAdapter(
    private val settingsController: SettingsController
) : ListAdapter<OrderElement, ViewHolder>(OrderItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when(viewType){
            R.layout.order_item -> {
                val binding = DataBindingUtil.inflate<OrderItemBinding>(
                    LayoutInflater.from(parent.context), viewType, parent,false)
                return OrderItemViewHolder(binding,settingsController)
            }
            R.layout.order_error -> {
                val binding = DataBindingUtil.inflate<OrderErrorBinding>(
                    LayoutInflater.from(parent.context), viewType, parent,false)
                return OrderErrorViewHolder(binding,settingsController)
            }
            R.layout.order_loading -> {
                val binding = DataBindingUtil.inflate<OrderLoadingBinding>(
                    LayoutInflater.from(parent.context), viewType, parent,false)
                return OrderLoadingViewHolder(binding)
            }
            else -> throw ClassCastException("Unknown viewType $viewType")
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(holder is OrderItemViewHolder){
            val orderItem = getItem(position) as OrderElement.OrderItem
            holder.bind(orderItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is OrderElement.OrderItem -> R.layout.order_item
            is OrderElement.Error -> R.layout.order_error
            is OrderElement.Loading -> R.layout.order_loading
            else -> throw IllegalStateException()
        }
    }

    class OrderErrorViewHolder(binding: OrderErrorBinding, settingsController: SettingsController
    ): ViewHolder(binding.root){
        init{
            binding.ordersRetryButton.setOnClickListener {
                settingsController.reloadOrders()
            }
        }
    }

    class OrderLoadingViewHolder(binding: OrderLoadingBinding): ViewHolder(binding.root)

    class OrderItemViewHolder(
        private val binding: OrderItemBinding,
        private val settingsController: SettingsController
    ) : ViewHolder(binding.root){
        fun bind(orderItem: OrderElement.OrderItem){
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