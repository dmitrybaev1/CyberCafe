package ru.shawarma.core.data

import android.app.PendingIntent

interface OrderFragmentPendingIntentCreator {
    fun createPendingIntentWithOrderId(id: Int): PendingIntent
}