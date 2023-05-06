package com.tson.study.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import java.lang.reflect.ParameterizedType

/**
 *  Date 2023/5/5 3:40 下午
 *
 * @author Tson
 */
abstract class BaseActivity<VB : ViewDataBinding> : Activity() {
    lateinit var binding: VB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val clazz = type.actualTypeArguments[0] as Class<VB>
            val method = clazz.getMethod("inflate", LayoutInflater::class.java)
            binding = method.invoke(null, layoutInflater) as VB
            setContentView(binding.root)
        }
        initView()
    }

    abstract fun initView()
}