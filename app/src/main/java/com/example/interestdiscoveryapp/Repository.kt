package com.example.interestdiscoveryapp

class Repository {
    suspend fun getPols(): PolResponse {
        return RetrofitInstances.api.getPols()
    }
}