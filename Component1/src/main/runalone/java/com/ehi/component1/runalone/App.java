package com.ehi.component1.runalone;

import android.app.Application;

/**
 * time   : 2019/01/21
 *
 * @author : xiaojinzi 30212
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("hello world");
    }

}
