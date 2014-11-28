package com.example.steve.impromptu;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.steve.impromptu.Main.ActivityMain;
import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by stephen on 11/28/14.
 */
public class ImpromptuReceiver extends ParsePushBroadcastReceiver {

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Log.e("Push", "Clicked");
        Intent i = new Intent(context, ActivityMain.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}
