package com.example.interestdiscoveryapp

import retrofit2.http.GET


interface Services {

    @GET("api.php?action=query&format=json&prop=coordinates&titles=Wikimedia%20Foundation")
    suspend fun getPols(): PolResponse

}