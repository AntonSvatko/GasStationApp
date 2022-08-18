package com.test.trackensuredrivers.data.repository

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


    fun getRefuels() = dao.getRefuel()
}