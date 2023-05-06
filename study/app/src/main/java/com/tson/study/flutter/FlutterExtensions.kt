package com.tson.study.flutter

import android.content.Context
import io.flutter.embedding.android.FlutterActivity

/**
 *  Date 2023/5/5 4:18 下午
 *
 * @author Tson
 */
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
