package com.example.steve.impromptu.Tests;

import android.test.InstrumentationTestCase;
import android.text.format.Time;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.FriendRequest;
import com.example.steve.impromptu.Entity.Group;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scobb on 11/30/14.
 */
public class EventTest extends InstrumentationTestCase {

    @Override
    protected void setUp() throws Exception {

        ParseUser.registerSubclass(ImpromptuUser.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(FriendRequest.class);
        // Parse.initialize(getActivity(), "sP5YdlJxg1WiwfKgSXX4KdrgpZzAV5g69dV8ryY0", "houV8Brg8oIuBKSLheR7qAW4AJfGq1QZmH62Spgk");
    }

    public void testBuild() throws Exception {
        // slight dependence on ImpromptuUser
        Event e = new Event();

        // test clear functionality -- instantiates all arrays
        e.clear();
        List<Group> exp = new ArrayList<Group>();
        List<Group> act = e.getStreamGroups();

        assertEquals(exp, act);

        act = e.getPushGroups();

        assertEquals(exp, act);

        // test setters - values
        Time testTime = new Time();
        String testString = "test";
        boolean testBool = false;
        double testDouble = 0.0;
        int testInt = 0;
        List<ImpromptuUser> testFriends = new ArrayList<ImpromptuUser>();
        List<Group> testGroups = new ArrayList<Group>();
        ImpromptuUser testFriend = ImpromptuUser.getUserById("mQMe4SHJNe");

        // run through setters, getters (pre save)

        // strings
        e.setTitle(testString);
        e.setDescription(testString);
        e.setFormattedAddress(testString);
        e.setType(testString);
        e.setLocationName(testString);

        assertEquals(e.getTitle(), testString);
        assertEquals(e.getDescription(), testString);
        assertEquals(e.getFormattedAddress(), testString);
        assertEquals(e.getType(), testString);
        assertEquals(e.getLocationName(), testString);


        // bools
        e.setPushed(testBool);
        e.setEventTimeMorning(testBool);

        assertEquals(e.getPushed(), testBool);
        assertEquals((boolean)e.getEventTimeMorning(), testBool);

        // times
        e.setEventTime(testTime);
        e.setCreationTime(testTime);

        assertEquals(e.getEventTime(), testTime);
        assertEquals(e.getCreationTime(), testTime);

        // doubles
        e.setLatitude(testDouble);
        e.setLongitude(testDouble);

        assertEquals(e.getLatitude(), testDouble);
        assertEquals(e.getLongitude(), testDouble);

        // ints
        e.setDurationHour(testInt);
        e.setDurationMinute(testInt);
        e.setSeekDuration(testInt);
        e.setSeekStart(testInt);

        assertEquals(e.getDurationHour(), testInt);
        assertEquals(e.getDurationMinute(), testInt);
        assertEquals(e.getSeekDuration(), testInt);
        assertEquals(e.getSeekStart(), testInt);

        // lists
        e.setAllFriends(testFriends);
        e.setAllGroups(testGroups);

        assertEquals(e.getAllFriends(), testFriends);
        assertEquals(e.getAllGroups(), testGroups);

        // impromptu User
        e.setOwner(testFriend);
        e.addPushFriend(testFriend);
        e.addStreamFriend(testFriend);
        e.addUserGoing(testFriend);

        testFriend.getObjectId();
        e.getOwner();
        e.getOwner().getObjectId();
        assertEquals(testFriend, e.getOwner());
        assertEquals(testFriend.getObjectId(), e.getOwner().getObjectId());
        assertEquals(e.getPushFriends().get(0), testFriend);
        assertEquals(e.getStreamFriends().get(0), testFriend);
        assertEquals(e.getUsersGoing().get(0), testFriend);


        e.persist();

        Thread.sleep(2000);

        // post-save getters--access parse
        assertEquals(e.getDescription(), testString);
        assertEquals(e.getTitle(), testString);
        assertEquals(e.getFormattedAddress(), testString);
        assertEquals(e.getType(), testString);
        assertEquals(e.getPushed(), testBool);
        assertEquals((boolean)e.getEventTimeMorning(), testBool);
        assertEquals(e.getEventTime(), testTime);
        assertEquals(e.getCreationTime(), testTime);
        assertEquals(e.getLatitude(), testDouble);
        assertEquals(e.getLongitude(), testDouble);
        assertEquals(e.getDurationHour(), testInt);
        assertEquals(e.getDurationMinute(), testInt);
        assertEquals(e.getSeekDuration(), testInt);
        assertEquals(e.getSeekStart(), testInt);
        assertEquals(e.getAllFriends(), testFriends);
        assertEquals(e.getAllGroups(), testGroups);
        assertEquals(e.getPushFriends().get(0), testFriend);
        assertEquals(e.getStreamFriends().get(0), testFriend);
        assertEquals(e.getUsersGoing().get(0), testFriend);


    }
}
