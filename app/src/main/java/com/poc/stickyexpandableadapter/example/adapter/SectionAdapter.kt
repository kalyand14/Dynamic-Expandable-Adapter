package com.poc.stickyexpandableadapter.example.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.poc.stickyexpandableadapter.R
import com.poc.stickyexpandableadapter.example.model.Section

class SectionAdapter(
    private val isPostSection: Boolean,
    private val onSectionClick: (
        (
        Section,
        Boolean
    ) -> Unit)? = null
) : ListAdapter<Section, SectionAdapter.SectionViewHolder>(SectionDiffCallback) {


    abstract class SectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: Section)
    }


    /* ViewHolder for displaying header. */
    class GroupViewHolder(view: View) : SectionViewHolder(view) {

        private val heading: TextView = itemView.findViewById(R.id.textview_group_name)
        private val description: TextView = itemView.findViewById(R.id.textview_group_description)
        private var currentItem: Section? = null

        override fun bind(section: Section) {
            currentItem = section as Section.Group
            heading.text = section.name
            description.text = section.description
        }
    }


    class GroupAssetClassViewHolder(
        view: View, private val isPostSection: Boolean, private val
        onSectionClick: ((Section, Boolean) -> Unit)? =
            null
    ) : SectionViewHolder(view) {

        private val heading: TextView = itemView.findViewById(R.id.textview_group_section_name)

        private var currentItem: Section? = null

        init {
            itemView.setOnClickListener {
                currentItem?.let {
                    onSectionClick?.let { invoke -> invoke(it, isPostSection) }
                }
            }
        }

        override fun bind(section: Section) {
            currentItem = section as Section.GroupedAssetClass
            heading.text = section.name
        }
    }

    class AssetClassViewHolder(
        view: View, private val isPostSection: Boolean, private val
        onSectionClick: ((Section, Boolean) -> Unit)? =
            null
    ) : SectionViewHolder(view) {

        private val heading: TextView = itemView.findViewById(R.id.textview_section_name)

        private var currentItem: Section? = null

        init {
            itemView.setOnClickListener {
                currentItem?.let {
                    onSectionClick?.let { invoke -> invoke(it, isPostSection) }
                }
            }
        }

        override fun bind(section: Section) {
            currentItem = section as Section.AssetClass
            heading.text = section.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        return when (viewType) {
            R.layout.group_layout -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.group_layout, parent, false)
                GroupViewHolder(view)
            }
            R.layout.group_section_last_layout,
            R.layout.group_section_layout -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(viewType, parent, false)
                GroupAssetClassViewHolder(view, isPostSection, onSectionClick)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.section_layout, parent, false)
                AssetClassViewHolder(view, isPostSection, onSectionClick)
            }
        }
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun isLastSectionInGroup(position: Int): Boolean {

        /*  if (position == itemCount - 1)
              return true

          val currentSection = getItem(position)
          val nextSection = getItem(position + 1)
          return (nextSection is Section.AssetClass)*/

        val section = getItem(position) as? Section.GroupedAssetClass
        return section?.isLastSectionInGroup ?: false
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Section.Group -> R.layout.group_layout
            is Section.GroupedAssetClass -> {
                return if (isLastSectionInGroup(position)) {
                    R.layout.group_section_last_layout
                } else {
                    R.layout.group_section_layout
                }
            }
            else -> R.layout.section_layout
        }
    }
}

object SectionDiffCallback : DiffUtil.ItemCallback<Section>() {
    override fun areItemsTheSame(oldItem: Section, newItem: Section): Boolean {
        return when {
            oldItem is Section.Group && newItem is Section.Group -> {
                oldItem == newItem
            }
            oldItem is Section.GroupedAssetClass && newItem is Section.GroupedAssetClass -> {
                oldItem == newItem
            }
            oldItem is Section.AssetClass && newItem is Section.AssetClass -> {
                oldItem == newItem
            }
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: Section, newItem: Section): Boolean {

        return when {
            oldItem is Section.Group && newItem is Section.Group -> {
                oldItem.id == newItem.id
            }
            oldItem is Section.GroupedAssetClass && newItem is Section.GroupedAssetClass -> {
                oldItem.id == newItem.id
            }
            oldItem is Section.AssetClass && newItem is Section.AssetClass -> {
                oldItem.id == newItem.id
            }
            else -> false
        }
    }
}