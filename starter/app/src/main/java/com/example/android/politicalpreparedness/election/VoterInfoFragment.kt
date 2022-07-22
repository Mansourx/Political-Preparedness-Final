package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
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
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var viewModel: VoterInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val electionId = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
        val division = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision
        val database = ElectionDatabase.getInstance(requireContext()).electionDao
        viewModel = ViewModelProvider(this, VoterInfoViewModelFactory(electionId, division,
            database)).get(VoterInfoViewModel::class.java)

        val binding: FragmentVoterInfoBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_voter_info, container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.locationUrl.observe(viewLifecycleOwner, Observer {
            it?.let { url ->
                binding.stateLocations.visibility = View.VISIBLE
                binding.stateLocations.setOnClickListener { openUrl(url) }
            }
        })

        viewModel.ballotUrl.observe(viewLifecycleOwner, Observer {
            it?.let { url ->
                binding.stateBallot.visibility = View.VISIBLE
                binding.stateBallot.setOnClickListener { openUrl(url) }
            }
        })

        viewModel.savedElection.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (!it) {
                    binding.saveButton.text = getString(R.string.save_button_text)
                    binding.saveButton.setOnClickListener { viewModel.saveElection() }
                } else {
                    binding.saveButton.text = getString(R.string.unfollow_button_text)
                    binding.saveButton.setOnClickListener { viewModel.removeElection() }
                }
            }
        })

        return binding.root

    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}