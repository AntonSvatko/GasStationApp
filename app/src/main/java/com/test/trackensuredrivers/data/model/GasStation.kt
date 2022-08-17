package com.test.trackensuredrivers.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gas_station")
data class GasStation (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)
