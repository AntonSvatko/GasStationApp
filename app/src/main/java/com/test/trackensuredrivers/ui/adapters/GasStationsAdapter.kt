package com.test.trackensuredrivers.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.test.trackensuredrivers.R
import com.test.trackensuredrivers.data.model.GasStation
import com.test.trackensuredrivers.databinding.ItemStatsGasStationBinding
import com.test.trackensuredrivers.utills.execute
import com.test.trackensuredrivers.utills.getAddress

class GasStationsAdapter :
    androidx.recyclerview.widget.ListAdapter<GasStation, GasStationsAdapter.GasStationHolder>(
        GasStationCallback()
    ) {

    inner class GasStationHolder(private val binding: ItemStatsGasStationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindCall(gasStation: GasStation) {
            var address = "${gasStation.latitude},${gasStation.longitude}"
            execute(bgrWork = {
                address =
                    itemView.context.getAddress(LatLng(gasStation.latitude, gasStation.longitude))
            }, uiWork = {
                binding.addressText.text = address
            })

            binding.nameText.text = gasStation.name
            binding.numberOfVisitsText.text = gasStation.totalAmount.toString()

            binding.executePendingBindings()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GasStationHolder {
        val binding: ItemStatsGasStationBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_stats_gas_station,
            parent, false
        )
        return GasStationHolder(binding)
    }

    override fun onBindViewHolder(holder: GasStationHolder, position: Int) {
        holder.bindCall(currentList[position])
    }
}

class GasStationCallback : DiffUtil.ItemCallback<GasStation>() {
    override fun areItemsTheSame(oldItem: GasStation, newItem: GasStation): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GasStation, newItem: GasStation): Boolean {
        return oldItem == newItem
    }
}