package com.test.trackensuredrivers.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.test.trackensuredrivers.R
import com.test.trackensuredrivers.databinding.FragmentStatsGasStationBinding
import com.test.trackensuredrivers.ui.adapters.GasStationsAdapter
import com.test.trackensuredrivers.ui.base.BaseFragment
import com.test.trackensuredrivers.ui.viewmodel.MainViewModel
import com.test.trackensuredrivers.ui.viewmodel.MainViewModelFactory

class StatsGasStationFragment : BaseFragment<FragmentStatsGasStationBinding>(R.layout.fragment_stats_gas_station) {
    private val adapter by lazy { GasStationsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.adapter = adapter

        viewModel.allGasStation.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}