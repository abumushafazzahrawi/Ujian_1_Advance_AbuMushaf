package com.example.helper

import androidx.recyclerview.widget.DiffUtil
import com.example.database.EventEntity

class NoteDiffCallBack (private val oldEventList: List<EventEntity>, private
val newEventList: List<EventEntity>) :
DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldEventList.size
    override fun getNewListSize(): Int = newEventList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldEventList[oldItemPosition].id == oldEventList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEvent = oldEventList[oldItemPosition]
        val newEvent = newEventList[newItemPosition]

        return oldEvent.name == newEvent.name && oldEvent.description == newEvent.description
    }
}