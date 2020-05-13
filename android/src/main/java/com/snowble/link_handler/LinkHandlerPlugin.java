package com.snowble.link_handler;

import android.app.Activity;
import android.content.Intent;
import android.provider.Browser;
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.EventChannel.StreamHandler;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.NewIntentListener;

public class LinkHandlerPlugin implements FlutterPlugin, MethodCallHandler, NewIntentListener,
    StreamHandler, ActivityAware {

  private String lastLink;
  private EventSink eventSink;
  private Activity activity;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    final MethodChannel channel = new MethodChannel(
        flutterPluginBinding.getFlutterEngine().getDartExecutor(),
        "plugins.snowble.com/link_handler");
    channel.setMethodCallHandler(this);

    final EventChannel eventChannel =
        new EventChannel(flutterPluginBinding.getBinaryMessenger(), "plugins.snowble.com/links");
    eventChannel.setStreamHandler(this);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getLastLink")) {
      result.success(lastLink);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public boolean onNewIntent(Intent intent) {
    setLinkFromIntent(intent);
    if (eventSink != null) {
      eventSink.success(lastLink);
    }
    return false;
  }

  private void setLinkFromIntent(Intent intent) {
    // workaround firefox launching the Activity in its own task stack
    String browserAppId = intent.getStringExtra(Browser.EXTRA_APPLICATION_ID);
    if (browserAppId != null && browserAppId.equals("org.mozilla.firefox") && activity != null) {
      // we relaunch the intent with this flag so that it ends up in our stack (instead of firefox's)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      // firefox removes the category for some reason, so add it back
      intent.addCategory(Intent.CATEGORY_BROWSABLE);
      // remove the app id to avoid an infinite loop
      intent.removeExtra(Browser.EXTRA_APPLICATION_ID);
      activity.startActivity(intent);
      activity.finish();
      return;
    }

    if (intent.getAction() == null || !intent.getAction().equals(Intent.ACTION_VIEW)) {
      lastLink = null;
      return;
    }

    if (!intent.hasCategory(Intent.CATEGORY_BROWSABLE)) {
      lastLink = null;
      return;
    }

    if (intent.getDataString() == null || intent.getDataString().isEmpty()) {
      lastLink = null;
      return;
    }

    lastLink = intent.getDataString();
  }

  @Override
  public void onListen(Object o, EventSink eventSink) {
    this.eventSink = eventSink;
  }

  @Override
  public void onCancel(Object o) {
    eventSink = null;
  }

  @Override
  public void onAttachedToActivity(ActivityPluginBinding binding) {
    activity = binding.getActivity();
    setLinkFromIntent(binding.getActivity().getIntent());
    binding.addOnNewIntentListener(this);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
    activity = binding.getActivity();
    binding.addOnNewIntentListener(this);
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }
}
