package com.test.trackensuredrivers.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.test.trackensuredrivers.data.model.GasStation
import com.test.trackensuredrivers.data.model.Refuel

@Dao
abstract class RefuelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRefuel(refuel: Refuel)

    @Query("DELETE FROM refuel_table WHERE id = :id")
    abstract fun deleteRefuel(id: Long)

    @Update
    abstract fun updateRefuel(refuel: Refuel)

    @Query("select * from refuel_table where id = :id")
    abstract fun getRefuel(id: Long): Refuel?

    @Query("select * from refuel_table")
    abstract fun getRefuel(): LiveData<List<Refuel>>

    @Query("select * from refuel_table ORDER BY id DESC LIMIT 1")
    abstract fun getLast(): Refuel
}