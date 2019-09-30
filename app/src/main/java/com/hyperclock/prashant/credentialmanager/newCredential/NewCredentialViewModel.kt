package com.hyperclock.prashant.credentialmanager.newCredential

import android.app.Application
import androidx.lifecycle.ViewModel
import com.hyperclock.prashant.credentialmanager.database.Credential
import com.hyperclock.prashant.credentialmanager.database.CredentialDao
import kotlinx.coroutines.*

class NewCredentialViewModel(val database: CredentialDao, application: Application) : ViewModel(){

    private val WORDS = "[a-z]".toRegex()
    private val WORDS_CAPS = "[A-Z]".toRegex()
    private val NUMBERS = "[1-9]".toRegex()
    private val SPECIAL_CHARS = "[#|$|!|@|%|^|(|)|]".toRegex()

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    private suspend fun insert(cred : Credential){
        withContext(Dispatchers.IO){
            database.insert(cred)
        }
    }

    fun insertNewCred(passedCred : Credential){
        uiScope.launch {
            insert(passedCred)
        }
    }




    fun validatePassword(chars : CharSequence): Int{
        return when{
            chars.length < 6 -> 1
            chars.length > 6 && chars.matches(WORDS) -> 2
            chars.length > 6 && chars.matches(WORDS) && chars.matches(WORDS_CAPS) -> 3
            chars.length > 6 && chars.matches(WORDS) && chars.matches(WORDS_CAPS) && chars.matches(NUMBERS) -> 4
            chars.length > 6 && chars.matches(WORDS) && chars.matches(WORDS_CAPS) && chars.matches(NUMBERS) && chars.matches(SPECIAL_CHARS)  -> 5
            else -> 1
        }

    }
}