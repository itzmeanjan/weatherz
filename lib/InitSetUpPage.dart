import 'package:flutter/material.dart';
import 'package:flutter/services.dart' show MethodChannel;
import 'dart:async' show Timer;

class InitSetUpPage extends StatefulWidget {
  MethodChannel methodChannel;
  InitSetUpPage({Key key, this.methodChannel}) : super(key: key);

  @override
  _InitSetUpPageState createState() => _InitSetUpPageState();
}

class _InitSetUpPageState extends State<InitSetUpPage> {
  FocusNode _focusNode;
  TextEditingController _textEditingController;
  String _errorText;
  double _visibilityUpper;
  double _visibilityLower;
  String _statusText;

  @override
  void initState() {
    super.initState();
    _visibilityUpper = 0.0;
    _visibilityLower = 1.0;
    _statusText = 'Storing API Key ...';
    _focusNode = FocusNode();
    _textEditingController = TextEditingController(text: '');
  }

  @override
  void dispose() {
    super.dispose();
    _focusNode.dispose();
    _textEditingController.dispose();
  }

  Future<bool> openInTargetApp() async {
    return await widget.methodChannel.invokeMethod(
        'openInTargetApp', <String, String>{
      'getAPIKeyURL': 'https://home.openweathermap.org/users/sign_in'
    }).then((val) => val);
  }

  Future<bool> storeAPIKey() async {
    return await widget.methodChannel.invokeMethod(
        'storeAPIKey', <String, String>{
      'apiKey': _textEditingController.text
    }).then((val) => val);
  }

  inputValidator() {
    if (_textEditingController.text.isEmpty) {
      FocusScope.of(context).requestFocus(_focusNode);
      setState(() {
        _errorText = "This can't be kept blank";
      });
    } else {
      _focusNode.unfocus();
      setState(() {
        _visibilityUpper = 0.75;
        _visibilityLower = 0.25;
      });
      Timer(Duration(seconds: 1), () {
        storeAPIKey().then((bool val) {
          if (val)
            setState(() {
              _statusText = 'Downloading City Names ...';
            });
        });
      });
    }
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
      body: Center(
        child: Padding(
          padding: EdgeInsets.all(8),
          child: Stack(
            alignment: Alignment.center,
            children: <Widget>[
              Opacity(
                opacity: _visibilityLower,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: <Widget>[
                    TextField(
                      focusNode: _focusNode,
                      onTap: () =>
                          FocusScope.of(context).requestFocus(_focusNode),
                      onSubmitted: (String val) {
                        if (_textEditingController.text.isEmpty) {
                          FocusScope.of(context).requestFocus(_focusNode);
                          setState(() {
                            _errorText = 'This can\'t be kept blank';
                          });
                        }
                      },
                      onChanged: (String val) {
                        if (_errorText != null && _errorText.isNotEmpty)
                          setState(() {
                            _errorText = null;
                          });
                      },
                      controller: _textEditingController,
                      textInputAction: TextInputAction.done,
                      autofocus: true,
                      cursorWidth: 1,
                      textAlign: TextAlign.justify,
                      decoration: InputDecoration(
                        border: UnderlineInputBorder(
                          borderRadius: BorderRadius.circular(10),
                          borderSide: BorderSide(
                            color: Colors.cyanAccent,
                            width: 1,
                            style: BorderStyle.solid,
                          ),
                        ),
                        labelText: 'API Key',
                        contentPadding: EdgeInsets.only(
                            left: 10, right: 8, top: 10, bottom: 4),
                        errorText: _errorText,
                        hintText: 'Enter OpenWeatherMap API Key',
                        helperText: 'E.g. : b1b15e88fa797225412429c1c50c122a1',
                      ),
                    ),
                    Divider(
                      height: 8,
                      color: Colors.white,
                    ),
                    ButtonBar(
                      alignment: MainAxisAlignment.spaceAround,
                      children: <Widget>[
                        RaisedButton(
                          onPressed: openInTargetApp,
                          child: Text('Get API Key'),
                          elevation: 12,
                          color: Colors.cyanAccent,
                          splashColor: Colors.white,
                        ),
                        RaisedButton(
                          onPressed: inputValidator,
                          child: Text('Continue'),
                          elevation: 12,
                          color: Colors.cyanAccent,
                          splashColor: Colors.white,
                        )
                      ],
                    ),
                  ],
                ),
              ),
              Opacity(
                opacity: _visibilityUpper,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: <Widget>[
                    CircularProgressIndicator(
                      backgroundColor: Colors.cyanAccent,
                      semanticsLabel: "Storing API Key ...",
                    ),
                    Divider(
                      height: 12,
                      color: Colors.white,
                    ),
                    Text(
                      _statusText,
                      style: TextStyle(
                          letterSpacing: 2,
                          color: Colors.black,
                          fontStyle: FontStyle.italic),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
