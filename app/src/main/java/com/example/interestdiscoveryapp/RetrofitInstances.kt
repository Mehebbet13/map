package com.example.interestdiscoveryapp

import com.example.interestdiscoveryapp.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstances {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api:Services by lazy {
        retrofit.create(Services::class.java)
    }
}