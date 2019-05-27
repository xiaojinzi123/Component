package com.xiaojinzi.component1.view;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.bean.User;
import com.xiaojinzi.base.bean.UserWithParcelable;
import com.xiaojinzi.base.bean.UserWithSerializable;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.support.ParameterSupport;
import com.xiaojinzi.component1.R;

import java.util.ArrayList;

/**
 * 测试除了基本属性之外的数据类型
 */
@RouterAnno(
        host = ModuleConfig.Module1.NAME,
        path = ModuleConfig.Module1.TEST_INJECT2
)
public class TestInjectParameterAct2 extends BaseAct {

    // 基本数据类型
    @ParameterAnno("data40")
    String data40;
    @ParameterAnno("data41")
    CharSequence data41;
    @ParameterAnno("data42")
    byte data42;
    @ParameterAnno("data43")
    char data43;
    @ParameterAnno("data44")
    boolean data44;
    @ParameterAnno("data45")
    short data45;
    @ParameterAnno("data46")
    int data46;
    @ParameterAnno("data47")
    long data47;
    @ParameterAnno("data48")
    float data48;
    @ParameterAnno("data49")
    double data49;


    // ArrayList 的几种类型

    /*@ParameterAnno("data30")
    ArrayList<CharSequence> data30;
    @ParameterAnno("data31")
    ArrayList<String> data31;
    @ParameterAnno("data32")
    ArrayList<Integer> data32;
    @ParameterAnno("data13")
    ArrayList<Parcelable> data33;*/

    // array 的几种类型

    /*@ParameterAnno("data1")
    byte[] data1;
    @ParameterAnno("data2")
    char[] data2;
    @ParameterAnno("data3")
    String[] data3;
    @ParameterAnno("data4")
    short[] data4;
    @ParameterAnno("data5")
    int[] data5;
    @ParameterAnno("data6")
    long[] data6;
    @ParameterAnno("data7")
    float[] data7;
    @ParameterAnno("data8")
    double[] data8;
    @ParameterAnno("data9")
    boolean[] data9;
    @ParameterAnno("data10")
    Parcelable[] data10;
    @ParameterAnno("data11")
    CharSequence[] data11;*/

    // 其他的类型

    /*@ParameterAnno("data12")
    User data12;
    @ParameterAnno("data13")
    UserWithSerializable data13;
    @ParameterAnno("data14")
    UserWithParcelable data14;*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_inject_parameter_act);
        ParameterSupport.inject(this);
        Bundle bundle = getIntent().getExtras();
    }

}
