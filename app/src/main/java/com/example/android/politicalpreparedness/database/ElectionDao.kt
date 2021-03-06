package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //COMPLETE: Add insert query
    @Insert
    suspend fun insert(election: Election)

    //COMPLETE: Add select all election query
    @Query("SELECT * FROM election_table")
    suspend fun getAllElections() : List<Election>

    //COMPLETE: Add select single election query
    @Query("SELECT * FROM election_table WHERE id = :electionId")
    suspend fun getElectionById(electionId: Int) : Election?

    //COMPLETE: Add delete query
    @Query("DELETE FROM election_table WHERE id = :electionId")
    suspend fun deleteElection(electionId: Int)

    //COMPLETE: Add clear query
    @Query("DELETE FROM election_table")
    suspend fun clear()

}