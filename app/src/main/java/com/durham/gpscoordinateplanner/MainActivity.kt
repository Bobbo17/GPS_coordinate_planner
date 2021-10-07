package com.durham.gpscoordinateplanner

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    lateinit var btnLocation: Button
    lateinit var btnExport:Button
    lateinit var tvList:TextView
    private var locationGps : Location? = null
    private var locationNetwork : Location? = null
    private var locationList:ArrayList<Location> = ArrayList<Location>()

    var hasGPS:Boolean = false
    var hasNetwork:Boolean = false
    lateinit var flpc: FusedLocationProviderClient
    lateinit var locationManager:LocationManager

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                setContentView(R.layout.main_a)
            } else {
                this@MainActivity.finish()
                exitProcess(0)
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()


//        btnLocation = findViewById(R.id.btnAddRecord)
//        btnExport = findViewById(R.id.btnExportList)
        tvList = findViewById(R.id.tvLocationList)



        //initialize FLP (fused location provider client)

        flpc = LocationServices.getFusedLocationProviderClient(this)



    }

    //launches the permission request for the specific record_audio permission required
    fun requestPermissions() {

        when {
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                setContentView(R.layout.main_a)

            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION)
                if ( ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                }
            }
        }
    }
    fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                setContentView(R.layout.main_a)

            }
            else -> {

            }
        }
    }

    //GPS Function
    fun getLocation() {

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGPS) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("CodeAndroidLocation", "hasGPS")
                                  }


            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,750,2F,object :
                LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (location!=null) {
                        locationGps = location
                        locationList.add(location)
                    }
                }

            }
            )
            val localGpsLocation  = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (localGpsLocation!=null){
                locationGps = localGpsLocation
            }

        }
        if (hasNetwork) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("CodeAndroidLocation", "hasNETWORK")
                }


            }

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,0F,object :
                LocationListener {
                override fun onLocationChanged(location: Location) {
                    if (location!=null) {
                        locationNetwork = location

                    }
                    else{
                        Log.d("Location Error:", "The location was null please try again")
                    }
                }

            }
            )
            val localNetworkLocation  = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (localNetworkLocation!=null){
                locationNetwork = localNetworkLocation
            }

        }
        if (locationGps!=null && locationNetwork != null){
            if (locationGps!!.accuracy > locationNetwork!!.accuracy){
                Log.d("AndroidLocationDebug", "Network Latitude: ${locationNetwork!!.latitude}" )
                Log.d("AndroidLocationDebug", "Network Longitude: ${locationNetwork!!.longitude}" )
            }else{
                Log.d("AndroidLocationDebug", "GPS Latitude: ${locationGps!!.latitude}" )
                Log.d("AndroidLocationDebug", "GPS Longitude: ${locationGps!!.longitude}" )
            }
        }else if (locationGps!=null){
            Log.d("AndroidLocationDebug", "GPS Latitude: ${locationGps!!.latitude}" )
            Log.d("AndroidLocationDebug", "GPS Longitude: ${locationGps!!.longitude}" )
        }

    }
//        when {
//            ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
//
//                flpc.lastLocation
//                    .addOnSuccessListener { location : Location? ->
//                        // Got last known location. In some rare situations this can be null.
//                        flpc.getLastLocation()
//                    }
//                return .getResult()
//            }
//            else -> {
//                return null
//            }
//        }


    fun AddRecord() {

    }

    //Buttons

    fun onContinueClick(v: View){
        checkPermissions()
    }

    fun onAddRecordClick(v:View){
        getLocation()
        var displayText:String = ""
        if (locationList.isNotEmpty()) {
            var ogs:StringBuilder = StringBuilder()
            for (l in locationList){
                ogs.append("alt: " + l.altitude + "\nlat: " + l.latitude + "\nlong: " + l.longitude +
                        "\naccuracy: " + l.accuracy + "\n")
                Log.d("GPS SUCCESS", "Stringbuilder has appended the altitude, latitude, longitude, and accuracy")
            }
            displayText = ogs.toString()
        } else{
            

        }
//        val l = locationGps
//        var ogs:String = tvList.text.toString()
//        if (l != null){
//            val s: String = l.latitude.toString() + " , " + l.longitude.toString() + " , " +
//                    l.altitude.toString()
//            ogs += "\n$s"
//        }else {
//            ogs += "\nlocation value was null"
//        }
        tvList.text = displayText
    }

}