package com.test.trackensuredrivers.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.test.trackensuredrivers.data.GasStationRepository
import com.test.trackensuredrivers.data.database.AppDataBase
import com.test.trackensuredrivers.data.model.GasStation

class MainViewModel(private val application: Application) : ViewModel() {
    var allGasStation: LiveData<List<GasStation>>
    private val repository: GasStationRepository

    // on below line we are initializing
    // our dao, repository and all notes
    init {
        val gas = AppDataBase.getDatabase(application).gasStationDao
        repository = GasStationRepository(gas)
        allGasStation = repository.getGasStations()
    }

    fun insertGasStation(gasStation: GasStation) {
        repository.insert(gasStation)
    }
}