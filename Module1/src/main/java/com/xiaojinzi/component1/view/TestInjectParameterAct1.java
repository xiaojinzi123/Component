package com.xiaojinzi.component1.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.support.ParameterSupport;
import com.xiaojinzi.component1.R;

/**
 * 测试属性注入功能的界面
 */
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

    @ParameterAnno("valueInt")
    int valueInt;
    @ParameterAnno("valueIntDefalut")
    int valueIntDefalut = defaultValue;
    @ParameterAnno("valueIntBox")
    Integer valueIntBox;
    @ParameterAnno("valueIntBoxDefalut")
    Integer valueIntBoxDefalut = defaultValue;

    @ParameterAnno("valueLong")
    long valueLong;
    @ParameterAnno("valueLongDefalut")
    long valueLongDefalut = defaultValue;
    @ParameterAnno("valueLongBox")
    Long valueLongBox;
    @ParameterAnno("valueLongBoxDefalut")
    Long valueLongBoxDefalut = Long.valueOf(defaultValue);

    @ParameterAnno("valueFloat")
    float valueFloat;
    @ParameterAnno("valueFloatDefalut")
    float valueFloatDefalut = defaultValue;
    @ParameterAnno("valueFloatBox")
    Float valueFloatBox;
    @ParameterAnno("valueFloatBoxDefalut")
    Float valueFloatBoxDefalut = Float.valueOf(defaultValue);

    @ParameterAnno("valueDouble")
    double valueDouble;
    @ParameterAnno("valueDoubleDefalut")
    double valueDoubleDefalut = defaultValue;
    @ParameterAnno("valueDoubleBox")
    Double valueDoubleBox;
    @ParameterAnno("valueDoubleBoxDefalut")
    Double valueDoubleBoxDefalut = Double.valueOf(defaultValue);

    @ParameterAnno("valueBoolean")
    boolean valueBoolean;
    @ParameterAnno("valueBooleanDefalut")
    boolean valueBooleanDefalut = true;
    @ParameterAnno("valueBooleanBox")
    Boolean valueBooleanBox;
    @ParameterAnno("valueBooleanBoxDefalut")
    Boolean valueBooleanBoxDefalut = true;

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
        } else {
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
            if (valueShortBoxDefalut != defaultValue) {
                return false;
            }
        }

        // 检查 Int 参数的 ======================================================
        // 如果存在
        if (bundle.containsKey("valueInt")) {
            if (valueInt != bundle.getInt("valueInt")) {
                return false;
            }
        } else {
            if (valueInt != 0) {
                return false;
            }
        }

        if (bundle.containsKey("valueIntDefalut")) {
            if (valueIntDefalut != bundle.getInt("valueIntDefalut")) {
                return false;
            }
        } else {
            if (valueIntDefalut != defaultValue) {
                return false;
            }
        }

        if (bundle.containsKey("valueIntBox")) {
            if (valueIntBox == null) {
                return false;
            } else {
                if (!valueIntBox.equals(bundle.getInt("valueIntBox"))) {
                    return false;
                }
            }
        } else {
            if (valueIntBox != null) {
                return false;
            }
        }

        if (bundle.containsKey("valueIntBoxDefalut")) {
            if (valueIntBoxDefalut == null || valueIntBoxDefalut == defaultValue) {
                return false;
            } else {
                if (valueIntBoxDefalut != bundle.getInt("valueIntBoxDefalut")) {
                    return false;
                }
            }
        } else {
            if (valueIntBoxDefalut != defaultValue) {
                return false;
            }
        }

        // 检查 Long 参数的 ======================================================
        // 如果存在
        if (bundle.containsKey("valueLong")) {
            if (valueLong != bundle.getLong("valueLong")) {
                return false;
            }
        } else {
            if (valueLong != 0) {
                return false;
            }
        }

        if (bundle.containsKey("valueLongDefalut")) {
            if (valueLongDefalut != bundle.getLong("valueLongDefalut")) {
                return false;
            }
        } else {
            if (valueLongDefalut != defaultValue) {
                return false;
            }
        }

        if (bundle.containsKey("valueLongBox")) {
            if (valueLongBox == null) {
                return false;
            } else {
                if (!valueLongBox.equals(bundle.getLong("valueLongBox"))) {
                    return false;
                }
            }
        } else {
            if (valueLongBox != null) {
                return false;
            }
        }

        if (bundle.containsKey("valueLongBoxDefalut")) {
            if (valueLongBoxDefalut == null || valueLongBoxDefalut == defaultValue) {
                return false;
            } else {
                if (valueLongBoxDefalut != bundle.getLong("valueLongBoxDefalut")) {
                    return false;
                }
            }
        } else {
            if (valueLongBoxDefalut != defaultValue) {
                return false;
            }
        }

        // 检查 Float 参数的 ======================================================
        // 如果存在
        if (bundle.containsKey("valueFloat")) {
            if (valueFloat != bundle.getFloat("valueFloat")) {
                return false;
            }
        } else {
            if (valueFloat != 0) {
                return false;
            }
        }

        if (bundle.containsKey("valueFloatDefalut")) {
            if (valueFloatDefalut != bundle.getFloat("valueFloatDefalut")) {
                return false;
            }
        } else {
            if (valueFloatDefalut != defaultValue) {
                return false;
            }
        }

        if (bundle.containsKey("valueFloatBox")) {
            if (valueFloatBox == null) {
                return false;
            } else {
                if (!valueFloatBox.equals(bundle.getFloat("valueFloatBox"))) {
                    return false;
                }
            }
        } else {
            if (valueFloatBox != null) {
                return false;
            }
        }

        if (bundle.containsKey("valueFloatBoxDefalut")) {
            if (valueFloatBoxDefalut == null || valueFloatBoxDefalut == defaultValue) {
                return false;
            } else {
                if (valueFloatBoxDefalut != bundle.getFloat("valueFloatBoxDefalut")) {
                    return false;
                }
            }
        } else {
            if (valueFloatBoxDefalut != defaultValue) {
                return false;
            }
        }

        // 检查 Double 参数的 ======================================================
        // 如果存在
        if (bundle.containsKey("valueDouble")) {
            if (valueDouble != bundle.getDouble("valueDouble")) {
                return false;
            }
        } else {
            if (valueDouble != 0) {
                return false;
            }
        }

        if (bundle.containsKey("valueDoubleDefalut")) {
            if (valueDoubleDefalut != bundle.getDouble("valueDoubleDefalut")) {
                return false;
            }
        } else {
            if (valueDoubleDefalut != defaultValue) {
                return false;
            }
        }

        if (bundle.containsKey("valueDoubleBox")) {
            if (valueDoubleBox == null) {
                return false;
            } else {
                if (!valueDoubleBox.equals(bundle.getDouble("valueDoubleBox"))) {
                    return false;
                }
            }
        } else {
            if (valueDoubleBox != null) {
                return false;
            }
        }

        if (bundle.containsKey("valueDoubleBoxDefalut")) {
            if (valueDoubleBoxDefalut == null || valueDoubleBoxDefalut == defaultValue) {
                return false;
            } else {
                if (valueDoubleBoxDefalut != bundle.getDouble("valueDoubleBoxDefalut")) {
                    return false;
                }
            }
        } else {
            if (valueDoubleBoxDefalut != defaultValue) {
                return false;
            }
        }

        // 检查 Boolean 参数的 ======================================================
        // 如果存在
        if (bundle.containsKey("valueBoolean")) {
            if (valueBoolean != bundle.getBoolean("valueBoolean")) {
                return false;
            }
        } else {
            if (valueBoolean != false) {
                return false;
            }
        }

        if (bundle.containsKey("valueBooleanDefalut")) {
            if (valueBooleanDefalut != bundle.getBoolean("valueBooleanDefalut")) {
                return false;
            }
        } else {
            if (valueBooleanDefalut != true) {
                return false;
            }
        }

        if (bundle.containsKey("valueBooleanBox")) {
            if (valueBooleanBox == null) {
                return false;
            } else {
                if (!valueBooleanBox.equals(bundle.getBoolean("valueBooleanBox"))) {
                    return false;
                }
            }
        } else {
            if (valueBooleanBox != null) {
                return false;
            }
        }

        if (bundle.containsKey("valueBooleanBoxDefalut")) {
            if (valueBooleanBoxDefalut == null || valueBooleanBoxDefalut == true) {
                return false;
            } else {
                if (valueBooleanBoxDefalut != bundle.getBoolean("valueBooleanBoxDefalut")) {
                    return false;
                }
            }
        } else {
            if (valueBooleanBoxDefalut != true) {
                return false;
            }
        }

        return true;

    }

}
