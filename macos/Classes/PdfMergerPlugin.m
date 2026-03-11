#import "PdfMergerPlugin.h"
#if __has_include(<pdf_merger/pdf_merger-Swift.h>)
#import <pdf_merger/pdf_merger-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "pdf_merger-Swift.h"
#endif

@implementation PdfMergerPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftPdfMergerPlugin registerWithRegistrar:registrar];
}
@end
