package com.ril.pdf_merger

import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import org.apache.pdfbox.io.MemoryUsageSetting
import org.apache.pdfbox.multipdf.PDFMergerUtility
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/** PdfMergerPlugin */
class PdfMergerPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "pdf_merger")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "mergePDFPath") {
      result.success(mergePDFs(call.argument("paths"),call.argument("outputDirPath")))
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  @Throws(IOException::class)
  private fun mergePDFs(paths: List<String>?, outputDirPath: String?): String? {

    val ut = PDFMergerUtility()

    for (item in paths!!){
      print("Loop")
      ut.addSource(item)
    }

    val file = File(outputDirPath!!)
    val fileOutputStream = FileOutputStream(file)
    try {
      ut.destinationStream = fileOutputStream
      ut.mergeDocuments(MemoryUsageSetting.setupTempFileOnly())
    } catch (e : Exception){
      return "Error"
    }finally {
      fileOutputStream.close()
    }

    return "Success"
  }

}

