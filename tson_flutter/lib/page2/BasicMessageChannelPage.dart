

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

import '../text/textStyle.dart';

class BasicMessageChannelPage extends StatefulWidget {
  const BasicMessageChannelPage({super.key, required this.route});

  final String route;

  @override
  State<StatefulWidget> createState() => _BasicMessageChannelPageState();
}

class _BasicMessageChannelPageState extends State<BasicMessageChannelPage> {
  static const basicMessageChannel =
  BasicMessageChannel('basicMessageChannel1', StringCodec());
  String result = '无返回消息'; // 用来存储Flutter->Native的返回消息
  String message = ''; //Native发来的消息

  @override
  void initState() {
    super.initState();
    basicMessageChannel.setMessageHandler((m) {
      setState(() {
        message = m.toString();
      });
      return Future.value("收到Native来的消息，over！");
    });
  }

  Future<void> _nativeToast() async {
    try {
      String? replyString =
      await basicMessageChannel.send("Android, 我是Flutter端！");
      setState(() {
        result = replyString.toString();
      });
    } catch (e) {
      print(e);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        children: [
          Text("当前Page2，路由地址：${widget.route}", style: MyTextStyle.Normal()),
          CupertinoButton(
            // 一个普通按钮
              onPressed: _nativeToast,
              child: const Text("向原生发消息并获取返回值")),
          Text("消息返回值是：$result", style: MyTextStyle.Normal()),
          Text("原生发来消息：$message", style: MyTextStyle.Normal()),
        ],
      ),
    );
  }
}