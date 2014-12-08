package com.example.steve.impromptu.Entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.example.steve.impromptu.Main.AsyncTasks.AsyncTaskPopulateEvents;
import com.example.steve.impromptu.Main.AsyncTasks.AsyncTaskPopulateFriends;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.google.android.gms.games.GamesMetadata;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RefreshCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
   // private String myEventsKey = "myEvents";
    private String visibleEventsKey = "events";
    private static String nameKey = "name";
    private static String facebookIdKey = "fbid";

    private List<Group> groups = null;
    private Bitmap picture = null;
    private String name = null;
    private List<FriendRequest> toRequests = null;
    private List<ImpromptuUser> facebookFriends = null;
    public List<ImpromptuUser> friends = null;
    public List<Event> streamEvents = new ArrayList<>();
    public HashMap<String, ImpromptuUser> friendMap = new HashMap<>();
    public HashMap<String, Event> eventMap = new HashMap<>();

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

    public List<FriendRequest> getToRequests() {
        if (toRequests != null) {
            toRequests = FriendRequest.getPendingRequestToUser(this);
        } else {
            populatePendingToRequestsInBackground();
        }
        return toRequests;
    }

    public void populateFriendsInBackground() {
        Log.d("Impromptu", "Populating friends in background.");
        new AsyncTaskPopulateFriends().execute(this);
    }


    public void populateGroupsInBackground() {
        List<Group> lGroups = getGroups();
        for (Group g : lGroups) {
            g.populate();
        }
        groups = lGroups;
    }

    public void populatePendingToRequestsInBackground() {
        ParseQuery<FriendRequest> query = ParseQuery.getQuery("FriendRequest");
        Log.d("Impromptu", "Trying to get pending requests for id " + this.getName());
        query.whereEqualTo(FriendRequest.toKey, this);
        final ImpromptuUser targ = this;
        query.findInBackground(new FindCallback<FriendRequest>() {
            @Override
            public void done(List<FriendRequest> friendRequests, ParseException e) {
                targ.toRequests = friendRequests;
            }
        });
    }

    public void clearStreamEvents() {
        setStreamEvents(new ArrayList<Event>());
    }

    public void setStreamEvents(List<Event> events) {
        streamEvents = events;
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
        long nowMillis = System.currentTimeMillis();
        Iterator<Event> i = events.iterator();
        while (i.hasNext()){
            Event event = i.next();
            HashMap<String, String> args = new HashMap<>();
            long endMillis = event.getEventTime().getTime() + event.getDurationHour() * 3600 * 1000 + event.getDurationMinute() * 60 * 1000;
            Log.d("Impromptu", "nowMillis: " + nowMillis + "\nendMillis: " + endMillis);
            if (endMillis < nowMillis){
                Log.d("Impromptu", "Would remove " + event.getObjectId());
                args.clear();
                args.put("eventId", event.getObjectId());
                ParseCloud.callFunctionInBackground("eventCleanup", args, null);
                i.remove();
            }
        }
//        verifyEvents(events);
        Log.d("Impromptu", "List Size: " + events.size());

        return events;
    }

    public List<Event> getStreamEvents(final UpdateView updateView) {
        final ImpromptuUser targ = this;
        try {
            this.fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (e != null) {
                        Log.e("Impromptu", "Error fetching.", e);
                    } else {
                        Log.d("Impromptu", "Successful fetch in bg.");
                        AsyncTaskPopulateEvents task = new AsyncTaskPopulateEvents();
                        task.setUpdateView(updateView);
                        task.execute(targ);
                        // updateView.update(targ.get(str));
                    }
                }
            });
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching User:", exc);
        }
        Log.d("Impromptu", "List Size: " + streamEvents.size());

        return streamEvents;
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

    public void setGroups(List<Group> groupList) {
        this.groups = groupList;
        this.put(groupKey, groupList);
    }

    public void setFriends(List<ImpromptuUser> friends) {
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
        if (friends == null) {
            try {
                this.fetchIfNeeded();
                this.refresh();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching User:", exc);
            }
            friends = this.getList(friendsKey);
            this.verifyFriends(friends);
            Collections.sort(friends);
            for (ImpromptuUser friend: friends) {
                friendMap.put(friend.getObjectId(), friend);
            }
        } else {
            populateFriendsInBackground();
        }
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
        if (groups == null) {
            try {
                this.fetchIfNeeded();
                this.refresh();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching User:", exc);
            }
            groups = getList(groupKey);
            verifyGroups(groups);
            Collections.sort(groups);
        }
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
        this.name = name;
        this.put(this.nameKey, name);
    }


    public String getName() {
        if (name == null) {
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
        return name;
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
        if (picture == null) {
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
                    picture = BitmapFactory.decodeByteArray(picBlock, 0, picBlock.length);
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
                    picture = bitmap;
                } catch (Exception exc) {
                    Log.e("Impromptu", "malformed exception", exc);
                }
            }
        }
        return picture;
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

    public void populateFacebookFriendsInBackground() {
        if (getFacebookId() != null) {
            Request friendReq = Request.newMyFriendsRequest(ParseFacebookUtils.getSession(), null);
            friendReq.setCallback(new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    if (response == null) {
                        Log.d("Impromptu", "reponses was null");
                    } else {
                        Log.d("Impromptu", "Response: " + response);

                        try {
                            JSONObject respJSON = new JSONObject(response.getRawResponse());
                            JSONArray data = respJSON.getJSONArray("data");
                            Log.d("Impromptu", "Number of fb friends also using impromptu: " + data.length());
                            facebookFriends = new ArrayList<ImpromptuUser>();
                            for (int i = 0; i < data.length(); i++) {
                                String id = data.getJSONObject(i).getString("id");
                                ImpromptuUser user = ImpromptuUser.getUserByFacebookId(id);
                                if (user != null) {
                                    facebookFriends.add(user);
                                }
                            }

                        } catch (Exception exc) {
                            Log.e("Impromptu", "json excpetion: ", exc);
                        }


                    }

                }
            });
            friendReq.executeAsync();

        }
    }

    public ArrayList<ImpromptuUser> getFacebookFriends() {
            if (facebookFriends == null) {
                facebookFriends = new ArrayList<>();
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
                    } else {
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
                                        facebookFriends.add(user);
                                    }
                                }

                            } catch (Exception exc) {
                                Log.e("Impromptu", "json excpetion: ", exc);
                            }

                        }
                    }
                }
            } else {
                populateFriendsInBackground();
            }
        return (ArrayList<ImpromptuUser>)facebookFriends;
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

    public static void test() throws Exception {

        ImpromptuUser u1 = new ImpromptuUser("angelBoyX31", "iLikeDoves", "wingT@heaven.com");
        ImpromptuUser u2 = new ImpromptuUser("angelBoyX311", "iLikeDoves", "wingTT@heaven.com");
        u1.signUp();
        u1.save();
        u2.signUp();
        u2.save();

        InstrumentationTestCase.assertFalse(u1.equals(u2));
        InstrumentationTestCase.assertFalse(u1.getUsername().equals(u2.getUsername()));
        //InstrumentationTestCase.assertEquals(u1.getEmail(), u2.getEmail());

        /*
        boolean flag = false;

        try {
            u1.setEmail("thedamnthing@gmail.com");
            u2.setEmail("thedamnthing@gmail.com");
            flag = true;
        }
        catch (Exception e) {
            Log.d("Impromptu", "Good, exception caught, assigning same email to two users.");
        }

        if(flag) {
            //throw new Exception("Didn't catch same email exception");
        }*/

        u1.setFacebookId("123");
        u2.setFacebookId("123");
        u1.setFriends(new ArrayList<ImpromptuUser>());
        u2.setFriends(new ArrayList<ImpromptuUser>());
        u1.setGroups(new ArrayList<Group>());
        u2.setGroups(new ArrayList<Group>());
        u1.setName("fU");
        u2.setName("fU");
        //TODO: bitmaps u1.setPicture(new Bitmap());
        u1.setSelected(true);
        u2.setSelected(true);
        u1.setStreamEvents(new ArrayList<Event>());
        u2.setStreamEvents(new ArrayList<Event>());

        //InstrumentationTestCase.assertEquals(u1.getEmail(), u2.getEmail());
        InstrumentationTestCase.assertEquals(u1.getFacebookId(), u2.getFacebookId());
        InstrumentationTestCase.assertEquals(u1.getFriends(), u2.getFriends());
        InstrumentationTestCase.assertEquals(u1.getGroups(), u2.getGroups());
        InstrumentationTestCase.assertEquals(u1.getName(), u2.getName());
        InstrumentationTestCase.assertEquals(u1.isSelected(), u2.isSelected());
        InstrumentationTestCase.assertEquals(u1.getStreamEvents(), u2.getStreamEvents());

        //TODO: faulting b/c "cannot delete a ParseUser that is not authenticated???
        //u1.deleteEventually();
        //u2.deleteEventually();
    }

}
