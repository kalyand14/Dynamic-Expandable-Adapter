package com.poc.stickyexpandableadapter.example.model

sealed class Item {
    data class Position(
        val id: Int,
        val name: String,
        val amount: String
    ) : Item()

    data class LoadMore(
        val id: Int,
        val name: String,
        val nextPageIndex: Int
    ) : Item()
}




