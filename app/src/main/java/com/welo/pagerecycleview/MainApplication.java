package com.welo.pagerecycleview;

import com.anupcowkur.reservoir.Reservoir;

import android.app.Application;

import java.io.IOException;

/**
 * Created by Amy on 2019-12-30
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Reservoir.init(this, 1000000000);
        } catch (IOException ignored) {
        }
    }
}
