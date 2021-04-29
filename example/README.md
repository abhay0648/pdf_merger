# pdf_merger_example

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

## Getting Started

This project is a starting point for a Flutter application.

A few resources to get you started if this is your first Flutter project:

- [Lab: Write your first Flutter app](https://flutter.dev/docs/get-started/codelab)
- [Cookbook: Useful Flutter samples](https://flutter.dev/docs/cookbook)

For help getting started with Flutter, view our
[online documentation](https://flutter.dev/docs), which offers tutorials,
samples, guidance on mobile development, and a full API reference.
