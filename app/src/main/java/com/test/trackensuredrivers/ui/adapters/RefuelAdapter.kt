package com.test.trackensuredrivers.ui.adapters

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.test.trackensuredrivers.R
import com.test.trackensuredrivers.data.model.GasStation
import com.test.trackensuredrivers.data.model.Refuel
import com.test.trackensuredrivers.databinding.ItemRefuelBinding
import com.test.trackensuredrivers.utills.getAddress


class RefuelAdapter(private val callBack: (Int, Int) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<Refuel, RefuelAdapter.RefuelHolder>(
        RefuelCallback()
    ) {
    private var holder: RefuelHolder? = null

    var listGasStations: List<GasStation> = listOf()

    inner class RefuelHolder(private val binding: ItemRefuelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindCall(refuel: Refuel) {
            binding.amountText.text = "${refuel.amount}L"
            binding.priceText.text = "${refuel.price}$"
            binding.supplierText.text = "Supplier: ${refuel.supplier}"
            val gasStation = listGasStations.find { it.id == refuel.gasStationId }
            gasStation?.let {
                binding.addressText.text =
                    itemView.context.getAddress(LatLng(gasStation.latitude, gasStation.longitude))
                binding.nameGasStationText.text = gasStation.name
            }

            binding.moreBtn.setOnClickListener {
                PopupMenu(itemView.context, itemView).apply {
                    inflate(R.menu.popup_menu)
                    gravity = Gravity.END
                    setOnMenuItemClickListener { item ->
                        callBack(item.itemId, refuel.id)
                        true
                    }
                    show()
                }
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RefuelHolder {
        val binding: ItemRefuelBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_refuel,
            parent, false
        )
        return RefuelHolder(binding)
    }

    override fun onBindViewHolder(holder: RefuelHolder, position: Int) {
        this.holder = holder
        holder.bindCall(currentList[position])
    }
}

class RefuelCallback : DiffUtil.ItemCallback<Refuel>() {
    override fun areItemsTheSame(oldItem: Refuel, newItem: Refuel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Refuel, newItem: Refuel): Boolean {
        return oldItem == newItem
    }
}