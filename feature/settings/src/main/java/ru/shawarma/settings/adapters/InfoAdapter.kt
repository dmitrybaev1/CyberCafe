package ru.shawarma.settings.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.shawarma.settings.R
import ru.shawarma.settings.databinding.InfoItemBinding
import ru.shawarma.settings.entities.InfoItem

class InfoAdapter(private val list: List<InfoItem>) : RecyclerView.Adapter<InfoAdapter.InfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val binding = DataBindingUtil.inflate<InfoItemBinding>(LayoutInflater.from(parent.context),
            R.layout.info_item,parent,false)
        return InfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    class InfoViewHolder(private val binding: InfoItemBinding) : ViewHolder(binding.root){
        fun bind(infoItem: InfoItem){
            binding.infoItem = infoItem
        }
    }
}