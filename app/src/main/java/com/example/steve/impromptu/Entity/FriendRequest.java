package com.example.steve.impromptu.Entity;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steve on 11/15/2014.
 */
@ParseClassName("FriendRequest")
public class FriendRequest extends ParseObject {
    private static String pendingKey = "pending";
    private static String fromKey = "from";
    private static String toKey = "to";

    public FriendRequest() {
        super();
    }

    public void persist() {
        saveInBackground();
    }

    public void accept() {
        setPending(false);
        ImpromptuUser from = getFrom();
        ImpromptuUser to = getTo();

        from.addFriend(to);
        to.addFriend(from);

        from.persist();
        to.persist();
        persist();

        //TODO - add a message to both that they have a new friend?

    }

    public static List<FriendRequest> getPendingRequestFromUser(ImpromptuUser user) {
        ParseQuery<FriendRequest> query = ParseQuery.getQuery("FriendRequest");
        try {
            Log.d("Impromptu", "Trying to get pending requests for id " + user.getName());
            query.whereEqualTo(FriendRequest.fromKey, user);
            query.whereEqualTo(FriendRequest.pendingKey, true);
            return query.find();
        }
        catch (Exception exc) {
            Log.e("Impromptu", "Error getting from requests", exc);
        }
        return null;
    }

    public static List<FriendRequest> getPendingRequestToUser(ImpromptuUser user) {
        ParseQuery<FriendRequest> query = ParseQuery.getQuery("FriendRequest");
        try {
            Log.d("Impromptu", "Trying to get pending requests for id " + user.getName());
            query.whereEqualTo(FriendRequest.toKey, user);
            query.whereEqualTo(FriendRequest.pendingKey, true);
            return query.find();
        }
        catch (Exception exc) {
            Log.e("Impromptu", "Error getting from requests", exc);
        }
        return null;
    }

    public void decline() {
        setPending(false);
        persist();
    }

    public void setPending(boolean b) {
        this.put(pendingKey, b);
    }
    public void setFrom(ImpromptuUser user) {
        this.put(fromKey, user);
    }
    public void setTo(ImpromptuUser user) {
        this.put(toKey, user);
    }

    public boolean getPending() {
        try {
            fetchIfNeeded();
        }
        catch (Exception exc) {
            Log.e("Impromptu", "Fetch exception: ", exc);
        }
        return getBoolean(pendingKey);
    }

    public ImpromptuUser getTo() {
        try {
            fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Fetch exception: ", exc);
        }
        return (ImpromptuUser) get(toKey);
    }

    public ImpromptuUser getFrom() {
        try {
            fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Fetch exception: ", exc);
        }
        return (ImpromptuUser) get(fromKey);
    }


}
