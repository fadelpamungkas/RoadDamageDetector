package com.example.roaddamagedetector.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.roaddamagedetector.data.local.RoadDataEntity
import com.example.roaddamagedetector.databinding.ItemRoadBinding

class RoadAdapter : RecyclerView.Adapter<RoadAdapter.CardViewViewHolder>() {

    private var listData = ArrayList<RoadDataEntity>()
    var onItemClick: ((RoadDataEntity) -> Unit)? = null

    fun setData(data: List<RoadDataEntity>) {
        listData.clear()
        listData.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewViewHolder {
        val binding = ItemRoadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount(): Int = listData.size

    inner class CardViewViewHolder(private val binding: ItemRoadBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RoadDataEntity){
            with(binding){
                Glide.with(itemView.context)
                    .load(data.photo)
                    .into(ivPoster)
                tvTitle.text = data.username
                tvAddress.text = data.city
                tvStatus.text = data.date

            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(listData[adapterPosition])
            }
        }
    }

}