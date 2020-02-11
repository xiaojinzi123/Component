package com.xiaojinzi.componentdemo.view

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.xiaojinzi.componentdemo.R

class ProxyAct : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.proxy_act)
        findViewById<TextView>(R.id.tv)!!.text = intent.data.toString()
    }

}
