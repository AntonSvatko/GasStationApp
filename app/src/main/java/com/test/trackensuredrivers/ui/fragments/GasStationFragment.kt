package com.test.trackensuredrivers.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.test.trackensuredrivers.MapsActivity
import com.test.trackensuredrivers.databinding.FragmentGasStationBinding

class GasStationFragment : Fragment() {
    private lateinit var binding: FragmentGasStationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGasStationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(requireActivity(), MapsActivity::class.java))
        }
    }
}