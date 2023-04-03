package ru.shawarma.core.data.utils

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.shawarma.core.data.services.AuthService
import ru.shawarma.core.data.services.MenuService
import ru.shawarma.core.data.services.OrderService
import java.util.*

enum class FeatureApi{
    AUTH,MENU,ORDER
}

object ApplicationRetrofit {

    private var authInstance: Retrofit? = null
    private var menuInstance: Retrofit? = null
    private var orderInstance: Retrofit? = null

    fun getInstance(api: FeatureApi = FeatureApi.AUTH): Retrofit{
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor{chain ->
                val original: Request = chain.request()
                val request: Request = original.newBuilder()
                    .header("Accept-Language", Locale.getDefault().toLanguageTag())
                    .method(original.method(), original.body())
                    .build()
                chain.proceed(request)
            }.build()
        when (api) {
            FeatureApi.AUTH -> {
                return authInstance ?: run {
                    val builder = Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("http://10.0.2.2:5236/api/v1/")
                        .client(client)
                    val retrofit = builder.build()
                    authInstance = retrofit
                    retrofit
                }
            }
            FeatureApi.MENU -> {
                return menuInstance ?: run {
                    val builder = Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("http://10.0.2.2:5291/api/v1/")
                        .client(client)
                    val retrofit = builder.build()
                    menuInstance = retrofit
                    retrofit
                }
            }
            FeatureApi.ORDER -> {
                return orderInstance ?: run {
                    val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
                    val builder = Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .baseUrl("http://10.0.2.2:5029/api/v1/")
                        .client(client)
                    val retrofit = builder.build()
                    orderInstance = retrofit
                    retrofit
                }
            }
        }
    }

    val authService: AuthService by lazy {
        getInstance().create(AuthService::class.java)
    }

    val menuService: MenuService by lazy {
        getInstance(FeatureApi.MENU).create(MenuService::class.java)
    }

    val orderService: OrderService by lazy {
        getInstance(FeatureApi.ORDER).create(OrderService::class.java)
    }
}