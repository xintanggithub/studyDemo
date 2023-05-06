package com.tson.study.basicmessagechannel

import android.os.Bundle
import com.tson.study.base.mySubscribe
import com.tson.study.flutter.BASIC_MESSAGE_CHANNEL_1
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.StringCodec
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

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
                // 定时发送消息
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