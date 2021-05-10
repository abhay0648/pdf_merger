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
      return '42';
    });

  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('mergeMultiplePDF', () async {
    expect(await PdfMerger.mergeMultiplePDF, '42');
  });

  test('createPDFFromMultipleImage', () async {
    expect(await PdfMerger.createPDFFromMultipleImage, '42');
  });

}
