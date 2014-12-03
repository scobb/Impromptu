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
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;


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
    private String eventEndTimeKey = "eventEndTime";
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

    private String localTitle = null;
    private Time localCreationTime;
    private String localDescription = null;
    private int localDurationHour;
    private int localDurationMinute;
    private int localSeekStart;
    private int localSeekDuration;
    private ImpromptuUser localOwner;
    private Time localEventTime;
    private String localLocation = null;
    private String localType = null;
    private double localLatitude;
    private double localLongitude;
    private String localFormattedAddress = null;
    public boolean localEventTimeMorning;
    private boolean localPushed = false;
    private List<ImpromptuUser> localAllFriends = new ArrayList<ImpromptuUser>();
    private List<Group> localAllGroups = new ArrayList<Group>();
    private List<ImpromptuUser> localStreamFriends = new ArrayList<ImpromptuUser>();
    private List<ImpromptuUser> localPushFriends = new ArrayList<ImpromptuUser>();
    private List<ImpromptuUser> localUsersGoing = new ArrayList<ImpromptuUser>();
    private List<Group> localPushGroups = new ArrayList<Group>();
    private List<Group> localStreamGroups = new ArrayList<Group>();



//    public void setStreamFriends(ArrayList<ImpromptuUser> streamFriends) {
//        this.put(streamFriendsKey, streamFriends);
//    }

    public void setAllFriends(List<ImpromptuUser> friends) {
        localAllFriends = friends;
    }

    public List<ImpromptuUser> getAllFriends() {
        Collections.sort(localAllFriends);
        return localAllFriends;
    }

    public void setAllGroups(List<Group> groups) {
        localAllGroups = groups;
    }

    public List<Group> getAllGroups() {
        Collections.sort(localAllGroups);
        return localAllGroups;
    }

    public void setPushed(boolean val) {
        this.put(pushedKey, val);
    }

    public boolean getPushed() {
        if (getObjectId() != null) {
            localPushed = true;
        }
        return localPushed;
    }

    public void addStreamFriend(ImpromptuUser friend) {
        if (localPushed || getPushed()) {
            try {
                this.fetchIfNeeded();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            ParseRelation<ImpromptuUser> relation = this.getRelation(streamFriendsKey);
            relation.add(friend);
            saveInBackground();
        } else {
            Log.d("Impromptu", "Local addStreamFriend...");
            localStreamFriends.add(friend);
        }
    }

    public void removeStreamFriend(ImpromptuUser friend) {
        if (localPushed || getPushed()) {
            try {
                this.fetchIfNeeded();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            ParseRelation<ImpromptuUser> relation = this.getRelation(streamFriendsKey);
            relation.remove(friend);
            saveInBackground();
        } else {
            Log.d("Impromptu", "Local removeStreamFriend...");
            localStreamFriends.remove(friend);
        }
    }

    public void addPushFriend(ImpromptuUser friend) {

        if (localPushed || getPushed()) {
            try {
                this.fetchIfNeeded();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            ParseRelation<ImpromptuUser> relation = this.getRelation(pushFriendsKey);
            relation.add(friend);
            saveInBackground();
        } else {
            Log.d("Impromptu", "Local addPushFriend...");
            localPushFriends.add(friend);
        }

    }

    public void removePushFriend(ImpromptuUser friend) {
        if (localPushed || getPushed()) {
            try {
                this.fetchIfNeeded();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            ParseRelation<ImpromptuUser> relation = this.getRelation(pushFriendsKey);
            relation.remove(friend);
            saveInBackground();
        } else {
            Log.d("Impromptu", "Local removePushFriend...");
            localPushFriends.remove(friend);
        }

    }

    public List<ImpromptuUser> getStreamFriends() {
        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            ParseRelation relation = this.getRelation(streamFriendsKey);
            ParseQuery q = relation.getQuery();
            //        q.orderByAscending("name");
            try {
                save();
                List<ImpromptuUser> list = q.find();
                Collections.sort(list);
                return list;
            } catch (ParseException exc) {
                Log.e("Impromptu", "Error querying: ", exc);
            }
            return null;
        } else {
            Log.d("Impromptu", "local getStreamFriends");
            Collections.sort(localStreamFriends);
            return localStreamFriends;
        }

    }

    public List<ImpromptuUser> getPushFriends() {
        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            ParseRelation relation = this.getRelation(pushFriendsKey);
            ParseQuery q = relation.getQuery();
            //        q.orderByAscending("name");
            try {
                save();
                List<ImpromptuUser> list = q.find();
                Collections.sort(list);
                return list;
            } catch (ParseException exc) {
                Log.e("Impromptu", "Error querying: ", exc);
            }
            return null;
        } else {
            Log.d("Impromptu", "local getPushFriends");
            Collections.sort(localPushFriends);
            return localPushFriends;
        }
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

        Date currentDate = new Date();
        Date eventDate = this.getDate(eventTimeKey);

        Log.d("Impromptu", "Event " + eventDate);
        Log.d("Impromptu", "Current " + currentDate);


        long timeDifference = eventDate.getTime() - currentDate.getTime();
        long hourDifference = TimeUnit.MILLISECONDS.toHours(timeDifference);

        int difference = (int) Math.ceil(hourDifference);

        map.put("date", "In about " + difference + " hours");
        map.put("title", this.getTitle());
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

    /**
     *
     * @param user User to determine "going" status
     * @return true if user is going, false otherwise
     */
    public boolean userIsGoing(ImpromptuUser user) {
        ParseRelation<ImpromptuUser> rel = this.getRelation(usersGoingKey);
        ParseQuery<ImpromptuUser> query = rel.getQuery();
        try {
            List<ImpromptuUser> result = query.find();
            return result.contains(user);
        } catch (ParseException e) {
            Log.e("Impromptu", "error getting relation query", e);
        }
        return false;
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
        if (!getPushed()) {
            Log.d("Impromptu", "Saving all data to parse object in preparation for save.");
            localPushed = true;
            setTitle(localTitle);
            setCreationTime(localCreationTime);
            setDescription(localDescription);
            setDurationHour(localDurationHour);
            setDurationMinute(localDurationMinute);
            setSeekStart(localSeekStart);
            setSeekDuration(localSeekDuration);
            setOwner((ImpromptuUser)ParseUser.createWithoutData("_User", localOwner.getObjectId()));
            setEventTime(localEventTime);
            setLocationName(localLocation);
            setType(localType);
            setLatitude(localLatitude);
            setLongitude(localLongitude);
            setFormattedAddress(localFormattedAddress);
            setEventTimeMorning(localEventTimeMorning);
            ParseRelation<ImpromptuUser> streamFriends = getRelation(streamFriendsKey);
            for (ImpromptuUser friend : localStreamFriends) {
                streamFriends.add(friend);
            }
            ParseRelation<ImpromptuUser> pushFriends = getRelation(pushFriendsKey);
            for (ImpromptuUser friend : localPushFriends) {
                pushFriends.add(friend);
            }
            ParseRelation<ImpromptuUser> usersGoing = getRelation(usersGoingKey);
            for (ImpromptuUser friend : localUsersGoing) {
                usersGoing.add(friend);
            }

            setPushGroups((ArrayList<Group>) localPushGroups);
            setStreamGroups((ArrayList<Group>) localStreamGroups);
            setPushed(true);
        }


        final Event ref = this;
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null) {
                    String objectId = ref.getObjectId();
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("eventId", objectId);
                    Log.d("Impromptu", "Object id: " + objectId);
                    ParseCloud.callFunctionInBackground("addNewEvent", params, null);
                } else {
                    Log.e("Impromptu", "Exception when persisting event: ", e);
                }
            }
        });
    }

    public void setOwner(ImpromptuUser owner) {

        if (localPushed || getPushed()) {
            this.put(ownerKey, owner);
        } else {
            Log.d("Impromptu", "Local setOwner");
            localOwner = owner;
        }
    }

    public void setType(String type) {

        if (localPushed || getPushed()) {
            this.put(typeKey, type);
        } else {
            Log.d("Impromptu", "Local setType");
            localType = type;
        }
    }

    public void setTitle(String title) {

        if (localPushed || getPushed()) {
            this.put(titleKey, title);
        } else {
            Log.d("Impromptu", "local setTitle");
            localTitle = title;
        }
    }

    public void setDescription(String description) {

        if (localPushed || getPushed()) {
            this.put(descriptionKey, description);
        } else {
            localDescription = description;
        }
    }

    public void setStreamGroups(ArrayList<Group> streamGroups) {
        if (localPushed || getPushed()) {
            this.put(streamGroupsKey, streamGroups);
        } else {
            localStreamGroups = streamGroups;
        }
    }

    public void setPushGroups(ArrayList<com.example.steve.impromptu.Entity.Group> pushGroups) {
        if (localPushed || getPushed()) {
            this.put(pushGroupsKey, pushGroups);
        } else {
            localPushGroups = pushGroups;
        }
    }
