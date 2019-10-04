package com.hyperclock.prashant.credentialmanager.credential_tracker

import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.hyperclock.prashant.credentialmanager.database.Credential
import java.util.*

@BindingAdapter("credentialNameString")
fun TextView.setCredentialNameString(item: Credential) {
    this.text = item.account
}

@BindingAdapter("credentialExpiryTime")
fun TextView.setCredentialExpiry(item: Credential) {
    val result = "this password will expire in" + item.resetTime.toString()
    this.text = result
}

@BindingAdapter("credentialHealthMeter")
fun ProgressBar.setCredentialMeter(item: Credential) {
    this.max = 31
    this.progress = 31 - item.resetTime
}
