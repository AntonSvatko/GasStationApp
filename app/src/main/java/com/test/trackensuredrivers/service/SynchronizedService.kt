package com.test.trackensuredrivers.service

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.test.trackensuredrivers.App
import com.test.trackensuredrivers.MainActivity
import com.test.trackensuredrivers.R
import com.test.trackensuredrivers.data.database.AppDataBase
import com.test.trackensuredrivers.data.model.GasStation
import com.test.trackensuredrivers.data.model.Refuel
import com.test.trackensuredrivers.data.repository.GasStationRepository
import com.test.trackensuredrivers.data.repository.RefuelRepository
import com.test.trackensuredrivers.utills.Constants

class SynchronizedService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            generateForegroundNotification()
        }

        val gasStationDap = AppDataBase.getDatabase(application).gasStationDao
        val refuelDao = AppDataBase.getDatabase(application).refuelDao
        val gasStationRepository = GasStationRepository(gasStationDap)
        val refuelRepository = RefuelRepository(refuelDao)

        if (intent?.extras?.containsKey(Constants.ADD_GAS_STATION_KEY) == true) {
            gasStationRepository.getLast {
                it?.localAmount = 0
                App.database?.reference?.child("gas_station")?.child(it?.id.toString())
                    ?.setValue(it)
            }
        }
        if (intent?.extras?.containsKey(Constants.UPDATE_GAS_STATION_KEY) == true) {
            App.database?.reference?.child("gas_station")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val gasStationList = snapshot.getValue<HashMap<String, GasStation>>()
                        gasStationList?.forEach { gasStation ->
                            var isUpdated = false
                            gasStationRepository.getGasStations().observeForever {
                                if (!isUpdated) {
                                    isUpdated = true
                                    val localGasStation = it.find { gasStation.value.id == it.id }
                                    if (localGasStation == null) {
                                        gasStationRepository.insert(gasStation.value)
                                    } else {
                                        gasStation.value.totalAmount += localGasStation.localAmount
                                        gasStationRepository.update(gasStation.value)
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

            if (!App.auth?.uid.isNullOrEmpty()) {
                App.database?.reference?.child("refuels")?.child(App.auth?.uid.toString())
                    ?.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            Log.d("test6", snapshot.value.toString())
                            val gasStationList = snapshot.getValue<HashMap<String, Refuel>>()
                            gasStationList?.forEach { gasStation ->
                                var isUpdated = false
                                refuelRepository.getRefuels().observeForever {
                                    if (!isUpdated) {
                                        isUpdated = true
                                        val localGasStation =
                                            it.find { gasStation.value.id == it.id }
                                        if (localGasStation == null) {
                                            refuelRepository.insert(gasStation.value)
                                        } else {
                                            refuelRepository.update(gasStation.value)
                                        }
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
            }
        }
        if (intent?.extras?.containsKey(Constants.ADD_REFUEL_STATION_KEY) == true) {
            val idGasStation = intent.extras?.getLong(Constants.ADD_REFUEL_STATION_KEY)!!
            var isUpdated = false
            refuelRepository.getLast {
                if (!App.auth?.uid.isNullOrEmpty())
                    App.database?.reference?.child("refuels")
                        ?.child(App.auth?.uid.toString())
                        ?.child(it?.id.toString())
                        ?.setValue(it)
            }

            App.database?.reference?.child("gas_station")?.child(idGasStation.toString())
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val gasStation = snapshot.getValue<GasStation>()
                        gasStationRepository.getGasStation(idGasStation) {
                            if (gasStation != null) {
                                if (!isUpdated) {
                                    isUpdated = true
                                    gasStation.totalAmount += it?.localAmount ?: 0
                                    it?.localAmount = 0
                                    App.database?.reference?.child("gas_station")
                                        ?.child(it?.id.toString())
                                        ?.setValue(it)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
        if (intent?.extras?.containsKey(Constants.DELETE_REFUEL_STATION_KEY) == true) {
            val id = intent.extras?.getLong(Constants.DELETE_REFUEL_STATION_KEY)!!
            if (!App.auth?.uid.isNullOrEmpty())
                gasStationRepository.getLast {
                    it?.localAmount = 0
                    App.database?.reference?.child("refuels")?.child(App.auth?.uid.toString())
                        ?.child(id.toString())
                        ?.removeValue()
                }
        }


//        gasStationRepository.getGasStations().observeForever {
//            App.database?.reference?.child("gas_station")?.setValue(it)?.addOnCompleteListener {
//                Log.d("test3", it.toString())
//            }
//        }

        return START_NOT_STICKY
    }

    private var iconNotification: Bitmap? = null
    private var notification: Notification? = null
    var mNotificationManager: NotificationManager? = null
    private val mNotificationId = 123

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateForegroundNotification() {
        val intentMainLanding = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intentMainLanding, FLAG_IMMUTABLE)
        iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        if (mNotificationManager == null) {
            mNotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        assert(mNotificationManager != null)
        mNotificationManager?.createNotificationChannelGroup(
            NotificationChannelGroup("chats_group", "Chats")
        )
        val notificationChannel =
            NotificationChannel(
                "service_channel", "Service Notifications",
                NotificationManager.IMPORTANCE_MIN
            )
        notificationChannel.enableLights(false)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
        mNotificationManager?.createNotificationChannel(notificationChannel)
        val builder = NotificationCompat.Builder(this, "service_channel")

        builder.setContentTitle(
            StringBuilder(resources.getString(R.string.app_name)).append(" service is running")
                .toString()
        )
            .setTicker(
                StringBuilder(resources.getString(R.string.app_name)).append("service is running")
                    .toString()
            )
            .setContentText("Touch to open")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setWhen(0)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
        if (iconNotification != null) {
            builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification!!, 128, 128, false))
        }
        builder.color = resources.getColor(R.color.purple_200)
        notification = builder.build()
        startForeground(mNotificationId, notification)
    }
}
