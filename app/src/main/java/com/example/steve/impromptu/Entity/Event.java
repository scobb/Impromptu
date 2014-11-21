package com.example.steve.impromptu.Entity;

import android.text.format.Time;
import android.util.Log;

import com.example.steve.impromptu.R;
import com.parse.FunctionCallback;
import com.parse.ParseClassName;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * Created by jonreynolds on 10/26/14.
 */
@ParseClassName("Event")

public class Event extends ParseObject implements Comparable<Event> {
    private String creationTimeKey = "creationTime";
    private String descriptionKey = "description";
    private String durationHourKey = "durationHour";
    private String durationMinuteKey = "durationMinute";
    private String seekStartKey = "startProgress";
    private String seekDurationKey = "durationProgress";
    private String eventTimeKey = "eventTime";
    private String locationKey = "location";
    private String ownerKey = "owner";
    private String pushGroupsKey = "pushGroups";
    private String streamGroupsKey = "streamGroups";
    private String titleKey = "title";
    private String typeKey = "type";
    private String streamFriendsKey = "streamFriends";
    private String pushFriendsKey = "pushFriends";
    private String latitudeKey = "latitude";
    private String longitudeKey = "longitude";
    private String addressKey = "formattedAddress";
    private String usersGoingKey = "usersGoing";

    public void setStreamFriends(ArrayList<ImpromptuUser> streamFriends) {
        this.put(streamFriendsKey, streamFriends);
    }

    public void addStreamFriend(ImpromptuUser friend) {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        ParseRelation<ImpromptuUser> relation = this.getRelation(streamFriendsKey);
        relation.add(friend);
        persist();

    }

    public void removeStreamFriend(ImpromptuUser friend) {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        ParseRelation<ImpromptuUser> relation = this.getRelation(streamFriendsKey);
        relation.remove(friend);
        persist();
    }

    public void addPushFriend(ImpromptuUser friend) {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        ParseRelation<ImpromptuUser> relation = this.getRelation(pushFriendsKey);
        relation.add(friend);
        persist();

    }

    public void removePushFriend(ImpromptuUser friend) {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        ParseRelation<ImpromptuUser> relation = this.getRelation(pushFriendsKey);
        relation.remove(friend);
        persist();

    }

    public List<ImpromptuUser> getStreamFriends() {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        ParseRelation relation = this.getRelation(streamFriendsKey);
        ParseQuery q = relation.getQuery();
        try {
            save();
            return q.find();
        } catch (ParseException exc) {
            Log.e("Impromptu", "Error querying: ", exc);
        }
        return null;

    }

    public List<ImpromptuUser> getPushFriends() {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        ParseRelation relation = this.getRelation(pushFriendsKey);
        ParseQuery q = relation.getQuery();
        try {
            save();
            return q.find();
        } catch (ParseException exc) {
            Log.e("Impromptu", "Error querying: ", exc);
        }
        return null;
    }

    public Event() {
        super();

    }

    public void clear() {
        this.setStreamGroups(new ArrayList<Group>());
        this.setPushGroups(new ArrayList<com.example.steve.impromptu.Entity.Group>());
        this.setUsersGoing(new ArrayList<ImpromptuUser>());
        this.setDurationHour(-1);
    }

    public void test() {
        this.clear();
        ImpromptuUser stephen = ImpromptuUser.getUserById("mQMe4SHJNe");
        this.setOwner(stephen);
        this.setCreationTime(new Time());
        this.setDescription("my awesome test event");
        this.setDurationHour(1);
        this.setDurationMinute(27);
        this.setEventTime(new Time());
        this.setTitle("awesome");
        this.setType("awesomeType");
        this.setLatitude(30.25);
        this.setLongitude(-97.75);
        this.setLocationName("Austin");
//        this.addUserGoing((ImpromptuUser)ParseUser.getCurrentUser());
//        this.addUserGoing((ImpromptuUser)ParseUser.getCurrentUser());

//        ImpromptuUser testUser = this.getOwner();
//        Time testCreation = this.getCreationTime();
//        String test = this.getDescription();
//        Integer test_int = this.getDurationHour();
//        test_int = this.getDurationMinute();
//        testCreation = this.getEventTime();
//        test = this.getLocationName();
//        test = this.getTitle();
//        test = this.getType();
//        List<ImpromptuUser> test_list = getUsersGoing();

//        removeUserGoing((ImpromptuUser)ParseUser.getCurrentUser());
//        removeUserGoing((ImpromptuUser)ParseUser.getCurrentUser());
    }


    public HashMap<String, String> getHashMap() {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("user", this.getOwner().getName());
        map.put("picture", Integer.toString(R.drawable.ic_launcher));

        Time creationTime = new Time();
        creationTime.set(this.getDate("creationTime").getTime());

        int hour = creationTime.hour;
        int minute = creationTime.minute;


        map.put("date", hour + ":" + minute);
        map.put("content", this.getDescription());
        return map;
    }

    public static Event getEventById(String id) {
        /**
         * method - gets existing user given their id
         * Returns null if ID not found
         */
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        ParseObject event = null;
        try {
            Log.d("Impromptu", "Trying to get event for id " + id);
            event = query.get(id);
        } catch (Exception exc) {
            Log.e("Impromptu", "Exception querying...", exc);
            return null;
        }
        return (Event) event;
    }


