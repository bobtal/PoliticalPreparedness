package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Division
import java.lang.IllegalArgumentException

//COMPLETE: Create Factory to generate VoterInfoViewModel with provided election datasource
class VoterInfoViewModelFactory(
        private val database: ElectionDao,
        private val electionId: Int,
        private val division: Division
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            return VoterInfoViewModel(database, electionId, division) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}