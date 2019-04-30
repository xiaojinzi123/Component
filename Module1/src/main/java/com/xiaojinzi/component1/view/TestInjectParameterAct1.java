package com.xiaojinzi.component1.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.support.ParameterSupport;
import com.xiaojinzi.component1.R;

@RouterAnno(
        host = ModuleConfig.Module1.NAME,
        path = ModuleConfig.Module1.TEST_INJECT1
)
public class TestInjectParameterAct1 extends BaseAct {

    public static final int defaultValue = -1;

    @ParameterAnno("valueString")
    String valueString;

    @ParameterAnno("valueStringDefault")
    String valueStringDefault = "hello";

    @ParameterAnno("valueByte")
    byte valueByte;
    @ParameterAnno("valueByteDefalut")
    byte valueByteDefalut = defaultValue;
    @ParameterAnno("valueByteBox")
    Byte valueByteBox;
    @ParameterAnno("valueByteBoxDefalut")
    Byte valueByteBoxDefalut = defaultValue;

    @ParameterAnno("valueShort")
    short valueShort;
    @ParameterAnno("valueShortDefalut")
    short valueShortDefalut = defaultValue;
    @ParameterAnno("valueShortBox")
    Short valueShortBox;
    @ParameterAnno("valueShortBoxDefalut")
    Short valueShortBoxDefalut = defaultValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_inject_parameter_act);
        ParameterSupport.inject(this);
    }

    @Override
    protected void returnData() {
        if (check()) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }else {
            Intent intent = new Intent();
            setResult(RESULT_ERROR, intent);
            finish();
        }
    }

    private boolean check() {

        Bundle bundle = getIntent().getExtras();

        // 检查 String 参数的 =============================================

        if (bundle.containsKey("valueString")) {
            if (!valueString.equals(bundle.getString("valueString"))) {
                return false;
            }
        } else {
            if (valueString != null) {
                return false;
            }
        }
        if (bundle.containsKey("valueStringDefault")) {
            if (!valueStringDefault.equals(bundle.getString("valueStringDefault"))) {
                return false;
            }
        } else {
            if (!"hello".equals(valueStringDefault)) {
                return false;
            }
        }

        // 检查 Byte 参数的 ======================================================
        // 如果存在
        if (bundle.containsKey("valueByte")) {
            if (valueByte != bundle.getByte("valueByte")) {
                return false;
            }
        } else {
            if (valueByte != 0) {
                return false;
            }
        }

        if (bundle.containsKey("valueByteDefalut")) {
            if (valueByteDefalut != bundle.getByte("valueByteDefalut")) {
                return false;
            }
        } else {
            if (valueByteDefalut != defaultValue) {
                return false;
            }
        }

        if (bundle.containsKey("valueByteBox")) {
            if (valueByteBox == null) {
                return false;
            } else {
                if (!valueByteBox.equals(bundle.getByte("valueByteBox"))) {
                    return false;
                }
            }
        } else {
            if (valueByteBox != null) {
                return false;
            }
        }

        if (bundle.containsKey("valueByteBoxDefalut")) {
            if (valueByteBoxDefalut == null || valueByteBoxDefalut == defaultValue) {
                return false;
            } else {
                if (valueByteBoxDefalut != bundle.getByte("valueByteBoxDefalut")) {
                    return false;
                }
            }
        } else {
            if (valueByteBoxDefalut != defaultValue) {
                return false;
            }
        }

        // 检查 Short 参数的 ======================================================
        // 如果存在
        if (bundle.containsKey("valueShort")) {
            if (valueShort != bundle.getShort("valueShort")) {
                return false;
            }
        } else {
            if (valueShort != 0) {
                return false;
            }
        }

        if (bundle.containsKey("valueShortDefalut")) {
            if (valueShortDefalut != bundle.getShort("valueShortDefalut")) {
                return false;
            }
        } else {
            if (valueShortDefalut != defaultValue) {
                return false;
            }
        }

        if (bundle.containsKey("valueShortBox")) {
            if (valueShortBox == null) {
                return false;
            } else {
                if (!valueShortBox.equals(bundle.getShort("valueShortBox"))) {
                    return false;
                }
            }
        } else {
            if (valueShortBox != null) {
                return false;
            }
        }

        if (bundle.containsKey("valueShortBoxDefalut")) {
            if (valueShortBoxDefalut == null || valueShortBoxDefalut == defaultValue) {
                return false;
            } else {
                if (valueShortBoxDefalut != bundle.getShort("valueShortBoxDefalut")) {
                    return false;
                }
            }
        } else {
            if (valueShortBoxDefalut != valueShortBoxDefalut) {
                return false;
            }
        }

        return true;

    }

}
