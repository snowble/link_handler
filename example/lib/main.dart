import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:link_handler/link_handler.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _link = 'Unknown';
  LinkHandler _linkHandler = LinkHandler();
  StreamSubscription<String> _linksSubscription;

  @override
  void initState() {
    super.initState();
    initLink();
    _linksSubscription = _linkHandler.links.listen((link) {
      setLink(link);
    });
  }

  @override
  void dispose() {
    _linksSubscription.cancel();
    super.dispose();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initLink() async {
    String link;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      link = await _linkHandler.getLastLink;
    } on PlatformException {
      link = 'Failed to get link.';
    }
    setLink(link);
  }

  void setLink(String link) {
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
