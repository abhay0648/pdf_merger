import 'dart:io';

import 'package:ext_storage/ext_storage.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:path_provider/path_provider.dart';
import 'package:pdf_merger/pdf_merger.dart';
import 'package:permission_handler/permission_handler.dart';

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

  @override
  void initState() {
    super.initState();
    files = [];
    filesPath = [];
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Container(
            margin: EdgeInsets.all(25),
            child: TextButton(
              style: ButtonStyle(overlayColor:
                  MaterialStateProperty.resolveWith<Color>(
                      (Set<MaterialState> states) {
                if (states.contains(MaterialState.focused)) return Colors.red;
                if (states.contains(MaterialState.hovered)) return Colors.green;
                if (states.contains(MaterialState.pressed)) return Colors.blue;
                return null; // Defer to the widget's default.
              })),
              child: Text(
                "Select Files",
                style: TextStyle(fontSize: 20.0),
              ),
              onPressed: () {
                multipleFilePicker();
              },
            ),
          ),
        ),
      ),
    );
  }

  multipleFilePicker() async {
    bool isGranted = await checkPermission();

    if (isGranted) {
      try {
        FilePickerResult result = GetPlatform.isIOS
            ? await FilePicker.platform.pickFiles(allowMultiple: true)
            : await FilePicker.platform.pickFiles(
                allowMultiple: true,
                type: FileType.custom,
                allowedExtensions: ['pdf']);

        if (result != null) {
          files.addAll(result.files);

          // IS PDF check added for IOS for picking add file
          bool isPDF = true;
          for (int i = 0; i < result.files.length; i++) {
            if (isPDF) {
              isPDF = GetUtils.isPDF(files[i].path);
              filesPath.add(result.files[i].path);
            }
          }

          if (isPDF) {
            if (files.length > 1) {
              //Can pass output file name
              String dirPath = await getFilePath("TestPDFMerger");
              initPlatformState(dirPath);
            }
          } else {
            filesPath = [];
            print("Only PDF file selection allow");
          }
        } else {
          // User canceled the picker
        }
      } on Exception catch (e) {
        print('never reached' + e.toString());
      }
    }
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState(outputDirPath) async {
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      String response = await PdfMerger.platformVersion(
          paths: filesPath, outputDirPath: outputDirPath);
      print("File" + response);

      if (response == "Success") {
        // File save successfully
      } else {
        // File not save in call-back response == "Error"
      }
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
      print(appDocDir.path);
      path = appDocDir.path;
    } else {
      path = await ExtStorage.getExternalStoragePublicDirectory(
          ExtStorage.DIRECTORY_DOWNLOADS);
    }

    return path + "/" + fileStartName + "ABC" + ".pdf";
  }
}
