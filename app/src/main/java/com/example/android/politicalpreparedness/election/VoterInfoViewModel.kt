package com.example.android.politicalpreparedness.election

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.State
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val dataSource: ElectionDao) : ViewModel() {

    private val TAG = VoterInfoViewModel::class.java.simpleName

    private val voterInfoResponse = MutableLiveData<VoterInfoResponse>()
    val election : LiveData<Election> =
            Transformations.map(voterInfoResponse) { it.election }
    val state : LiveData<State> =
            Transformations.map(voterInfoResponse) { it.state?.get(0) }
    val administrationBody: LiveData<AdministrationBody> =
            Transformations.map(state) { it.electionAdministrationBody }

    private val _navigateToUrl = MutableLiveData<Uri>()
    val navigateToUrl : LiveData<Uri>
        get() = _navigateToUrl

    private val _savedState = MutableLiveData<Election>()
    val savedState : LiveData<Election>
        get() = _savedState

    val hasUrls : LiveData<Boolean> = Transformations.map(administrationBody) {
        it.let {
            it.votingLocationFinderUrl != null || it.ballotInfoUrl != null
        }
    }

    fun getVoterInfo(address: String, electionId: Int) {
        viewModelScope.launch {
            try {
                voterInfoResponse.value = CivicsApi.retrofitService.getVoterInfo(address, electionId)
                _savedState.value = dataSource.getElectionById(electionId)
                Log.i(TAG, administrationBody.value.toString())
            } catch(e: Exception) {
                Log.i(TAG, "getVoterInfo exception: ${e.message}")
            }
        }
    }

    fun openUrl(url: String) {
        _navigateToUrl.value = Uri.parse(url)
    }

    fun onDoneNavigating() {
        _navigateToUrl.value = null
    }

    fun toggleElectionSavedState() {
        viewModelScope.launch {
            try {
                if (savedState.value != null) {
                    dataSource.deleteElectionById(election.value!!.id)
                } else {
                    dataSource.insertElection(election.value!!)
                }
                _savedState.value = dataSource.getElectionById(election.value!!.id)
            } catch (e: Exception) {
                Log.i(TAG, "Error while saving election: ${e.message}")
            }
        }
    }

}