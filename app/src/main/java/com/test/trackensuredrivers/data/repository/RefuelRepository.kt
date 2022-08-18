package com.test.trackensuredrivers.data.repository

import androidx.lifecycle.MutableLiveData
import com.test.trackensuredrivers.data.database.dao.RefuelDao
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

    fun delete(id: Int) {
        execute(bgrWork = {
            dao.deleteRefuel(id)
        })
    }

    fun getRefuel(id: Int): MutableLiveData<Refuel> {
        val liveDataRefuel = MutableLiveData<Refuel>()
        execute(bgrWork = {
            liveDataRefuel.postValue(dao.getRefuel(id))
        })
        return liveDataRefuel
    }

    fun getRefuels() = dao.getRefuel()
}
