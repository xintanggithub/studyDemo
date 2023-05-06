package com.tson.study

import com.tson.study.base.BaseActivity
import com.tson.study.basicmessagechannel.BasicMessageChannelActivity
import com.tson.study.databinding.ActivityMainBinding
import com.tson.study.eventchannel.EventChannelActivity
import com.tson.study.flutter.HOME_PAGE_1
import com.tson.study.flutter.HOME_PAGE_2
import com.tson.study.flutter.HOME_PAGE_3
import com.tson.study.flutter.open
import com.tson.study.methodchannel.MethodChannelActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun initView() {
        binding.run {
            // 不同方式通讯页面的跳转
            button3.setOnClickListener {
                open(HOME_PAGE_1, MethodChannelActivity::class.java)
            }
            button.setOnClickListener {
                open(HOME_PAGE_2, BasicMessageChannelActivity::class.java)
            }
            button2.setOnClickListener {
                open(HOME_PAGE_3, EventChannelActivity::class.java)
            }
        }
    }

}