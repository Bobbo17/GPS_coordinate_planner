package com.durham.gpscoordinateplanner

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*
import java.lang.Exception

class JSONSerializer (private val filename:String, private val context: Context) {

    //save function
//    @Throws(IOException::class, JSONException::class) fun save(cd:CoordinateData){
//
//        val jArray = JSONArray() //more comparable to arrayList
//
//        jArray.put(cd.parseJSON())
//
//        lateinit var w: Writer
//        try {
//            val out = context.openFileOutput(filename, Context.MODE_PRIVATE)
//            w = OutputStreamWriter(out)
//            w.write(jArray.toString())
//        }catch (e:Exception){
//
//        } finally {
//            if (w != null){
//
//                w.close()
//            }
//        }
//    }

//    //load function
//    @Throws(IOException::class, JSONException::class) fun load(): CoordinateData {
//
//        lateinit var r: BufferedReader
//
//        val j = context.openFileInput(filename)
//        r = BufferedReader(InputStreamReader(j))
//        val jsonString = StringBuilder()
//
//        for (line in r.readLine()) jsonString.append(line)
//
//        val jArray = JSONTokener(jsonString.toString()).nextValue() as JSONArray
//
//        val cD = CoordinateData(jArray.getJSONObject(0))
//
////
//        r.close()
//
//        try {
//
//
//        }catch (e: FileNotFoundException) {
//
//        } finally {
//
//        }
//        return cD
//    }
}