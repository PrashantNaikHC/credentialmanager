package com.hyperclock.prashant.credentialmanager.homeScreen


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hyperclock.prashant.credentialmanager.database.Credential
import com.hyperclock.prashant.credentialmanager.database.CredentialDao
import kotlinx.coroutines.*

class HomeViewModel(val database : CredentialDao,application : Application) : AndroidViewModel(application){

    val creds =database.getAllCredentials()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //private var viewModelJob = Job()
    //private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var currentCred = MutableLiveData<Credential?>()

    //livedata for the navigation
    private val _navigateToSelectedCredential = MutableLiveData<Long>()

    val navigateToSelectedCredential: LiveData<Long>
         get() = _navigateToSelectedCredential

    fun onCredentialClicked(id : Long){
        _navigateToSelectedCredential.value = id
    }

    fun clearAllCred(){
        uiScope.launch {
            deleteAll()
        }
    }

    suspend fun deleteAll(){
        withContext(Dispatchers.IO){
            database.clear()
        }
    }

    fun findCredential(text: String) : List<Credential>{
        lateinit var values : List<Credential>
        uiScope.launch {
            values = searchCredential(text)
        }
        return values
    }


    suspend fun searchCredential(text: String) : List<Credential> {
        return withContext(Dispatchers.Main) {
            database.findCredentials(text)
        }
    }



    override fun onCleared() {
        super.onCleared()
    }





}