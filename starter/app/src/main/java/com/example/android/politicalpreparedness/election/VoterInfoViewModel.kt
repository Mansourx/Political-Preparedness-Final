package com.example.android.politicalpreparedness.election

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.models.Division
import com.example.android.politicalpreparedness.models.SavedElection
import com.example.android.politicalpreparedness.models.VoterInfoResponse
import kotlinx.coroutines.launch
import java.lang.Exception

class VoterInfoViewModel(private val electionId: Int, private val division: Division,
                         private val dataSource: ElectionDao) : ViewModel() {

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _savedCount: LiveData<Int>
        get() = dataSource.isSaved(electionId)
    val savedElection: LiveData<Boolean> = Transformations.map(_savedCount) {
        it?.let {
            it > 0
        }
    }

    val locationUrl = Transformations.map(_voterInfo) {
        it?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
    }

    val ballotUrl = Transformations.map(_voterInfo) {
        it?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
    }

    init {
        getVoterInfo()
    }

    private fun getVoterInfo() {
        try {
            viewModelScope.launch {
                val address =
                    if (division.state.isBlank()) division.country else "${division.country},${division.state}"
                _voterInfo.value = CivicsApi.retrofitService.getVoterInfo(electionId, address)
            }
        } catch (e: Exception){

        }
    }

    fun saveElection() {
        viewModelScope.launch {
            dataSource.saveElection(SavedElection(electionId))
        }
    }

    fun removeElection() {
        viewModelScope.launch {
            dataSource.deleteSavedElectionById(electionId)
        }
    }

}