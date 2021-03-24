package com.example.android.politicalpreparedness.election

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import javax.sql.DataSource

class VoterInfoViewModel(
        private val dataSource: ElectionDao,
        private val electionId: Int,
        private val division: Division) : ViewModel() {

    private val TAG = VoterInfoViewModel::class.java.simpleName

    //COMPLETE: Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    // saved election info from database
    var electionFromDb : Election? = null

    init {
        getVoterInfo()
        getElectionFromDb()
    }

    //COMPLETE: Add var and methods to populate voter info
    private fun getVoterInfo() {
        viewModelScope.launch {
            var address = "country:${division.country}"
            // state can be sometimes missing from the division retrieved
            // from the electionQuery API call,
            // but have to add some state to the voterinfo API call or it will be rejected
            if (!division.state.isBlank() && !division.state.isEmpty()) {
                address += "/state:${division.state}"
            } else {
                address += "/state:ca"
            }
            _voterInfo.value = CivicsApi.retrofitService.getVoterInfo(
                    address, electionId)
        }
    }

    //COMPLETE: Add var and methods to support loading URLs
    private val _votingLocationsUrl = MutableLiveData<String?>()
    val votingLocationsUrl: LiveData<String?>
        get() = _votingLocationsUrl

    fun onVotingLocationClick() {
        _votingLocationsUrl.value =
                _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.votingLocationFinderUrl
    }

    fun onVotingLocationNavigated() {
        _votingLocationsUrl.value = null
    }

    private val _ballotInformationUrl = MutableLiveData<String?>()
    val ballotInformationUrl: LiveData<String?>
        get() = _ballotInformationUrl

    fun onBallotInformationNavigated() {
        _ballotInformationUrl.value = null
    }

    fun onBallotInformationClick() {
        _votingLocationsUrl.value =
                _voterInfo.value?.state?.get(0)?.electionAdministrationBody?.ballotInfoUrl
    }

    //COMPLETE: Add var and methods to save and remove elections to local database
    //COMPLETE: cont'd -- Populate initial state of save button to reflect proper action based on election saved status
    private fun saveElectionToDb() {
        viewModelScope.launch {
            voterInfo.value?.let { dataSource.insert(it.election) }
            _isElectionSaved.value = true
        }
    }

    private fun removeElectionFromDb() {
        viewModelScope.launch {
            voterInfo.value?.let { dataSource.deleteElection(it.election.id) }
            _isElectionSaved.value = false
        }
    }

    private val _isElectionSaved = MutableLiveData<Boolean>()
    val isElectionSaved: LiveData<Boolean>
        get() = _isElectionSaved

    fun onSaveButtonClick() {
        if (_isElectionSaved.value == true) {
            removeElectionFromDb()
        } else {
            saveElectionToDb()
        }
    }

    private fun getElectionFromDb() {
        viewModelScope.launch {
            electionFromDb = dataSource.getElectionById(electionId)
            if (electionFromDb != null) {
                _isElectionSaved.value = true
            } else {
                _isElectionSaved.value = false
            }
        }
    }

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}