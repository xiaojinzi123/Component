package com.xiaojinzi.component2.view

import android.os.Bundle
import android.support.v4.app.Fragment

import com.xiaojinzi.component.anno.FragmentAnno

@FragmentAnno(value = ["component2.test1Fragment1"])
class Test1Fragment : Fragment() {

    companion object {
        @FragmentAnno("component2.test1Fragment2")
        fun newInstance(bundle: Bundle): Test1Fragment {
            val f = Test1Fragment()
            f.arguments = bundle
            return f
        }
    }

}

