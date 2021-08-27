package com.xiaojinzi.componentdemo.view

import com.xiaojinzi.component.anno.AttrValueAutowiredAnno

class Test {

    @AttrValueAutowiredAnno("name")
    lateinit var name: String

}