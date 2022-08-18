package com.test.trackensuredrivers

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.test.trackensuredrivers.data.database.AppDataBase
import com.test.trackensuredrivers.data.model.FuelType
import com.test.trackensuredrivers.data.model.GasStation
import com.test.trackensuredrivers.data.model.Refuel
import com.test.trackensuredrivers.databinding.ActivityMapsBinding
import com.test.trackensuredrivers.ui.viewmodel.MainViewModel
import com.test.trackensuredrivers.ui.viewmodel.MainViewModelFactory
import com.test.trackensuredrivers.utills.getAddress


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var viewModel: MainViewModel
    private val refuelToSave = Refuel()

    private val fuesdLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val mLocationManager by lazy {
        getSystemService(LOCATION_SERVICE) as LocationManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = application
        val dataSource = AppDataBase.getDatabase(application).gasStationDao
        val viewModelFactory = MainViewModelFactory(application)
        viewModel =
            ViewModelProvider(
                this, viewModelFactory
            )[MainViewModel::class.java]

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            FuelType.values().map { it.toString() })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.typeFuelSpinner.adapter = adapter

        binding.saveBtn.setOnClickListener {
            val amount = binding.amountEditText.text.toString().trim().toFloat()
            val price = binding.priceEditText.text.toString().trim().toFloat()
            val supplier = binding.supplierEditText.text.toString()
            val type = binding.typeFuelSpinner.selectedItem.toString()

            refuelToSave.amount = amount
            refuelToSave.price = price
            refuelToSave.type = type
            refuelToSave.supplier = supplier

            if (refuelToSave.nameGasStation.isNotEmpty()) {
                if (amount != 0f && price != 0f)
                    viewModel.insertRefuel(refuelToSave)
                else
                    Snackbar.make(binding.root, "Input all fields", Snackbar.LENGTH_SHORT).show()
            } else
                Snackbar.make(binding.root, "Chose gas station", Snackbar.LENGTH_SHORT).show()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Need location permission")
//                    .setMessage("safalfa")
                    .setPositiveButton("OK") { dialogInterface, i -> //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {

                        //Request location updates:
                        requestUpdateLocation()
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    private fun showGPSDisabledAlertToUser() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Goto Settings Page To Enable GPS"
            ) { dialog, id ->
                val callGPSSettingIntent = Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                startActivity(callGPSSettingIntent)
            }
        alertDialogBuilder.setNegativeButton(
            "Cancel"
        ) { dialog, id -> dialog.cancel() }
        val alert = alertDialogBuilder.create()
        alert.show()
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun requestUpdateLocation() {

        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            showGPSDisabledAlertToUser()


        val mLocationListener = LocationListener {
            Log.d("test2", it.longitude.toString())
        }

        mLocationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
            LOCATION_REFRESH_DISTANCE, mLocationListener
        )

        mMap.isMyLocationEnabled = true

        var currentLocation: Location
        val task = fuesdLocationClient.lastLocation
        task.addOnSuccessListener {
            if (it != null) {
                currentLocation = it
                val latLng = LatLng(
                    currentLocation.latitude,
                    currentLocation.longitude
                )
                mMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng,
                        15f
                    )
                )
//                mMap.addMarker(MarkerOptions().position(latLng))
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        mMap.setOnMarkerClickListener { marker ->
            val position = marker.position
            viewModel.allGasStation.observe(this) {
                it.forEach { gasStation ->
                    if (gasStation.latitude == position.latitude && gasStation.longitude == position.longitude) {
                        refuelToSave.gasStationId = gasStation.id
                        refuelToSave.nameGasStation = gasStation.name
                    }
                }
            }
            false
        }


        viewModel.allGasStation.observe(this) {
            it?.forEach { gasStation ->
                val marker =
                    MarkerOptions().position(LatLng(gasStation.latitude, gasStation.longitude))
                        .title(gasStation.name)
                googleMap.addMarker(marker)
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkLocationPermission()
        } else {
            requestUpdateLocation()
        }

    }

    override fun onMapLongClick(latLng: LatLng) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Enter the name of the gas station")
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK") { dialog, which ->
            val gasStationAddress: String = getAddress(latLng)
            viewModel.insertGasStation(
                GasStation(
                    name = input.text.toString(),
                    latitude = latLng.latitude,
                    longitude = latLng.longitude
                )
            )
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(input.text.toString())
            )
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
        builder.show()

    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99

        private const val LOCATION_REFRESH_TIME = 15000L // 15 seconds to update
        private const val LOCATION_REFRESH_DISTANCE = 500f
    }
}
