package com.hyperclock.prashant.credentialmanager.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CredentialDao {

    /**
     * to insert the credential into the database
     * @param cred is the credential to be inserted
     */
    @Insert
    fun insert(cred : Credential)

    /**
     * to update the credential
     */
    @Update
    fun update(cred : Credential)

    /**
     * to get the list of all the credentials in a list
     * with type being the LiveData
     */
    @Query("SELECT * FROM credentials_table ORDER BY id DESC")
    fun getAllCredentials() : LiveData<List<Credential>>


    /**
     * get a single LiveData for the detailed view
     */
    @Query("SELECT * FROM credentials_table WHERE id = :key")
    fun getSelectedCredentials(key: Int) : LiveData<Credential>

    /**
     * get a list of matching credentials for search
     */
    @Query("SELECT * FROM credentials_table WHERE account_column LIKE :selection")
    fun findCredentials(selection : String) : List<Credential>


    /**
     * clears everything in the table
     */
    @Query("DELETE FROM credentials_table")
    fun clear()

}