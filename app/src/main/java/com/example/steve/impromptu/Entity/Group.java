package com.example.steve.impromptu.Entity;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonreynolds on 10/26/14.
 */

@ParseClassName("Group")
public class Group extends ParseObject {

    private boolean selected = false;
    private String friendsInGroupKey = "friendsInGroup";

    public Group() {
        super();
        this.clear();
    }

    public void persist() {
        this.saveInBackground();
    }
    public List<ImpromptuUser> getFriendsInGroup() {
        /**
         * method - returns list of friends in group
         */
        return this.getList(friendsInGroupKey);
    }

    public void setFriendsInGroup(ArrayList<ImpromptuUser> list) {
        /**
         * method - sets friendsInGroup to arrayList list
         */
        this.put(friendsInGroupKey, list);
    }


    public void add(ImpromptuUser friend) {
        /**
         * method - adds user if they are not already in group
         */

        ArrayList<ImpromptuUser> friends = (ArrayList<ImpromptuUser>) this.getFriendsInGroup();
        if (!friends.contains(friend)) {
            friends.add(friend);
        } else {
            Log.d("Impromptu", "friend was already in");
        }
    }

    public void remove(ImpromptuUser friend) {
        /**
         * method - removes user if they are in group
         */
        ArrayList<ImpromptuUser> friends = (ArrayList<ImpromptuUser>) this.getFriendsInGroup();
        if (friends.contains(friend)) {
            friends.remove(friend);
        } else {
            Log.d("Impromptu", "friend was not in");
        }
    }

    public void clear() {
        /**
         * method - clears users from group
         */
        this.setFriendsInGroup(new ArrayList<ImpromptuUser>());
    }

    public int getSize() {
        /**
         * method - returns # of members in group
         */

        return this.getFriendsInGroup().size();

    }

}
