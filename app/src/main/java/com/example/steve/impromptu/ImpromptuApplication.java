package com.example.steve.impromptu;

import android.app.Application;

import com.example.steve.impromptu.Main.ActivityMain;
import com.parse.Parse;
import com.parse.PushService;

/**
 * Created by stephen on 11/28/14.
 */
public class ImpromptuApplication extends android.app.Application {

    @Override
    public void onCreate(){
        super.onCreate();

        // Initialize the Parse SDK.
        Parse.initialize(this, "sP5YdlJxg1WiwfKgSXX4KdrgpZzAV5g69dV8ryY0", "houV8Brg8oIuBKSLheR7qAW4AJfGq1QZmH62Spgk");

        // Specify an Activity to handle all pushes by default.
        PushService.setDefaultPushCallback(this, ActivityMain.class);
    }

}
