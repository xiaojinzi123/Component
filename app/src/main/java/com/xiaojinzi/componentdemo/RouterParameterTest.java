package com.xiaojinzi.componentdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.bean.User;
import com.xiaojinzi.component.anno.ParameterAnno;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.anno.parameter.BooleanDefaultAnno;
import com.xiaojinzi.component.anno.parameter.ByteDefaultAnno;
import com.xiaojinzi.component.anno.parameter.DoubleDefaultAnno;
import com.xiaojinzi.component.anno.parameter.FloatDefaultAnno;
import com.xiaojinzi.component.anno.parameter.IntDefaultAnno;
import com.xiaojinzi.component.anno.parameter.LongDefaultAnno;
import com.xiaojinzi.component.anno.parameter.ShortDefaultAnno;
import com.xiaojinzi.component.anno.parameter.StringDefaultAnno;
import com.xiaojinzi.component.impl.RouterRequest;

public class RouterParameterTest {

    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER_BYTE
    )
    public static void testParameterByte(@NonNull RouterRequest request,
                                         @ParameterAnno("value") byte value,
                                         @ParameterAnno("valueDefault") @ByteDefaultAnno(8) byte valueDefault) {
        Bundle bundle = request.bundle;
        if (bundle.containsKey("value")) {
            if (value != bundle.getByte("value")) {
                throw new CheckFailException();
            }
        } else {
            if (value != 0) {
                throw new CheckFailException();
            }
        }
        if (bundle.containsKey("valueDefault")) {
            if (valueDefault != bundle.getByte("valueDefault")) {
                throw new CheckFailException();
            }
        } else {
            if (valueDefault != 8) {
                throw new CheckFailException();
            }
        }
    }


    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER_BYTE_BOX
    )
    public static void testParameterByteBox(@NonNull RouterRequest request,
                                            @ParameterAnno("value") Byte value,
                                            @ParameterAnno("valueDefault") @ByteDefaultAnno(8) Byte valueDefault) {
        Bundle bundle = request.bundle;
        if (bundle.containsKey("value")) {
            if (value != bundle.getByte("value")) {
                throw new CheckFailException();
            }
        } else {
            if (value != null) {
                throw new CheckFailException();
            }
        }
        if (bundle.containsKey("valueDefault")) {
            if (valueDefault != bundle.getByte("valueDefault")) {
                throw new CheckFailException();
            }
        } else {
            if (valueDefault != 8) {
                throw new CheckFailException();
            }
        }
    }

    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER_SHORT
    )
    public static void testParameterShort(@NonNull RouterRequest request,
                                          @ParameterAnno("value") short value,
                                          @ParameterAnno("valueDefault") @ShortDefaultAnno(8) short valueDefault) {
        Bundle bundle = request.bundle;
        if (bundle.containsKey("value")) {
            if (value != bundle.getShort("value")) {
                throw new CheckFailException();
            }
        } else {
            if (value != 0) {
                throw new CheckFailException();
            }
        }
        if (bundle.containsKey("valueDefault")) {
            if (valueDefault != bundle.getShort("valueDefault")) {
                throw new CheckFailException();
            }
        } else {
            if (valueDefault != 8) {
                throw new CheckFailException();
            }
        }
    }


    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER_SHORT_BOX
    )
    public static void testParameterShortBox(@NonNull RouterRequest request,
                                             @ParameterAnno("value") Short value,
                                             @ParameterAnno("valueDefault") @ShortDefaultAnno(8) Short valueDefault) {
        Bundle bundle = request.bundle;
        if (bundle.containsKey("value")) {
            if (value != bundle.getShort("value")) {
                throw new CheckFailException();
            }
        } else {
            if (value != null) {
                throw new CheckFailException();
            }
        }
        if (bundle.containsKey("valueDefault")) {
            if (valueDefault != bundle.getShort("valueDefault")) {
                throw new CheckFailException();
            }
        } else {
            if (valueDefault != 8) {
                throw new CheckFailException();
            }
        }
    }

    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER_INT
    )
    public static void testParameterInt(@NonNull RouterRequest request,
                                          @ParameterAnno("value") int value,
                                          @ParameterAnno("valueDefault") @IntDefaultAnno(8) int valueDefault) {
        Bundle bundle = request.bundle;
        if (bundle.containsKey("value")) {
            if (value != bundle.getInt("value")) {
                throw new CheckFailException();
            }
        } else {
            if (value != 0) {
                throw new CheckFailException();
            }
        }
        if (bundle.containsKey("valueDefault")) {
            if (valueDefault != bundle.getInt("valueDefault")) {
                throw new CheckFailException();
            }
        } else {
            if (valueDefault != 8) {
                throw new CheckFailException();
            }
        }
    }


    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER_INT_BOX
    )
    public static void testParameterIntBox(@NonNull RouterRequest request,
                                             @ParameterAnno("value") Integer value,
                                             @ParameterAnno("valueDefault") @IntDefaultAnno(8) Integer valueDefault) {
        Bundle bundle = request.bundle;
        if (bundle.containsKey("value")) {
            if (value != bundle.getInt("value")) {
                throw new CheckFailException();
            }
        } else {
            if (value != null) {
                throw new CheckFailException();
            }
        }
        if (bundle.containsKey("valueDefault")) {
            if (valueDefault != bundle.getInt("valueDefault")) {
                throw new CheckFailException();
            }
        } else {
            if (valueDefault != 8) {
                throw new CheckFailException();
            }
        }
    }
    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER_LONG
    )
    public static void testParameterLong(@NonNull RouterRequest request,
                                        @ParameterAnno("value") long value,
                                        @ParameterAnno("valueDefault") @LongDefaultAnno(8) long valueDefault) {
        Bundle bundle = request.bundle;
        if (bundle.containsKey("value")) {
            if (value != bundle.getLong("value")) {
                throw new CheckFailException();
            }
        } else {
            if (value != 0) {
                throw new CheckFailException();
            }
        }
        if (bundle.containsKey("valueDefault")) {
            if (valueDefault != bundle.getLong("valueDefault")) {
                throw new CheckFailException();
            }
        } else {
            if (valueDefault != 8) {
                throw new CheckFailException();
            }
        }
    }


    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER_LONG_BOX
    )
    public static void testParameterLongBox(@NonNull RouterRequest request,
                                           @ParameterAnno("value") Long value,
                                           @ParameterAnno("valueDefault") @LongDefaultAnno(8) Long valueDefault) {
        Bundle bundle = request.bundle;
        if (bundle.containsKey("value")) {
            if (value != bundle.getLong("value")) {
                throw new CheckFailException();
            }
        } else {
            if (value != null) {
                throw new CheckFailException();
            }
        }
        if (bundle.containsKey("valueDefault")) {
            if (valueDefault != bundle.getLong("valueDefault")) {
                throw new CheckFailException();
            }
        } else {
            if (valueDefault != 8) {
                throw new CheckFailException();
            }
        }
    }

    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER_FLOAT
    )
    public static void testParameterFloat(@NonNull RouterRequest request,
                                         @ParameterAnno("value") float value,
                                         @ParameterAnno("valueDefault") @FloatDefaultAnno(8) float valueDefault) {
        Bundle bundle = request.bundle;
        if (bundle.containsKey("value")) {
            if (value != bundle.getFloat("value")) {
                throw new CheckFailException();
            }
        } else {
            if (value != 0) {
                throw new CheckFailException();
            }
        }
        if (bundle.containsKey("valueDefault")) {
            if (valueDefault != bundle.getFloat("valueDefault")) {
                throw new CheckFailException();
            }
        } else {
            if (valueDefault != 8) {
                throw new CheckFailException();
            }
        }
    }


    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER_FLOAT_BOX
    )
    public static void testParameterFloatBox(@NonNull RouterRequest request,
                                            @ParameterAnno("value") Float value,
                                            @ParameterAnno("valueDefault") @FloatDefaultAnno(8) Float valueDefault) {
        Bundle bundle = request.bundle;
        if (bundle.containsKey("value")) {
            if (value != bundle.getFloat("value")) {
                throw new CheckFailException();
            }
        } else {
            if (value != null) {
                throw new CheckFailException();
            }
        }
        if (bundle.containsKey("valueDefault")) {
            if (valueDefault != bundle.getFloat("valueDefault")) {
                throw new CheckFailException();
            }
        } else {
            if (valueDefault != 8) {
                throw new CheckFailException();
            }
        }
    }

    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER_DOUBLE
    )
    public static void testParameterDouble(@NonNull RouterRequest request,
                                          @ParameterAnno("value") double value,
                                          @ParameterAnno("valueDefault") @DoubleDefaultAnno(8) double valueDefault) {
        Bundle bundle = request.bundle;
        if (bundle.containsKey("value")) {
            if (value != bundle.getDouble("value")) {
                throw new CheckFailException();
            }
        } else {
            if (value != 0) {
                throw new CheckFailException();
            }
        }
        if (bundle.containsKey("valueDefault")) {
            if (valueDefault != bundle.getDouble("valueDefault")) {
                throw new CheckFailException();
            }
        } else {
            if (valueDefault != 8) {
                throw new CheckFailException();
            }
        }
    }


    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER_DOUBLE_BOX
    )
    public static void testParameterDoubleBox(@NonNull RouterRequest request,
                                             @ParameterAnno("value") Double value,
                                             @ParameterAnno("valueDefault") @DoubleDefaultAnno(8) Double valueDefault) {
        Bundle bundle = request.bundle;
        if (bundle.containsKey("value")) {
            if (value != bundle.getDouble("value")) {
                throw new CheckFailException();
            }
        } else {
            if (value != null) {
                throw new CheckFailException();
            }
        }
        if (bundle.containsKey("valueDefault")) {
            if (valueDefault != bundle.getDouble("valueDefault")) {
                throw new CheckFailException();
            }
        } else {
            if (valueDefault != 8) {
                throw new CheckFailException();
            }
        }
    }

    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER_BOOLEAN
    )
    public static void testParameterBoolean(@NonNull RouterRequest request,
                                           @ParameterAnno("value") boolean value,
                                           @ParameterAnno("valueDefault") @BooleanDefaultAnno(true) boolean valueDefault) {
        Bundle bundle = request.bundle;
        if (bundle.containsKey("value")) {
            if (value != bundle.getBoolean("value")) {
                throw new CheckFailException();
            }
        } else {
            if (value != false) {
                throw new CheckFailException();
            }
        }
        if (bundle.containsKey("valueDefault")) {
            if (valueDefault != bundle.getBoolean("valueDefault")) {
                throw new CheckFailException();
            }
        } else {
            if (valueDefault != true) {
                throw new CheckFailException();
            }
        }
    }


    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER_BOOLEAN_BOX
    )
    public static void testParameterBooleanBox(@NonNull RouterRequest request,
                                              @ParameterAnno("value") Boolean value,
                                              @ParameterAnno("valueDefault") @BooleanDefaultAnno(true) Boolean valueDefault) {
        Bundle bundle = request.bundle;
        if (bundle.containsKey("value")) {
            if (value != bundle.getBoolean("value")) {
                throw new CheckFailException();
            }
        } else {
            if (value != null) {
                throw new CheckFailException();
            }
        }
        if (bundle.containsKey("valueDefault")) {
            if (valueDefault != bundle.getBoolean("valueDefault")) {
                throw new CheckFailException();
            }
        } else {
            if (valueDefault != true) {
                throw new CheckFailException();
            }
        }
    }

    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER_STRING
    )
    public static void testParameterString(@NonNull RouterRequest request,
                                               @ParameterAnno("value") String value,
                                               @ParameterAnno("valueDefault") @StringDefaultAnno("hello") String valueDefault) {
        Bundle bundle = request.bundle;
        if (bundle.containsKey("value")) {
            if (value == null) {
                if (bundle.getString("value") != null) {
                    throw new CheckFailException();
                }
            }else {
                if (!value.equals(bundle.getString("value"))) {
                    throw new CheckFailException();
                }
            }
        } else {
            if (value != null) {
                throw new CheckFailException();
            }
        }
        if (bundle.containsKey("valueDefault")) {
            if (valueDefault == null) {
                if (bundle.getString("valueDefault") != null) {
                    throw new CheckFailException();
                }
            }else {
                if (!valueDefault.equals(bundle.getString("valueDefault"))) {
                    throw new CheckFailException();
                }
            }
        } else {
            if (!"hello".equals(valueDefault)) {
                throw new CheckFailException();
            }
        }
    }



    @RouterAnno(
            host = ModuleConfig.System.NAME,
            path = ModuleConfig.System.TEST_PARAMETER
    )
    public static void testParameter(@NonNull RouterRequest request,
                                     @ParameterAnno("test1") byte test1,
                                     @ParameterAnno("test2") Byte test2,
                                     @ParameterAnno("test3") short test3,
                                     @ParameterAnno("test4") Short test4,
                                     @ParameterAnno("test5") int test5,
                                     @ParameterAnno("test6") Integer test6,
                                     @ParameterAnno("test7") long test7,
                                     @ParameterAnno("test8") Long test8,
                                     @ParameterAnno("test9") float test9,
                                     @ParameterAnno("test10") Float test10,
                                     @ParameterAnno("test11") double test11,
                                     @ParameterAnno("test12") Double test12,
                                     @ParameterAnno("test13") boolean test13,
                                     @ParameterAnno("test14") Boolean test14,
                                     @ParameterAnno("test15") User test15,
                                     @ParameterAnno("name") String name,
                                     @ParameterAnno("pass") String pass,
                                     @ParameterAnno("age") Integer age) {

    }


}
