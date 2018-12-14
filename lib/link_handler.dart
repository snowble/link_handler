import 'dart:async';

import 'package:flutter/services.dart';

class LinkHandler {
  static const MethodChannel _channel =
      const MethodChannel('link_handler');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
