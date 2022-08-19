package com.test.trackensuredrivers.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.test.trackensuredrivers.data.model.GasStation

@Dao
abstract class GasStationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertGasStation(gasStation: GasStation)

    @Delete
    abstract fun deleteGasStation(gasStation: GasStation)

    @Update
    abstract fun updateGasStation(gasStation: GasStation)

    @Query("select * from gas_station where id = :id")
    abstract fun getGasStation(id: Long): GasStation?

    @Query("select * from gas_station")
    abstract fun getGasStations(): LiveData<List<GasStation>>

    @Query("select * from gas_station ORDER BY id DESC LIMIT 1")
    abstract fun getLast(): GasStation
}
