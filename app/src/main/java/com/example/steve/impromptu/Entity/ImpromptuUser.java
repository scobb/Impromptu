package com.example.steve.impromptu.Entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    private static String nameKey = "name";
    private static String facebookIdKey = "fbid";

    public ImpromptuUser() {
        super();
    }

    public ImpromptuUser(String username) {
        super();
        this.setUsername(username);
    }

    @Override
    public int compareTo(ImpromptuUser other) {
        return this.getObjectId().compareTo(other.getObjectId());
    }

    public ImpromptuUser(String username, String pw, String email) {
        // use parse's interface to set basic info
        super();
        this.setUsername(username);
        this.setName(username);
        this.setPassword(pw);
        this.setEmail(email);
        this.clearGroups();
        this.clearFriends();
        this.clearStreamEvents();
    }

    public void clearStreamEvents() {
        setStreamEvents(new ArrayList<Event>());
    }

    public void setStreamEvents(List<Event> events) {
        this.put(visibleEventsKey, events);
    }

    /**
     *
     * @return - a list of events owned by this user.
     */
    public List<Event> getOwnedEvents() {
        ParseQuery<Event> q = new ParseQuery<>(Event.class);
        q.whereEqualTo("owner", this);
        try {
            return q.find();
        } catch (ParseException e) {
            Log.e("Impromptu", "Exception getting owned events: ", e);
        }
        return new ArrayList<>();
    }

    /**
     * generates and persists a friend request from this user to friend
     * @param friend - friend to create a request to
     * @return - true if request successfully created, false otherwise.
     */
    public boolean createFriendRequest(ImpromptuUser friend) {
        ParseQuery<FriendRequest> q = new ParseQuery<>(FriendRequest.class);
        q.whereEqualTo("from", this);
        q.whereEqualTo("to", friend);
        try {
            List<FriendRequest> result = q.find();
            if (result.size() > 0) {
                Log.d("Impromptu", "Friend request already exists.");
                return false;
            } else if (getFriends().contains(friend)) {
                Log.d("Impromptu", "User already in friends list.");
                return false;
            } else {
                FriendRequest fr = new FriendRequest();
                fr.setFrom(this);
                fr.setTo(friend);
                fr.persist();
                Log.d("Impromptu", "Adding friend request.");
                return true;
            }
        } catch (ParseException e ) {
            Log.e("Impromptu", "Error finding friend request:", e);
            return false;
        }
    }

    public List<Event> getStreamEvents() {
        try {
            this.fetchIfNeeded();
            this.refresh();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching User:", exc);
        }
        List<Event> events = this.getList(visibleEventsKey);
//        verifyEvents(events);
        Log.d("Impromptu", "List Size: " + events.size());

        return events;
    }

    public void destroyFriendship(ImpromptuUser friend) {
        HashMap<String, String> params = new HashMap<>();
        params.put("fromId", this.getObjectId());
        params.put("toId", friend.getObjectId());

        ParseCloud.callFunctionInBackground("destroyFriendship", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
                if (e != null) {
                    Log.e("Impromptu", "Exception destroying friendship: ", e);
                }
            }
        });
    }

    /**
     * NOTE - only verifies existence, not that we're within timing
     *
     * @param events - list of events to confirm existence on DB
     */
    public void verifyEvents(List<Event> events) {
        Iterator<Event> it = events.iterator();
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

    public static List<ImpromptuUser> getUserByName(String name) {
        /**
         * method - gets existing user given their id
         * Returns null if ID not found
         */
        ParseQuery<ImpromptuUser> query = ParseQuery.getQuery("_User");
        query.whereContains(ImpromptuUser.nameKey, name);
        try {
            Log.d("Impromptu", "Trying to get user " + name);
            return query.find();
        } catch (Exception exc) {
            Log.e("Impromptu", "Exception querying...", exc);
            return null;
        }
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
                    String email = respJSON.getString("email");
                    setEmail(email);
                    return email;
                } catch (Exception exc) {
                    Log.e("Impromptu", "json exception: ", exc);
                    return null;
                }

            }

        } else return super.getEmail();
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
            this.refresh();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching User:", exc);
        }
        List<ImpromptuUser> friends = this.getList(friendsKey);
        this.verifyFriends(friends);
        Collections.sort(friends);
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
            this.refresh();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching User:", exc);
        }
        List<Group> groups = getList(groupKey);
        verifyGroups(groups);
        Collections.sort(groups);
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
        this.put(this.nameKey, name);
    }


    public String getName() {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching User:", exc);
        }
        String name = this.getString(this.nameKey);
        if (name != null) {
            return name;
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
                    this.setName(respJSON.getString("name"));
                    this.persist();
                    return respJSON.getString("name");
                } catch (Exception exc) {
                    Log.e("Impromptu", "json excpetion: ", exc);
                }

            }
            return "";
        } else {
            this.setName(this.getUsername());
            this.persist();
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
        if (picFile != null) {
            try {
                byte[] picBlock = picFile.getData();
                Log.d("Impromptu", "picBlock: " + picBlock);
                return BitmapFactory.decodeByteArray(picBlock, 0, picBlock.length);
            } catch (ParseException exc) {
                Log.e("Impromptu", "exception in getPicture", exc);

            }
        } else if (getFacebookId() != null) {
            Log.d("Impromptu", "Getting fb pic");
            try {
                URL imageURL = new URL("https://graph.facebook.com/" + getFacebookId() + "/picture?type=large");
                Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                if (bitmap == null) {
                    Log.e("Impromptu", "bitmap is null");
                }
                return bitmap;
            } catch (Exception exc) {
                Log.e("Impromptu", "malformed exception", exc);
            }
        }
        Log.e("Impromptu", "didn't get in either block.");
        return null;
    }

    // Returns the current user key
    public String getUserKey() {
        return this.get("objectId").toString();
    }

    public Boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(Boolean selected) {
        this.isSelected = selected;
    }

    public void setFacebookId(String id) {
        this.put(facebookIdKey, id);
    }

    public String getFacebookId() {
        return this.getString(facebookIdKey);
    }

    public static ImpromptuUser getUserByFacebookId(String fbid) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery("_User");
        try {
            Log.d("Impromptu", "Trying to get user for id " + fbid);
            query.whereEqualTo(ImpromptuUser.facebookIdKey, fbid);
            List<ParseUser> users = query.find();
            if (!users.isEmpty()) {
                return (ImpromptuUser) users.get(0);
            } else {
                Log.d("Impromptu", "Users was empty.");
            }
        } catch (Exception exc) {
            Log.e("Impromptu", "Exception querying...", exc);
            return null;
        }
        return null;

    }

    public ArrayList<ImpromptuUser> getFacebookFriends() {
        ArrayList<ImpromptuUser> result = new ArrayList<>();
        if (getFacebookId() != null) {
            Request friendReq = Request.newMyFriendsRequest(ParseFacebookUtils.getSession(), null);
            List<Response> responses = null;
            try {
                responses = friendReq.executeAsync().get();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error getting async result", exc);
            }
            if (responses == null) {
                Log.d("Impromptu", "reponses was null");
                return result;
            }
            for (Response resp : responses) {
                Log.d("Impromptu", "Response: " + resp);

                try {
                    JSONObject respJSON = new JSONObject(resp.getRawResponse());
                    JSONArray data = respJSON.getJSONArray("data");
                    Log.d("Impromptu", "Number of fb friends also using impromptu: " + data.length());
                    for (int i = 0; i < data.length(); i++) {
                        String id = data.getJSONObject(i).getString("id");
                        ImpromptuUser user = ImpromptuUser.getUserByFacebookId(id);
                        if (user != null) {
                            result.add(user);
                        }
                    }

                } catch (Exception exc) {
                    Log.e("Impromptu", "json excpetion: ", exc);
                    return result;
                }

            }
        }
        return result;
    }

    @Override
    public boolean equals(Object object) {
        boolean same = false;

        if (object != null && object instanceof ImpromptuUser)
        {
            same = (this.getObjectId().equals(((ImpromptuUser) object).getObjectId()));
        }

        return same;
    }

    @Override
    public int hashCode() {
        int hc = (this.getObjectId()).hashCode();
        return hc;
    }

}
