package com.hyperclock.prashant.credentialmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Credential::class], version = 2, exportSchema = false)
abstract class CredentialDatabase : RoomDatabase() {

    abstract val credentialDao: CredentialDao

    companion object {
        @Volatile
        private var INSTANCE: CredentialDatabase? = null

        fun getInstance(context: Context): CredentialDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CredentialDatabase::class.java,
                        "credentials_table"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}