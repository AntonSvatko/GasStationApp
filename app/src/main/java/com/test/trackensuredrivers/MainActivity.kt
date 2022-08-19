package com.test.trackensuredrivers

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.test.trackensuredrivers.App.Companion.auth
import com.test.trackensuredrivers.service.SynchronizedService
import com.test.trackensuredrivers.ui.viewpager.ViewPagerAdapter
import com.test.trackensuredrivers.utills.Constants


class MainActivity : AppCompatActivity() {
    private lateinit var client: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.SERVER_ID)
            .requestEmail()
            .build()

        client = GoogleSignIn.getClient(this, gso)

        val myRef = App.database?.getReference("gas_stations")

//        myRef?.setValue("Hello, World!")?.addOnCompleteListener {
//            Log.d("test2", it.exception.toString())
//        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(Intent(this, SynchronizedService::class.java))
//        } else {

//        }

        val intent = Intent(this, SynchronizedService::class.java)
        intent.putExtra(Constants.UPDATE_GAS_STATION_KEY, 0)
        startService(intent)

        val pager = findViewById<ViewPager2>(R.id.pager)
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        pager.adapter = adapter

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.addTab(tabLayout.newTab().setText("Refuel"))
        tabLayout.addTab(tabLayout.newTab().setText("Gas Stations"))

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        pager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

    override fun onStart() {
        super.onStart()
        App.currentUser = auth?.currentUser
        if (App.currentUser == null) {
            resultLauncher.launch(client.signInIntent)
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account: GoogleSignInAccount =
                    task.getResult(ApiException::class.java)
                account.idToken?.let { firebaseAuthWithGoogle(it) }
            }
        }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    App.currentUser = auth?.currentUser
                } else {
                    Log.d("exe1", task.exception.toString() + "asfsa")
                }
            }
    }
}