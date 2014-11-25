package com.example.steve.impromptu.Entity;

import android.text.format.Time;
import android.util.Log;

import com.example.steve.impromptu.R;
import com.parse.ParseClassName;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
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
    private String pushedKey = "pushed";

//    public void setStreamFriends(ArrayList<ImpromptuUser> streamFriends) {
//        this.put(streamFriendsKey, streamFriends);
//    }

    public void setPushed(boolean val) {
        this.put(pushedKey, val);
    }

    public boolean getPushed() {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getBoolean(pushedKey);
    }

    public void addStreamFriend(ImpromptuUser friend) {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        ParseRelation<ImpromptuUser> relation = this.getRelation(streamFriendsKey);
        relation.add(friend);
        saveInBackground();

    }

    public void removeStreamFriend(ImpromptuUser friend) {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        ParseRelation<ImpromptuUser> relation = this.getRelation(streamFriendsKey);
        relation.remove(friend);
        saveInBackground();
    }

    public void addPushFriend(ImpromptuUser friend) {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        ParseRelation<ImpromptuUser> relation = this.getRelation(pushFriendsKey);
        relation.add(friend);
        saveInBackground();

    }

    public void removePushFriend(ImpromptuUser friend) {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        ParseRelation<ImpromptuUser> relation = this.getRelation(pushFriendsKey);
        relation.remove(friend);
        saveInBackground();

    }

    public List<ImpromptuUser> getStreamFriends() {
        try {
            this.fetch();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        ParseRelation relation = this.getRelation(streamFriendsKey);
        ParseQuery q = relation.getQuery();
        q.orderByAscending("name");
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
            this.fetch();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        ParseRelation relation = this.getRelation(pushFriendsKey);
        ParseQuery q = relation.getQuery();
        q.orderByAscending("name");
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
        this.setDurationHour(-1);
    }

    public void test() {
        this.clear();
        ImpromptuUser stephen = ImpromptuUser.getUserById("mQMe4SHJNe");
        ImpromptuUser jon = ImpromptuUser.getUserById("tqaTgngvn2");
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

        this.addStreamFriend(jon);
        this.addStreamFriend(stephen);
//        this.saveInBackground();

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
        map.put("picture", getPicture());

        Time creationTime = new Time();
        creationTime.set(this.getDate("creationTime").getTime());

        int hour = creationTime.hour;
        int minute = creationTime.minute;


        map.put("date", hour + ":" + minute);
        map.put("content", this.getDescription());
        return map;
    }

    public String getPicture(){
        switch(this.getType()){
            case "Drinking":
                return Integer.toString(R.drawable.drinking);
            case "Eating":
                return Integer.toString(R.drawable.food);
            case "Sports":
                return Integer.toString(R.drawable.sport_icon);
            case "Studying":
                return Integer.toString(R.drawable.studying);
            case "TV":
                return Integer.toString(R.drawable.tv);
            case "Working Out":
                return Integer.toString(R.drawable.working_out);
            default:
                return Integer.toString(R.drawable.drinking);
        }

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
        Log.d("Impromptu", "Persisting event");
        final Event ref = this;
        setPushed(true);
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null) {
                    String objectId = ref.getObjectId();
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("eventId", objectId);
                    Log.d("Impromptu", "Object id: " + objectId);
                    ParseCloud.callFunctionInBackground("addNewEvent", params, null);
                }
            }
        });
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
//
//    public void setPushFriends(ArrayList<ImpromptuUser> pushFriends) {
//        this.put(pushFriendsKey, pushFriends);
//    }


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

//    public void setUsersGoing(ArrayList<ImpromptuUser> users) {
//        this.put(usersGoingKey, users);
//    }

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

    public void setDurationMinute(int durationMinute) {
        this.put(durationMinuteKey, durationMinute);
    }


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
            this.fetch();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return (String) this.get(titleKey);
    }

    public String getDescription() {

        try {
            this.fetch();
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
            this.saveInBackground();
    }

    public List<com.example.steve.impromptu.Entity.Group> getStreamGroups() {

        try {
            this.fetch();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        List<Group> groups = this.getList(streamGroupsKey);
        verifyGroups(groups);
        Collections.sort(groups);
        return groups;
    }

    public List<com.example.steve.impromptu.Entity.Group> getPushGroups() {

        try {
            this.fetch();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        List<Group> groups = this.getList(pushGroupsKey);
        verifyGroups(groups);
        Collections.sort(groups);
        return groups;
    }

    public Time getEventTime() {
        try {
            this.fetch();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        Time eventTime = new Time();
        eventTime.set(this.getDate(eventTimeKey).getTime());
        return eventTime;
    }

    public Time getCreationTime() {
        try {
            this.fetch();
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
            this.fetch();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getString(locationKey);
    }

    public String getFormattedAddress() {

        try {
            this.fetch();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getString(addressKey);
    }

    public Double getLongitude() {

        try {
            this.fetch();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getDouble(longitudeKey);
    }

    public Double getLatitude() {

        try {
            this.fetch();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getDouble(latitudeKey);
    }

    public int getDurationHour() {

        try {
            this.fetch();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getInt(durationHourKey);
    }

    public int getDurationMinute() {

        try {
            this.fetch();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getInt(durationMinuteKey);
    }

    public int getSeekStart() {
        try {
            this.fetch();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getInt(seekStartKey);

    }

    public int getSeekDuration() {
        try {
            this.fetch();
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
            this.saveInBackground();

        }
    }

    public List<ImpromptuUser> getUsersGoing() {
        try {
            this.fetch();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        ParseRelation relation = this.getRelation(usersGoingKey);
        ParseQuery q = relation.getQuery();
        q.orderByAscending("name");
        try {
            save();
            return q.find();
        } catch (ParseException exc) {
            Log.e("Impromptu", "Error querying: ", exc);
        }
        return null;
    }

    public void addUserGoing(ImpromptuUser user) {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        ParseRelation<ImpromptuUser> relation = this.getRelation(usersGoingKey);
        relation.add(user);
        saveInBackground();

    }

    public void removeUserGoing(ImpromptuUser user) {
        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        ParseRelation<ImpromptuUser> relation = this.getRelation(usersGoingKey);
        relation.remove(user);
        saveInBackground();

    }


    @Override
    public int compareTo(Event other) {
        Long myMillis = this.getEventTime().toMillis(true);
        Long otherMillis = other.getEventTime().toMillis(true);
        return myMillis.compareTo(otherMillis);
    }
}
