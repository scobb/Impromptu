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

    private List<ImpromptuUser> friendsInGroup = null;
    private String name = null;

    Boolean isSelected = false;

    public Group() {
        super();
    }

    public Group(String name) {
        super();
        setGroupName(name);
        clear();
    }

    public void populate() {
        getGroupName();
        getFriendsInGroup();
    }


    public void persist() {
        this.saveInBackground();
    }

    public void setGroupName(String name) {
        this.name = name;
        put(nameKey, name);
    }

    public String getGroupName() {
        if (this.name == null) {
            try {
                this.fetchIfNeeded();
            } catch (ParseException exc) {
                Log.e("Impromptu", "Error fetching Group:", exc);
            }
            this.name = getString(nameKey);
        }
        return this.name;
    }

    @Override
    public int compareTo(Group other) {
        return this.getObjectId().compareTo(other.getObjectId());
    }

    public List<ImpromptuUser> getFriendsInGroup() {
        /**
         * method - returns list of friends in group
         */
        if (friendsInGroup == null) {
            try {
                this.fetchIfNeeded();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Group:", exc);
            }
            friendsInGroup = this.getList(friendsInGroupKey);
        }
        return friendsInGroup;
    }

    public void setFriendsInGroup(List<ImpromptuUser> list) {
        /**
         * method - sets friendsInGroup to arrayList list
         */
        this.friendsInGroup = list;
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

    @Override
    public boolean equals(Object object) {
        boolean same = false;

        if (object != null && object instanceof Group)
        {
            same = this.getObjectId().equals(((Group) object).getObjectId());
        }

        return same;
    }

    @Override
    public int hashCode() {
        int hc = (this.getObjectId()).hashCode();
        return hc;
    }

}
