package com.example.steve.impromptu.Main.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.steve.impromptu.Entity.ImpromptuUser;

import java.util.Collections;
import java.util.List;

/**
 * Created by Steve on 12/6/2014.
 */
public class AsyncTaskPopulateFriends extends AsyncTask<ImpromptuUser, Integer, Void> {
    @Override
    protected Void doInBackground(ImpromptuUser... users) {

        // grab user to populate
        ImpromptuUser user = users[0];

        // preload the picture, save it for use with profile page
        user.getPicture();
        user.friendMap.put(user.getObjectId(), user);

        List<ImpromptuUser> friends = user.getList("friends");
        user.verifyFriends(friends);
        Collections.sort(friends);
        user.friends = friends;

        // get their friends' pictures.
        for (ImpromptuUser friend: user.friends) {
            friend.getPicture();
            friend.getName();
            user.friendMap.put(friend.getObjectId(), friend);
        }

        return null;
    }
}
