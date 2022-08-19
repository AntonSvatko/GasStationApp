package com.test.trackensuredrivers

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.test.trackensuredrivers.utills.Constants

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        auth = FirebaseAuth.getInstance()

        database = FirebaseDatabase.getInstance(Constants.REAL_TIME_DATABASE_LINK)
    }

    companion object {
        var currentUser: FirebaseUser? = null
        var database: FirebaseDatabase? = null
        var auth: FirebaseAuth? = null
    }
}