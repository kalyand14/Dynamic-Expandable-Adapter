package com.poc.stickyexpandableadapter.example.recycleritemdecoration

import android.content.Context

internal object ItemDecorations {
    fun vertical(context: Context?): VerticalItemDecoration.Builder {
        return VerticalItemDecoration.Builder(context!!)
    }
}