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
public class Event extends ParseObject{

    public void setStreamFriends(ArrayList<ImpromptuUser> streamFriends) {
            this.put("streamFriends", streamFriends);
    }

    public List<ImpromptuUser> getStreamFriends() {

        return this.getList("streamFriends");
    }

    public List<ImpromptuUser> getPushFriends() {
        return this.getList("pushFriends");
    }

    public Event() {
        super();
        this.setStreamGroups(new ArrayList<Group>());
        this.setPushGroups(new ArrayList<Group>());
        this.setStreamFriends(new ArrayList<ImpromptuUser>());
        this.setPushFriends(new ArrayList<ImpromptuUser>());

    }

    public void test() {
        this.setOwner((ImpromptuUser)ParseUser.getCurrentUser());
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
        try {
            ImpromptuUser owner = (ImpromptuUser)this.getOwner().fetchIfNeeded();
            map.put("user", owner.getName());
        }
        catch (Exception exc) {
            Log.e("Impromptu", "Exception", exc);
        }
        map.put("picture", Integer.toString(R.drawable.ic_launcher));
        map.put("date", this.getEventTime().toString());
        map.put("content", this.getDescription());
        return map;
    }


    public void persist() {
        this.saveInBackground();
    }

    public void setOwner(ImpromptuUser owner) {
        this.put("owner", owner);
    }

    public void setType(String type) {
        this.put("type", type);
    }

    public void setTitle(String title) {
        this.put("title", title);
    }

    public void setDescription(String description) {
        this.put("description", description);
    }

    public void setStreamGroups(ArrayList<Group> streamGroups) {
        this.put("streamGroups", streamGroups);
    }

    public void setPushGroups(ArrayList<Group> pushGroups) {
        this.put("pushGroups", pushGroups);
    }

    public void setPushFriends(ArrayList<ImpromptuUser> pushFriends) { this.put("pushFriends", pushFriends); }


    public void setEventTime(Time eventTime) {
        this.put("eventTime", new Date(eventTime.toMillis(false)));
    }

    public void setCreationTime(Time creationTime) {
        this.put("creationTime", new Date(creationTime.toMillis(false)));
    }

    public void setLocation(String location) {
        this.put("location", location);
    }

    public ImpromptuUser getOwner() {

        return (ImpromptuUser)this.get("owner");
    }

    public void setDurationHour(int durationHour) {
        this.put("durationHour", durationHour);
    }

    public void setDurationMinute(int durationMinute) {

        this.put("durationMinute", durationMinute);
    }

    public String getType() {
        return (String)this.get("type");
    }

    public String getTitle() {
        return (String)this.get("title");
    }

    public String getDescription() {
        return (String)this.get("description");
    }

    public ArrayList<Group> getStreamGroups() {
        return (ArrayList<Group>)this.get("streamGroups");
    }

    public ArrayList<Group> getPushGroups() {
        return (ArrayList<Group>)this.get("pushGroups");
    }

    public Time getEventTime() {
        Time eventTime = new Time();
        eventTime.set(this.getDate("eventTime").getTime());
        return eventTime;
    }

    public Time getCreationTime(){
        Time creationTime = new Time();
        creationTime.set(this.getDate("creationTime").getTime());
        return creationTime;
    }

    public String getLocation() {
        return (String)this.get("location");
    }

    public int getDurationHour() {
        return (int)this.get("durationHour");
    }

    public int getDurationMinute() {
        return (int)this.get("durationMinute");
    }
}
