package com.example.android.politicalpreparedness.election

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.models.Election
import kotlinx.coroutines.launch


class ElectionsViewModel(context: Context) : ViewModel() {

    private val database = ElectionDatabase.getInstance(context)
    private val repository = ElectionRepository(database)

    val upcomingElections: LiveData<List<Election>> = repository.elections
    val savedElections: LiveData<List<Election>> = repository.savedElections

    init {
        viewModelScope.launch {
            repository.refreshElections()
        }
    }

}