package com.test.trackensuredrivers.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gas_station")
data class GasStation(
    @PrimaryKey
    var id: Long = System.currentTimeMillis(),
    var name: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var isSynchronized: Boolean = false,
    var totalAmount: Int = 0,
    var localAmount: Int = 0
)
