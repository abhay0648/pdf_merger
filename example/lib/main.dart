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
  String _platformVersion = 'Unknown';
  List<PlatformFile> files;
  List<String> filesPath;

  @override
  void initState() {
    super.initState();
    files = [];
    filesPath = [];
  }


  multipleFilePicker() async{

    bool isGranted =  await checkPermission();

    if(isGranted){

      print("FilePicker");
      try{
        FilePickerResult result = GetPlatform.isIOS ? await FilePicker.platform.pickFiles(allowMultiple: true) :
        await FilePicker.platform.pickFiles(allowMultiple: true, type: FileType.custom , allowedExtensions: ['pdf']);

        if(result != null) {
          files.addAll(result.files);


          for(int i= 0 ; i < result.files.length ; i++) {
            filesPath.add(result.files[i].path);
          }


          if(files.length > 1){
            String dirPath = await getFilePath("TestPDFMerger");
            initPlatformState(files[0].path,files[1].path, dirPath, "Test123.pdf");
          }

        } else {
          // User canceled the picker
        }
      }on Exception catch (e) {
        print('never reached'+ e.toString());
      }
    }
  }


  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState(path1,path2, outputDirPath, outputFileName) async {
    String response;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      response = await PdfMerger.platformVersion(paths : filesPath, outputDirPath: outputDirPath);
      print("File" + response);
    } on PlatformException {
      print('Failed to get platform version.');
    }
  }



  Future<bool> checkPermission() async{
    await PermissionHandler().requestPermissions([PermissionGroup.storage]);
    PermissionStatus permission = await PermissionHandler().checkPermissionStatus(PermissionGroup.storage);
    print(permission);
    if (permission == PermissionStatus.neverAskAgain) {
      print("Go to Settings and provide media access");
      return false;
    }else if (permission == PermissionStatus.granted) {
      return true;
    }else{
      return false;
    }
  }

  Future<String> getFilePath(String fileStartName) async{
    String path;
    if(GetPlatform.isIOS) {
      Directory appDocDir = await getApplicationDocumentsDirectory();
      print(appDocDir.path);
      path = appDocDir.path;
    }else{
      path = await ExtStorage.getExternalStoragePublicDirectory(ExtStorage.DIRECTORY_DOWNLOADS);
    }

    return path + "/" +  fileStartName + "ABC" + ".pdf";
  }



  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: InkWell(
              onTap: (){
                multipleFilePicker();
              },
              child : Text('Running on: $_platformVersion\n')),
        ),
      ),
    );
  }
}
