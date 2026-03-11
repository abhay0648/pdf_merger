import FlutterMacOS
import AppKit
import ImageIO
import AVFoundation

public class SwiftPdfMergerPlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "pdf_merger", binaryMessenger: registrar.messenger)
        let instance = SwiftPdfMergerPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if call.method == "mergeMultiplePDF" {
            if let args = call.arguments as? Dictionary<String, Any> {
                DispatchQueue.global().async {
                    let singlePDFFromMultiplePDF = SwiftPdfMergerPlugin.mergeMultiplePDF(args: args)
                    DispatchQueue.main.async {
                        result(singlePDFFromMultiplePDF)
                    }
                }
            } else {
                result(FlutterError(code: "invalid_arguments", message: "Invalid arguments", details: nil))
            }
        } else {
            result(FlutterMethodNotImplemented)
        }
    }

    class func mergeMultiplePDF(args: Dictionary<String, Any>) -> String? {
        guard let paths = args["paths"] as? [String], let outputDirPath = args["outputDirPath"] as? String else {
            return nil
        }

        do {
            let pdfData = NSMutableData()
            guard let consumer = CGDataConsumer(data: pdfData) else { return nil }

            guard let context = CGContext(consumer: consumer, mediaBox: nil, nil) else {
                return nil
            }

            for path in paths {
                let pdfUrl = URL(fileURLWithPath: path)
                guard let pdfRef = CGPDFDocument(pdfUrl as CFURL) else {
                    continue
                }

                for i in 1 ... pdfRef.numberOfPages {
                    if let page = pdfRef.page(at: i) {
                        let pageRect = page.getBoxRect(.mediaBox) // Get original page size
                        var mediaBox = pageRect // Assign to mediaBox to be used in beginPage
                        context.beginPage(mediaBox: &mediaBox)
                        context.drawPDFPage(page)
                        context.endPage()
                    }
                }
            }

            context.closePDF()

            try pdfData.write(to: URL(fileURLWithPath: outputDirPath))

            return outputDirPath
        } catch {
            return nil
        }
    }
}
