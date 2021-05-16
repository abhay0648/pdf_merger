package com.ril.pdf_merger

import android.content.Context
import android.content.pm.PackageInfo
import io.flutter.plugin.common.MethodChannel
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class BuildInfo(getContext : Context, getResult : MethodChannel.Result) {

    private var context: Context = getContext
    private var result: MethodChannel.Result = getResult
    private val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

    fun buildDate()  {
        var status = ""
        try {
            val buildDate = Date(packageInfo.lastUpdateTime)
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            result.success(format.format(buildDate))
        } catch (e: IOException) {
            e.printStackTrace()
            status = "error"
            result.success(status)
        }
    }


    fun buildDateWithTime()  {
        var status = ""
        try {
            val buildDate = Date(packageInfo.lastUpdateTime)
            val format = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)
            result.success(format.format(buildDate))
        } catch (e: IOException) {
            e.printStackTrace()
            status = "error"
            result.success(status)
        }
    }


    fun versionName()  {
        var status = ""
        try {
            result.success(packageInfo.versionName)
        } catch (e: IOException) {
            e.printStackTrace()
            status = "error"
            result.success(status)
        }
    }


    fun versionCode()  {
        var status = ""
        try {
            result.success(packageInfo.versionCode.toString())
        } catch (e: IOException) {
            e.printStackTrace()
            status = "error"
            result.success(status)
        }
    }


    fun packageName()  {
        var status = ""
        try {
            result.success(packageInfo.packageName)
        } catch (e: IOException) {
            e.printStackTrace()
            status = "error"
            result.success(status)
        }
    }

    fun appName()  {
        var status = ""
        try {
            result.success(packageInfo.applicationInfo.loadLabel(context.packageManager).toString())
        } catch (e: IOException) {
            e.printStackTrace()
            status = "error"
            result.success(status)
        }
    }

}

