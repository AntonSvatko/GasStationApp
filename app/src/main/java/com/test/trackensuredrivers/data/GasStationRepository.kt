package com.test.trackensuredrivers.data

import com.test.trackensuredrivers.data.database.dao.GasStationDao
import com.test.trackensuredrivers.data.model.GasStation
import com.test.trackensuredrivers.utills.execute


class GasStationRepository(private val dao: GasStationDao) {
    fun insert(gasStation: GasStation) {
        execute(bgrWork = {
            dao.insertGasStation(gasStation)
        })
    }

    fun update(gasStation: GasStation) {
        execute(bgrWork = {
            dao.updateGasStation(gasStation)
        })
    }

    fun getGasStations() = dao.getGasStations()
}