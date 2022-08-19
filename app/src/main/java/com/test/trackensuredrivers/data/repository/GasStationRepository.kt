package com.test.trackensuredrivers.data.repository

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

    fun getGasStation(id: Long, getGasStation: (GasStation?) -> Unit) {
        execute(bgrWork = {
            getGasStation(dao.getGasStation(id))
        })
    }

    fun getLast(getGasStation: (GasStation?) -> Unit) {
        execute(bgrWork = {
            getGasStation(dao.getLast())
        })
    }

    fun getGasStations() = dao.getGasStations()
}