package com.xiaojinzi.component1.view;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.bean.SubParcelable;
import com.xiaojinzi.base.bean.User;
import com.xiaojinzi.base.bean.UserWithParcelable;
import com.xiaojinzi.base.bean.UserWithSerializable;
import com.xiaojinzi.base.service.inter.app.AnnoMethodService;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.Component;
import com.xiaojinzi.component.anno.AttrValueAutowiredAnno;
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

    // array 的几种类型

    @AttrValueAutowiredAnno("data1")
    byte[] data1;
    @AttrValueAutowiredAnno("data2")
    char[] data2;
    @AttrValueAutowiredAnno("data3")
    String[] data3;
    @AttrValueAutowiredAnno("data4")
    short[] data4;
    @AttrValueAutowiredAnno("data5")
    int[] data5;
    @AttrValueAutowiredAnno("data6")
    long[] data6;
    @AttrValueAutowiredAnno("data7")
    float[] data7;
    @AttrValueAutowiredAnno("data8")
    double[] data8;
    @AttrValueAutowiredAnno("data9")
    boolean[] data9;

    @AttrValueAutowiredAnno("data10")
    Parcelable[] data10;
    // 不支持注入的类型
    /*@AttrValueAutowiredAnno("data101")
    UserWithParcelable[] data101;*/
    @AttrValueAutowiredAnno("data11")
    CharSequence[] data11;

    // 基本数据类型

    @AttrValueAutowiredAnno("data40")
    String data40;
    @AttrValueAutowiredAnno("data41")
    CharSequence data41;
    @AttrValueAutowiredAnno("data42")
    byte data42;
    @AttrValueAutowiredAnno("data43")
    char data43;
    @AttrValueAutowiredAnno("data44")
    boolean data44;
    @AttrValueAutowiredAnno("data45")
    short data45;
    @AttrValueAutowiredAnno("data46")
    int data46;
    @AttrValueAutowiredAnno("data47")
    long data47;
    @AttrValueAutowiredAnno("data48")
    float data48;
    @AttrValueAutowiredAnno("data49")
    double data49;


    // ArrayList 的几种类型

    @AttrValueAutowiredAnno("data30")
    ArrayList<CharSequence> data30;
    @AttrValueAutowiredAnno("data31")
    ArrayList<String> data31;
    @AttrValueAutowiredAnno("data32")
    ArrayList<Integer> data32;

    // Parcelable 的一些类型
    @AttrValueAutowiredAnno("data33")
    ArrayList<Parcelable> data33;
    @AttrValueAutowiredAnno("data34")
    ArrayList<UserWithParcelable> data34;
    @AttrValueAutowiredAnno("data341")
    ArrayList<UserWithSerializable> data341;
    @AttrValueAutowiredAnno("data35")
    ArrayList<SubParcelable> data35;

    @AttrValueAutowiredAnno("data36")
    SparseArray<Parcelable> data36;
    @AttrValueAutowiredAnno("data37")
    SparseArray<UserWithParcelable> data37;
    @AttrValueAutowiredAnno("data38")
    SparseArray<SubParcelable> data38;

    // 其他的类型

    @AttrValueAutowiredAnno("data12")
    User data12;
    @AttrValueAutowiredAnno("data13")
    UserWithSerializable data13;
    @AttrValueAutowiredAnno("data14")
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
        System.out.println("注入了");
    }

}
