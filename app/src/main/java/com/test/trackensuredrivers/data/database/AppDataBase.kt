package com.test.trackensuredrivers.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.test.trackensuredrivers.data.database.dao.GasStationDao
import com.test.trackensuredrivers.data.model.GasStation

@Database(entities = [GasStation::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract val gasStationDao: GasStationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "gas_stations_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}