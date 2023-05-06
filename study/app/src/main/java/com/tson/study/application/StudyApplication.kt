package com.tson.study.application

import android.app.Application
import com.tson.study.flutter.FLUTTER_HOME_ID
import com.tson.study.flutter.HOME_PAGE_1
import com.tson.study.flutter.HOME_PAGE_2
import com.tson.study.flutter.HOME_PAGE_3
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.embedding.engine.loader.FlutterLoader
import io.flutter.view.FlutterMain

/**
 *  Date 2023/5/5 3:29 下午
 *
 * @author Tson
 */
class StudyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 其中一个模块预加载，如果多个模块，创建多个FlutterEngine即可
        FlutterEngine(this).also {
            it.navigationChannel.run {
                setInitialRoute(HOME_PAGE_1)
                setInitialRoute(HOME_PAGE_2)
                setInitialRoute(HOME_PAGE_3)
            }
            it.dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
            FlutterEngineCache.getInstance().put(FLUTTER_HOME_ID, it)
        }
    }

}