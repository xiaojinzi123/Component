package com.xiaojinzi.componentdemo.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.xiaojinzi.componentdemo.R
import com.xiaojinzi.componentdemo.util.ToastUtil

class ProxyAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.proxy_act)
        val url = intent.data.toString()
        findViewById<TextView>(R.id.tv)!!.text = url
    }

}
