
import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

import '../text/textStyle.dart';

class MethodChannelPage extends StatefulWidget {
  const MethodChannelPage({super.key, required this.route});

  final String route;

  @override
  State<StatefulWidget> createState() => _MethodChannelPageState();
}

class _MethodChannelPageState extends State<MethodChannelPage> {
  static const platform =
  MethodChannel('methodChannel1'); // 初始化这个方法通道，根据通道名，要和Activity里面的对应
  String result = '无返回消息'; // 用来存储返回消息

  Future<void> _nativeToast() async {
    try {
      // 异步调用这个方法
      result = await platform.invokeMethod("toastTest", {'count': 0});
      setState(() {
        // 刷新状态，同步result的值
        result;
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
          Text("当前Page1，路由地址：${widget.route}", style: MyTextStyle.Normal()),
          CupertinoButton(
            // 一个普通按钮
              onPressed: _nativeToast,
              child: const Text("调用原生方法进行Toast且返回结果")),
          Text("Method调用结果是：$result", style: MyTextStyle.Normal())
        ],
      ),
    );
  }
}