package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

// Construct ViewModel and provide election datasource
class ElectionsViewModel(private val database: ElectionDatabase) : ViewModel() {
    private val TAG = ElectionsViewModel::class.java.simpleName

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo : LiveData<Election>
        get() = _navigateToVoterInfo

    init {
        refreshUpcomingElectionsFromApi()
        refreshSavedElectionsFromDb()
    }

    fun refreshUpcomingElectionsFromApi() {
//        Log.i(TAG, "refresh upcoming elections from api called")
        viewModelScope.launch {
            try {
                val electionResponse = CivicsApi.retrofitService.getElections()
                Log.i(TAG, electionResponse.kind)
                _upcomingElections.value = electionResponse.elections
            } catch (e: Exception) {
                Log.i(TAG, "electionsQuery error: ${e.message}")
                _upcomingElections.value = ArrayList()
            }
        }
    }

    fun refreshSavedElectionsFromDb() {
//        Log.i(TAG, "refresh saved elections from DB called")
        viewModelScope.launch {
            try {
                val resultList = database.electionDao.getElections()
                _savedElections.value = resultList
            } catch (e: Exception) {
                Log.i(TAG, "database error: ${e.message}")
                _savedElections.value = ArrayList()
            }
        }
    }

    fun onUpcomingElectionClicked(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun onSavedElectionClicked(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun onDoneNavigating() {
        _navigateToVoterInfo.value = null
    }

}