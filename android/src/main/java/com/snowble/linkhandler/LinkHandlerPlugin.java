package com.snowble.linkhandler;

import android.content.Intent;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.EventChannel.StreamHandler;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.NewIntentListener;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * LinkHandlerPlugin
 */
public class LinkHandlerPlugin implements MethodCallHandler, NewIntentListener, StreamHandler {

  private String data;
  private EventSink eventSink;

  private LinkHandlerPlugin(Registrar registrar) {
    data = getData(registrar.activity().getIntent());
  }

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    LinkHandlerPlugin plugin = new LinkHandlerPlugin(registrar);

    final MethodChannel channel = new MethodChannel(registrar.messenger(),
        "plugins.snowble.com/link_handler");
    channel.setMethodCallHandler(plugin);

    final EventChannel eventChannel =
        new EventChannel(registrar.messenger(), "plugins.snowble.com/links");
    eventChannel.setStreamHandler(plugin);

    registrar.addNewIntentListener(plugin);
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getLink")) {
      result.success(data);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public boolean onNewIntent(Intent intent) {
    data = getData(intent);
    if (eventSink != null) {
      eventSink.success(data);
    }
    return false;
  }

  private String getData(Intent intent) {
    // FIXME return null if no data string
    return intent.getDataString() == null ? "No data" : intent.getDataString();
  }

  @Override
  public void onListen(Object o, EventSink eventSink) {
    this.eventSink = eventSink;
  }

  @Override
  public void onCancel(Object o) {
    eventSink = null;
  }
}
