package com.example.itzmeanjan.weatherz

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle

import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity: FlutterActivity() {

  var methodChannel: MethodChannel? = null

  @SuppressLint("CommitPrefEdits")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    GeneratedPluginRegistrant.registerWith(this)
    methodChannel = MethodChannel(flutterView, "method_channel_weatherZ")
    methodChannel?.setMethodCallHandler{
      methodCall, result ->
      when(methodCall.method){
        "isInitSetUpDone" -> {
          result.success(try{
            getSharedPreferences("weatherZ", Context.MODE_PRIVATE).contains("initSetUp")
          }catch (e: Exception){
            false
          })
        }
        "initSetUpDone" -> {
          result.success(try{
            getSharedPreferences("weatherZ", Context.MODE_PRIVATE).edit().apply {
              this.putBoolean("initSetUp", true)
            }
            true
          }
          catch (e: Exception){
            false
          })
        }
        "storeAPIKey" -> {
          result.success(
                  try{
                    getSharedPreferences("weatherZ", Context.MODE_PRIVATE).edit().apply {
                      this.putString("apiKey", methodCall.argument<String>("apiKey"))
                    }
                    true
                  }
                  catch (e: Exception){
                    false
                  }
          )
        }
        "openInTargetApp" -> {
          result.success(try{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(methodCall.argument<String>("getAPIKeyURL")))
            startActivity(intent)
            true
          }
          catch (e: Exception){
            false
          })
        }
        else -> result.notImplemented()
      }
    }
  }
}
