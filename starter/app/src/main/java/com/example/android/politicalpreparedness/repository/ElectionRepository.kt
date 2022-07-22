package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class ElectionRepository(private val database: ElectionDatabase) {

    val elections: LiveData<List<Election>> = database.electionDao.getUpcomingElections()
    val savedElections: LiveData<List<Election>> = database.electionDao.getSavedElections()

    suspend fun refreshElections() {
        try {
            withContext(Dispatchers.IO) {
                val electionResponse = CivicsApi.retrofitService.getElections()
                database.electionDao.insertElections(*electionResponse.elections.toTypedArray())
            }
        } catch (e: Exception) {
        }
    }

}