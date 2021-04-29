# pdf_merger

A new Flutter plugin for merge List of PDF files. It supports both android and IOS. Before call this package make sure you allow permission and file picker see example.
Also check file_picker and permission handle to implement before calling the plugin. 


## Method

  String response;\
    // Platform messages may fail, so we use a try/catch PlatformException.\
    try {\
      response = await PdfMerger.platformVersion(paths : filesPath, outputDirPath: outputDirPath);\
      print("File" + response);\
    } on PlatformException {\
      print('Failed to get platform version.');\
    }
    
Response -> "Success" when file save successfully otherwise "Error" message come.

paths -> List<String> path.
outputDirPath -> Add output directly path with file name. Example "0/Android/download/abc.pdf".
    

## Usage

See Example. Use with file picker and add permission handler before calling pdf_merger.

file_picker

https://pub.dev/packages/file_picker

permission_handler

https://pub.dev/packages/permission_handler 


## Support

Android & IOS


