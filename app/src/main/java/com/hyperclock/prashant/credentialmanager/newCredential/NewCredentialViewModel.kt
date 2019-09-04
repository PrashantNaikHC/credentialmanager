package com.hyperclock.prashant.credentialmanager.newCredential

import androidx.lifecycle.ViewModel

class NewCredentialViewModel : ViewModel(){


    fun validatePassword(chars : CharSequence): Int{

        when{
            chars.length < 6 -> return 1 //insufficient length
            chars.length > 6 -> return 2 //has length but
            chars.length > 6 && chars.matches("!|@|#|$|%|^|&|(|)".toRegex()) -> return 4
            chars.length > 6 && chars.matches("!|@|#|$|%|^|&|(|)".toRegex()) && chars.matches("[A-Z]|[a-z]|[0-9]".toRegex())-> return 5
            else -> return 3
        }

    }
}