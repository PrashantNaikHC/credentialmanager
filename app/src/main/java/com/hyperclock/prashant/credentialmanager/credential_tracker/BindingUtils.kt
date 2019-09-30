package com.hyperclock.prashant.credentialmanager.credential_tracker

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.hyperclock.prashant.credentialmanager.database.Credential

@BindingAdapter("credentialNameString")
fun TextView.setCredentialNameString(item: Credential) {
    this.text = item.account
}

@BindingAdapter("credentialExpiryTime")
fun TextView.setCredentialExpiry(item: Credential) {
    this.text = item.resetTime.toString()
}

