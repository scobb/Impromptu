package com.example.steve.impromptu.Entity;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * Created by jonreynolds on 10/26/14.
 */

@ParseClassName("Group")
public class Group extends ParseObject implements Comparable<Group> {

    private boolean selected = false;
    private String friendsInGroupKey = "friendsInGroup";
    private String nameKey = "groupName";

    Boolean isSelected = false;

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

    public String getGroupName() {
        try {
            this.fetchIfNeeded();
        } catch (ParseException exc) {
            Log.e("Impromptu", "Error fetching Group:", exc);
        }
        return getString(nameKey);
    }

    @Override
    public int compareTo(Group other) {
        return this.getGroupName().compareTo(other.getGroupName());
    }

    public List<ImpromptuUser> getFriendsInGroup() {
        /**
         * method - returns list of friends in group
         */
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Group:", exc);
        }
        List<ImpromptuUser> userList = this.getList(friendsInGroupKey);
        return userList;
    }

    public void setFriendsInGroup(List<ImpromptuUser> list) {
        /**
         * method - sets friendsInGroup to arrayList list
         */
        this.put(friendsInGroupKey, list);
    }


    public void add(ImpromptuUser friend) {
        /**
         * method - adds user if they are not already in group
         */

        List<ImpromptuUser> friends = this.getFriendsInGroup();
        if (!friends.contains(friend)) {
            friends.add(friend);
            Collections.sort(friends);
            setFriendsInGroup(friends);
        } else {
            Log.d("Impromptu", "friend was already in");
        }
    }

    public void remove(ImpromptuUser friend) {
        /**
         * method - removes user if they are in group
         */
        List<ImpromptuUser> friends = getFriendsInGroup();
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
        this.setFriendsInGroup(new ArrayList<ImpromptuUser>());
    }

    public int getSize() {
        /**
         * method - returns # of members in group
         */

        return this.getFriendsInGroup().size();

    }

    public Boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(Boolean selected) {
        this.isSelected = selected;
    }

}
