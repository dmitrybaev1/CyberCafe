package ru.shawarma.core.data.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.google.gson.GsonBuilder
import com.microsoft.signalr.GsonHubProtocol
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleSource
import io.reactivex.rxjava3.functions.Supplier
import kotlinx.coroutines.*
import ru.shawarma.core.data.R
import ru.shawarma.core.data.entities.OrderResponse
import ru.shawarma.core.data.entities.OrderStatus
import ru.shawarma.core.data.utils.TokenManager

@HiltWorker
class OrderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val tokenManager: TokenManager
): CoroutineWorker(context,params) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
    private var isEnd = false

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            try {
                val orderId = inputData.getInt("orderId",0)
                val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
                val hubConnection = HubConnectionBuilder
                    .create("http://10.0.2.2:5029/notifications/client/orders")
                    .withHubProtocol(GsonHubProtocol(gson))
                    .withAccessTokenProvider(Single.defer {
                        runBlocking {
                            Single.just(tokenManager.getAuthData().accessToken)
                        }
                    }).build()
                hubConnection.on("Notify", {message ->
                    Log.d("orderWorker",message.toString())
                    runBlocking {
                        //проверка на orderId, за которым будем следить
                        setForeground(createForegroundInfo(OrderStatus.CLOSED,orderId))
                        isEnd = true
                    }
                }, OrderResponse::class.java)
                hubConnection.start().blockingAwait()
                Log.d("connection",hubConnection.connectionState.name)
                setForeground(createForegroundInfo(OrderStatus.IN_QUEUE,orderId))
                while(!isEnd){
                    delay(5000)
                }
                hubConnection.stop().blockingAwait()
                Result.success()
            }
            catch (e: Exception){
                e.printStackTrace()
                Result.retry()
            }
        }

    private fun createForegroundInfo(
        status: OrderStatus,
        orderId: Int
    ): ForegroundInfo {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        return ForegroundInfo(orderId,buildNotification(status, orderId))
    }
    private fun buildNotification(
        status: OrderStatus,
        orderId: Int,
    ): Notification {
        val notification = NotificationCompat.Builder(applicationContext, ORDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(applicationContext.getString(R.string.order) + " №$orderId")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setGroup(ORDER_NOTIFICATION_GROUP)
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
        return notification.build()
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
    companion object{
        const val ORDER_CHANNEL_ID = "ORDER_STATUS_CHANNEL"
        const val ORDER_NOTIFICATION_ID_BASE = "ORDER_ID_"
        const val ORDER_NOTIFICATION_GROUP = "ORDER_GROUP"
    }
}