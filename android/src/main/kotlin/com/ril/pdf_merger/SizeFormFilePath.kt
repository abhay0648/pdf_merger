package com.ril.pdf_merger

import android.content.Context
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException


class SizeFormFilePath(getContext : Context, getResult : MethodChannel.Result) {

    private var context: Context = getContext
    private var result: MethodChannel.Result = getResult


    fun size(path: String?)  {
        var status = ""

        val pdfFromMultipleImage =  GlobalScope.launch(Dispatchers.IO) {
            try {
                val file = File(path)
                file.sizeInMb
                if (file.sizeInKb > 1024){
                    if (file.sizeInMb > 1024){
                        if (file.sizeInGb > 1024){
                            status = file.sizeStrWithTb(decimals = 2)
                        }else{
                            status = file.sizeStrWithGb(decimals = 2)
                        }
                    }else{
                        status = file.sizeStrWithMb(decimals = 2)
                    }
                }else{
                    status = file.sizeStrWithKb(decimals = 2)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                status = "error"

            }
        }

        pdfFromMultipleImage.invokeOnCompletion {
            if(status == "error")
                status = "error"

            GlobalScope.launch(Dispatchers.Main) {
                result.success(status)
            }
        }
    }



    val File.size get() = if (!exists()) 0.0 else length().toDouble()
    val File.sizeInKb get() = size / 1000
    val File.sizeInMb get() = sizeInKb / 1000
    val File.sizeInGb get() = sizeInMb / 1000
    val File.sizeInTb get() = sizeInGb / 1000

    fun File.sizeStr(): String = size.toString()
    fun File.sizeStrInKb(decimals: Int = 0): String = "%.${decimals}f".format(sizeInKb)
    fun File.sizeStrInMb(decimals: Int = 0): String = "%.${decimals}f".format(sizeInMb)
    fun File.sizeStrInGb(decimals: Int = 0): String = "%.${decimals}f".format(sizeInGb)
    fun File.sizeStrInTb(decimals: Int = 0): String = "%.${decimals}f".format(sizeInTb)

    fun File.sizeStrWithBytes(): String = sizeStr() + "(bytes)"
    fun File.sizeStrWithKb(decimals: Int = 0): String = sizeStrInKb(decimals) + "(KB)"
    fun File.sizeStrWithMb(decimals: Int = 0): String = sizeStrInMb(decimals) + "(MB)"
    fun File.sizeStrWithGb(decimals: Int = 0): String = sizeStrInGb(decimals) + "(GB)"
    fun File.sizeStrWithTb(decimals: Int = 0): String = sizeStrInTb(decimals) + "(TB)"

}