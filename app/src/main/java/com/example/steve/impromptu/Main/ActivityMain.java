package com.example.steve.impromptu.Main;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Entity.StreamPost;
import com.example.steve.impromptu.Login.ActivityLogin;
import com.example.steve.impromptu.Main.Compose.FragmentComposeLocation;
import com.example.steve.impromptu.Main.Compose.FragmentComposeMain;
import com.example.steve.impromptu.Main.Compose.FragmentComposeTime;
import com.example.steve.impromptu.R;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ActivityMain extends FragmentActivity implements FragmentComposeTime.OnComposeTimeFinishedListener, FragmentComposeMain.OnAttributeSelectedListener, FragmentComposeMain.OnComposeMainFinishedListener {

    Event newEvent;
    public Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shell_main);

        ParseObject.registerSubclass(StreamPost.class);
        ParseObject.registerSubclass(Event.class);
        Parse.initialize(this, "sP5YdlJxg1WiwfKgSXX4KdrgpZzAV5g69dV8ryY0", "houV8Brg8oIuBKSLheR7qAW4AJfGq1QZmH62Spgk");

        //Remove title bar
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        //Remove notification bar
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeMain fragment = new FragmentComposeMain();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        newEvent = new Event();
    }
    public void forwardToProfileFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentProfile fragment = new FragmentProfile();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void forwardToLoginActivity() {
        Intent intent = new Intent(ActivityMain.this, ActivityLogin.class);
        startActivity(intent);
        finish();
    }

    public void onDebugClicked(View v) {
//        ImpromptuUser currentUser = (ImpromptuUser)ParseUser.getCurrentUser();
//        Bitmap profilePic = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//        currentUser.setPicture(profilePic);
        ImpromptuUser currentUser = (ImpromptuUser)ParseUser.getCurrentUser();
        Bitmap profilePic = currentUser.getPicture();
        ImageView picView = (ImageView)findViewById(R.id.fragProfile_imageView_profilePic);
        picView.setImageBitmap(profilePic);

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
    public void onComposeTimeFinished() {

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

        }
        else {

            Toast.makeText(this, "cancel new event", Toast.LENGTH_SHORT).show();

        }
    }

    public static class LoginFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_login, container, false);
        }
    }

    public void stream (View view) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentStream fragment = new FragmentStream();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void map (View view) {
        Toast.makeText(this, "show map", Toast.LENGTH_SHORT).show();
    }

    public void invites (View view) {
        Toast.makeText(this, "show invites", Toast.LENGTH_SHORT).show();
    }

    public void friends (View view) {
        Toast.makeText(this, "show friends", Toast.LENGTH_SHORT).show();
    }

    public void profile (View view) {
        forwardToProfileFragment();
        //Toast.makeText(this, "show profile", Toast.LENGTH_SHORT).show();
    }

    public void compose (View view) {
        newEvent = new Event();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentComposeMain fragment = new FragmentComposeMain();
        fragmentTransaction.replace(R.id.activityMain_frameLayout_shell, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    public Event getNewEvent() {
        return newEvent;
    }
}
