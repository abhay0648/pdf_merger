import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:pdf_merger/pdf_merger_response.dart';
export 'package:pdf_merger/pdf_merger_response.dart';

class PdfMerger {
  static const MethodChannel _channel = const MethodChannel('pdf_merger');
  static final mergeMultiplePDFResponse = MergeMultiplePDFResponse().obs;
  static final createPDFFromMultipleImageResponse = CreatePDFFromMultipleImageResponse().obs;
  static final createImageFromPDFResponse = CreateImageFromPDFResponse().obs;
  static final sizeForLocalFilePathResponse = SizeForLocalFilePathResponse().obs;

  static Future<MergeMultiplePDFResponse> mergeMultiplePDF({@required List<String> paths, @required  String outputDirPath}) async {
    final Map<String, dynamic> params = <String, dynamic>{
      'paths': paths,
      'outputDirPath': outputDirPath
    };



    if(paths == null || paths.length == 0){

        mergeMultiplePDFResponse.value.status =  Status.error;
        mergeMultiplePDFResponse.value.message = Status.errorMessage;

    }else{
      try {

        bool isPDF = true;

        for(int i=0; i< paths.length ; i++){
          if(!GetUtils.isPDF(paths[i])){
            isPDF = false;
          }
        }

        if(!isPDF){

            mergeMultiplePDFResponse.value.status =  Status.error;
            mergeMultiplePDFResponse.value.message = Status.errorMessagePDF;

        }else{
          final String response = await _channel.invokeMethod('mergeMultiplePDF', params);

          if(response != "error"){

              mergeMultiplePDFResponse.value.status =  Status.success;
              mergeMultiplePDFResponse.value.message = Status.successMessage;
              mergeMultiplePDFResponse.value.response = response;

          }else{

              mergeMultiplePDFResponse.value.status =  Status.error;
              mergeMultiplePDFResponse.value.message = Status.errorMessage;

          }
        }
      } on Exception catch (exception) {

          mergeMultiplePDFResponse.value.status =  Status.error;
          mergeMultiplePDFResponse.value.message = Status.errorMessage;

      } catch (error) {

          mergeMultiplePDFResponse.value.status =  Status.error;
          mergeMultiplePDFResponse.value.message = Status.errorMessage;

      }
    }

    return mergeMultiplePDFResponse.value;
  }

  static Future<CreatePDFFromMultipleImageResponse> createPDFFromMultipleImage({@required List<String> paths, @required String outputDirPath,
    int maxWidth, int maxHeight, bool needImageCompressor}) async {
    final Map<String, dynamic> params = <String, dynamic>{
      'paths': paths,
      'outputDirPath': outputDirPath,
      'needImageCompressor' : needImageCompressor ?? true,
      'maxWidth' : maxWidth ?? 360,
      'maxHeight' : maxHeight ?? 360
    };


    if(paths == null || paths.length == 0){

        createPDFFromMultipleImageResponse.value.status =  Status.error;
        createPDFFromMultipleImageResponse.value.message = Status.errorMessage;

    }else{
      try {

        bool isImage = true;

        for(int i=0; i< paths.length ; i++){
          if(!GetUtils.isImage(paths[i])){
            isImage = false;
          }
        }

        if(!isImage){

            createPDFFromMultipleImageResponse.value.status =  Status.error;
            createPDFFromMultipleImageResponse.value.message = Status.errorMessageImage;

        }else{
          final String response = await _channel.invokeMethod('createPDFFromMultipleImage', params);

          if(response != "error"){

              createPDFFromMultipleImageResponse.value.status =  Status.success;
              createPDFFromMultipleImageResponse.value.message = Status.successMessage;
              createPDFFromMultipleImageResponse.value.response = response;

          }else{

              createPDFFromMultipleImageResponse.value.status =  Status.error;
              createPDFFromMultipleImageResponse.value.message = Status.errorMessage;

          }
        }
      } on Exception catch (exception) {

          createPDFFromMultipleImageResponse.value.status =  Status.error;
          createPDFFromMultipleImageResponse.value.message = Status.errorMessage;

      } catch (error) {

          createPDFFromMultipleImageResponse.value.status =  Status.error;
          createPDFFromMultipleImageResponse.value.message = Status.errorMessage;

      }
    }

    return createPDFFromMultipleImageResponse.value;
  }


  static Future<CreateImageFromPDFResponse> createImageFromPDF({@required String path, @required  String outputDirPath,
    int maxWidth, int maxHeight, createOneImage}) async {
    final Map<String, dynamic> params = <String, dynamic>{
      'path': path,
      'outputDirPath': outputDirPath,
      'maxWidth' : maxWidth ?? 360,
      'maxHeight' : maxHeight ?? 360,
      'createOneImage' : createOneImage ?? true
    };


    if(path == null || path == ""){

        createImageFromPDFResponse.value.status =  Status.error;
        createImageFromPDFResponse.value.message = Status.errorMessage;

    }else{
      try {

        bool isImage = GetUtils.isPDF(path);

        if(!isImage){

            createImageFromPDFResponse.value.status =  Status.error;
            createImageFromPDFResponse.value.message = Status.errorMessageImage;

        }else{
          final  response = await _channel.invokeMethod('createImageFromPDF', params);

          if(response != null && response.length != 0){

            createImageFromPDFResponse.value.response = [];
            for(int i =0; i< response.length; i++){
              createImageFromPDFResponse.value.response.add(response[i]);
            }

              createImageFromPDFResponse.value.status =  Status.success;
              createImageFromPDFResponse.value.message = Status.successMessage;
          }else{

              createImageFromPDFResponse.value.status =  Status.error;
              createImageFromPDFResponse.value.message = Status.errorMessage;

          }
        }
      } on Exception catch (exception) {

        print(exception);

          createImageFromPDFResponse.value.status =  Status.error;
          createImageFromPDFResponse.value.message = Status.errorMessage;

      } catch (error) {
        print(error);

          createImageFromPDFResponse.value.status =  Status.error;
          createImageFromPDFResponse.value.message = Status.errorMessage;

      }
    }

    return createImageFromPDFResponse.value;
  }

  static Future<SizeForLocalFilePathResponse> sizeForLocalFilePath({@required String path}) async {
    final Map<String, dynamic> params = <String, dynamic>{
      'path': path
    };


    if(path == null || path == ""){

        sizeForLocalFilePathResponse.value.status =  Status.error;
        sizeForLocalFilePathResponse.value.message = Status.errorMessage;

    }else{
      try {

          final String response = await _channel.invokeMethod('sizeForLocalFilePath', params);

          if(response != "error"){

              sizeForLocalFilePathResponse.value.status =  Status.success;
              sizeForLocalFilePathResponse.value.message = Status.successMessage;
              sizeForLocalFilePathResponse.value.response = response;

          }else{

              sizeForLocalFilePathResponse.value.status =  Status.error;
              sizeForLocalFilePathResponse.value.message = Status.errorMessage;

          }

      } on Exception catch (exception) {

          sizeForLocalFilePathResponse.value.status =  Status.error;
          sizeForLocalFilePathResponse.value.message = Status.errorMessage;

      } catch (error) {

          sizeForLocalFilePathResponse.value.status =  Status.error;
          sizeForLocalFilePathResponse.value.message = Status.errorMessage;

      }
    }

    return sizeForLocalFilePathResponse.value;
  }
}
