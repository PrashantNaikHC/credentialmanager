package com.hyperclock.prashant.credentialmanager.homeScreen


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hyperclock.prashant.credentialmanager.database.Credential

class HomeViewModel :ViewModel(){

    //livedata for the navigation
    private val _navigateToSelectedCredential = MutableLiveData<Credential>()
    val navigateToSelectedCredential: LiveData<Credential>
         get() = _navigateToSelectedCredential



}