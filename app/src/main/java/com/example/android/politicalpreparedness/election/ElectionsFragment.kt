package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment : Fragment() {

    // Declare ViewModel
    private val viewModel by viewModels<ElectionsViewModel>() {
        ElectionsViewModelFactory(ElectionDatabase.getInstance(requireContext()))
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.refreshSavedElectionsFromDb()
        viewModel.refreshUpcomingElectionsFromApi()

        val upcomingElectionsAdapter = ElectionListAdapter(ElectionListener {
            viewModel.onUpcomingElectionClicked(it)
        })

        val savedElectionsAdapter = ElectionListAdapter(ElectionListener {
            viewModel.onSavedElectionClicked(it)
        })

        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(ElectionsFragmentDirections
                        .actionElectionsFragmentToVoterInfoFragment(it.id, it.division))
                viewModel.onDoneNavigating()
            }
        }

        viewModel.upcomingElections.observe(viewLifecycleOwner) {
            it?.let {
                upcomingElectionsAdapter.submitList(it)
            }
        }

        viewModel.savedElections.observe(viewLifecycleOwner) {
            it?.let {
                savedElectionsAdapter.submitList(it)
            }
        }

        binding.upcomingElectionsRecyclerView.adapter = upcomingElectionsAdapter
        binding.savedElectionsRecyclerView.adapter = savedElectionsAdapter

        return binding.root
    }

}