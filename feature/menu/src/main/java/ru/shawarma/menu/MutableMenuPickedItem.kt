package ru.shawarma.menu

import androidx.databinding.ObservableBoolean

data class MutableMenuPickedItem(
    val isPicked: ObservableBoolean = ObservableBoolean(false),
    var count: Int = 0
)
