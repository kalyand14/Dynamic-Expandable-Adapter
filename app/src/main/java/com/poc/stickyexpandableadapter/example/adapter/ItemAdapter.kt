package com.poc.stickyexpandableadapter.example.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.poc.stickyexpandableadapter.R
import com.poc.stickyexpandableadapter.example.model.Item


const val VIEW_TYPE_ITEM: Int = 1
const val VIEW_TYPE_LOAD_MORE: Int = 2

class ItemAdapter(private val onItemClick: ((Item) -> Unit)? = null) :
    ListAdapter<Item, ItemAdapter.ItemViewHolder>(ItemDiffCallback) {

    abstract class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: Item)
    }

    /* ViewHolder for displaying header. */
    class PositionViewHolder(view: View, private val onItemClick: ((Item) -> Unit)? = null) :
        ItemViewHolder(view) {

        lateinit var item: Item.Position

        private val name: TextView = itemView.findViewById(R.id.textview_item_name)
        private val amount: TextView = itemView.findViewById(R.id.textview_item_amount)
        private var currentItem: Item? = null

        init {
            itemView.setOnClickListener {
                currentItem?.let {
                    onItemClick?.let { invoke -> invoke(it) }
                }
            }
        }

        override fun bind(item: Item) {
            currentItem = item as Item.Position
            name.text = item.name
            amount.text = item.amount
        }
    }

    class LoadMoreViewHolder(view: View, private val onItemClick: ((Item) -> Unit)? = null) :
        ItemViewHolder(view) {

        private val btnLoadMore: TextView = itemView.findViewById(R.id.btn_loadmore)
        private var currentItem: Item? = null

        init {
            btnLoadMore.setOnClickListener {
                currentItem?.let {
                    onItemClick?.let { invoke -> invoke(it) }
                }
            }
        }

        override fun bind(item: Item) {
            currentItem = item
            btnLoadMore.text = "SeeMore"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
                PositionViewHolder(view, onItemClick)
            }
            VIEW_TYPE_LOAD_MORE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.loadmore_layout, parent, false)
                LoadMoreViewHolder(view, onItemClick)
            }
            else -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
                PositionViewHolder(view, onItemClick)
            }
        }

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1)
            VIEW_TYPE_LOAD_MORE
        else
            VIEW_TYPE_ITEM
    }

}

object ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return when {
            oldItem is Item.Position && newItem is Item.Position -> {
                oldItem == newItem
            }
            oldItem is Item.LoadMore && newItem is Item.LoadMore -> {
                oldItem == newItem
            }
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return when {
            oldItem is Item.Position && newItem is Item.Position -> {
                oldItem.id == newItem.id
            }
            oldItem is Item.LoadMore && newItem is Item.LoadMore -> {
                oldItem.id == newItem.id
            }
            else -> false
        }

    }
}