package com.hyperclock.prashant.credentialmanager.credential_tracker

import android.animation.ObjectAnimator
import android.util.Log
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.hyperclock.prashant.credentialmanager.database.Credential
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

@BindingAdapter("credentialNameString")
fun TextView.setCredentialNameString(item: Credential) {
    this.text = item.account
}

@BindingAdapter("credentialExpiryTime")
fun TextView.setCredentialExpiry(item: Credential) {
    val result = getTimediff(item.time + item.resetTime.dayToMillis())
    this.text = result
}

@BindingAdapter("credentialHealthMeter")
fun ProgressBar.setCredentialMeter(item: Credential) {
    this.max = 100
    //this.progress = getPercent(item.resetTime.dayToMillis()+System.currentTimeMillis(),item.time)
    val percent = getPercent(item.resetTime.dayToMillis()+System.currentTimeMillis(),item.time)
    val progressAnimator : ObjectAnimator = ObjectAnimator.ofInt(this,"progress",this.progress,percent)
    progressAnimator.duration = 1000
    progressAnimator.interpolator = AccelerateInterpolator()
    progressAnimator.start()
    Log.d("BindingUtil","progress is ${this.progress}")
}

fun getPercent(futureTime:Long, lastUpdatedTime:Long):Int  = ((futureTime - System.currentTimeMillis())*100/(futureTime - lastUpdatedTime).absoluteValue).toInt()


/**
 * Extension function to convert day to milliseconds
 */
fun Int.dayToMillis(): Long = this*24*60*60*1000.toLong()

fun getTimediff(time:Long):String{
    val difference = (System.currentTimeMillis() - time).absoluteValue
    //extension for adding string detail if it's past or future
    fun String.addStringDetail():String = if (System.currentTimeMillis() > time) "Expired $this Ago" else "Will expire in $this"

    val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(difference)

    var days = totalSeconds / (60*60*24)
    var hours = totalSeconds / (60*60)
    var minutes = totalSeconds / (60)
    var seconds = totalSeconds

    when {
        days > 0 -> {
            //show in days and hours
            if(hours > 24){
                days = hours / 24
                hours %= 24
            }
            return "$days day and $hours Hrs".addStringDetail()
        }
        hours > 0 -> {
            //show in hours and minutes
            if(minutes > 60) {
                hours = minutes / 60
                minutes %= 60
            }
            return  "$hours Hrs and $minutes min".addStringDetail()
        }
        minutes > 0 ->{
            //show in minutes
            if(seconds > 60){
                minutes = seconds / 60
                seconds %= 60
            }
            return  "$minutes mins and $seconds sec".addStringDetail()
        }
        else ->{
            //show in seconds
            return  "$seconds sec".addStringDetail()
        }
    }
}
