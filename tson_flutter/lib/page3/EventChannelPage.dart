import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:tson_flutter/text/textStyle.dart';

class EventChannelPage extends StatefulWidget {
  const EventChannelPage({super.key, required this.route});

  final String route;

  @override
  State<StatefulWidget> createState() => _EventChannelPageState();
}

class _EventChannelPageState extends State<EventChannelPage> {
  // 创建EventChannel
  static const eventChannel = EventChannel('eventChannel1');

  String message = "";

  @override
  void initState() {
    super.initState();
    // 监听原生发来的消息
    eventChannel.receiveBroadcastStream().listen((event) {
      setState(() {
        message = event.toString();
      });
    }, onError: (e) {
      print(e);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        children: [
          Text('路由：${widget.route}', style: MyTextStyle.Normal()),
          Text('接收到原生发来的消息：$message')
        ],
      ),
    );
  }
}
