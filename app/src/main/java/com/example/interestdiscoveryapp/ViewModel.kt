package com.example.interestdiscoveryapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class ViewModel(private val repository: Repository) : ViewModel() {
    val polResponse: MutableLiveData<PolResponse> = MutableLiveData()
    fun getPols() {
        viewModelScope.launch {
            val response = repository.getPols()
            polResponse.value = response
        }
    }
}