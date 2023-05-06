import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:tson_flutter/page1/MethodChannelPage.dart';
import 'package:tson_flutter/page2/BasicMessageChannelPage.dart';
import 'package:tson_flutter/page3/EventChannelPage.dart';

void main() => runApp(TestApp(route: window.defaultRouteName));

class TestApp extends StatelessWidget {
  const TestApp({super.key, required this.route});

  final String route;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        title: route,
        theme: ThemeData(
          primarySwatch: Colors.blue,
        ),
        home: Scaffold(
          appBar: AppBar(title: const Text("测试页面")),
          body: Center(
            child: Column(
              children: [
                //根据路由显示不同页面
                if (route == 'FLUTTER_HOME_home_tab_1')
                  MethodChannelPage(
                    route: route,
                  )
                else if (route == 'FLUTTER_HOME_home_tab_2')
                  BasicMessageChannelPage(
                    route: route,
                  )
                else if (route == 'FLUTTER_HOME_home_tab_3')
                  EventChannelPage(
                    route: route,
                  )
                else
                  const Text('这是一个默认页面'),
              ],
            ),
          ),
        ));
  }
}
