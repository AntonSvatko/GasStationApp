package com.test.trackensuredrivers.data.model

data class Refuel(
    var name: String = "",
    var suplier: String = "",
    var address: String = "",
    var isSynchronized: Boolean = false,
    var numberFillings: Int = 0,
    var price : Float = 0f
)