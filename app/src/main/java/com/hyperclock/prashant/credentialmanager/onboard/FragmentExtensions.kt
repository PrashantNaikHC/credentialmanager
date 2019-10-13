package com.hyperclock.prashant.credentialmanager.onboard

import android.widget.Toast
import androidx.fragment.app.Fragment


fun Fragment.showToast(text: String){
    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
}