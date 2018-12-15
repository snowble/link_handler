import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:link_handler/link_handler.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _link = 'Unknown';

  @override
  void initState() {
    super.initState();
    initLink();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initLink() async {
    String link;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      link = await LinkHandler.getLink;
    } on PlatformException {
      link = 'Failed to get link.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _link = link;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('$_link'),
        ),
      ),
    );
  }
}
