package com.hyperclock.prashant.credentialmanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "credentials_table")
data class Credential (
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0L,

    @ColumnInfo(name = "account_column")
    var account : String,

    @ColumnInfo(name = "password_column")
    var password : String,

    @ColumnInfo(name = "reset_column")
    var resetTime : Int,

    @ColumnInfo(name = "link_column")
    var link : String
)