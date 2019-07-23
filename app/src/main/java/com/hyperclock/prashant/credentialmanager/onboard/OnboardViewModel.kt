package com.hyperclock.prashant.credentialmanager.onboard

import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import android.util.Log


class OnboardViewModel : ViewModel() {

    private val TAG = javaClass.name
    private val defaultPass  = listOf(3,0,6,8)
    private lateinit var fetchedPass : String
    var randomIntegerString = (0..9).shuffled()

    private val _randomString = MutableLiveData<List<Int>>()
    val randomString: LiveData<List<Int>>
        get() = _randomString

    init {

        _randomString.value = randomIntegerString
    }





    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password

    var date = Date();
    var appendMin = DateFormat.format("m", date.getTime());



    fun genShowString() {

        for (i in randomIntegerString) {
            if (((randomIntegerString.indexOf(i) + 1) % 3) == 0) {
                print(i)
                print("\n")
                //TODO show the value
            } else print(i)
        }
    }

    fun getfetchedPass() : String{
        fetchedPass=""
        for (letter in defaultPass){
            fetchedPass = randomIntegerString[letter].toString() + fetchedPass
            Log.i(TAG,"getfetchedPass is ${letter} and ${fetchedPass}")
        }
        return fetchedPass
    }

    fun checkPassword(pass: String): Boolean = pass == finalPassGen()

    fun finalPassGen(): String? {
        _password.value =  appendMin.toString() + getfetchedPass()
        Log.i(TAG,"final password ${_password.value} with ${defaultPass.toString()} and ${appendMin.toString()} and ${getfetchedPass()}")
        return _password.value
    }
}