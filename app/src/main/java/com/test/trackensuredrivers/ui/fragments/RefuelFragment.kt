package com.test.trackensuredrivers.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.test.trackensuredrivers.MapsActivity
import com.test.trackensuredrivers.R
import com.test.trackensuredrivers.databinding.FragmentRefuelBinding
import com.test.trackensuredrivers.service.SynchronizedService
import com.test.trackensuredrivers.ui.adapters.RefuelAdapter
import com.test.trackensuredrivers.ui.base.BaseFragment
import com.test.trackensuredrivers.ui.viewmodel.MainViewModel
import com.test.trackensuredrivers.ui.viewmodel.MainViewModelFactory
import com.test.trackensuredrivers.utills.Constants

class RefuelFragment : BaseFragment<FragmentRefuelBinding>(R.layout.fragment_refuel) {
    private val adapter by lazy {
        RefuelAdapter { action, refuel ->
            if (action == R.id.delete) {
                val intent = Intent(requireActivity(), SynchronizedService::class.java)
                intent.putExtra(Constants.DELETE_REFUEL_STATION_KEY, longArrayOf(refuel.id, refuel.gasStationId))
                requireActivity().startService(intent)
                viewModel.deleteRefuel(refuel)
            }
            else{
                val intent = Intent(requireActivity(), MapsActivity::class.java)
                intent.putExtra(Constants.SEND_REFUEL_INTENT_KEY, refuel.id)
                startActivity(intent)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.adapter = adapter

        viewModel.allRefuel.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.allGasStation.observe(viewLifecycleOwner) {
            adapter.listGasStations = it
            adapter.notifyDataSetChanged()
        }

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(requireActivity(), MapsActivity::class.java))
        }
    }
}