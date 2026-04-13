package com.example.indonesianevents

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.database.EventEntity
import com.example.indonesianevents.databinding.EventsMenuBinding

class EventAdapter(private val onItemClick: (EventEntity) -> Unit) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    private var listEvent = listOf<EventEntity>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newListData: List<EventEntity>) {
        listEvent = newListData
        notifyDataSetChanged()
    }

    class EventViewHolder(val binding: EventsMenuBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = EventsMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = listEvent[position]
        holder.binding.tvName.text = event.name
        holder.binding.tvCategory.text = event.category
        holder.binding.tvSummary.text = event.summary

        Glide.with(holder.itemView.context)
            .load(event.imageLogo)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.binding.ivEvent)

        holder.itemView.setOnClickListener {
            onItemClick(event)
        }
    }

    override fun getItemCount(): Int = listEvent.size
}
