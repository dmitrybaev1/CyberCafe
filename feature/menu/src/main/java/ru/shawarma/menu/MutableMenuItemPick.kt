package ru.shawarma.menu

import androidx.databinding.ObservableBoolean

data class MutableMenuItemPick(
    val isPicked: ObservableBoolean = ObservableBoolean(false),
    var count: Int = 0
)
