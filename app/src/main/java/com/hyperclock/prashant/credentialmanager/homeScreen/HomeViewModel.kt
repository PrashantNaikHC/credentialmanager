package com.hyperclock.prashant.credentialmanager.homeScreen


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hyperclock.prashant.credentialmanager.database.CredentialDao

class HomeViewModel(
    val database : CredentialDao,
    application : Application
) : AndroidViewModel(application){

    val creds =database.getAllCredentials()

    //livedata for the navigation
    private val _navigateToSelectedCredential = MutableLiveData<Long>()
    val navigateToSelectedCredential: LiveData<Long>
         get() = _navigateToSelectedCredential

    fun onCredentialClicked(id : Long){
        _navigateToSelectedCredential.value = id
    }
}