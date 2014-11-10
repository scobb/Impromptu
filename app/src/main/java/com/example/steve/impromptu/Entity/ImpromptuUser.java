package com.example.steve.impromptu.Entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * ImpromptuUser object
 * Created by scobb on 10/19/14.
 */
public class ImpromptuUser extends ParseUser implements Comparable<ImpromptuUser> {
    Boolean isSelected = false;
    private String groupKey = "groups";
    private String friendsKey = "friends";
    private String visibleEventsKey = "events";

    public ImpromptuUser() {
        super();
    }

    public ImpromptuUser(String username) {
        super();
        this.setName(username);
    }

    @Override
    public int compareTo(ImpromptuUser other) {
        return this.getName().compareTo(other.getName());
    }

    public ImpromptuUser(String username, String pw, String email) {
        // use parse's interface to set basic info
        super();
        this.setUsername(username);
        this.setPassword(pw);
        this.setEmail(email);
        this.clearGroups();
        this.clearFriends();
    }

    public static ImpromptuUser getUserById(String id) {
        /**
         * method - gets existing user given their id
         * Returns null if ID not found
         */
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        ParseUser user = null;
        try {
            Log.d("Impromptu", "Trying to get user for id " + id);
            user = query.get(id);
        } catch (Exception exc) {
            Log.e("Impromptu", "Exception querying...", exc);
            return null;
        }
        return (ImpromptuUser) user;
    }

    public String getEmail() {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching User:", exc);
        }
            if (ParseFacebookUtils.isLinked(this)) {
                // display facebook name
                Request meReq = Request.newMeRequest(ParseFacebookUtils.getSession(), null);
                List<Response> responses = null;
                try {
                    responses = meReq.executeAsync().get();
                } catch (Exception exc) {
                    Log.e("Impromptu", "Error getting async result", exc);
                }
                if (responses == null) {
                    Log.d("Impromptu", "reponses was null");
                    return null;
                }
                for (Response resp : responses) {
                    try {
                        JSONObject respJSON = new JSONObject(resp.getRawResponse());
                        return respJSON.getString("email");
                    } catch (Exception exc) {
                        Log.e("Impromptu", "json excpetion: ", exc);
                        return null;
                    }

                }

            }

        else return super.getEmail();
        return null;
    }

    public void persist() {
        this.saveInBackground();
    }

    public void clearFriends() {
        this.setFriends(new ArrayList<ImpromptuUser>());
    }

    public void addFriend(ImpromptuUser friend) {
        List<ImpromptuUser> friends = getFriends();
        if (!(friends.contains(friend))) {
            friends.add(friend);
            Collections.sort(friends);
        } else {
            Log.d("Impromptu", "friend was already in friends list");
        }
    }


    public void clearGroups() {
        this.setGroups(new ArrayList<Group>());
    }

    public void setGroups(ArrayList<Group> groupList) {
        this.put(groupKey, groupList);
    }

    public void setFriends(ArrayList<ImpromptuUser> friends) {
        Collections.sort(friends);
        this.put(friendsKey, friends);
    }

    public void removeFriend(ImpromptuUser friend) {
        List<ImpromptuUser> friends = getFriends();
        friends.remove(friend);
    }

    public void verifyFriends(List<ImpromptuUser> friends) {
        Iterator<ImpromptuUser> it = friends.iterator();
        boolean needPersist = false;
        while (it.hasNext()) {
            try {
                it.next().fetchIfNeeded();
            } catch (ParseException exc) {
                it.remove();
                needPersist = true;
            }
        }
        if (needPersist)
            this.persist();
    }

    public List<ImpromptuUser> getFriends() {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching User:", exc);
        }
        List<ImpromptuUser> friends = this.getList(friendsKey);
        this.verifyFriends(friends);
        return friends;
    }


    public void verifyGroups(List<Group> groups) {
        Iterator<Group> it = groups.iterator();
        boolean needPersist = false;
        while (it.hasNext()) {
            try {
                it.next().fetchIfNeeded();
            } catch (ParseException exc) {
                it.remove();
                needPersist = true;
            }
        }
        if (needPersist)
            this.persist();
    }


    public List<Group> getGroups() {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching User:", exc);
        }
        List<Group> groups = getList(groupKey);
        verifyGroups(groups);
        return groups;
    }

    public void addGroup(Group group) {
        ArrayList<Group> groups = (ArrayList<Group>) getGroups();
        if (!groups.contains(group)) {
            groups.add(group);
            Collections.sort(groups);
        } else {
            Log.d("Impromptu", group + " was already in groups.");
        }
    }

    public void deleteGroup(Group group) {
        ArrayList<Group> groups = (ArrayList<Group>) getGroups();
        groups.remove(group);
    }

    public Group getGroup(String groupName) {
        for (Group group : getGroups()) {
            if (group.getGroupName() != null && group.getGroupName().equals(groupName)) {
                return group;
            }
        }
        return null;
    }

    public void setName(String name) {
        this.setUsername(name);
    }


    public String getName() {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching User:", exc);
        }
        if (ParseFacebookUtils.isLinked(this)) {
            // display facebook name
            Request meReq = Request.newMeRequest(ParseFacebookUtils.getSession(), null);
            List<Response> responses = null;
            try {
                responses = meReq.executeAsync().get();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error getting async result", exc);
            }
            if (responses == null) {
                Log.d("Impromptu", "reponses was null");
                return "";
            }
            for (Response resp : responses) {
                try {
                    JSONObject respJSON = new JSONObject(resp.getRawResponse());
                    return respJSON.getString("name");
                } catch (Exception exc) {
                    Log.e("Impromptu", "json excpetion: ", exc);
                }

            }
            return "";
        } else {
            return this.getUsername();
        }
    }

    /**
     * method - setPicture - saves user's profile picture to parse. Note, filename must end in .png
     */
    public void setPicture(Bitmap pic) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();

        final ParseFile parseImage = new ParseFile(data);

        parseImage.saveInBackground();
        this.put("profilePic", parseImage);
        this.saveInBackground();
    }

    /**
     * method - getPicture - returns bitmap for profile picture, null if user doesn't have one.
     */
    public Bitmap getPicture() {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching User:", exc);
        }
        ParseFile picFile = (ParseFile) this.get("profilePic");
        if (picFile == null) {
            return null;
        }
        byte[] picBlock = null;
        try {
            picBlock = picFile.getData();
        } catch (ParseException exc) {
            Log.e("Impromptu", "exception in getPicture", exc);

        }
        return BitmapFactory.decodeByteArray(picBlock, 0, picBlock.length);
    }

    public Boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(Boolean selected) {
        this.isSelected = selected;
    }


}
