package com.ril.pdf_merger

import android.content.Context
import android.graphics.*
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler


/** PdfMergerPlugin */
class PdfMergerPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context: Context
  private  lateinit var result : MethodChannel.Result


  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "pdf_merger")
    channel.setMethodCallHandler(this)
    this.context = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
    this.result = result
    if (call.method == "mergeMultiplePDF") {
      MergeMultiplePDF(context, result).merge(call.argument("paths"), call.argument("outputDirPath"))
    }else if (call.method == "createPDFFromMultipleImage") {
      CreatePDFFromMultipleImage(context, result).create(call.argument("paths"), call.argument("outputDirPath"), call.argument("needImageCompressor")
              , call.argument("maxWidth"), call.argument("maxHeight"))
    }else if (call.method == "createImageFromPDF") {
      CreateImageFromPDF(context, result).create(call.argument("path"), call.argument("outputDirPath")
              , call.argument("maxWidth"), call.argument("maxHeight"), call.argument("createOneImage"))
    }else if (call.method == "sizeForLocalFilePath") {
      SizeFormFilePath(context, result).size(call.argument("path"))
    }else if (call.method == "buildDate") {
      BuildInfo(context, result).buildDate()
    }else if (call.method == "buildDateWithTime") {
      BuildInfo(context, result).buildDateWithTime()
    }else if (call.method == "versionName") {
      BuildInfo(context, result).versionName()
    }else if (call.method == "versionCode") {
      BuildInfo(context, result).versionCode()
    }else if (call.method == "packageName") {
      BuildInfo(context, result).packageName()
    }else if (call.method == "appName") {
      BuildInfo(context, result).appName()
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }










}

