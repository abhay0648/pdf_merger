import Flutter
import UIKit

public class SwiftPdfMergerPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "pdf_merger", binaryMessenger: registrar.messenger())
    let instance = SwiftPdfMergerPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {

     if call.method == "mergePDFPath" {
          if let args = call.arguments as? Dictionary<String, Any>,
          let paths = args["paths"] as? [String], let outputDirPath = args["outputDirPath"] as? String{

               guard UIGraphicsBeginPDFContextToFile(outputDirPath, CGRect.zero, nil) else {
                   result("Error")
                   return
               }
               guard let destContext = UIGraphicsGetCurrentContext() else {
                  result("Error")
                   return
               }


                     for index in 0 ..< paths.count {
                            let pdfFile = paths[index]
                            let pdfUrl = NSURL(fileURLWithPath: pdfFile)
                            guard let pdfRef = CGPDFDocument(pdfUrl) else {
                                continue
                            }

                            for i in 1 ... pdfRef.numberOfPages {
                                if let page = pdfRef.page(at: i) {
                                    var mediaBox = page.getBoxRect(.mediaBox)
                                    destContext.beginPage(mediaBox: &mediaBox)
                                    destContext.drawPDFPage(page)
                                    destContext.endPage()
                                }
                            }
                        }


                   destContext.closePDF()
                   UIGraphicsEndPDFContext()

                   result("Success")

           }
       }
  }
}
