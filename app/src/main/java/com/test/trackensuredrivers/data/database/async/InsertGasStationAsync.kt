package com.test.trackensuredrivers.data.database.async

import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import com.test.trackensuredrivers.data.model.GasStation
import java.lang.ref.WeakReference


class InsertGasStationAsync(context: WeakReference<Context>): AsyncTaskLoader<GasStation>(context.get()!!) {
    override fun loadInBackground(): GasStation? {
        TODO("Not yet implemented")
    }
}