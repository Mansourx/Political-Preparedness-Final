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
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment: Fragment() {

    private val viewModel: ElectionsViewModel by lazy {
        ViewModelProvider(this, ElectionsViewModelFactory(requireContext()))
            .get(ElectionsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding: FragmentElectionBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_election, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val listener = ElectionListener { election ->
            findNavController().navigate(ElectionsFragmentDirections
                .actionElectionsFragmentToVoterInfoFragment(election.id, election.division))
        }
        val listener2 = ElectionListener { election ->
            findNavController().navigate(ElectionsFragmentDirections
                .actionElectionsFragmentToVoterInfoFragment(election.id,election.division))
        }

        val upcomingElectionsAdapter = ElectionListAdapter(listener)
        val savedElectionsAdapter = ElectionListAdapter(listener2)

        binding.savedElecsList.adapter = savedElectionsAdapter
        binding.upcomingElecsList.adapter = upcomingElectionsAdapter

        observerUpcomingElections(upcomingElectionsAdapter)
        observerSavedElections(savedElectionsAdapter)

        return binding.root
    }

    private fun observerUpcomingElections(upcomingElectionsAdapter: ElectionListAdapter) {
        viewModel.upcomingElections.observe(viewLifecycleOwner, Observer {
            it?.let{
                upcomingElectionsAdapter.submitList(it)
            }
        })
    }

    private fun observerSavedElections(savedElectionsAdapter: ElectionListAdapter) {
        viewModel.savedElections.observe(viewLifecycleOwner, Observer {
            it?.let{
                savedElectionsAdapter.submitList(it)
            }
        })
    }

}