package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private val viewModel by viewModels<VoterInfoViewModel> {
        VoterInfoViewModelFactory(ElectionDatabase.getInstance(requireContext()).electionDao)
    }

    private val args by navArgs<VoterInfoFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val division = args.argDivision
        val electionId = args.argElectionId
        val address = if (division.state.isEmpty()) "CO,${division.country}" else "${division.state},${division.country}"

        viewModel.getVoterInfo(address, electionId)

        viewModel.navigateToUrl.observe(viewLifecycleOwner) {
            it?.let {
                loadUrl(it)
                viewModel.onDoneNavigating()
            }
        }

        viewModel.savedState.observe(viewLifecycleOwner) {
            binding.saveElectionButton.text = if (it == null) {
                getString(R.string.follow_election)
            } else {
                getString(R.string.unfollow_election)
            }
        }

        return binding.root
    }

    private fun loadUrl(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

}