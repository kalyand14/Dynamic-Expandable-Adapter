package com.poc.stickyexpandableadapter.example.model

sealed class Section {
    data class Group(
        val groupId: Int,
        val id: Int,
        val name: String,
        val description: String
    ) : Section()

    data class GroupedAssetClass(
        val groupId: Int,
        val groupIndex: Int,
        val isLastSectionInGroup: Boolean = false,
        val id: Int,
        val name: String,
        val amount: String
    ) : Section()

    data class AssetClass(
        val groupId: Int,
        val id: Int,
        val name: String,
        val amount: String
    ) : Section()
}

