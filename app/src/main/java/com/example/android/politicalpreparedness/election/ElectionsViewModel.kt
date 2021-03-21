package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

//COMPLETE: Construct ViewModel and provide election datasource
class ElectionsViewModel(
        val database: ElectionDao
): ViewModel() {

    //COMPLETE: Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections : LiveData<List<Election>>
        get() = _upcomingElections

    //COMPLETE: Create live data val for saved elections
    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections : LiveData<List<Election>>
        get() = _savedElections


    //COMPLETE: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    init {
        getSavedElectionsFromDatabase()
        getUpcomingElectionsFromNetwork()
    }

    fun getSavedElectionsFromDatabase() {
        viewModelScope.launch { _savedElections.value = database.getAllElections() }
    }

    fun getUpcomingElectionsFromNetwork() {
        viewModelScope.launch {
            _upcomingElections.value = CivicsApi.retrofitService.getElections().elections
        }
    }

    //COMPLETE: Create functions to navigate to saved or upcoming election voter info
    private val _navigateToElection = MutableLiveData<Election>()
    val navigateToElection : LiveData<Election>
        get() = _navigateToElection

    fun onElectionClicked(election: Election) {
        _navigateToElection.value = election
    }

    fun onElectionNavigated() {
        _navigateToElection.value = null
    }
}