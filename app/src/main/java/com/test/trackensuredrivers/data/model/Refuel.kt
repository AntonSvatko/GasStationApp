package com.test.trackensuredrivers.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "refuel_table")
data class Refuel(
    @PrimaryKey
    var id: Long = System.currentTimeMillis(),
    var supplier: String = "",
    var isSynchronized: Boolean = false,
    var gasStationId: Long = 0,
    var nameGasStation: String = "",
    var amount: Float = 0f,
    var price : Float = 0f,
    var type: String = ""
)