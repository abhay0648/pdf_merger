import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pdf_merger/pdf_merger.dart';

void main() {
  const MethodChannel channel = MethodChannel('pdf_merger');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '43';
    });

    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '44';
    });

    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '45';
    });

    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '46';
    });

    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '47';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('mergeMultiplePDF', () async {
    expect(PdfMerger.mergeMultiplePDF, '42');
  });

  test('createPDFFromMultipleImage', () async {
    expect(PdfMerger.createPDFFromMultipleImage, '43');
  });

  test('createImageFromPDF', () async {
    expect(PdfMerger.createImageFromPDF, '44');
  });

  test('sizeFormPath', () async {
    expect(PdfMerger.sizeFormPath, '45');
  });

  test('createPDFFromMultipleImage', () async {
    expect(PdfMerger.createPDFFromMultipleImage, '46');
  });

  test('buildInfo', () async {
    expect(PdfMerger.buildInfo, '47');
  });
}
