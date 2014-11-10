package com.example.steve.impromptu.Entity;

import android.text.format.Time;
import android.util.Log;

import com.example.steve.impromptu.R;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by jonreynolds on 10/26/14.
 */
@ParseClassName("Event")
public class Event extends ParseObject implements Comparable<Event>{
    private String creationTimeKey = "creationTime";
    private String descriptionKey = "description";
    private String durationHourKey = "durationHour";
    private String durationMinuteKey = "durationMinute";
    private String eventTimeKey = "eventTime";
    private String locationKey = "location";
    private String ownerKey = "owner";
    private String pushGroupsKey = "pushGroups";
    private String streamGroupsKey = "streamGroups";
    private String titleKey = "title";
    private String typeKey = "type";
    private String createdAtKey = "createdAt";
    private String updatedAtKey = "updatedAt";
    private String streamFriendsKey = "streamFriends";
    private String pushFriendsKey = "pushFriends";

    public void setStreamFriends(ArrayList<ImpromptuUser> streamFriends) {
        this.put(streamFriendsKey, streamFriends);
    }

    public List<ImpromptuUser> getStreamFriends() {
        //TODO - add verification

        return this.getList(streamFriendsKey);
    }

    public List<ImpromptuUser> getPushFriends() {

        //TODO - add verification
        return this.getList(pushFriendsKey);
    }

    public Event() {
        super();

    }

    public void clear() {
        this.setStreamGroups(new ArrayList<Group>());
        this.setPushGroups(new ArrayList<com.example.steve.impromptu.Entity.Group>());
        this.setStreamFriends(new ArrayList<ImpromptuUser>());
        this.setPushFriends(new ArrayList<ImpromptuUser>());
    }

    public void test() {
        this.setOwner((ImpromptuUser) ParseUser.getCurrentUser());
        this.setCreationTime(new Time());
        this.setDescription("my awesome test event");
        this.setDurationHour(1);
        this.setDurationMinute(27);
        this.setEventTime(new Time());
        this.setLocation("here");
        this.setTitle("awesome");
        this.setType("awesomeType");

        ImpromptuUser testUser = this.getOwner();
        Time testCreation = this.getCreationTime();
        String test = this.getDescription();
        Integer test_int = this.getDurationHour();
        test_int = this.getDurationMinute();
        testCreation = this.getEventTime();
        test = this.getLocation();
        test = this.getTitle();
        test = this.getType();
    }


    public HashMap<String, String> getHashMap(){
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


    public void persist() {
        this.saveInBackground();
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

    public void setCreationTime(Time creationTime) {
        this.put(creationTimeKey, new Date(creationTime.toMillis(false)));
    }

    public void setLocation(String location) {
        this.put(locationKey, location);
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

    public ArrayList<com.example.steve.impromptu.Entity.Group> getStreamGroups() {
        //TODO - add verification

        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }

        return (ArrayList<com.example.steve.impromptu.Entity.Group>) this.get(streamGroupsKey);
    }

    public ArrayList<com.example.steve.impromptu.Entity.Group> getPushGroups() {

        //TODO - add verification

        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return (ArrayList<com.example.steve.impromptu.Entity.Group>) this.get(pushGroupsKey);
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

    public String getLocation() {

        try {
            this.fetchIfNeeded();
        } catch (Exception exc) {
            Log.e("Impromptu", "Error fetching Event:", exc);
        }
        return this.getString(locationKey);
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

    @Override
    public int compareTo(Event other) {
        Long myMillis = new Long(this.getEventTime().toMillis(true));
        Long otherMillis = new Long(other.getEventTime().toMillis(true));
        return myMillis.compareTo(otherMillis);
    }
}
