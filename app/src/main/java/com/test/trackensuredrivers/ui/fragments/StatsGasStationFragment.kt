package com.test.trackensuredrivers.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.test.trackensuredrivers.databinding.FragmentGasStationBinding
import com.test.trackensuredrivers.databinding.FragmentStatsGasStationBinding

class StatsGasStationFragment: Fragment() {
    private lateinit var binding: FragmentStatsGasStationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatsGasStationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}