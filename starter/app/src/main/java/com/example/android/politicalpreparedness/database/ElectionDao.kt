package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.models.Election
import com.example.android.politicalpreparedness.models.SavedElection

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElections(vararg elections: Election)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveElection(election: SavedElection)

    @Query(value = "SELECT * FROM election_table WHERE id IN (SELECT * FROM saved_election_table)")
    fun getSavedElections(): LiveData<List<Election>>

    @Query(value = "SELECT * FROM election_table WHERE id NOT IN (SELECT * FROM saved_election_table)")
    fun getUpcomingElections(): LiveData<List<Election>>

    @Query(value = "SELECT COUNT(*) FROM saved_election_table WHERE id = :id")
    fun isSaved(id: Int): LiveData<Int>

    @Query(value = "DELETE FROM saved_election_table WHERE id = :id")
    suspend fun deleteSavedElectionById(id: Int)

}