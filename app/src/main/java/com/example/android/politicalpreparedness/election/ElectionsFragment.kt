package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsFragment: Fragment() {

    //COMPLETE: Declare ViewModel
    private lateinit var viewModel: ElectionsViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        //COMPLETE: Add ViewModel values and create ViewModel
        val application = requireNotNull(this.activity).application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = ElectionsViewModelFactory(dataSource)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)

        //COMPLETE: Add binding values
        val binding: FragmentElectionBinding =
                DataBindingUtil.inflate(
                        inflater, R.layout.fragment_election, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        //COMPLETE: Link elections to voter info
        viewModel.navigateToElection.observe(viewLifecycleOwner, Observer { election ->
            election?.let {
                this.findNavController().navigate(
                        ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                                it.id, it.division))
                viewModel.onElectionNavigated()
            }
        })

        //COMPLETE: Initiate recycler adapters
        val clickListener = {election : Election -> viewModel.onElectionClicked(election)}
        val upcomingElectionsAdapter = ElectionListAdapter(ElectionListener(clickListener))
        binding.upcomingElectionsRecycler.adapter = upcomingElectionsAdapter
        val savedElectionsAdapter = ElectionListAdapter(ElectionListener(clickListener))
        binding.savedElectionsRecycler.adapter = savedElectionsAdapter

        //COMPLETE: Populate recycler adapters
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

    //COMPLETE: Refresh adapters when fragment loads
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSavedElectionsFromDatabase()
    }
}