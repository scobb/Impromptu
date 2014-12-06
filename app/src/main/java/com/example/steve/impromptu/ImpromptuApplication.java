package com.example.steve.impromptu;

import android.app.Application;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.FriendRequest;
import com.example.steve.impromptu.Entity.Group;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.ActivityMain;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

/**
 * Created by stephen on 11/28/14.
 */
public class ImpromptuApplication extends android.app.Application {

    @Override
    public void onCreate(){
        super.onCreate();

        // Initialize the Parse SDK
        ParseUser.registerSubclass(ImpromptuUser.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(FriendRequest.class);
        Parse.initialize(this, "sP5YdlJxg1WiwfKgSXX4KdrgpZzAV5g69dV8ryY0", "houV8Brg8oIuBKSLheR7qAW4AJfGq1QZmH62Spgk");


        // Specify an Activity to handle all pushes by default.
        PushService.setDefaultPushCallback(this, ActivityMain.class);
    }

}
