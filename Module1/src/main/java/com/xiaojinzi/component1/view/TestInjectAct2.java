package com.xiaojinzi.component1.view;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.bean.User;
import com.xiaojinzi.base.bean.UserWithParcelable;
import com.xiaojinzi.base.bean.UserWithSerializable;
import com.xiaojinzi.base.service.inter.app.AnnoMethodService;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.anno.FiledAutowiredAnno;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.anno.ServiceAutowiredAnno;
import com.xiaojinzi.component1.R;

import java.util.ArrayList;

/**
 * 测试除了基本属性之外的数据类型
 */
@RouterAnno(
        host = ModuleConfig.Module1.NAME,
        path = ModuleConfig.Module1.TEST_INJECT2
)
public class TestInjectAct2 extends BaseAct {

    // 基本数据类型
    @FiledAutowiredAnno("data40")
    String data40;
    @FiledAutowiredAnno("data41")
    CharSequence data41;
    @FiledAutowiredAnno("data42")
    byte data42;
    @FiledAutowiredAnno("data43")
    char data43;
    @FiledAutowiredAnno("data44")
    boolean data44;
    @FiledAutowiredAnno("data45")
    short data45;
    @FiledAutowiredAnno("data46")
    int data46;
    @FiledAutowiredAnno("data47")
    long data47;
    @FiledAutowiredAnno("data48")
    float data48;
    @FiledAutowiredAnno("data49")
    double data49;


    // ArrayList 的几种类型

    @FiledAutowiredAnno("data30")
    ArrayList<CharSequence> data30;
    @FiledAutowiredAnno("data31")
    ArrayList<String> data31;
    @FiledAutowiredAnno("data32")
    ArrayList<Integer> data32;
    @FiledAutowiredAnno("data13")
    ArrayList<Parcelable> data33;

    // array 的几种类型

    @FiledAutowiredAnno("data1")
    byte[] data1;
    @FiledAutowiredAnno("data2")
    char[] data2;
    @FiledAutowiredAnno("data3")
    String[] data3;
    @FiledAutowiredAnno("data4")
    short[] data4;
    @FiledAutowiredAnno("data5")
    int[] data5;
    @FiledAutowiredAnno("data6")
    long[] data6;
    @FiledAutowiredAnno("data7")
    float[] data7;
    @FiledAutowiredAnno("data8")
    double[] data8;
    @FiledAutowiredAnno("data9")
    boolean[] data9;
    @FiledAutowiredAnno("data10")
    Parcelable[] data10;
    @FiledAutowiredAnno("data11")
    CharSequence[] data11;

    // 其他的类型

    @FiledAutowiredAnno("data12")
    User data12;
    @FiledAutowiredAnno("data13")
    UserWithSerializable data13;
    @FiledAutowiredAnno("data14")
    UserWithParcelable data14;

    /**
     * 注入一个服务
     */
    @ServiceAutowiredAnno
    AnnoMethodService annoMethodService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_inject_parameter_act);
        Component.inject(this);
    }

}
