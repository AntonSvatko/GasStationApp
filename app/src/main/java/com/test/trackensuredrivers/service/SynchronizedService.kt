package com.test.trackensuredrivers.service

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
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
import com.test.trackensuredrivers.utills.observeOnce


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

        val extras = intent?.extras

        if (extras?.containsKey(Constants.ADD_GAS_STATION_KEY) == true) {
            gasStationRepository.getLast {
                it?.localAmount = 0
                getGasStationList(it?.id)
                    ?.setValue(it)
                    ?.addOnSuccessListener {
                        stopSelf()
                    }
            }
        }
        if (extras?.containsKey(Constants.UPDATE_GAS_STATION_KEY) == true) {
            App.database?.reference?.child("gas_station")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val gasStationList = snapshot.getValue<HashMap<String, GasStation>>()
                        gasStationList?.forEach { gasStation ->
                            var isUpdated = false
                            gasStationRepository.getGasStations().observeOnce {
                                if (!isUpdated) {
                                    isUpdated = true
                                    val localGasStation =
                                        it.find { gasStation.value.id == it.id }
                                    if (localGasStation == null) {
                                        gasStationRepository.insert(gasStation.value)
                                    } else {
                                        gasStation.value.totalAmount += localGasStation.localAmount
                                        gasStationRepository.update(gasStation.value)
                                    }
                                }
                            }
                        }

                        if (!App.auth?.uid.isNullOrEmpty()) {
                            App.database?.reference?.child("refuels")
                                ?.child(App.auth?.uid.toString())
                                ?.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        var refuelList = listOf<Refuel?>()
                                        kotlin.runCatching {
                                            refuelList =
                                                snapshot.getValue<HashMap<String, Refuel?>>()?.values?.toList()
                                                    ?: listOf()
                                        }.onFailure {
                                            refuelList =
                                                snapshot.getValue<List<Refuel?>>() ?: listOf()
                                        }

                                        refuelRepository.getRefuels().observeOnce {
                                            refuelList.forEach { gasStation ->
                                                if (gasStation != null) {
                                                    val localGasStation =
                                                        it.find { gasStation.id == it.id }
                                                    if (localGasStation == null) {
                                                        refuelRepository.insert(gasStation)
                                                    } else {
                                                        refuelRepository.update(gasStation)
                                                    }
                                                }
                                            }
                                        }
                                        refuelRepository.getRefuels().observeOnce {
                                            if (refuelList.size < it.size) {
                                                it.forEach { localItem ->
                                                    val needDeleteItem =
                                                        refuelList.find { it?.id == localItem.id }
                                                    if (needDeleteItem == null) {
                                                        refuelRepository.delete(localItem.id)
                                                    }
                                                }
                                            }
                                        }
                                        stopSelf()
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })

        }
        if (extras?.containsKey(Constants.ADD_REFUEL_KEY) == true) {
            var isUpdated = false
            refuelRepository.getLast { refuel ->
                if (!App.auth?.uid.isNullOrEmpty())
                    App.database?.reference?.child("refuels")
                        ?.child(App.auth?.uid.toString())
                        ?.child(refuel?.id.toString())
                        ?.setValue(refuel)?.addOnSuccessListener {
                            getGasStationList(refuel?.gasStationId)
                                ?.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val gasStation = snapshot.getValue<GasStation>()
                                        gasStationRepository.getGasStation(
                                            refuel?.gasStationId ?: 0L
                                        ) {
                                            if (gasStation != null) {
                                                if (!isUpdated) {
                                                    isUpdated = true
                                                    gasStation.totalAmount += 1
                                                    getGasStationList(gasStation.id)
                                                        ?.setValue(gasStation)
                                                        ?.addOnSuccessListener {
                                                            stopSelf()
                                                        }
                                                }
                                            }
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                        }
            }
        }

        if (extras?.containsKey(Constants.DELETE_REFUEL_STATION_KEY) == true) {
            val id = intent.extras?.getLongArray(Constants.DELETE_REFUEL_STATION_KEY)
                ?: longArrayOf()
            if (!App.auth?.uid.isNullOrEmpty())
                App.database?.reference?.child("refuels")?.child(App.auth?.uid.toString())
                    ?.child(id[0].toString())
                    ?.removeValue()?.addOnSuccessListener {
                        var isUpdated = false
                        App.database?.reference?.child("gas_station")
                            ?.child(id[1].toString())
                            ?.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val gasStation = snapshot.getValue<GasStation>()
                                    gasStationRepository.getGasStation(
                                        id[1]
                                    ) {
                                        if (gasStation != null) {
                                            if (!isUpdated) {
                                                isUpdated = true
                                                gasStation.totalAmount -= 1
                                                getGasStationList(gasStation.id)
                                                    ?.setValue(gasStation)
                                                    ?.addOnSuccessListener {
                                                        stopSelf()
                                                    }
                                            }
                                        }
                                    }
                                    stopSelf()
                                }

                                override fun onCancelled(error: DatabaseError) {}
                            })

                    }
        }

        return START_NOT_STICKY
    }

    fun getGasStationList(id: Long?) =
        App.database?.reference?.child("gas_station")
            ?.child(id.toString())


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
