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
import com.xiaojinzi.component.anno.FieldAutowiredAnno;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.anno.ServiceAutowiredAnno;
import com.xiaojinzi.component1.R;

import java.util.ArrayList;

/**
 * 测试除了基本属性之外的数据类型
 */
@RouterAnno(
        path = ModuleConfig.Module1.TEST_INJECT2
)
public class TestInjectAct2 extends BaseAct {

    // 基本数据类型
    @FieldAutowiredAnno("data40")
    String data40;
    @FieldAutowiredAnno("data41")
    CharSequence data41;
    @FieldAutowiredAnno("data42")
    byte data42;
    @FieldAutowiredAnno("data43")
    char data43;
    @FieldAutowiredAnno("data44")
    boolean data44;
    @FieldAutowiredAnno("data45")
    short data45;
    @FieldAutowiredAnno("data46")
    int data46;
    @FieldAutowiredAnno("data47")
    long data47;
    @FieldAutowiredAnno("data48")
    float data48;
    @FieldAutowiredAnno("data49")
    double data49;


    // ArrayList 的几种类型

    @FieldAutowiredAnno("data30")
    ArrayList<CharSequence> data30;
    @FieldAutowiredAnno("data31")
    ArrayList<String> data31;
    @FieldAutowiredAnno("data32")
    ArrayList<Integer> data32;
    @FieldAutowiredAnno("data13")
    ArrayList<Parcelable> data33;

    // array 的几种类型

    @FieldAutowiredAnno("data1")
    byte[] data1;
    @FieldAutowiredAnno("data2")
    char[] data2;
    @FieldAutowiredAnno("data3")
    String[] data3;
    @FieldAutowiredAnno("data4")
    short[] data4;
    @FieldAutowiredAnno("data5")
    int[] data5;
    @FieldAutowiredAnno("data6")
    long[] data6;
    @FieldAutowiredAnno("data7")
    float[] data7;
    @FieldAutowiredAnno("data8")
    double[] data8;
    @FieldAutowiredAnno("data9")
    boolean[] data9;
    @FieldAutowiredAnno("data10")
    Parcelable[] data10;
    @FieldAutowiredAnno("data11")
    CharSequence[] data11;

    // 其他的类型

    @FieldAutowiredAnno("data12")
    User data12;
    @FieldAutowiredAnno("data13")
    UserWithSerializable data13;
    @FieldAutowiredAnno("data14")
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
