package com.tson.study.eventchannel

import android.os.Bundle
import com.tson.study.base.mySubscribe
import com.tson.study.flutter.EVENT_CHANNEL_1
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.EventChannel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.logging.StreamHandler

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