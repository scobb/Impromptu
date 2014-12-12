package com.example.steve.impromptu.Main;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Login.ActivityLogin;
import com.example.steve.impromptu.Main.Compose.FragmentComposeLocation;
import com.example.steve.impromptu.Main.Compose.FragmentComposeMain;
import com.example.steve.impromptu.Main.Compose.FragmentComposePush;
import com.example.steve.impromptu.Main.Compose.FragmentComposePushGroups;
import com.example.steve.impromptu.Main.Compose.FragmentComposeStream;
import com.example.steve.impromptu.Main.Compose.FragmentComposeStreamGroups;
import com.example.steve.impromptu.Main.Compose.FragmentComposeTime;
import com.example.steve.impromptu.Main.Compose.FragmentComposeType;
import com.example.steve.impromptu.Main.Friends.FragmentFriendsList;
import com.example.steve.impromptu.Main.Groups.FragmentGroupsList;
import com.example.steve.impromptu.Main.Profile.FragmentProfile;
import com.example.steve.impromptu.R;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Hashtable;

public class ActivityMain extends FragmentActivity implements FragmentComposeTime.OnComposeTimeFinishedListener, FragmentComposeMain.OnAttributeSelectedListener,
        FragmentComposeMain.OnComposeMainFinishedListener, FragmentComposeLocation.OnComposeLocationFinishedListener, FragmentComposePush.OnComposePushFinishedListener, FragmentComposePush.OnComposePushChooseGroupsListener
        , FragmentComposePushGroups.OnComposePushChooseGroupsFinishedListener, FragmentComposeStream.OnComposeStreamChooseGroupsListener, FragmentComposeStream.OnComposeStreamFinishedListener, FragmentComposeStreamGroups.OnComposeStreamChooseGroupsFinishedListener, FragmentComposeType.OnComposeTypeFinishedListener, FragmentEventDetail.OnBackToStreamListener {


    Event composeEvent;
    public ImpromptuUser currentUser;
    public Dialog progressDialog;

    String locationWithinApp = "Stream";

    LinearLayout vTopMenu;
    LinearLayout vBottomMenu;
    TextView vLocationWithinApp;
    LinearLayout vProfile;
    LinearLayout vCompose;
    LinearLayout vStream;
    LinearLayout vFriends;
    LinearLayout vGroups;
    LinearLayout vMap;

    // Filters
    private static Hashtable<String, Boolean> filters = new Hashtable<String, Boolean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = (ImpromptuUser)ImpromptuUser.getCurrentUser();
        currentUser.populateGroupsInBackground();
        currentUser.populatePendingToRequestsInBackground();
        currentUser.populateFriendsInBackground();
        currentUser.populateFacebookFriendsInBackground();
        currentUser.getOwnedEvents();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_shell_main);

        vProfile = (LinearLayout) findViewById(R.id.activityMain_linearLayout_profile);
        vCompose = (LinearLayout) findViewById(R.id.activityMain_linearLayout_compose);
        vStream = (LinearLayout) findViewById(R.id.activityMain_linearLayout_stream);
        vFriends = (LinearLayout) findViewById(R.id.activityMain_linearLayout_friends);
        vGroups = (LinearLayout) findViewById(R.id.activityMain_linearLayout_groups);
        vMap = (LinearLayout) findViewById(R.id.activityMain_linearLayout_map);
        vTopMenu = (LinearLayout) findViewById(R.id.activityMain_linearLayout_topMenu);
        vBottomMenu = (LinearLayout) findViewById(R.id.activityMain_linearLayout_bottomMenu);
        vLocationWithinApp = (TextView) findViewById(R.id.activityMain_textView_locationWithinApp);

        vLocationWithinApp.setText(locationWithinApp);

        // TODO Might have to move this to a different part of the Activity lifecycle - Arifin
        // Default filters
        filters.put("Drinking", true);
        filters.put("Eating", true);
        filters.put("Sports", true);
        filters.put("Studying", true);
        filters.put("TV", true);
        filters.put("Working Out", true);

        setHighlightedButton("Stream");
        updateLocationWithinApp("Stream");

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        registerForPush();
        ParseInstallation inst = ParseInstallation.getCurrentInstallation();
        inst.put("user", ImpromptuUser.getCurrentUser());
        inst.saveInBackground();

        FragmentStream fragment = new FragmentStream();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();
    }

    public void registerForPush() {
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }


    public void forwardToProfileFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ImpromptuUser currentUser = (ImpromptuUser) ParseUser.getCurrentUser();

        // Bundle up the data getting passed
        Bundle userData = new Bundle();
        userData.putBoolean("currentuser", true);
        userData.putString("ownerId", currentUser.getObjectId());

        FragmentProfile fragment = new FragmentProfile();
        fragment.setArguments(userData);
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();

    }

    public void forwardToLoginActivity() {
        Intent intent = new Intent(ActivityMain.this, ActivityLogin.class);
        startActivity(intent);
        finish();
    }

    public void debug(View v) {

        try {
            ImpromptuUser.test();
            Log.d("Impromptu", "ImpromptuUser tests passed!");
        } catch (Exception e) {
            Log.d("Impromptu", "ImpromptuUser tests failed!");
            e.printStackTrace();
        }

//        ImpromptuUser currentUser = (ImpromptuUser)ParseUser.getCurrentUser();
//        Bitmap profilePic = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//        currentUser.setPicture(profilePic);
        Log.d("Impromptu", "In debug.");
        ImpromptuUser currentUser = (ImpromptuUser) ParseUser.getCurrentUser();
        ImpromptuUser lala = ImpromptuUser.getUserById("CEEe8UEHBx");
        ImpromptuUser stephen = ImpromptuUser.getUserById("mQMe4SHJNe");
        ImpromptuUser jon = ImpromptuUser.getUserById("tqaTgngvn2");
//        currentUser.createFriendRequest(stephen);
//        currentUser.createFriendRequest(jon);
//        List<Event> myOwnedEvents = currentUser.getOwnedEvents();
//        for (Event e: myOwnedEvents) {
//            Log.d("Impromptu", e.getTitle());
//        }
        currentUser.destroyFriendship(lala);
//        Event e = new Event();
//        e.getStreamFriends();
//        e.addStreamFriend(currentUser);
//        e.addPushFriend(currentUser);
//        e.getPushFriends();
//        e.removeStreamFriend(currentUser);
//        e.removePushFriend(currentUser);
//        e.removePushFriend(currentUser);
//        currentUser.clearFriends();

//        Bitmap profilePic = currentUser.getPicture();
//        ImageView picView = (ImageView)findViewById(R.id.fragProfile_imageView_profilePic);
//        picView.setImageBitmap(profilePic);
//
//        ImpromptuUser lala = ImpromptuUser.getUserById("CEEe8UEHBx");
//        if (lala == null) {
//            Log.d("Impromptu", "lala is null");
//        }
//        else {
//            Log.d("Impromptu", "lala's username: " + lala.getName());
//        }
//        currentUser.addFriend(lala);
//        List<ImpromptuUser> friends = currentUser.getFriends();
//        for (ImpromptuUser friend: friends) {
//            Log.d("Impromptu", "Friend: " + friend.getName());
//        }
//        currentUser.persist();

//        ArrayList<ImpromptuUser> friends = currentUser.getFacebookFriends();
//        Log.d("Impromptu", "Friends...");
//        for (ImpromptuUser friend : friends) {
//            Log.d("Impromptu", friend.getName());
//        }
//        List<ImpromptuUser> results = ImpromptuUser.getUserByName("bob");
//        Log.d("Impromptu", "num results: " + results.size());
//        for (ImpromptuUser user : results) {
//            Log.d("Impromptu", user.getName());
//        }
//        Event event = new Event();
//        event.test();
//        event.persist();
//        currentUser.clearStreamEvents();
//        currentUser.addStreamEvent(event);
//        currentUser.persist();
//        ImageView pic = (ImageView)v.findViewById(R.id.fragProfile_imageView_pic);
//        Bitmap bmp = currentUser.getPicture();
//        if (bmp != null) {
//            pic.setImageBitmap(bmp);
//        }

//        for (Event innerEvent: currentUser.getStreamEvents()){
//            Log.d("Impromptu", "Event: " + innerEvent.getDescription());
//        }
//
//        Group group = new Group("moreAwesomeGroup");
//        group.add(lala);
//        group.add(lala);
//        group.remove(lala);
//        group.add(lala);
//        TreeSet<ImpromptuUser> list = group.getFriendsInGroup();
//        for (ImpromptuUser friend: list) {
//            Log.d("Impromptu", "Friend in group: " + friend.getName());
//        }
//        group.persist();
//        currentUser.addGroup(group);
//        currentUser.persist();
//        for (Group innerGroup: currentUser.getGroups()) {
//            Log.d("Impromptu", "Group name: " + innerGroup.getGroupName());
//
//            for (ImpromptuUser user: innerGroup.getFriendsInGroup()){
//                Log.d("Impromptu", "Member in group: " + user.getName());
//            }
//        }
        //ImpromptuUser currentUser = (ImpromptuUser)ParseUser.getCurrentUser();
//        group = currentUser.getGroup("moreAwesomeGroup");
//        Log.d("Impromptu", group.getGroupName());
//        for (ImpromptuUser user: group.getFriendsInGroup()){
//            Log.d("Impromptu", "user in " + group.getGroupName() +": " + user.getName());
//        }
    }


    public void onLogoutClicked(View v) {
        ParseUser.logOut();
        com.facebook.Session fbs = com.facebook.Session.getActiveSession();
        if (fbs == null) {
            fbs = new com.facebook.Session(ActivityMain.this);
            com.facebook.Session.setActiveSession(fbs);
        }
        fbs.closeAndClearTokenInformation();

        forwardToLoginActivity();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.impromptu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onComposeTypeFinished() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeMain fragment = new FragmentComposeMain();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onComposeTimeFinished() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeMain fragment = new FragmentComposeMain();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onComposeLocationFinished() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeMain fragment = new FragmentComposeMain();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void OnAttributeSelected(String attribute) {

//        InputMethodManager imm = (InputMethodManager) this.getSystemService(Service.INPUT_METHOD_SERVICE);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment frag;

        switch (attribute) {
            case "time":
                frag = new FragmentComposeTime();
                break;
            case "location":
                frag = new FragmentComposeLocation();
                break;
            case "push":
                frag = new FragmentComposePush();
                break;
            case "type":
                frag = new FragmentComposeType();
                // TODO: hide keyboard for convenience
//                imm.hideSoftInputFromWindow(, 0);
                break;
            case "stream":
                frag = new FragmentComposeStream();
                break;
            default:
                frag = new FragmentComposeMain();
        }

        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, frag);
        fragmentTransaction.commit();

    }

    @Override
    public void onComposeMainFinished(Boolean create) {
        if (create) {

//            Toast.makeText(this, "create new event", Toast.LENGTH_SHORT).show();

            Event myEvent = getComposeEvent();
            myEvent.addUserGoing((ImpromptuUser) ImpromptuUser.getCurrentUser());
            myEvent.persist();

        }

        updateLocationWithinApp("Stream");
        setHighlightedButton("Stream");

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentStream fragment = new FragmentStream();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onComposePushFinished() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeMain fragment = new FragmentComposeMain();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onComposePushChooseGroups() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposePushGroups fragment = new FragmentComposePushGroups();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onComposePushChooseGroupsFinished() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposePush fragment = new FragmentComposePush();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onComposeStreamChooseGroupsFinished() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeStream fragment = new FragmentComposeStream();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onComposeStreamChooseGroups() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeStreamGroups fragment = new FragmentComposeStreamGroups();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onComposeStreamFinished() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeMain fragment = new FragmentComposeMain();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void OnBackToStream() {
        // TODO: maybe just pop fragment off the backstack?
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;

        if (locationWithinApp.equals("Stream")) {
            fragment = new FragmentStream();
        }
        else if (locationWithinApp.equals("Map")) {
            fragment = new FragmentMap();
        }
        else {
            // default return location
            fragment = new FragmentStream();
        }
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();
    }

    public static class LoginFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_login, container, false);
        }
    }

    public void stream(View view) {
        setHighlightedButton("Stream");
        updateLocationWithinApp("Stream");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentStream fragment = new FragmentStream();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();
    }

