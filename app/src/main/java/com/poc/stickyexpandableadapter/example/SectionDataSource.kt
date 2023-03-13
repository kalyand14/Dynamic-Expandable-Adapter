package com.poc.stickyexpandableadapter.example

import com.poc.stickyexpandableadapter.example.model.Section

object SectionDataSource {

    private val sectionList = mutableListOf<Section>().apply {
        add(
            Section.Group(
                groupId = 1,
                id = 1,
                name = "Total Cash",
                description = "Total Cash Description"
            )
        )
        add(
            Section.GroupedAssetClass(
                groupId = 1,
                groupIndex = 0,
                id = 2,
                name = "Investment Cash",
                amount = "2342.23"
            )
        )
        add(
            Section.GroupedAssetClass(
                groupId = 1,
                groupIndex = 1,
                id = 2,
                name = "Fixed Cash",
                amount = "234.15"
            )
        )
        add(
            Section.GroupedAssetClass(
                groupId = 1,
                groupIndex = 2,
                id = 3,
                isLastSectionInGroup = true,
                name = "Normal Cash",
                amount = "6324.52"
            )
        )

        /* (4..(0..50).random())
             .toList().map { id ->
                 add(
                     Section.AssetClass(
                         groupId = 2,
                         id = id,
                         name = "section $id",
                         amount = "${DataSource.getRandomAmount()}"
                     )
                 )
             }*/


        add(
            Section.AssetClass(
                groupId = 2,
                id = this.size - 1,
                name = "Equities",
                amount = "${DataSource.getRandomAmount()}"
            )
        )

        add(
            Section.AssetClass(
                groupId = 2,
                id = this.size - 1,
                name = "Fixed Income",
                amount = "${DataSource.getRandomAmount()}"
            )
        )

        add(
            Section.AssetClass(
                groupId = 2,
                id = this.size - 1,
                name = "Commodities",
                amount = "${DataSource.getRandomAmount()}"
            )
        )

        add(
            Section.Group(
                groupId = 3,
                id = this.size - 1,
                name = "Other Cash",
                description = "Other Cash Description"
            )
        )
        add(
            Section.GroupedAssetClass(
                groupId = 3,
                groupIndex = 0,
                id = this.size - 1,
                isLastSectionInGroup = true,
                name = "Other Cash 1",
                amount = "34534.34"
            )
        )

        add(
            Section.AssetClass(
                groupId = 4,
                id = this.size - 1,
                name = "Liabilities",
                amount = "${DataSource.getRandomAmount()}"
            )
        )

        add(
            Section.AssetClass(
                groupId = 4,
                id = this.size - 1,
                name = "Escrow",
                amount = "${DataSource.getRandomAmount()}"
            )
        )

    }

    fun getSectionFirstPart(section: Section? = null):  MutableList<Section> {
        val splitAt = if (section != null) sectionList.indexOf(section) else sectionList.size
        return sectionList.subList(0, splitAt)
    }

    fun getSectionSecondPart(section: Section): MutableList<Section> {
        val splitAt = sectionList.indexOf(section)
        return sectionList.subList(splitAt + 1, sectionList.size)
    }

}