package com.poc.stickyexpandableadapter.example

import com.poc.stickyexpandableadapter.example.model.Item

object ItemDataSource {

    private val itemList = mutableListOf<Item>().apply {
        (1..(1..25).random())
            .toList().map { id ->
                add(
                    Item.Position(
                        id = id,
                        name = "${DataSource.getRandomString()}-$id",
                        amount = "${DataSource.getRandomAmount()}"
                    )
                )
            }
    }

    fun getItemList(pageIndex: Int = 0) = mutableListOf<Item>().apply {
        val startIndex = pageIndex * 100 + 1
        val endIndex = startIndex + 99
        (startIndex..(endIndex))
            .toList().map { id ->
                add(
                    Item.Position(
                        id = id,
                        name = "${DataSource.getRandomString()}-$id",
                        amount = "${DataSource.getRandomAmount()}"
                    )
                )
            }
        add(
            Item.LoadMore(
                id = -1,
                name = "LoadMore",
                nextPageIndex = pageIndex + 1
            )
        )

    }
}