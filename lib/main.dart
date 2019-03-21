import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'InitSetUpPage.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'WeatherZ',
      theme: ThemeData.light(),
      darkTheme: ThemeData.dark(),
      home: MyHome(),
    );
  }
}

class MyHome extends StatefulWidget {
  @override
  _MyHomeState createState() => _MyHomeState();
}

class _MyHomeState extends State<MyHome> {
  MethodChannel _methodChannel;

  Widget _myHomeWidget;

  @override
  void initState() {
    super.initState();
    _methodChannel = MethodChannel('method_channel_weatherZ');
    _myHomeWidget = Center(
      child: CircularProgressIndicator(
        backgroundColor: Colors.cyanAccent,
      ),
    );
    isInitSetUpDone().then((bool val) {
      if (val)
        setState(() {
          _myHomeWidget = Center(
            child: Text('Init SetUp Done'),
          );
        });
      else
        Navigator.of(context).push(MaterialPageRoute(
            builder: (context) => InitSetUpPage(
                  methodChannel: _methodChannel,
                )));
    });
  }

  Future<bool> isInitSetUpDone() async {
    return await _methodChannel
        .invokeMethod('isInitSetUpDone')
        .then((val) => val);
  }

  Future<bool> initSetUpDone() async {
    return await _methodChannel
        .invokeMethod('initSetUpDone')
        .then((val) => val);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('WeatherZ'),
        textTheme: TextTheme(
          title: TextStyle(
              color: Colors.black,
              letterSpacing: 3,
              fontWeight: FontWeight.bold,
              fontStyle: FontStyle.italic),
        ),
        elevation: 12,
        backgroundColor: Colors.cyanAccent,
      ),
      body: _myHomeWidget,
    );
  }
}
