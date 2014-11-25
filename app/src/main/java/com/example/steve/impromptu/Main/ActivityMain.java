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
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.FriendRequest;
import com.example.steve.impromptu.Entity.Group;
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
import com.example.steve.impromptu.R;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ActivityMain extends FragmentActivity implements FragmentComposeTime.OnComposeTimeFinishedListener, FragmentComposeMain.OnAttributeSelectedListener,
        FragmentComposeMain.OnComposeMainFinishedListener, FragmentComposeLocation.OnComposeLocationFinishedListener, FragmentComposePush.OnComposePushFinishedListener, FragmentComposePush.OnComposePushChooseGroupsListener
        , FragmentComposePushGroups.OnComposePushChooseGroupsFinishedListener, FragmentComposeStream.OnComposeStreamChooseGroupsListener, FragmentComposeStream.OnComposeStreamFinishedListener, FragmentComposeStreamGroups.OnComposeStreamChooseGroupsFinishedListener, FragmentComposeType.OnComposeTypeFinishedListener {


    //TODO: remove
    static public Boolean firstTime = true;
    Event composeEvent;
    public Dialog progressDialog;


    // Filters
    private static Hashtable<String, Boolean> filters = new Hashtable<String, Boolean>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_shell_main);

        // TODO Might have to move this to a different part of the Activity lifecycle - Arifin
        // Default filters
        filters.put("Drinking", true);
        filters.put("Eating", true);
        filters.put("Sports", true);
        filters.put("Studying", true);
        filters.put("TV", true);
        filters.put("Working Out", true);


        // this block might be redundant because we do it in ActivityMain. not sure...
        ParseUser.registerSubclass(ImpromptuUser.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(FriendRequest.class);
        Parse.initialize(this, "sP5YdlJxg1WiwfKgSXX4KdrgpZzAV5g69dV8ryY0", "houV8Brg8oIuBKSLheR7qAW4AJfGq1QZmH62Spgk");

        //Remove title bar
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        //Remove notification bar
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentStream fragment = new FragmentStream();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        fragmentTransaction.commit();
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
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void forwardToLoginActivity() {
        Intent intent = new Intent(ActivityMain.this, ActivityLogin.class);
        startActivity(intent);
        finish();
    }

    public void debug(View v) {
//        ImpromptuUser currentUser = (ImpromptuUser)ParseUser.getCurrentUser();
//        Bitmap profilePic = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//        currentUser.setPicture(profilePic);
        ImpromptuUser currentUser = (ImpromptuUser) ParseUser.getCurrentUser();
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

        ArrayList<ImpromptuUser> friends = currentUser.getFacebookFriends();
        Log.d("Impromptu", "Friends...");
        for (ImpromptuUser friend : friends) {
            Log.d("Impromptu", friend.getName());
        }
        List<ImpromptuUser> results = ImpromptuUser.getUserByName("bob");
        Log.d("Impromptu", "num results: " + results.size());
        for (ImpromptuUser user : results) {
            Log.d("Impromptu", user.getName());
        }
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
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onComposeTimeFinished() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeMain fragment = new FragmentComposeMain();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onComposeLocationFinished() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeMain fragment = new FragmentComposeMain();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void OnAttributeSelected(String attribute) {

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
                break;
            case "stream":
                frag = new FragmentComposeStream();
                break;
            default:
                frag = new FragmentComposeMain();
        }

        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, frag).addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onComposeMainFinished(Boolean create) {
        if (create) {

            Toast.makeText(this, "create new event", Toast.LENGTH_SHORT).show();

            Event myEvent = getComposeEvent();
            myEvent.persist();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            FragmentStream fragment = new FragmentStream();
            fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
            fragmentTransaction.commit();

        } else {

            Toast.makeText(this, "cancel new event", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onComposePushFinished() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeMain fragment = new FragmentComposeMain();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onComposePushChooseGroups() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposePushGroups fragment = new FragmentComposePushGroups();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onComposePushChooseGroupsFinished() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposePush fragment = new FragmentComposePush();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onComposeStreamChooseGroupsFinished() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeStream fragment = new FragmentComposeStream();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onComposeStreamChooseGroups() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeStreamGroups fragment = new FragmentComposeStreamGroups();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onComposeStreamFinished() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeMain fragment = new FragmentComposeMain();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
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
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentStream fragment = new FragmentStream();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void map(View view) {
        Toast.makeText(this, "show map", Toast.LENGTH_SHORT).show();
    }

    public void invites(View view) {
        Toast.makeText(this, "show invites", Toast.LENGTH_SHORT).show();
    }

    public void friends(View view) {
        Toast.makeText(this, "show friends", Toast.LENGTH_SHORT).show();
    }

    public void profile(View view) {
        forwardToProfileFragment();
        //Toast.makeText(this, "show profile", Toast.LENGTH_SHORT).show();
    }

    public void compose(View view) {
        composeEvent = new Event();
        composeEvent.clear();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeMain fragment = new FragmentComposeMain();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void filter(View view){
        DialogFragment filterFragment = FragmentFilterDialog.newInstance();
        filterFragment.show(getFragmentManager(), "dialog");
    }

    public Event getComposeEvent() {
        return composeEvent;
    }

    public static Hashtable<String, Boolean> getFiltersMap(){
        return filters;
    }

    public static void setFilter(String type, boolean state){

    }
}
