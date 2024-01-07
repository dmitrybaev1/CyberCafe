package ru.shawarma.clientapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.shawarma.core.data.entities.FirebaseTokenRequest
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.entities.OrderStatus
import ru.shawarma.core.data.repositories.OrderRepository
import javax.inject.Inject

@AndroidEntryPoint
class MessagingService : FirebaseMessagingService(), LifecycleOwner {

    @Inject
    lateinit var orderRepository: OrderRepository

    private lateinit var notificationManager: NotificationManager

    private lateinit var lifecycleRegistry: LifecycleRegistry

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override fun onCreate() {
        super.onCreate()

        notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }



    override fun onDestroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        super.onDestroy()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("TAG", "invoked")
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
        when(message.data["event"]){
            "orderStatusUpdate" -> {
                val order = gson.fromJson(message.data["payload"],OrderResponse::class.java)
                showOrderStatusNotification(order.status,order.id)
            }
            "orderCreate" -> {
                val order = gson.fromJson(message.data["payload"],OrderResponse::class.java)
                showOrderStatusNotification(order.status,order.id)
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d("TAG", token)
        super.onNewToken(token)
        lifecycleScope.launchWhenCreated {
            orderRepository.saveFirebaseToken(FirebaseTokenRequest(token))
        }
    }

    private fun showOrderStatusNotification(
        status: OrderStatus,
        orderId: Int,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        val notification = NotificationCompat.Builder(applicationContext, ORDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.order) + " №$orderId")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(false)
            .setContentIntent(createPendingIntentWithOrderId(orderId))
        when(status){
            OrderStatus.IN_QUEUE ->
                notification.setContentText(applicationContext.getString(R.string.status_in_queue))
            OrderStatus.COOKING ->
                notification.setContentText(applicationContext.getString(R.string.status_cooking))
            OrderStatus.READY ->
                notification.setContentText(applicationContext.getString(R.string.status_ready))
            OrderStatus.CLOSED ->
                notification.setContentText(applicationContext.getString(R.string.status_closed))
            OrderStatus.CANCELED ->
                notification.setContentText(applicationContext.getString(R.string.status_canceled))
        }
        notificationManager.notify(System.currentTimeMillis().toInt(),notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val name = applicationContext.getString(R.string.channel_name)
        val descriptionText = applicationContext.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(ORDER_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createPendingIntentWithOrderId(id: Int): PendingIntent {
        val navDeepLinkBuilder = NavDeepLinkBuilder(this).setGraph(R.navigation.main_nav_graph)
        return navDeepLinkBuilder.setDestination(
            ru.shawarma.order.R.id.order_nav_graph, bundleOf("orderId" to id)
        )
            .createPendingIntent()
    }

    companion object{
        const val ORDER_CHANNEL_ID = "ORDER_STATUS_CHANNEL"
        const val ORDER_NOTIFICATION_GROUP = "ORDER_GROUP"
    }

}