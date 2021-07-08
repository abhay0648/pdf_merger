import 'dart:io';

import 'package:ext_storage/ext_storage.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:open_file/open_file.dart';
import 'package:path_provider/path_provider.dart';
import 'package:pdf_merger/pdf_merger.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:get/get.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<PlatformFile> files;
  List<String> filesPath;
  String singleFile;

  @override
  void initState() {
    super.initState();
    clear();
  }

  @override
  Widget build(BuildContext context) {
    return GetMaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('PDF Merger'),
        ),
        body: Center(
          child: Container(
              margin: EdgeInsets.all(25),
              child: Column(children: [
                TextButton(
                  style: ButtonStyle(overlayColor:
                      MaterialStateProperty.resolveWith<Color>(
                          (Set<MaterialState> states) {
                    if (states.contains(MaterialState.focused))
                      return Colors.red;
                    if (states.contains(MaterialState.hovered))
                      return Colors.green;
                    if (states.contains(MaterialState.pressed))
                      return Colors.blue;
                    return null; // Defer to the widget's default.
                  })),
                  child: Text(
                    "Chose File",
                    style: TextStyle(fontSize: 14.0),
                  ),
                  onPressed: () {
                    multipleFilePicker();
                  },
                ),
                SizedBox(height: 10),
                TextButton(
                  style: ButtonStyle(overlayColor:
                      MaterialStateProperty.resolveWith<Color>(
                          (Set<MaterialState> states) {
                    if (states.contains(MaterialState.focused))
                      return Colors.red;
                    if (states.contains(MaterialState.hovered))
                      return Colors.green;
                    if (states.contains(MaterialState.pressed))
                      return Colors.blue;
                    return null; // Defer to the widget's default.
                  })),
                  child: Text(
                    "Merge Multiple PDF",
                    style: TextStyle(fontSize: 14.0),
                  ),
                  onPressed: () {
                    callMethod(1);
                  },
                ),
                SizedBox(height: 10),
                TextButton(
                  style: ButtonStyle(overlayColor:
                      MaterialStateProperty.resolveWith<Color>(
                          (Set<MaterialState> states) {
                    if (states.contains(MaterialState.focused))
                      return Colors.red;
                    if (states.contains(MaterialState.hovered))
                      return Colors.green;
                    if (states.contains(MaterialState.pressed))
                      return Colors.blue;
                    return null; // Defer to the widget's default.
                  })),
                  child: Text(
                    "Create PDF From Multiple Image",
                    style: TextStyle(fontSize: 14.0),
                  ),
                  onPressed: () {
                    callMethod(2);
                  },
                ),
                SizedBox(height: 10),
                TextButton(
                  style: ButtonStyle(overlayColor:
                      MaterialStateProperty.resolveWith<Color>(
                          (Set<MaterialState> states) {
                    if (states.contains(MaterialState.focused))
                      return Colors.red;
                    if (states.contains(MaterialState.hovered))
                      return Colors.green;
                    if (states.contains(MaterialState.pressed))
                      return Colors.blue;
                    return null; // Defer to the widget's default.
                  })),
                  child: Text(
                    "Create Image From PDF",
                    style: TextStyle(fontSize: 14.0),
                  ),
                  onPressed: () {
                    singleFilePicker(1);
                  },
                ),
                SizedBox(height: 10),
                TextButton(
                  style: ButtonStyle(overlayColor:
                      MaterialStateProperty.resolveWith<Color>(
                          (Set<MaterialState> states) {
                    if (states.contains(MaterialState.focused))
                      return Colors.red;
                    if (states.contains(MaterialState.hovered))
                      return Colors.green;
                    if (states.contains(MaterialState.pressed))
                      return Colors.blue;
                    return null; // Defer to the widget's default.
                  })),
                  child: Text(
                    "Get File Size",
                    style: TextStyle(fontSize: 14.0),
                  ),
                  onPressed: () {
                    singleFilePicker(2);
                  },
                ),
                SizedBox(height: 10),
                TextButton(
                  style: ButtonStyle(overlayColor:
                      MaterialStateProperty.resolveWith<Color>(
                          (Set<MaterialState> states) {
                    if (states.contains(MaterialState.focused))
                      return Colors.red;
                    if (states.contains(MaterialState.hovered))
                      return Colors.green;
                    if (states.contains(MaterialState.pressed))
                      return Colors.blue;
                    return null; // Defer to the widget's default.
                  })),
                  child: Text(
                    "Clear",
                    style: TextStyle(fontSize: 14.0),
                  ),
                  onPressed: () {
                    clear();
                  },
                ),
                SizedBox(height: 10),
                TextButton(
                  style: ButtonStyle(overlayColor:
                      MaterialStateProperty.resolveWith<Color>(
                          (Set<MaterialState> states) {
                    if (states.contains(MaterialState.focused))
                      return Colors.red;
                    if (states.contains(MaterialState.hovered))
                      return Colors.green;
                    if (states.contains(MaterialState.pressed))
                      return Colors.blue;
                    return null; // Defer to the widget's default.
                  })),
                  child: Text(
                    "Build Info",
                    style: TextStyle(fontSize: 14.0),
                  ),
                  onPressed: () {
                    buildInfo();
                  },
                ),
              ])),
        ),
      ),
    );
  }

  clear() {
    files = [];
    filesPath = [];
    singleFile = "";
  }

  multipleFilePicker() async {
    bool isGranted = await checkPermission();

    if (isGranted) {
      try {
        FilePickerResult result =
            await FilePicker.platform.pickFiles(allowMultiple: true);

        if (result != null) {
          files.addAll(result.files);

          for (int i = 0; i < result.files.length; i++) {
            filesPath.add(result.files[i].path);
          }
        } else {
          // User canceled the picker
        }
      } on Exception catch (e) {
        print('never reached' + e.toString());
      }
    }
  }

  singleFilePicker(int type) async {
    bool isGranted = GetPlatform.isIOS || GetPlatform.isAndroid
        ? await checkPermission()
        : true;

    if (isGranted) {
      try {
        FilePickerResult result =
            await FilePicker.platform.pickFiles(allowMultiple: false);
        if (result != null) {
          singleFile = result.files[0].path;

          switch (type) {
            case 1:
              callMethod(3);
              break;

            case 2:
              callMethod(4);
              break;
          }
        } else {
          // User canceled the picker
        }
      } on Exception catch (e) {
        print('never reached' + e.toString());
      }
    }
  }

  callMethod(int type) async {
    switch (type) {
      case 1:
        String dirPath = await getFilePath("TestPDFMerger");
        mergeMultiplePDF(dirPath);
        break;

      case 2:
        String dirPath = await getFilePath("TestPDFMerger");
        createPDFWithMultipleImage(dirPath);
        break;

      case 3:
        String dirPath = await getFilePathImage("TestPDFMerger");
        createImageFromPDF(dirPath);
        break;

      case 4:
        sizeForLocalFilePath();
        break;
    }
  }

  Future<void> mergeMultiplePDF(outputDirPath) async {
    /// Platform messages may fail, so we use a try/catch PlatformException.
    try {
      /// Get response either success or error
      MergeMultiplePDFResponse response = await PdfMerger.mergeMultiplePDF(
          paths: filesPath, outputDirPath: outputDirPath);

      Get.snackbar("Info", response.message);

      if (response.status == "success") {
        OpenFile.open(response.response);
      }

      print(response.status);
    } on PlatformException {
      print('Failed to get platform version.');
    }
  }

  Future<void> createPDFWithMultipleImage(outputDirPath) async {
    /// Platform messages may fail, so we use a try/catch PlatformException.
    try {
      /// Get response either success or error
      CreatePDFFromMultipleImageResponse response =
          await PdfMerger.createPDFFromMultipleImage(
              paths: filesPath, outputDirPath: outputDirPath);

      Get.snackbar("Info", response.message);

      if (response.status == "success") {
        OpenFile.open(response.response);
      }

      print(response.status);
    } on PlatformException {
      print('Failed to get platform version.');
    }
  }

  Future<void> createImageFromPDF(outputDirPath) async {
    /// Platform messages may fail, so we use a try/catch PlatformException.
    try {
      /// Get response either success or error
      CreateImageFromPDFResponse response = await PdfMerger.createImageFromPDF(
          path: singleFile, outputDirPath: outputDirPath, createOneImage: true);

      Get.snackbar("Info", response.status);

      if (response.status == "success") {
        OpenFile.open(response.response[0]);
      }

      print(response.message);
    } on PlatformException {
      print('Failed to get platform version.');
    }
  }

  Future<void> sizeForLocalFilePath() async {
    /// Platform messages may fail, so we use a try/catch PlatformException.
    try {
      /// Get response either success or error
      SizeFormPathResponse response =
          await PdfMerger.sizeFormPath(path: singleFile);

      if (response.status == "success") {
        Get.snackbar("Info", response.response);
      }

      print(response.status);
    } on PlatformException {
      print('Failed to get platform version.');
    }
  }

  Future<void> buildInfo() async {
    /// Platform messages may fail, so we use a try/catch PlatformException.
    try {
      /// Get response either success or error
      BuildInfoResponse response = await PdfMerger.buildInfo();

      Get.snackbar(
          "Info",
          "App Name : " +
              response.appName +
              "\n" +
              "Build Number : " +
              response.buildDate +
              "\n" +
              "Build Number with Time : " +
              response.buildDateWithTime +
              "\n" +
              "Package Name : " +
              response.packageName +
              "\n" +
              "Version Number : " +
              response.versionNumber +
              "\n" +
              "Build Number : " +
              response.buildNumber.toString());
    } on PlatformException {
      print('Failed to get platform version.');
    }
  }

  Future<bool> checkPermission() async {
    await PermissionHandler().requestPermissions([PermissionGroup.storage]);
    PermissionStatus permission = await PermissionHandler()
        .checkPermissionStatus(PermissionGroup.storage);
    print(permission);
    if (permission == PermissionStatus.neverAskAgain) {
      print("Go to Settings and provide media access");
      return false;
    } else if (permission == PermissionStatus.granted) {
      return true;
    } else {
      return false;
    }
  }

  Future<String> getFilePath(String fileStartName) async {
    String path;
    if (GetPlatform.isIOS) {
      Directory appDocDir = await getApplicationDocumentsDirectory();
      path = appDocDir.path;
    } else if (GetPlatform.isAndroid) {
      path = await ExtStorage.getExternalStoragePublicDirectory(
          ExtStorage.DIRECTORY_DOWNLOADS);
    }

    return path + "/" + fileStartName + "ABCEFG5" + ".pdf";
  }

  Future<String> getFilePathImage(String fileStartName) async {
    String path;
    if (GetPlatform.isIOS) {
      Directory appDocDir = await getApplicationDocumentsDirectory();
      path = appDocDir.path;
    } else if (GetPlatform.isAndroid) {
      path = await ExtStorage.getExternalStoragePublicDirectory(
          ExtStorage.DIRECTORY_DOWNLOADS);
    }

    return path + "/" + fileStartName + "ABCEFG5" + ".png";
  }
}