//
//    public void setPushFriends(ArrayList<ImpromptuUser> pushFriends) {
//        this.put(pushFriendsKey, pushFriends);
//    }


    public void setEventTime(Time eventTime) {

        if (localPushed || getPushed()) {
            this.put(eventTimeKey, new Date(eventTime.toMillis(false)));
        } else {
            localEventTime = eventTime;
        }
    }
    public void setEventEndTime(Time eventTime) {

        if (localPushed || getPushed()) {
            this.put(eventEndTimeKey, eventTime);
        } else {
            localEventTime = eventTime;
        }
    }
    public void setEventTimeMorning(Boolean morning) {

        if (localPushed || getPushed()) {
            this.put("eventTimeMorning", morning);
        } else {
            localEventTimeMorning = morning;
        }
    }

    public void setSeekStart(int progress) {
        if (localPushed || getPushed()) {
            this.put(seekStartKey, progress);
        } else {
            localSeekStart = progress;
        }
    }

    public void setSeekDuration(int progress) {
        if (localPushed || getPushed()) {
            this.put(seekDurationKey, progress);
        } else {
            localSeekDuration = progress;
        }
    }

    public void setCreationTime(Time creationTime) {

        if (localPushed || getPushed()) {
            this.put(creationTimeKey, new Date(creationTime.toMillis(false)));
        } else {
            localCreationTime = creationTime;
        }
    }

    public void setLocationName(String location) {

        if (localPushed || getPushed()) {
            this.put(locationKey, location);
        } else {
            localLocation = location;
        }
    }

    public void setLatitude(Double latitude) {
        if (localPushed || getPushed()) {
            this.put(latitudeKey, latitude);
        } else {
            localLatitude = latitude;
        }
    }

    public void setLongitude(Double longitude) {

        if (localPushed || getPushed()) {
            this.put(longitudeKey, longitude);
        } else {
            localLongitude = longitude;
        }
    }

    public void setFormattedAddress(String address) {

        if (localPushed || getPushed()) {
            this.put(addressKey, address);
        } else {
            localFormattedAddress = address;
        }
    }


    public ImpromptuUser getOwner() {
        //TODO - add verification

        if (localPushed || getPushed()) {
            try {
                this.fetchIfNeeded();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            return (ImpromptuUser) this.get(ownerKey);
        } else {
            return localOwner;
        }
    }

    public void setDurationHour(int durationHour) {

        if (localPushed || getPushed()) {
            this.put(durationHourKey, durationHour);
        } else {
            localDurationHour = durationHour;
        }
    }

    public void setDurationMinute(int durationMinute) {

        if (localPushed || getPushed()) {
            this.put(durationMinuteKey, durationMinute);
        } else {
            localDurationMinute = durationMinute;
        }
    }


    public String getType() {

        if (localPushed || getPushed()) {
            try {
                this.fetchIfNeeded();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            return (String) this.get(typeKey);
        } else {
            return localType;
        }
    }

    public String getTitle() {

        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            return (String) this.get(titleKey);
        } else {
            return localTitle;
        }
    }

    public String getDescription() {

        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            return this.getString(descriptionKey);
        } else {
            return localDescription;
        }
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

        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            List<Group> groups = this.getList(streamGroupsKey);
            verifyGroups(groups);
            Collections.sort(groups);
            return groups;
        } else {
            Collections.sort(localStreamGroups);
            return localStreamGroups;
        }
    }

    public List<com.example.steve.impromptu.Entity.Group> getPushGroups() {

        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            List<Group> groups = this.getList(pushGroupsKey);
            verifyGroups(groups);
            Collections.sort(groups);
            return groups;
        } else {
            Collections.sort(localPushGroups);
            return localPushGroups;
        }
    }

    public Time getEventTime() {

        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            Time eventTime = new Time();
            eventTime.set(this.getDate(eventTimeKey).getTime());
            return eventTime;
        } else {
            return localEventTime;
        }
    }
    public Time getEventEndTime() {
        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            Time eventEndTime = new Time();
            eventEndTime.set(this.getDate(eventEndTimeKey).getTime());
            return eventEndTime;
        } else {
            return localEventTime;
        }
    }

    public Date getEventDate(){
        return this.getDate(eventTimeKey);
    }

    public Time getCreationTime() {

        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            Time creationTime = new Time();
            creationTime.set(this.getDate(creationTimeKey).getTime());
            return creationTime;
        } else {
            return localCreationTime;
        }
    }

    public Boolean getEventTimeMorning() {

        if (localPushed || getPushed()) {
            return (Boolean) this.get("eventTimeMorning");
        } else {
            return localEventTimeMorning;
        }
    }

    public String getLocationName() {

        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            return this.getString(locationKey);
        } else {
            return localLocation;
        }
    }

    public String getFormattedAddress() {

        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            return this.getString(addressKey);
        } else {
            return localFormattedAddress;
        }
    }

    public Double getLongitude() {

        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            return this.getDouble(longitudeKey);
        } else {
            return localLongitude;
        }
    }

    public Double getLatitude() {

        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            return this.getDouble(latitudeKey);
        } else {
            return localLatitude;
        }
    }

    public int getDurationHour() {

        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            return this.getInt(durationHourKey);
        } else {
            return localDurationHour;
        }
    }

    public int getDurationMinute() {

        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            return this.getInt(durationMinuteKey);
        } else {
            return localDurationMinute;
        }
    }

    public int getSeekStart() {

        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            return this.getInt(seekStartKey);
        } else {
            return localSeekStart;
        }

    }

    public int getSeekDuration() {

        if (localPushed || getPushed()) {
            try {
                this.fetch();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            return this.getInt(seekDurationKey);
        } else {
            return localSeekDuration;
        }

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

        if (localPushed || getPushed()) {
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
        } else {
            return localUsersGoing;
        }
    }

    public void addUserGoing(ImpromptuUser user) {

        if (localPushed || getPushed()) {
            try {
                this.fetchIfNeeded();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            ParseRelation<ImpromptuUser> relation = this.getRelation(usersGoingKey);
            relation.add(user);
            saveInBackground();
        } else {
            localUsersGoing.add(user);
        }

    }

    public void removeUserGoing(ImpromptuUser user) {
        if (localPushed || getPushed()) {
            try {
                this.fetchIfNeeded();
            } catch (Exception exc) {
                Log.e("Impromptu", "Error fetching Event:", exc);
            }
            ParseRelation<ImpromptuUser> relation = this.getRelation(usersGoingKey);
            relation.remove(user);
            saveInBackground();
        } else {
            localUsersGoing.remove(user);
        }

    }


    @Override
    public int compareTo(Event other) {
        Long myMillis = this.getEventTime().toMillis(true);
        Long otherMillis = other.getEventTime().toMillis(true);
        return myMillis.compareTo(otherMillis);
    }
}
