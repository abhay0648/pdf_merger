import 'dart:async';
import 'package:flutter/services.dart';

class PdfMerger {
  static const MethodChannel _channel = const MethodChannel('pdf_merger');

  static Future<String> platformVersion({paths, outputDirPath}) async {
    final Map<String, dynamic> params = <String, dynamic>{
      'paths': paths,
      'outputDirPath': outputDirPath
    };

    final String response = await _channel.invokeMethod('mergePDFPath', params);
    return response;
  }
}
