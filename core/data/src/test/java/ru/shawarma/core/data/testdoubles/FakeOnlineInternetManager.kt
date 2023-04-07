package ru.shawarma.core.data.testdoubles

import ru.shawarma.core.data.managers.InternetManager

class FakeOnlineInternetManager: InternetManager {
    override fun isOnline(): Boolean = true
}