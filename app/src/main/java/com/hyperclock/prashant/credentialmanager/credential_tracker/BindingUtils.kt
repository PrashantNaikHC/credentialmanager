package com.hyperclock.prashant.credentialmanager.credential_tracker

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.hyperclock.prashant.credentialmanager.database.Credential

@BindingAdapter("credentialNameString")
fun TextView.setSleepQualityString(item: Credential?) {
    item?.let {
        text = it.account
    }
}
