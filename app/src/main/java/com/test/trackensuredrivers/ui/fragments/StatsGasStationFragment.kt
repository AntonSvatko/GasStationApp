package com.test.trackensuredrivers.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.test.trackensuredrivers.databinding.FragmentStatsGasStationBinding
import com.test.trackensuredrivers.ui.adapters.GasStationsAdapter
import com.test.trackensuredrivers.ui.viewmodel.MainViewModel
import com.test.trackensuredrivers.ui.viewmodel.MainViewModelFactory

class StatsGasStationFragment : Fragment() {
    private lateinit var binding: FragmentStatsGasStationBinding
    private lateinit var viewModel: MainViewModel
    private val adapter by lazy { GasStationsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatsGasStationBinding.inflate(inflater)
        val application = requireActivity().application
        val viewModelFactory = MainViewModelFactory(application)
        viewModel =
            ViewModelProvider(
                this, viewModelFactory
            )[MainViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.adapter = adapter

        viewModel.allGasStation.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}