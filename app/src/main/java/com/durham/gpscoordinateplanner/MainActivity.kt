package com.durham.gpscoordinateplanner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks.await
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.lang.Exception
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    lateinit var btnLocation: Button
    lateinit var btnExport:Button
    lateinit var tvList:TextView
    lateinit var ebEntranceInfo:EditText
    private var locationGps : Location? = null
    private var locationNetwork : Location? = null
    private var locationList:ArrayList<Location> = ArrayList<Location>()
    private var coordinateList:ArrayList<Coordinate> = ArrayList<Coordinate>()

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
        ebEntranceInfo = findViewById(R.id.tbEditText)



        //initialize FLP (fused location provider client)

        flpc = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //BobCode
        var saveData = "Name, latitude, longitude, altitude, bearing, bearingAccuracyDegrees, accuracy, verticalAccuracy\n"
        for( i in coordinateList){
            saveData += i.Name + ", " + i.Loc.latitude + ", " + i.Loc.longitude + ", " + i.Loc.altitude + ", " + i.Loc.bearing + ", " +
            i.Loc.bearingAccuracyDegrees + ", " + i.Loc.accuracy + ", " + i.Loc.verticalAccuracyMeters + "\n"
        }

        if (requestCode == CREATE_FILE && resultCode == RESULT_OK){
            val uri = data!!.data
            try{
                val outputStream = this.contentResolver.openOutputStream(uri!!)
                outputStream?.write(saveData.toByteArray())
                outputStream?.close()
                Toast.makeText(this, "coordinate list saved",Toast.LENGTH_LONG).show()
            }catch(e:Exception){
                print(e.localizedMessage)
                Toast.makeText(this, "Error While Saving File",Toast.LENGTH_LONG).show()
            }
        }
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
                        coordinateList.add(Coordinate(ebEntranceInfo.text.toString(), location))
                        updateText()
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
    private fun updateText(){
        var ogs:StringBuilder = StringBuilder()
        for (l in coordinateList.reversed()) {
            ogs.append(
                "location: " + l.Name + "\nalt: " + l.Loc.altitude + "\nlat: " + l.Loc.latitude + "\nlong: " + l.Loc.longitude +
                        "\naccuracy: " + l.Loc.accuracy + "\n\n"
            )
            Log.d(
                "GPS SUCCESS",
                "Stringbuilder has appended the altitude, latitude, longitude, and accuracy"
            )
        }
        tvList.text = ogs.toString()
    }
    //Buttons

    fun onContinueClick(v: View){
        checkPermissions()
    }

    fun onExportClick(v:View){
        //https://developer.android.com/training/data-storage/shared/documents-files
        //text/csv
        createFile()
        //Snackbar.make(v, this.getFilesDir().toString(), Snackbar.LENGTH_LONG).show()
    }

    fun onEditTextClick(v:View){
        if (ebEntranceInfo.text.toString() == "Location Info"){
            ebEntranceInfo.setText("")
            Snackbar.make(v, "blanking text... not!", Snackbar.LENGTH_LONG).show()
        }
    }
    // Request code for creating a PDF document.
    val CREATE_FILE = 0

    private fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "coordinates.txt")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, "/CollegeProjectPTK2022")
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    fun onAddRecordClick(v:View){
        if(ebEntranceInfo.text.toString() != "" && ebEntranceInfo.text.toString() != "Location Info"){
            getLocation()
        }else{
            Snackbar.make(v, "must have building info in the text input box", Snackbar.LENGTH_LONG).show()
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

    }

}