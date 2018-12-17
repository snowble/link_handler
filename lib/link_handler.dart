import 'dart:async';

import 'package:flutter/services.dart';

const MethodChannel _channel =
    const MethodChannel('plugins.snowble.com/link_handler');
const EventChannel _eventChannel = EventChannel('plugins.snowble.com/links');

class LinkHandler {
  static Future<String> get getLastLink async {
    final String version = await _channel.invokeMethod('getLastLink');
    return version;
  }

  Stream<String> _links;

  Stream<String> get links {
    if (_links == null) {
      _links = _eventChannel
          .receiveBroadcastStream()
          .map((dynamic link) => link.toString());
    }
    return _links;
  }
}