//    public void map(View view) {
//        Toast.makeText(this, "show map", Toast.LENGTH_SHORT).show();
//    }

    public void map(View view) {
        setHighlightedButton("Map");
        updateLocationWithinApp("Map");

        FragmentMap nextFrag = new FragmentMap();
        getFragmentManager().beginTransaction().replace(R.id.activityMain_frameLayout_shell, nextFrag).commit();
    }

    public void friends(View view) {
        setHighlightedButton("Friends");
        updateLocationWithinApp("Friends");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentFriendsList fragment = new FragmentFriendsList();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();
    }

    public void groups(View view) {
        setHighlightedButton("Groups");
        updateLocationWithinApp("Groups");
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentGroupsList fragment = new FragmentGroupsList();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();
    }

    public void profile(View view) {
        //(view);

        setHighlightedButton("Profile");
        updateLocationWithinApp("Profile");
        forwardToProfileFragment();
        //Toast.makeText(this, "show profile", Toast.LENGTH_SHORT).show();
    }

    public void compose(View view) {
        setHighlightedButton("Compose");
        updateLocationWithinApp("Compose");
        ImpromptuUser currentUser = (ImpromptuUser) ImpromptuUser.getCurrentUser();

        composeEvent = new Event();
        composeEvent.clear();

        composeEvent.setAllFriends(currentUser.getFriends());
        composeEvent.setAllGroups(currentUser.getGroups());

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeMain fragment = new FragmentComposeMain();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        fragmentTransaction.commit();
    }

    public void filter(View view) {
        DialogFragment filterFragment = FragmentFilterDialog.newInstance();
        filterFragment.show(getFragmentManager(), "dialog");
    }


    public Event getComposeEvent() {
        return composeEvent;
    }

    public static Hashtable<String, Boolean> getFiltersMap() {
        return filters;
    }

    public static void updateFilters(Hashtable<String, Boolean> updatedMap) {
        filters = new Hashtable<String, Boolean>(updatedMap);
    }

    public void updateLocationWithinApp(String newLocationWithinApp) {
        locationWithinApp = newLocationWithinApp;
        vLocationWithinApp.setText(locationWithinApp);
    }

    public void setHighlightedButton(String button) {

        vProfile.setBackgroundResource(R.color.transparent);
        vCompose.setBackgroundResource(R.color.transparent);
        vStream.setBackgroundResource(R.color.transparent);
        vFriends.setBackgroundResource(R.color.transparent);
        vGroups.setBackgroundResource(R.color.transparent);
        vMap.setBackgroundResource(R.color.transparent);

        switch (button) {
            case "Profile":
                vProfile.setBackgroundResource(R.color.impromptu_complementary_green);
                break;
            case "Compose":
                vCompose.setBackgroundResource(R.color.impromptu_complementary_green);
                break;
            case "Stream":
                vStream.setBackgroundResource(R.color.impromptu_complementary_green);
                break;
            case "Friends":
                vFriends.setBackgroundResource(R.color.impromptu_complementary_green);
                break;
            case "Groups":
                vGroups.setBackgroundResource(R.color.impromptu_complementary_green);
                break;
            case "Map":
                vMap.setBackgroundResource(R.color.impromptu_complementary_green);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
        } else {
//            super.onBackPressed();
        }
    }
}
