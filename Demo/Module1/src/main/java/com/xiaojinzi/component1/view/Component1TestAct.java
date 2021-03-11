package com.xiaojinzi.component1.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xiaojinzi.base.ModuleConfig;
import com.xiaojinzi.base.view.BaseAct;
import com.xiaojinzi.component.anno.RouterAnno;
import com.xiaojinzi.component.impl.RouterRequest;
import com.xiaojinzi.component.support.ParameterSupport;
import com.xiaojinzi.component1.R;

/**
 * 这个界面用于显示传递过来的 Data 数据,并且返回一个Result
 */
@RouterAnno(
        path = ModuleConfig.Module1.TEST,
        desc = "业务组件1的测试界面"
)
public class Component1TestAct extends BaseAct {

    @RouterAnno(
            path = ModuleConfig.Module1.TEST_AUTORETURN,
            desc = "业务组件1的测试界面"
    )
    public static Intent start1(RouterRequest request) {
        Intent intent = new Intent(request.getRawContext(), Component1TestAct.class);
        intent.putExtra("isReturnAuto", true);
        return intent;
    }

    @RouterAnno(
            path = ModuleConfig.Module1.TEST_AUTORETURN1,
            desc = "业务组件1的测试界面"
    )
    public static Intent start2(RouterRequest request) {
        Intent intent = new Intent(request.getRawContext(), Component1TestAct.class);
        intent.putExtra("isReturnAuto", true);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component1_test_act);

        TextView tv_data = findViewById(R.id.tv_data);
        tv_data.setText(ParameterSupport.getString(getIntent(), "data"));

    }

    public void returnData(View view) {
        returnData();
    }

    protected void returnData() {
        Intent intent = new Intent();
        intent.putExtra("data", "this is the return data，requestData is " + ParameterSupport.getString(getIntent(), "data"));
        if (!isReturnIntent()) {
            intent = null;
        }
        if (isReturnError()) {
            setResult(RESULT_ERROR, intent);
        }else {
            setResult(RESULT_OK, intent);
        }
        finish();
    }

}
