package com.tson.study.base

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

/**
 *  Date 2023/5/6 2:00 下午
 *
 * @author Tson
 */
fun <T> Observable<T>.mySubscribe(
    onNext: Consumer<in T>?
): Disposable {
    return this.subscribe(
        onNext,
        { println("error -> ${it.message}") },
        { println("complete") }) {
    }
}