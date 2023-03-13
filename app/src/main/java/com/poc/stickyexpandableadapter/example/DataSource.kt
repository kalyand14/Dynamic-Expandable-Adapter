package com.poc.stickyexpandableadapter.example

object DataSource {

    fun getRandomAmount() = (0..10000).random()

    fun getRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..10)
            .map { allowedChars.random() }
            .joinToString("")
    }
}