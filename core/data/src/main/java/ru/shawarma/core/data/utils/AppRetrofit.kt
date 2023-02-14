package ru.shawarma.core.data.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.shawarma.core.data.services.AuthService

object AppRetrofit {

     val instance: Retrofit by lazy {
         Retrofit.Builder()
             .baseUrl("https://localhost:7236/api/v1/")
             .addConverterFactory(GsonConverterFactory.create())
             .build()
     }

    val authService: AuthService by lazy {
        instance.create(AuthService::class.java)
    }
}