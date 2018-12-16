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

  private String link;
  private EventSink eventSink;

  private LinkHandlerPlugin(Registrar registrar) {
    setLinkFromIntent(registrar.activity().getIntent());
  }

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
      result.success(link);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public boolean onNewIntent(Intent intent) {
    setLinkFromIntent(intent);
    if (eventSink != null) {
      eventSink.success(link);
    }
    return false;
  }

  private void setLinkFromIntent(Intent intent) {
    if (intent.getAction() == null || !intent.getAction().equals(Intent.ACTION_VIEW)) {
      link = null;
      return;
    }

    if (!intent.hasCategory(Intent.CATEGORY_BROWSABLE)) {
      link = null;
      return;
    }

    if (intent.getDataString() == null || intent.getDataString().isEmpty()) {
      link = null;
      return;
    }

    link = intent.getDataString();
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
