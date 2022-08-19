package com.test.trackensuredrivers.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.test.trackensuredrivers.data.database.dao.GasStationDao
import com.test.trackensuredrivers.data.database.dao.RefuelDao
import com.test.trackensuredrivers.data.model.GasStation
import com.test.trackensuredrivers.data.model.Refuel

@Database(entities = [GasStation::class, Refuel::class], version = 7)
abstract class AppDataBase : RoomDatabase() {
    abstract val gasStationDao: GasStationDao
    abstract val refuelDao: RefuelDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "gas_stations_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}