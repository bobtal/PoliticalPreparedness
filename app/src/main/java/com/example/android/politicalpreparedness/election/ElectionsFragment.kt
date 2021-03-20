package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter

class ElectionsFragment: Fragment() {

    //TODO: Declare ViewModel
    private lateinit var viewModel: ElectionsViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        //TODO: Add ViewModel values and create ViewModel
        val application = requireNotNull(this.activity).application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = ElectionsViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)

        //TODO: Add binding values
        val binding: FragmentElectionBinding =
                DataBindingUtil.inflate(
                        inflater, R.layout.fragment_election, container, false)
        binding.viewModel = viewModel

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters
        val clickListener = {electionId : Int -> viewModel.onElectionClicked(electionId)}
        val upcomingElectionsAdapter = ElectionListAdapter(ElectionListener(clickListener))
        val savedElectionsAdapter = ElectionListAdapter(ElectionListener(clickListener))

        //TODO: Populate recycler adapters
        viewModel.upcomingElections.observe(viewLifecycleOwner, Observer {
            it?.let {
                upcomingElectionsAdapter.submitList(it)
            }
        })

        viewModel.savedElections.observe(viewLifecycleOwner, Observer {
            it?.let {
                savedElectionsAdapter.submitList(it)
            }
        })

        return binding.root
    }

    //TODO: Refresh adapters when fragment loads

}