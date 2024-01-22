package ru.shawarma.menu.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.shawarma.menu.R
import ru.shawarma.menu.databinding.MenuLoadStateBinding

class MenuLoadStateAdapter(
    private val retry: () -> Unit
): LoadStateAdapter<MenuLoadStateAdapter.MenuLoadStateViewHolder>() {

    class MenuLoadStateViewHolder(
        private val binding: MenuLoadStateBinding,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState){
            val isError = loadState is LoadState.Error
            binding.menuErrorImageView.isVisible = isError
            binding.menuErrorTextView.isVisible = isError
            binding.menuRetryButton.isVisible = isError
            binding.menuLoadingProgressBar.isVisible = loadState is LoadState.Loading

            binding.menuRetryButton.setOnClickListener {
                retry()
            }
        }
    }

    override fun onBindViewHolder(holder: MenuLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): MenuLoadStateViewHolder {
        val binding = MenuLoadStateBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MenuLoadStateViewHolder(binding, retry)
    }

}