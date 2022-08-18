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
import com.test.trackensuredrivers.ui.adapters.RefuelAdapter
import com.test.trackensuredrivers.ui.viewmodel.MainViewModel
import com.test.trackensuredrivers.ui.viewmodel.MainViewModelFactory
import com.test.trackensuredrivers.utills.Constants

class RefuelFragment : Fragment() {
    private lateinit var binding: FragmentRefuelBinding
    private lateinit var viewModel: MainViewModel
    private val adapter by lazy {
        RefuelAdapter { action, id ->
            if (action == R.id.delete)
                viewModel.deleteRefuel(id)
            else{
                val intent = Intent(requireActivity(), MapsActivity::class.java)
                intent.putExtra(Constants.SEND_REFUEL_INTENT_KEY, id)
                startActivity(intent)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRefuelBinding.inflate(inflater)
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