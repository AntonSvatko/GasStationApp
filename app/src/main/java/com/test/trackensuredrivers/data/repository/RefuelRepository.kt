package com.test.trackensuredrivers.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.test.trackensuredrivers.data.database.dao.RefuelDao
import com.test.trackensuredrivers.data.model.GasStation
import com.test.trackensuredrivers.data.model.Refuel
import com.test.trackensuredrivers.utills.execute


class RefuelRepository(private val dao: RefuelDao) {
    fun insert(refuel: Refuel) {
        execute(bgrWork = {
            dao.insertRefuel(refuel)
        })
    }

    fun update(refuel: Refuel) {
        execute(bgrWork = {
            dao.updateRefuel(refuel)
        })
    }

    fun delete(id: Long) {
        execute(bgrWork = {
            dao.deleteRefuel(id)
        })
    }

    fun getRefuel(id: Long, getRefuel: (Refuel?) -> Unit) {
        execute(bgrWork = {
            getRefuel(dao.getRefuel(id))
        })
    }

    fun getLast(refuel: (Refuel?) -> Unit) {
        execute(bgrWork = {
            refuel(dao.getLast())
        })
    }

    fun getRefuels() = dao.getRefuel()
}
