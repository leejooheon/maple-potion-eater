package com.jooheon.maple.potion.main

sealed class TabNavigation(val title: String, val index: Int) {
    data object Display: TabNavigation("Display", 0)
    data object Setting: TabNavigation("Setting", 1)

    companion object {
        val Tabs = listOf(Display, Setting).sortedBy { it.index }
        val PageNumber = Tabs.size
        const val InitialPage = 0
    }
}