package com.ehi.component1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ehi.component.impl.EHiRouter;

public class Component1Fragment extends Fragment {

    private Button bt_go_component2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.component1_fragment, null);

        bt_go_component2 = contentView.findViewById(R.id.bt_go_component2);

        bt_go_component2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EHiRouter.with(getContext())
                        .host("component2")
                        .path("component2")
                        .navigate();

            }
        });

        return contentView;
    }

}
