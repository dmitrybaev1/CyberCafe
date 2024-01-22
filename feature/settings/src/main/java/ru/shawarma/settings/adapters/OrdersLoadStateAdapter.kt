package ru.shawarma.settings.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.shawarma.settings.databinding.OrderLoadStateBinding

class OrdersLoadStateAdapter(
    private val retry: () -> Unit
): LoadStateAdapter<OrdersLoadStateAdapter.OrdersLoadStateViewHolder>() {

    class OrdersLoadStateViewHolder(
        private val binding: OrderLoadStateBinding,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState){
            val isError = loadState is LoadState.Error
            binding.orderErrorImageView.isVisible = isError
            binding.orderErrorTextView.isVisible = isError
            binding.orderRetryButton.isVisible = isError
            binding.orderLoadingProgressBar.isVisible = loadState is LoadState.Loading

            binding.orderRetryButton.setOnClickListener {
                retry()
            }
        }
    }

    override fun onBindViewHolder(holder: OrdersLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): OrdersLoadStateViewHolder {
        val binding = OrderLoadStateBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OrdersLoadStateViewHolder(binding, retry)
    }
}