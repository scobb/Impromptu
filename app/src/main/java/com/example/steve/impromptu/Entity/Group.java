package com.example.steve.impromptu.Entity;

import com.parse.ParseException;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by jonreynolds on 10/26/14.
 */

@ParseClassName("Group")
public class Group extends ParseObject {

    private boolean selected = false;
    private String friendsInGroupKey = "friendsInGroup";
    private String nameKey = "groupName";

    public Group() {
        super();
    }

    public Group(String name) {
        super();
        setGroupName(name);
        clear();
    }


    public void persist() {
        this.saveInBackground();
    }

    public void setGroupName(String name) {
        put(nameKey, name);
    }
    public String getGroupName()  {
        try {
            this.fetchIfNeeded();
        }
        catch (ParseException exc) {
            Log.e("Impromptu", "Error fetching Group:", exc);
        }
        return getString(nameKey);
    }

    public TreeSet<ImpromptuUser> getFriendsInGroup() {
        /**
         * method - returns list of friends in group
         */
        try {
            this.fetchIfNeeded();
        }
        catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Group:", exc);
        }
        List<ImpromptuUser> userList = this.getList(friendsInGroupKey);
        return new TreeSet<ImpromptuUser>(userList);
    }

    public void setFriendsInGroup(TreeSet<ImpromptuUser> tree) {
        /**
         * method - sets friendsInGroup to arrayList list
         */
        List<ImpromptuUser> list = new ArrayList<ImpromptuUser>(tree);
        this.put(friendsInGroupKey, list);
    }


    public void add(ImpromptuUser friend) {
        /**
         * method - adds user if they are not already in group
         */

        TreeSet<ImpromptuUser> friends = this.getFriendsInGroup();
        if (!friends.contains(friend)) {
            friends.add(friend);
            setFriendsInGroup(friends);
        } else {
            Log.d("Impromptu", "friend was already in");
        }
    }

    public void remove(ImpromptuUser friend) {
        /**
         * method - removes user if they are in group
         */
        TreeSet<ImpromptuUser> friends = getFriendsInGroup();
        if (friends.contains(friend)) {
            friends.remove(friend);
            setFriendsInGroup(friends);
        } else {
            Log.d("Impromptu", "friend was not in");
        }
    }

    public void clear() {
        /**
         * method - clears users from group
         */
        this.setFriendsInGroup(new TreeSet<ImpromptuUser>());
    }

    public int getSize() {
        /**
         * method - returns # of members in group
         */

        return this.getFriendsInGroup().size();

    }

}
