package com.example.interestdiscoveryapp

import com.google.gson.annotations.SerializedName

data class PolResponse(
    @SerializedName("query")
    val query: String
)