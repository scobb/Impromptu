package com.example.steve.impromptu.Main.AsyncTasks;

import android.os.AsyncTask;

import com.example.steve.impromptu.Entity.ImpromptuUser;

import java.util.List;

/**
 * Created by Steve on 12/6/2014.
 */
public class AsyncTaskPopulateFriends extends AsyncTask<ImpromptuUser, Integer, Void> {
    @Override
    protected Void doInBackground(ImpromptuUser... users) {

        // grab user to populate
        ImpromptuUser user = users[0];

        // get their friends' pictures.
        List<ImpromptuUser> friends = user.getList("friends");
        for (ImpromptuUser friend: friends) {
            friend.getPicture();
            friend.getName();
        }

        return null;
    }
}
