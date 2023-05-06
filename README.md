### 原生项目引入Flutter module，三种通讯方式示例

> 目录说明
> - study 原生项目
> - tson_flutter 引入的flutter module

### 如何在原生项目添加flutter的module？

- 创建`Flutter module`
> 菜单： File > New > New Flutter Project，然后如下填写你的项目信息

![image1](https://github.com/xintanggithub/studyDemo/blob/main/study/newflutterproject.png?raw=true)

- 下载 `Android Sdk Command Tools`，如下

![image2](https://github.com/xintanggithub/studyDemo/blob/main/study/downloadtoolssdk.png?raw=true)

- 根目录 `settings.gradle` 修改入下，注意看注释
```gradle
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT) // 这里要修改为RepositoriesMode.PREFER_PROJECT
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "study"
include ':app'

// 这里是新增的 ↓↓↓↓↓↓↓↓ (如果编译器提示Binding需要导包，不要管，报错不影响构建)
setBinding(new Binding([gradle: this]))
evaluate(new File(
        settingsDir.parentFile,
        'tson_flutter/.android/include_flutter.groovy'
))
// 上面是新增的 ↑↑↑↑↑↑↑↑

// 下面这个不用管，上面的添加完，构建成功之后会自动生成
include ':tson_flutter'
project(':tson_flutter').projectDir = new File('../tson_flutter')
```

然后构建，构建成功之后会自动生成下面两行配置

- `gradle.properties` 中添加 `android.enableJetifier=true`

- 最后在 `app` 下的 `build.gradle` 添加依赖
> 注意是 `':flutter'` 而不是 `':tson_flutter'` (不是上面自己创建的时候取的 `module` 名字)
```gradle
implementation project(':flutter')
```

然后就可以正常运行了，构建完成之后长这样：
![image3](https://github.com/xintanggithub/studyDemo/blob/main/study/project.png?raw=true)

- 跳转Flutter页面

> `AndroidManifest.xml` 对FlutterActivity进行注册
```xml
        <activity
            android:name="io.flutter.embedding.android.FlutterActivity"
            android:configChanges="orientation|keyboardHidden|keyboard|screenSize|locale|layoutDirection|fontScale|screenLayout|density|uiMode"
            android:hardwareAccelerated="true"
            android:windowSoftInputMode="adjustResize" />
```

> 默认跳转
```kotlin
startActivity(FlutterActivity.createDefaultIntent(context))
```

> 带路由跳转（我这里拓展了一个方法，方便使用）
```kotlin
/**
 * [route] 路由
 * [clazz] 继承于FlutterActivity的自定义Activity的class名称，不传就是打开默认的FlutterActivity
 */
fun Context?.open(route: String, clazz: Class<*>?) {
    this?.run {
        // 默认的路由传递创建Intent
        FlutterActivity.withNewEngine().initialRoute(route).build(this)
            .also {
                if (null != clazz) {
                    // 指定页面调整，如果不指定，则跳转到默认的FlutterActivity
                    it.setClass(this, clazz)
                }
                startActivity(it)
            }
    }
}


// 调用
button.setOnClickListener {
      open(HOME_PAGE_1)
}
```

### 原生和Flutter之间如何进行通讯？

> Flutter 提供了3种通讯方式
> - `MethodChannel` ： Flutter调用原生方法，原生可以在该方法回应消息
> - `BasicMessageChannel` ：原生与Flutter可互相通讯，需要对通讯消息进行编码
> - `EventChannel` ：原生调用Flutter方法

先定义一些后面用到的常量
```kotlin
const val METHOD_CHANNEL_1 = "methodChannel1"
const val METHOD_CHANNEL_1_FUNC = "toastTest"

const val BASIC_MESSAGE_CHANNEL_1 = "basicMessageChannel1"

const val EVENT_CHANNEL_1 = "eventChannel1"

const val FLUTTER_HOME_ID = "tson_flutter_home_id_01" // 模块1首页

private const val FLUTTER_HOME_GROUP = "FLUTTER_HOME" // 模块1路由分组
const val HOME_PAGE_1 = "${FLUTTER_HOME_GROUP}_home_tab_1" // 首页页面1
const val HOME_PAGE_2 = "${FLUTTER_HOME_GROUP}_home_tab_2" // 首页页面2
const val HOME_PAGE_3 = "${FLUTTER_HOME_GROUP}_home_tab_3" // 首页页面3
```

然后是改编的Flutter的 `main.dart`
```dart

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
```


#### MethodChannel通讯

##### Android端

- 新建 `MethodChannelActivity` 继承于 `FlutterActivity` ，要记得在androidManifest注册这个页面
```kotlin

import android.widget.Toast
import com.tson.study.flutter.METHOD_CHANNEL_1
import com.tson.study.flutter.METHOD_CHANNEL_1_FUNC
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MethodChannelActivity : FlutterActivity() {

    private lateinit var methodChannel: MethodChannel

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        // 创建这个方法渠道
        methodChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, METHOD_CHANNEL_1)
        methodChannel.setMethodCallHandler { call, result ->
            when (call.method) {
                METHOD_CHANNEL_1_FUNC -> { // 判断flutter传过来的MethodName
                    // 弹出Toast
                    Toast.makeText(
                        this@MethodChannelActivity,
                        call.arguments.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    // 返回结果
                    result.success("toast done ${System.currentTimeMillis()}")
                    println("test1111=>${call.arguments}")
                }
            }
        }
    }

}
```

##### Flutter端

```dart
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
```

#### BasicMessageChannel通讯

##### Android端
- 新建 `BasicMessageChannelActivity` 继承于 `FlutterActivity` ，要记得在androidManifest注册这个页面
```kotlin
class BasicMessageChannelActivity : FlutterActivity() {

    private lateinit var basicMessageChannel: BasicMessageChannel<String>

    private val cd = CompositeDisposable()

    private fun clearCompositeDisposable() {
        cd.dispose()
        cd.clear()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timer()
    }

    private fun timer() {
        Observable.interval(2, 2, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mySubscribe {
                // 定时发送消息给Flutter
                basicMessageChannel.send("rxjava定时消息：$it->${System.currentTimeMillis()}") { s ->
                    // 打印Flutter回复的消息
                    println("test11 原生发送的消息Flutter收到了，并回复：$s")
                }
            }.also {
                cd .add(it)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearCompositeDisposable()
    }


    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        basicMessageChannel = BasicMessageChannel(
            flutterEngine.dartExecutor,
            BASIC_MESSAGE_CHANNEL_1,
            StringCodec.INSTANCE
        )
        // 监听Flutter发送的消息
        basicMessageChannel.setMessageHandler { message, reply ->
            println("test11 接收到Flutter发送的消息了$message") // 打印消息
            // 然后回复消息给Flutter
            reply.reply("收到收到，over！${System.currentTimeMillis()}")
        }
    }
}
```

##### Flutter端

```dart

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
```

#### EventChannel通讯

##### Android端
- 新建 `EventChannel` 继承于 `FlutterActivity` ，要记得在androidManifest注册这个页面
```kotlin
class EventChannelActivity : FlutterActivity() {

    private val cd = CompositeDisposable()
    private var events: EventChannel.EventSink? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 创建渠道
        EventChannel(flutterEngine?.dartExecutor?.binaryMessenger, EVENT_CHANNEL_1)
            .setStreamHandler(object : StreamHandler(), EventChannel.StreamHandler {
                override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                    // 给EventSink赋值
                    this@EventChannelActivity.events = events
                }

                override fun onCancel(arguments: Any?) {
                    events = null
                }
            })
        // 模拟消息发送
        delaySendMessage()
    }

    override fun onDestroy() {
        super.onDestroy()
        cd.dispose()
        cd.clear()
    }

    private fun delaySendMessage() {
        Observable.interval(1, 1, TimeUnit.SECONDS)
            .delay(3, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .mySubscribe {
                // 发送消息给Flutter
                events?.success("我通过EventChannel给Flutter发送了一条消息$it")
            }
            .also { cd.add(it) }
    }

}
```

##### Flutter端
```dart

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
```
