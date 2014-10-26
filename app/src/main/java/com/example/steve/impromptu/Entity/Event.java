package com.example.steve.impromptu.Entity;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jonreynolds on 10/26/14.
 */
public class Event {

    // TODO change to User class
    private String owner;
    private String type;
    private String title;
    private String description;
    private ArrayList<Group> streamGroups;
    private ArrayList<Group> pushGroups;
    private Date eventTime;
    private Date creationTime;
    private String location;

    public Event() {

        streamGroups = new ArrayList<Group>();
        pushGroups = new ArrayList<Group>();

    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStreamGroups(ArrayList<Group> streamGroups) {
        this.streamGroups = streamGroups;
    }

    public void setPushGroups(ArrayList<Group> pushGroups) {
        this.pushGroups = pushGroups;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwner() {

        return owner;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<Group> getStreamGroups() {
        return streamGroups;
    }

    public ArrayList<Group> getPushGroups() {
        return pushGroups;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public String getLocation() {
        return location;
    }
}
