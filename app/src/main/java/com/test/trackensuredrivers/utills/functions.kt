package com.test.trackensuredrivers.utills

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Handler
import android.os.Looper
import android.text.Editable
import com.google.android.gms.maps.model.LatLng
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


inline fun execute(
    crossinline bgrWork: () -> Unit,
    crossinline uiWork: () -> Unit = {}
) {
    val executor: ExecutorService = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())

    executor.execute {
        bgrWork()
        handler.post {
            uiWork()
        }
    }
}

fun Context.getAddress(latLng: LatLng): String {
    var address = ""
    kotlin.runCatching {
        val geocoder = Geocoder(this, Locale.getDefault())

        val addresses: List<Address> = geocoder.getFromLocation(
            latLng.latitude,
            latLng.longitude,
            1
        )

        address =
            addresses[0].getAddressLine(0)
    }.onFailure {
        it.printStackTrace()
        address = latLng.toString()
    }

    return address
}

fun Editable.toFloat(): Float =
    toString().trim().takeIf { it.isNotEmpty() }?.toFloat() ?: 0f

