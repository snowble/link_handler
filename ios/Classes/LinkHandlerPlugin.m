#import "LinkHandlerPlugin.h"

@interface LinkHandlerPlugin () <FlutterStreamHandler>
@end

@implementation LinkHandlerPlugin  {
    FlutterEventSink _eventSink;
}

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"plugins.snowble.com/link_handler"
                                     binaryMessenger:[registrar messenger]];
    LinkHandlerPlugin* instance = [[LinkHandlerPlugin alloc] init];
    [registrar addMethodCallDelegate:instance channel:channel];

    FlutterEventChannel* streamChannel =
    [FlutterEventChannel eventChannelWithName:@"plugins.snowble.com/links"
                              binaryMessenger:[registrar messenger]];
    [streamChannel setStreamHandler:instance];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if ([@"getLastLink" isEqualToString:call.method]) {
        result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
    } else {
        result(FlutterMethodNotImplemented);
    }
}

#pragma mark FlutterStreamHandler impl

- (FlutterError*)onListenWithArguments:(id)arguments eventSink:(FlutterEventSink)eventSink {
    _eventSink = eventSink;
    return nil;
}

- (FlutterError*)onCancelWithArguments:(id)arguments {
    return nil;
}

@end
