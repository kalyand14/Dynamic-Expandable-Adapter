package com.poc.stickyexpandableadapter

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.poc.stickyexpandableadapter.example.ItemDataSource
import com.poc.stickyexpandableadapter.example.SectionDataSource
import com.poc.stickyexpandableadapter.example.adapter.ItemAdapter
import com.poc.stickyexpandableadapter.example.adapter.SectionAdapter
import com.poc.stickyexpandableadapter.example.model.Item
import com.poc.stickyexpandableadapter.example.model.Section
import com.poc.stickyexpandableadapter.example.recycleritemdecoration.ItemDecorations


class MainActivity : AppCompatActivity() {

    private val preSectionAdapter: SectionAdapter = SectionAdapter(false, this::onSectionSelected)
    private val postSectionAdapter: SectionAdapter = SectionAdapter(true, this::onSectionSelected)
    private val itemAdapter: ItemAdapter = ItemAdapter(this::onItemSelected)

    private lateinit var preRecycleViewer: RecyclerView
    private lateinit var expandedRecycleViewer: RecyclerView
    private lateinit var postRecycleViewer: RecyclerView

    private lateinit var nestedScrollView: NestedScrollView

    private fun onSectionSelected(section: Section, isPostSection: Boolean) {
        val stickContainer: LinearLayoutCompat = findViewById(R.id.stick_item)
        stickContainer.tag = "sticky"
        updatePreRecyclerView(section)
        updateStickyContainer(section, isPostSection)
        updatePostRecyclerView(section)
    }

    private fun onItemSelected(item: Item) {
        if (item is Item.LoadMore) {

            val newList = mutableListOf<Item>().apply {
                addAll(itemAdapter.currentList.toList())
                remove(last())
                addAll(
                    ItemDataSource.getItemList(
                        item
                            .nextPageIndex
                    )
                )
            }
            itemAdapter.submitList(newList)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nestedScrollView = findViewById(R.id.scrollView)
        bindPreRecycleViewer()
        bindExpandedRecycleViewer()
        bindPostRecycleViewer()
    }

    private fun bindPreRecycleViewer() {
        preRecycleViewer = findViewById(R.id.rvPreContainer)
        preRecycleViewer.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(getSectionRecycleViewerDecoration())
            adapter = preSectionAdapter
        }
        preSectionAdapter.submitList(SectionDataSource.getSectionFirstPart())
        preRecycleViewer.scheduleLayoutAnimation();
    }

    private fun bindExpandedRecycleViewer() {

        expandedRecycleViewer = findViewById(R.id.rvExpandedContainer)
        expandedRecycleViewer.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(getExpandedRecycleViewerDecoration())
            adapter = itemAdapter
        }
    }

    private fun bindPostRecycleViewer() {
        postRecycleViewer = findViewById(R.id.rvPostContainer)
        postRecycleViewer.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(getSectionRecycleViewerDecoration())
            adapter = postSectionAdapter
        }
    }

    private fun updatePreRecyclerView(section: Section) {
        preSectionAdapter.submitList(SectionDataSource.getSectionFirstPart(section))
    }

    private fun updateStickyContainer(section: Section, isPostSection: Boolean) {


        val stickContainer: LinearLayoutCompat = findViewById(R.id.stick_item)

        val bgDrawable = when (section) {
            is Section.AssetClass -> R.drawable.bg_top_radius
            else -> R.drawable.bg_no_radius
        }
        stickContainer.background = AppCompatResources.getDrawable(this, bgDrawable);

        stickContainer.visibility = View.VISIBLE

        val stickItem: AppCompatTextView = findViewById(R.id.textview_sticky_section_name)
        stickItem.text = when (section) {
            is Section.GroupedAssetClass -> section.name
            is Section.AssetClass -> section.name
            else -> ""
        }

        itemAdapter.submitList(ItemDataSource.getItemList(0))
        expandedRecycleViewer.scheduleLayoutAnimation()

        if (isPostSection) {
            expandedRecycleViewer.post {
                val y: Float =
                    expandedRecycleViewer.getY() + expandedRecycleViewer.getChildAt(0)
                        .getY()
                nestedScrollView.smoothScrollTo(0, y.toInt())
            }
        }

        stickContainer.setOnClickListener {
            stickContainer.tag = ""
            stickContainer.visibility = View.GONE

            postSectionAdapter.submitList(null)
            itemAdapter.submitList(null)


            val currentList = preSectionAdapter.currentList.toMutableList()
            val preEndIndex = currentList.size
            val newList = mutableListOf<Section>().apply {
                add(section)
                addAll(
                    SectionDataSource.getSectionSecondPart(section)
                )
            }
            currentList.addAll(preEndIndex, newList)
            //preSectionAdapter.notifyItemRangeInserted(preEndIndex, newList.size)
            //preRecycleViewer.scheduleLayoutAnimation()

            /* preRecycleViewer.postDelayed(
                 {
                     preRecycleViewer.smoothScrollToPosition(preEndIndex + 1)
                 }, 1000
             )*/

            preSectionAdapter.submitList(currentList)
        }
    }

    private fun updatePostRecyclerView(section: Section) {
        postSectionAdapter.submitList(SectionDataSource.getSectionSecondPart(section))
    }

    private fun getSectionRecycleViewerDecoration(): ItemDecoration {
        return ItemDecorations.vertical(this)
            .type(
                R.layout.section_layout,
                R.drawable.shape_decoration_default
            )
            .type(
                R.layout.group_section_last_layout,
                R.drawable.shape_decoration_default
            )
            .create()
    }

    private fun getExpandedRecycleViewerDecoration(): ItemDecoration {
        return ItemDecorations.vertical(this)
            .last(R.drawable.shape_decoration_default)
            .create()
    }


}