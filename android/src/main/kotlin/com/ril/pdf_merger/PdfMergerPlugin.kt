package com.ril.pdf_merger

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import androidx.exifinterface.media.ExifInterface
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.vudroid.core.DecodeServiceBase
import org.vudroid.core.codec.CodecPage
import org.vudroid.pdfdroid.codec.PdfContext
import java.io.ByteArrayOutputStream
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
      mergeMultiplePDF(call.argument("paths"), call.argument("outputDirPath"))
    }else if (call.method == "createPDFFromMultipleImage") {
      createPDFFromMultipleImage(call.argument("paths"), call.argument("outputDirPath"), call.argument("needImageCompressor")
              , call.argument("maxWidth"), call.argument("maxHeight"))
    }else if (call.method == "createImageFromPDF") {
      createImageFromPDF(call.argument("path"), call.argument("outputDirPath")
              , call.argument("maxWidth"), call.argument("maxHeight"), call.argument("createOneImage"))
    }else if (call.method == "sizeForLocalFilePath") {
      sizeForLocalFilePath(call.argument("path"))
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  @Throws(IOException::class)
  private fun mergeMultiplePDF(paths: List<String>?, outputDirPath: String?){
    var status = ""

    PDFBoxResourceLoader.init(context.getApplicationContext())

    val singlePDFFromMultiplePDF =  GlobalScope.launch(Dispatchers.IO) {

      val ut = PDFMergerUtility()

      for (item in paths!!){
        val file = File(item)
        ut.addSource(file)      }

      val file = File(outputDirPath!!)
      val fileOutputStream = FileOutputStream(file)
      try {
        ut.destinationStream = fileOutputStream
        ut.mergeDocuments(false)
        status = "success"
      } catch (e: Exception){
        status = "error"
      }finally {
        fileOutputStream.close()
      }
    }

    singlePDFFromMultiplePDF.invokeOnCompletion {
      if(status == "success")
        status = outputDirPath!!
      else if(status == "error")
        status = "error"

      GlobalScope.launch(Dispatchers.Main) {
        result.success(status)
      }
    }
  }


  @TargetApi(Build.VERSION_CODES.KITKAT)
  private fun createPDFFromMultipleImage(paths: List<String>?, outputDirPath: String?, needImageCompressor : Boolean?
                                         , maxWidth : Int?, maxHeight : Int?)  {
    var status = ""

    val pdfFromMultipleImage =  GlobalScope.launch(Dispatchers.IO) {
      try {
        val file = File(outputDirPath!!)
        val fileOutputStream = FileOutputStream(file)
        val pdfDocument = PdfDocument()
        val i=0;
        for (item in paths!!){
          var bitmap : Bitmap?

          if(needImageCompressor!!)
           bitmap =  compressImage(context, item, maxWidth!!, maxHeight!!)
          else
            bitmap = BitmapFactory.decodeFile(item)


          val pageInfo = PageInfo.Builder(bitmap!!.width, bitmap.height, i + 1).create()
          val page = pdfDocument.startPage(pageInfo)
          val canvas = page.canvas
          val paint = Paint()
          canvas.drawPaint(paint)
          canvas.drawBitmap(bitmap, 0f, 0f, paint)
          pdfDocument.finishPage(page)
          bitmap.recycle()
        }
        pdfDocument.writeTo(fileOutputStream)
        pdfDocument.close()
        status = "success"
      } catch (e: IOException) {
        e.printStackTrace()
        status = "error"

      }
    }

    pdfFromMultipleImage.invokeOnCompletion {
      if(status == "success")
        status = outputDirPath!!
      else if(status == "error")
        status = "error"

      GlobalScope.launch(Dispatchers.Main) {
        result.success(status)
      }
    }
  }


  @TargetApi(Build.VERSION_CODES.KITKAT)
  private fun createImageFromPDF(path: String?, outputDirPath: String?, maxWidth : Int?, maxHeight : Int?
                                 , createOneImage : Boolean?)  {
    var status = ""
    val pdfImagesPath :  MutableList<String> = mutableListOf<String>()


    val pdfFromMultipleImage =  GlobalScope.launch(Dispatchers.IO) {
      try {

        val decodeService = DecodeServiceBase(PdfContext())
        decodeService.setContentResolver(context.contentResolver)

        val file = File(path)
        decodeService.open(Uri.fromFile(file))

        val pdfImages :  MutableList<Bitmap> = mutableListOf<Bitmap>()

        val pageCount: Int = decodeService.pageCount
        for (i in 0 until pageCount) {
          val page: CodecPage = decodeService.getPage(i)
          val rectF = RectF(0.toFloat(), 0.toFloat(), 1.toFloat(), 1.toFloat())

          val bitmap: Bitmap = page.renderBitmap(maxWidth!!, maxHeight!!, rectF)
          pdfImages.add(bitmap)

          if(!createOneImage!!){

            val splitPath = outputDirPath!!.split(".")[0]
            val splitPathExt = outputDirPath.split(".")[1];

            print(splitPath)
            print(splitPathExt)

            val newPath = """$splitPath$i.$splitPathExt"""
            pdfImagesPath.add(newPath)
            val outputStream = FileOutputStream(newPath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
          }
        }


        if(createOneImage!!){
          pdfImagesPath.add(outputDirPath!!)
          val bitmap = mergeThemAll(pdfImages, maxWidth!!, maxHeight!!)
          val outputStream = FileOutputStream(outputDirPath)
          bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
          outputStream.close()
        }

        status = "success"
      } catch (e: IOException) {
        e.printStackTrace()
        status = "error"

      }
    }

    pdfFromMultipleImage.invokeOnCompletion {
      GlobalScope.launch(Dispatchers.Main) {
        result.success(pdfImagesPath)
      }
    }
  }



  private fun sizeForLocalFilePath(path: String?)  {
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


  private fun compressImage(context: Context, imagePath: String, maxWidthGet : Int, maxHeightGet : Int): Bitmap? {

    val maxHeight = maxWidthGet.toFloat()
    val maxWidth = maxHeightGet.toFloat()

    var scaledBitmap: Bitmap?

    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    @Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
    var bmp: Bitmap? = BitmapFactory.decodeFile(imagePath, options)

    var actualHeight = options.outHeight
    var actualWidth = options.outWidth

    var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
    val maxRatio = maxWidth / maxHeight

    if (actualHeight > maxHeight || actualWidth > maxWidth) {
      if (imgRatio < maxRatio) {
        imgRatio = maxHeight / actualHeight
        actualWidth = (imgRatio * actualWidth).toInt()
        actualHeight = maxHeight.toInt()
      } else if (imgRatio > maxRatio) {
        imgRatio = maxWidth / actualWidth
        actualHeight = (imgRatio * actualHeight).toInt()
        actualWidth = maxWidth.toInt()
      } else {
        actualHeight = maxHeight.toInt()
        actualWidth = maxWidth.toInt()

      }
    }

    calculateInSampleSize(options, actualWidth, actualHeight).also { options.inSampleSize = it }
    false.also { options.inJustDecodeBounds = it }
    false.also { options.inDither = it }
    true.also { options.inPurgeable = it }
    true.also { options.inInputShareable = it }
    ByteArray(16 * 1024).also { options.inTempStorage = it }

    try {
      bmp = BitmapFactory.decodeFile(imagePath, options)
      val baos = ByteArrayOutputStream()
      bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos)
    } catch (exception: OutOfMemoryError) {
      exception.printStackTrace()
      return null
    }

    try {
      scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565)
    } catch (exception: OutOfMemoryError) {
      exception.printStackTrace()
      return null
    }

    val ratioX = actualWidth / options.outWidth.toFloat()
    val ratioY = actualHeight / options.outHeight.toFloat()
    val middleX = actualWidth / 2.0f
    val middleY = actualHeight / 2.0f

    val scaleMatrix = Matrix()
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

    Canvas(scaledBitmap).also {
      it.setMatrix(scaleMatrix)
      it.drawBitmap(bmp, middleX - bmp!!.width / 2, middleY - bmp.height / 2, Paint(FILTER_BITMAP_FLAG))
    }

    bmp.run {
      recycle()
    }

    val exif: ExifInterface
    try {
      exif = ExifInterface(imagePath)
      val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
      val matrix = Matrix()
      when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
      }
      Bitmap.createBitmap(scaledBitmap!!,
              0,
              0,
              scaledBitmap.width,
              scaledBitmap.height,
              matrix,
              true).also { scaledBitmap = it }
    } catch (e: IOException) {
      e.printStackTrace()
    }
    return scaledBitmap
  }

  private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
      val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
      val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
      inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    val totalPixels = (width * height).toFloat()
    val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()

    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
      inSampleSize++
    }

    return inSampleSize
  }

  private fun mergeThemAll(orderImagesList: List<Bitmap>?, maxWidth: Int, maxHeight: Int): Bitmap? {
    var result: Bitmap? = null
    if (orderImagesList != null && orderImagesList.isNotEmpty()) {
      val chunkWidth: Int = orderImagesList[0].width
      val chunkHeight: Int = orderImagesList[0].height

      result = Bitmap.createBitmap(maxWidth, maxHeight *orderImagesList.size, Bitmap.Config.RGB_565)
      Log.d("myTag", "Create Bitmap")
      val canvas = Canvas(result)
      val paint = Paint()
      var chunkHeightCal: Int = 0
      for (i in orderImagesList.indices) {
        canvas.drawBitmap(orderImagesList[i],(0).toFloat(), (chunkHeightCal).toFloat(), paint)
        chunkHeightCal =  chunkHeightCal + maxHeight
      }
    } else {
      Log.e("MergeError", "Couldn't merge bitmaps")
    }
    return result
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

