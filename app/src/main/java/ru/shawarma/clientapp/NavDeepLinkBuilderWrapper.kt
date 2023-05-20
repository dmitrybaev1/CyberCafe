package ru.shawarma.clientapp

import android.app.PendingIntent
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import ru.shawarma.core.data.OrderFragmentPendingIntentCreator

class NavDeepLinkBuilderWrapper(private var navDeepLinkBuilder: NavDeepLinkBuilder)
    : OrderFragmentPendingIntentCreator {

    fun setupOrderFragmentPendingIntent(): OrderFragmentPendingIntentCreator{
        navDeepLinkBuilder = navDeepLinkBuilder.setGraph(R.navigation.main_nav_graph)
        return this
    }

    override fun createPendingIntentWithOrderId(id: Int): PendingIntent {
        return navDeepLinkBuilder.setDestination(
            ru.shawarma.order.R.id.order_nav_graph, bundleOf("orderId" to id))
            .createPendingIntent()
    }

}