    public void persist() {
        final String eventId = this.getObjectId();
        Log.d("Impromptu", "Persisting event");
        this.saveInBackground();
//        this.saveInBackground(new SaveCallback() {
//
//            @Override
//            public void done(ParseException e) {
//                if (e != null) {
//                    Log.e("Impromptu", "Error persisting an event: ", e);
//                }
//                HashMap<String, Object> params = new HashMap<String, Object>();
//                params.put("eventId", eventId);
//                ParseCloud.callFunctionInBackground("addEvent", params, new FunctionCallback<Object>() {
//                    @Override
//                    public void done(Object o, ParseException e) {
//                        if (e != null) {
//                            Log.e("Impromptu", "Exception in callback: ", e);
//                        }
//                    }
//                });
//            }
//        });
    }

    public void setOwner(ImpromptuUser owner) {
        this.put(ownerKey, owner);
    }

    public void setType(String type) {
        this.put(typeKey, type);
    }

    public void setTitle(String title) {
        this.put(titleKey, title);
    }

    public void setDescription(String description) {
        this.put(descriptionKey, description);
    }

    public void setStreamGroups(ArrayList<Group> streamGroups) {
        this.put(streamGroupsKey, streamGroups);
    }

    public void setPushGroups(ArrayList<com.example.steve.impromptu.Entity.Group> pushGroups) {
        this.put(pushGroupsKey, pushGroups);
    }

    public void setPushFriends(ArrayList<ImpromptuUser> pushFriends) {
        this.put(pushFriendsKey, pushFriends);
    }


    public void setEventTime(Time eventTime) {
        this.put(eventTimeKey, new Date(eventTime.toMillis(false)));
    }

    public void setEventTimeMorning(Boolean morning) {
        this.put("eventTimeMorning", morning);
    }

    public void setSeekStart(int progress) {
        this.put(seekStartKey, progress);
    }

    public void setSeekDuration(int progress) {
        this.put(seekDurationKey, progress);
    }

    public void setCreationTime(Time creationTime) {
        this.put(creationTimeKey, new Date(creationTime.toMillis(false)));
    }

    public void setLocationName(String location) {
        this.put(locationKey, location);
    }

    public void setLatitude(Double latitude) {
        this.put(latitudeKey, latitude);
    }

    public void setLongitude(Double longitude) {
        this.put(longitudeKey, longitude);
    }

    public void setFormattedAddress(String address) {
        this.put(addressKey, address);
    }

    public void setUsersGoing(ArrayList<ImpromptuUser> users) {
        this.put(usersGoingKey, users);
    }

    public ImpromptuUser getOwner() {
        //TODO - add verification
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return (ImpromptuUser) this.get(ownerKey);
    }

    public void setDurationHour(int durationHour) {
        this.put(durationHourKey, durationHour);
    }

    public void setDurationMinute(int durationMinute) { this.put(durationMinuteKey, durationMinute); }


    public String getType() {

        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return (String) this.get(typeKey);
    }

    public String getTitle() {

        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return (String) this.get(titleKey);
    }

    public String getDescription() {

        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getString(descriptionKey);
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

    public List<com.example.steve.impromptu.Entity.Group> getStreamGroups() {

        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        List<Group> groups = this.getList(streamGroupsKey);
        verifyGroups(groups);
        return groups;
    }

    public List<com.example.steve.impromptu.Entity.Group> getPushGroups() {

        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        List<Group> groups = this.getList(pushGroupsKey);
        verifyGroups(groups);
        return groups;
    }

    public Time getEventTime() {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        Time eventTime = new Time();
        eventTime.set(this.getDate(eventTimeKey).getTime());
        return eventTime;
    }

    public Time getCreationTime() {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        Time creationTime = new Time();
        creationTime.set(this.getDate(creationTimeKey).getTime());
        return creationTime;
    }

    public Boolean getEventTimeMorning() {
        return (Boolean) this.get("eventTimeMorning");
    }

    public String getLocation() {
        return (String) this.get("location");
    }

    public String getLocationName() {

        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getString(locationKey);
    }

    public String getFormattedAddress() {

        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getString(addressKey);
    }

    public Double getLongitude() {

        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getDouble(longitudeKey);
    }

    public Double getLatitude() {

        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getDouble(latitudeKey);
    }

    public int getDurationHour() {

        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getInt(durationHourKey);
    }

    public int getDurationMinute() {

        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getInt(durationMinuteKey);
    }

    public int getSeekStart() {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getInt(seekStartKey);

    }
    public int getSeekDuration() {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getInt(seekDurationKey);

    }

    public void verifyUsers(List<ImpromptuUser> users) {
        Iterator<ImpromptuUser> it = users.iterator();
        boolean needPersist = false;
        while (it.hasNext()) {
            try {
                it.next().fetchIfNeeded();
            } catch (ParseException exc) {
                Log.d("Impromptu", "Problem fetching a user in verifyUsers: ", exc);
                it.remove();
                needPersist = true;
            }
        }
        if (needPersist) {
            Log.d("Impromptu", "Verify Users in event needed to persist.");
            this.persist();

        }
    }

    public List<ImpromptuUser> getUsersGoing() {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        List<ImpromptuUser> users = this.getList(usersGoingKey);
        verifyUsers(users);
        return users;
    }

    public void addUserGoing(ImpromptuUser user) {
        ArrayList<ImpromptuUser> usersGoing = (ArrayList<ImpromptuUser>)getUsersGoing();
        if (!usersGoing.contains(user)) {
            usersGoing.add(user);
            persist();
        }
    }

    public void removeUserGoing(ImpromptuUser user) {
        ArrayList<ImpromptuUser> usersGoing = (ArrayList<ImpromptuUser>)getUsersGoing();
        if (usersGoing.contains(user)) {
            usersGoing.remove(user);
            persist();
        }
    }


    @Override
    public int compareTo(Event other) {
        Long myMillis = new Long(this.getEventTime().toMillis(true));
        Long otherMillis = new Long(other.getEventTime().toMillis(true));
        return myMillis.compareTo(otherMillis);
    }
}
