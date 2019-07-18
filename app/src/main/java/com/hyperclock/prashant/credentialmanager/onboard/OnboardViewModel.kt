package com.hyperclock.prashant.credentialmanager.onboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OnboardViewModel: ViewModel(){

    //livedata for the password
    private val _entryPassword = MutableLiveData<String>()
    val entryPassword : LiveData<String>
    get() = _entryPassword


}