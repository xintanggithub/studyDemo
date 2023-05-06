package com.tson.study.methodchannel

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