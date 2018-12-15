import 'dart:async';

import 'package:flutter/services.dart';

class LinkHandler {
  static const MethodChannel _channel =
      const MethodChannel('plugins.snowble.com/link_handler');

  static Future<String> get getLink async {
    final String version = await _channel.invokeMethod('getLink');
    return version;
  }
}
