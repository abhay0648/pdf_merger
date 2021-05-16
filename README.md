# pdf_merger

A new Flutter plugin for merge List of PDF files. It supports both android and IOS. Before call this package make sure you allow permission and file picker see example.
Also check file_picker and permission handle to implement before calling the plugin. 


## PDF merger method


# Merge Multiple PDF

```

  //paths is a list of paths, example List<String> allSelectedFilePath. 
  //outputDirPath is output path with filename, exmaple /user/android/download/ABC.pdf
  
 MergeMultiplePDFResponse response  = await PdfMerger.mergeMultiplePDF(paths: filesPath, outputDirPath: outputDirPath);
 
  if(response.status == "success") {
  //response.response for output path  in String
  //response.message for success message  in String
  }
  
  
```

# Create PDF From Multiple Image

```

  //paths is a list of paths, example List<String> allSelectedFilePath. 
  //outputDirPath is output path with filename, exmaple /user/android/download/ABC.pdf
  //Optional params maxWidth : defalut set to 360, maxHeight : defalut set to 360, needImageCompressor : default set to true.
  
 CreatePDFFromMultipleImageResponse response  = PdfMerger.createPDFFromMultipleImage(paths: filesPath, outputDirPath: outputDirPath);
 
  if(response.status == "success") {
   //response.response for output path in String
  //response.message for success message  in String
  }
  
```

# Create Image From PDF

```

  //paths selected file path (String). Example user/android.downlaod/MYPDF.pdf
  //outputDirPath is output path with filename, exmaple /user/android/download/ABC.pdf
 //Optional params maxWidth : defalut set to 360, maxHeight : defalut set to 360, createOneImage : default set to true.
 
 CreateImageFromPDFResponse response  = await PdfMerger.createImageFromPDF(path: singleFile, outputDirPath: outputDirPath);
 
  if(response.status == "success") {
   //response.response for output path in List<String>
  //response.message for success message  in String
  }
  
```

# Get File Size

```

  //paths selected file path (String). Example user/android.downlaod/MYPDF.pdf
  
 SizeFormPathResponse response  = await PdfMerger.sizeFormPath(path: singleFile);
 
  if(response.status == "success") {
   //response.response for size of file
  //response.message for success message  in String
  }
  
```

# Build Info

```

 BuildInfoResponse response  = await PdfMerger.buildInfo();
 
 Get.snackbar("Info", "App Name : " + response.appName + "\n" +
            "Build Number : " + response.buildDate + "\n" +
            "Build Number with Time : " + response.buildDateWithTime + "\n" +
            "Package Name : " + response.packageName + "\n" +
            "Version Number : " + response.versionNumber + "\n" +
            "Build Number : " + response.buildNumber.toString() );
  
```



## Android & IOS

![ios_1](https://user-images.githubusercontent.com/32450488/117724739-d26cf700-b201-11eb-9581-6038948706af.gif)

![ios_2](https://user-images.githubusercontent.com/32450488/117724745-d436ba80-b201-11eb-9b12-d8f21be0e505.gif)

![android](https://user-images.githubusercontent.com/32450488/117724751-d6007e00-b201-11eb-9abd-af85ba2b1b33.gif)


## Usage

See Example. Use with file picker and add permission handler before calling pdf_merger. Make sure to call permission handler for write permission before call pdf_merger.

file_picker

https://pub.dev/packages/file_picker

permission_handler

https://pub.dev/packages/permission_handler 


## Support

Android & IOS


# For Android

Add  these lines in your  progaurd

```
//required to prevent errors from the spongycastle libraries
-keep class org.spongycastle.** { *; }
-dontwarn org.spongycastle.**
```

# For IOS

All set 

