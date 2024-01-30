package com.xiaojinzi.component2.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Parcelable
import android.util.SparseArray
import android.widget.TextView
import androidx.annotation.NonNull
import com.xiaojinzi.base.ModuleConfig
import com.xiaojinzi.base.bean.SubParcelable
import com.xiaojinzi.base.bean.User
import com.xiaojinzi.base.bean.UserWithParcelable
import com.xiaojinzi.base.bean.UserWithSerializable
import com.xiaojinzi.base.service.inter.app.AnnoMethodService
import com.xiaojinzi.base.view.BaseAct
import com.xiaojinzi.component.Component
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno
import com.xiaojinzi.component.anno.RouterAnno
import com.xiaojinzi.component.anno.ServiceAutowiredAnno
import com.xiaojinzi.component.forward
import com.xiaojinzi.component.impl.Router
import com.xiaojinzi.component.impl.RouterRequest
import com.xiaojinzi.component2.R
import java.util.*

@RouterAnno(
    path = ModuleConfig.Module2.MAIN1,
    desc = "业务组件2的主界面"
)
fun component2ActIntent1(request: RouterRequest): Intent {
    return Intent(
        request.context,
        Component2Act::class.java,
    )
}

@RouterAnno(
        path = ModuleConfig.Module2.MAIN,
        desc = "业务组件2的主界面"
)
class Component2Act : BaseAct() {

    @AttrValueAutowiredAnno("data")
    var data: String? = null

    // array 的几种类型
    @AttrValueAutowiredAnno("data1")
    var data1: ByteArray? = null

    @AttrValueAutowiredAnno("data2")
    var data2: CharArray? = null

    @AttrValueAutowiredAnno("data3")
    var data3: Array<String>? = null

    @AttrValueAutowiredAnno("data4")
    var data4: ShortArray? = null

    @AttrValueAutowiredAnno("data5")
    var data5: IntArray? = null

    @AttrValueAutowiredAnno("data6")
    var data6: LongArray? = null

    @AttrValueAutowiredAnno("data7")
    var data7: FloatArray? = null

    @AttrValueAutowiredAnno("data8")
    var data8: DoubleArray? = null

    @AttrValueAutowiredAnno("data9")
    var data9: BooleanArray? = null

    @AttrValueAutowiredAnno("data10")
    var data10: Array<Parcelable>? = null

    @AttrValueAutowiredAnno("data11")
    var data11: Array<CharSequence>? = null

    // 基本数据类型

    // 基本数据类型
    @AttrValueAutowiredAnno("data40")
    var data40: String? = null

    @AttrValueAutowiredAnno("data41")
    var data41: CharSequence? = null

    @AttrValueAutowiredAnno("data42")
    var data42: Byte = 0

    @AttrValueAutowiredAnno("data43")
    var data43 = 0.toChar()

    @AttrValueAutowiredAnno("data44")
    var data44 = false

    @AttrValueAutowiredAnno("data45")
    var data45: Short = 0

    @AttrValueAutowiredAnno("data46")
    var data46 = 0

    @AttrValueAutowiredAnno("data47")
    var data47: Long = 0

    @AttrValueAutowiredAnno("data48")
    var data48 = 0f

    @AttrValueAutowiredAnno("data49")
    var data49 = 0.0

    // ArrayList 的几种类型
    @AttrValueAutowiredAnno("data30")
    var data30: ArrayList<CharSequence>? = null

    @AttrValueAutowiredAnno("data31")
    var data31: ArrayList<String>? = null

    @AttrValueAutowiredAnno("data32")
    var data32: ArrayList<Int>? = null

    // Parcelable 的一些类型
    @AttrValueAutowiredAnno("data33")
    var data33: ArrayList<Parcelable>? = null

    @AttrValueAutowiredAnno("data34")
    var data34: ArrayList<UserWithParcelable>? = null

    @AttrValueAutowiredAnno("data341")
    var data341: ArrayList<UserWithSerializable>? = null

    @AttrValueAutowiredAnno("data35")
    var data35: ArrayList<SubParcelable>? = null

    @AttrValueAutowiredAnno("data36")
    var data36: SparseArray<Parcelable>? = null

    @AttrValueAutowiredAnno("data37")
    var data37: SparseArray<UserWithParcelable>? = null

    @AttrValueAutowiredAnno("data38")
    var data38: SparseArray<SubParcelable>? = null

    // 其他的类型

    // 其他的类型
    @AttrValueAutowiredAnno("data12")
    var data12: User? = null

    @AttrValueAutowiredAnno("data13")
    var data13: UserWithSerializable? = null

    @AttrValueAutowiredAnno("data14")
    var data14: UserWithParcelable? = null

    /**
     * 注入一个服务
     */
    @ServiceAutowiredAnno
    var annoMethodService: AnnoMethodService? = null

    lateinit var tv_data: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component2_act)
        Component.inject(this);
        tv_data = findViewById(R.id.tv_data)
        tv_data.text = data;

        val fragment = Router
                .with(ModuleConfig.Module1.TEST_FRAGMENT)
                .navigate()

        Router
                .with(this)
                .host(ModuleConfig.Module2.NAME)
                .path(ModuleConfig.Module2.MAIN)
                .forward()

        Router.with(this)
                .host(ModuleConfig.Module1.NAME)
                .path(ModuleConfig.Module1.TEST)
                .forward()

        Router
                .with(this)
                .host(ModuleConfig.Module2.NAME)
                .path(ModuleConfig.Module2.MAIN)
                .forward {
                    finish()
                }

    }

}
