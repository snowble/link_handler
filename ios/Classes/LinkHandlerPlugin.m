#import "LinkHandlerPlugin.h"

@interface LinkHandlerPlugin () <FlutterStreamHandler>
@property(nonatomic, copy) NSString *lastLink;
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

    [registrar addApplicationDelegate:instance];
}

- (void)setLastLink:(NSString *)lastLink {
    _lastLink = [lastLink copy];

    if (_eventSink) _eventSink(_lastLink);
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if ([@"getLastLink" isEqualToString:call.method]) {
        result(self.lastLink);
    } else {
        result(FlutterMethodNotImplemented);
    }
}

- (BOOL)application:(UIApplication *)application
continueUserActivity:(NSUserActivity *)userActivity
 restorationHandler:(void (^)(NSArray *_Nullable))restorationHandler {
    if ([userActivity.activityType isEqualToString:NSUserActivityTypeBrowsingWeb]) {
        self.lastLink = [userActivity.webpageURL absoluteString];
        return YES;
    }
    return NO;
}

#pragma mark FlutterStreamHandler impl

- (FlutterError*)onListenWithArguments:(id)arguments eventSink:(FlutterEventSink)eventSink {
    _eventSink = eventSink;
    return nil;
}

- (FlutterError*)onCancelWithArguments:(id)arguments {
    _eventSink = nil;
    return nil;
}

@end
