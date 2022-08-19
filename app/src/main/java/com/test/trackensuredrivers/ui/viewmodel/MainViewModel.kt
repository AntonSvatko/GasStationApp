package com.test.trackensuredrivers.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.trackensuredrivers.data.database.AppDataBase
import com.test.trackensuredrivers.data.model.GasStation
import com.test.trackensuredrivers.data.model.Refuel
import com.test.trackensuredrivers.data.repository.GasStationRepository
import com.test.trackensuredrivers.data.repository.RefuelRepository

class MainViewModel(private val application: Application) : ViewModel() {
    var allGasStation: LiveData<List<GasStation>>
    var allRefuel: LiveData<List<Refuel>>
    private val gasStationRepository: GasStationRepository
    private val refuelRepository: RefuelRepository

    init {
        val gasStationDap = AppDataBase.getDatabase(application).gasStationDao
        val refuelDao = AppDataBase.getDatabase(application).refuelDao
        gasStationRepository = GasStationRepository(gasStationDap)
        refuelRepository = RefuelRepository(refuelDao)
        allGasStation = gasStationRepository.getGasStations()
        allRefuel = refuelRepository.getRefuels()
    }

    fun insertGasStation(gasStation: GasStation) {
        gasStationRepository.insert(gasStation)
    }

    fun insertRefuel(refuel: Refuel) {
        if (refuel.id == 0L) {
            gasStationRepository.getGasStation(refuel.gasStationId) { gasStation ->
                gasStation?.let {
                    it.localAmount++
                    it.totalAmount++
                    gasStationRepository.update(it)
                }
            }
            refuelRepository.insert(refuel)
        } else
            refuelRepository.update(refuel)
    }

    fun deleteRefuel(refuel: Refuel) {
        refuelRepository.delete(refuel.id)
        gasStationRepository.getGasStation(refuel.gasStationId) { gasStation ->
            gasStation?.let {
                it.localAmount--
                it.totalAmount--
                gasStationRepository.update(it)
            }
        }
    }

    fun getRefuel(id: Long): LiveData<Refuel> {
        val refuel = MutableLiveData<Refuel>()
        refuelRepository.getRefuel(id) {
            refuel.postValue(it)
        }
        return refuel
    }

}