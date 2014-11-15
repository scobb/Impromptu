package com.example.steve.impromptu.Entity;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
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
    private static String fromKey = "from";
    private static String toKey = "to";

    public FriendRequest() {
        super();
    }

    public void persist() {
        saveInBackground();
    }

    public void accept() {
        // TODO - this needs to be handled by cloud code. Current persist will not work on users not logged in.
        ImpromptuUser from = getFrom();
        ImpromptuUser to = getTo();
        try {
            from.fetchIfNeeded();
            to.fetchIfNeeded();
            from.addFriend(to);
            to.addFriend(from);

            from.persist();
            to.persist();
            persist();

            //TODO - add a message to both that they have a new friend? Delete?

        }
        catch (ParseException exc) {
            // either from or to no longer exists. we'll delete the request.
            Log.e("Impromptu", "Error accepting.", exc);
        }
        finally {
            deleteEventually();
        }

    }

    public static List<FriendRequest> getPendingRequestFromUser(ImpromptuUser user) {
        ParseQuery<FriendRequest> query = ParseQuery.getQuery("FriendRequest");
        try {
            Log.d("Impromptu", "Trying to get pending requests for " + user.getName());
            query.whereEqualTo(FriendRequest.fromKey, user);
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
            return query.find();
        }
        catch (Exception exc) {
            Log.e("Impromptu", "Error getting from requests", exc);
        }
        return null;
    }

    public static FriendRequest getFriendRequest(ImpromptuUser from, ImpromptuUser to) {
        ParseQuery<FriendRequest> query = ParseQuery.getQuery("FriendRequest");
        try {
            Log.d("Impromptu", "Trying to get pending requests from " + from.getName() + " to " + to.getName());
            query.whereEqualTo(FriendRequest.fromKey, from);
            query.whereEqualTo(FriendRequest.toKey, to);
            List<FriendRequest> result = query.find();
            if (result.isEmpty()){
                return null;
            }
            return result.get(0);
        }
        catch (Exception exc) {
            Log.e("Impromptu", "Error getting from requests", exc);
        }
        return null;

    }

    public void decline() {
        deleteEventually();
    }

    public void setFrom(ImpromptuUser user) {
        this.put(fromKey, createWithoutData(ImpromptuUser.class, user.getObjectId()));
    }
    public void setTo(ImpromptuUser user) {
        this.put(toKey, createWithoutData(ImpromptuUser.class, user.getObjectId()));
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
