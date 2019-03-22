package com.example.itzmeanjan.weatherz

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import androidx.room.Room

import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterActivity() {

    var methodChannel: MethodChannel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(this)
        methodChannel = MethodChannel(flutterView, "method_channel_weatherZ")
        methodChannel?.setMethodCallHandler { methodCall, result ->
            when (methodCall.method) {
                "isInitSetUpDone" -> {
                    result.success(try {
                        getSharedPreferences("weatherZ", Context.MODE_PRIVATE).contains("initSetUp")
                    } catch (e: Exception) {
                        false
                    })
                }
                "initSetUpDone" -> {
                    result.success(try {
                        getSharedPreferences("weatherZ", Context.MODE_PRIVATE).edit().apply {
                            this.putBoolean("initSetUp", true)
                        }
                        true
                    } catch (e: Exception) {
                        false
                    })
                }
                "storeAPIKey" -> {
                    result.success(
                            try {
                                getSharedPreferences("weatherZ", Context.MODE_PRIVATE).edit().apply {
                                    this.putString("apiKey", methodCall.argument<String>("apiKey"))
                                }
                                true
                            } catch (e: Exception) {
                                false
                            }
                    )
                }
                "openInTargetApp" -> {
                    result.success(try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(methodCall.argument<String>("getAPIKeyURL")))
                        startActivity(intent)
                        true
                    } catch (e: Exception) {
                        false
                    })
                }
                "inflateCityNamesDataBase" -> {
                    val cityNamesDataBase = Room.databaseBuilder(applicationContext, CityNamesDataBase::class.java, "cityNamesDataBase.db").build()
                    val cityNamesDataBaseInflaterCallback = object : CityNamesDataBaseInflaterCallback {
                        override fun success() {
                            cityNamesDataBase.close()
                            result.success(1)
                        }

                        override fun failure() {
                            cityNamesDataBase.close()
                            result.success(0)
                        }
                    }
                    val cityNamesDataBaseAsync = CityNamesDataBaseAsync(cityNamesDataBase.getCityNamesDao(), cityNamesDataBaseInflaterCallback)
                    cityNamesDataBaseAsync.execute(
                            *methodCall.argument<List<Map<String, String>>>("cityNames")!!.map {
                                CityNames(it.getValue("id"), it["name"] ?: "null", it["country"]
                                        ?: "null", it["lon"] ?: "null", it["lat"] ?: "null")
                            }.toTypedArray()
                    )
                }
                "getCityNames" -> {
                    val cityNamesDataBase = Room.databaseBuilder(applicationContext, CityNamesDataBase::class.java, "cityNamesDataBase.db").build()
                    val getCityNamesCallBack = object : GetCityNamesCallBack {
                        override fun success(cityNames: List<CityNames>) {
                            cityNamesDataBase.close()
                            result.success(
                                    cityNames.map {
                                        mapOf(
                                                "id" to it.cityId,
                                                "name" to it.cityName,
                                                "country" to it.countryCode,
                                                "lon" to it.lon,
                                                "lat" to it.lat
                                        )
                                    }.toList())
                        }

                        override fun failure() {
                            cityNamesDataBase.close()
                            result.success(listOf<Map<String, String>>())
                        }
                    }
                    GetCityNamesAsync(cityNamesDataBase.getCityNamesDao(), getCityNamesCallBack).execute()
                }
                else -> result.notImplemented()
            }
        }
    }
}

class CityNamesDataBaseAsync(private val cityNamesDao: CityNamesDao, private val cityNamesDataBaseInflaterCallback: CityNamesDataBaseInflaterCallback) : AsyncTask<CityNames, Void, Int>() {

    override fun doInBackground(vararg params: CityNames): Int {
        return try {
            cityNamesDao.insertData(*params)
            1
        } catch (e: Exception) {
            0
        }
    }

    override fun onPostExecute(result: Int) {
        super.onPostExecute(result)
        if (result == 1)
            cityNamesDataBaseInflaterCallback.success()
        else
            cityNamesDataBaseInflaterCallback.failure()
    }
}

interface CityNamesDataBaseInflaterCallback {
    fun success()
    fun failure()
}

class GetCityNamesAsync(private val cityNamesDao: CityNamesDao, private val getCityNamesCallBack: GetCityNamesCallBack) : AsyncTask<Void, Void, List<CityNames>>() {
    override fun doInBackground(vararg params: Void?): List<CityNames> {
        return try {
            cityNamesDao.getCityNames()
        } catch (e: Exception) {
            listOf()
        }
    }

    override fun onPostExecute(result: List<CityNames>) {
        super.onPostExecute(result)
        if (result.isNotEmpty())
            getCityNamesCallBack.success(result)
        else
            getCityNamesCallBack.failure()
    }
}

interface GetCityNamesCallBack {
    fun success(cityNames: List<CityNames>)
    fun failure()
}