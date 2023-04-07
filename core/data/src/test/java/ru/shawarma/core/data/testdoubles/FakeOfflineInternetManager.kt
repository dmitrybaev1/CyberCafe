package ru.shawarma.core.data.testdoubles

import ru.shawarma.core.data.managers.InternetManager

class FakeOfflineInternetManager: InternetManager {
    override fun isOnline(): Boolean = false
}