package com.test.trackensuredrivers.utills

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Handler
import android.os.Looper
import com.google.android.gms.maps.model.LatLng
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
        handler.post{
            uiWork()
        }
    }
}

fun Context.getAddress(latLng: LatLng): String {
    val geocoder: Geocoder = Geocoder(this, Locale.getDefault())

    val addresses: List<Address> = geocoder.getFromLocation(
        latLng.latitude,
        latLng.longitude,
        1
    ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


    val address: String =
        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

    val city: String = addresses[0].locality
    val state: String = addresses[0].adminArea
    val country: String = addresses[0].countryName
    val postalCode: String = addresses[0].postalCode
    val knownName: String = addresses[0].featureName

    return address
}