package com.example.steve.impromptu.Entity;

import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseClassName;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.HashMap;
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
        HashMap<String, String> args = new HashMap<>();
        args.put("fromId", from.getObjectId());
        args.put("toId", to.getObjectId());

        ParseCloud.callFunctionInBackground("acceptFriendRequest", args, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                if (e != null) {
                    Log.e("Impromptu", "error executing cloud code", e);
                }
            }
        });


        deleteEventually();


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
