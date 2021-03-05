package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {
    // Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElection(election: Election)

    // Add select all elections query
    @Query("SELECT * FROM election_table")
    fun observeElections() : LiveData<List<Election>>

    @Query("SELECT * FROM election_table")
    suspend fun getElections() : List<Election>

    // Add select single election query
    @Query("SELECT * FROM election_table WHERE id = :electionId")
    fun observeElection(electionId: Int) : LiveData<Election>

    @Query("SELECT * FROM election_table WHERE id = :electionId")
    suspend fun getElectionById(electionId : Int): Election?

    // Add delete query
    @Query("DELETE FROM election_table WHERE id = :electionId")
    suspend fun deleteElectionById(electionId: Int): Int

    // Add clear query
    @Query("DELETE FROM election_table")
    suspend fun clearElections()
}