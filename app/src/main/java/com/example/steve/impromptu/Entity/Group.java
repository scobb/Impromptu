package com.example.steve.impromptu.Entity;

import java.util.ArrayList;

/**
 * Created by jonreynolds on 10/26/14.
 */

public class Group {

    private ArrayList<Friend> friendsInGroup;
    private int size = 0;
    private boolean selected = false;

    public Group() {
        this.friendsInGroup = new ArrayList<Friend>();
    }

    public boolean add(Friend friend) {

        if (this.friendsInGroup.add(friend)) {
            this.size ++;
            return true;
        }
        else {
            return false;
        }
    }

    public boolean remove(Friend friend) {

        if (this.friendsInGroup.remove(friend)) {
            this.size --;
            return true;
        }
        else {
            return false;
        }

    }

    public void clear() {

        this.friendsInGroup.clear();

    }

    public int getSize() {

        return this.size;

    }

    public ArrayList<Friend> getFriends() {

        return this.friendsInGroup;

    }

}
