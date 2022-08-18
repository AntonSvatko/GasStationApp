package com.test.trackensuredrivers.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "refuel_table")
data class Refuel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var supplier: String = "",
//    var isSynchronized: Boolean = false,
    var gasStationId: Int = 0,
    var nameGasStation: String = "",
    var amount: Float = 0f,
    var price : Float = 0f,
    var type: String = ""
)