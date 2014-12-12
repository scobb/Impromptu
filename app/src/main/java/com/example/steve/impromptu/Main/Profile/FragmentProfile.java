package com.example.steve.impromptu.Main.Profile;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Entity.UpdateView;
import com.example.steve.impromptu.Main.AsyncTasks.AsyncTaskPopulateOwnedEvents;
import com.example.steve.impromptu.R;
import com.example.steve.impromptu.UI.ObservableScrollView;
import com.parse.FindCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.widget.ListView;

import com.example.steve.impromptu.Entity.Event;

import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Login.ActivityLogin;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.Main.FragmentEventDetail;
import com.example.steve.impromptu.R;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.games.GamesMetadata;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentProfile extends ListFragment{
    ListView eventsList;

    public class ProfileUpdateView extends UpdateView {
        @Override
        public void update(List<Event> events) {
            List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
            for (Event post : events) {
                aList.add(post.getHashMap());

            }

            if (getActivity() != null) {
                // If view is still active, initialize the adapter
                SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList,
                        R.layout.template_stream_event_item, from, to);


                // Setting the list adapter for the ListFragment
                eventsList.setAdapter(adapter);

                // Update the list adapter
                adapter.notifyDataSetChanged();
                Log.d("Impromptu", "Profile view updated.");
            }
        }
    }
    List<Event> posts;
    ImpromptuUser currentUser;
    // Keys used in HashMap
    private String[] from = {"picture", "user", "content", "date"};

    // Ids of views in listview layout
    private int[] to ={R.id.fragStream_imageView_picture, R.id.fragStream_textView_user,
            R.id.fragStream_textView_content, R.id.fragStream_textView_date};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Receive data passed in
        Bundle userData = getArguments();

        currentUser = ((ActivityMain)getActivity()).currentUser;

        // Get Views
        final View myInflatedView = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView nameView = (TextView) myInflatedView.findViewById(R.id.fragProfile_textView_name);
        final TextView emailView = (TextView) myInflatedView.findViewById(R.id.fragProfile_textView_email);
        final ImageView profileView = (ImageView) myInflatedView.findViewById(R.id.fragProfile_imageView_profilePic);

        eventsList = (ListView) myInflatedView.findViewById(android.R.id.list);

        final ImpromptuUser targetUser;
        // Get the user
        String ownerId = userData.getString("ownerId");
        if (ownerId.equals(currentUser.getObjectId())) {
            Log.d("Impromptu", "Looking at current user's profile.");
            targetUser = currentUser;
        } else if (currentUser.friendMap.containsKey(ownerId)) {
            Log.d("Impromptu", "User in map.");
            targetUser = currentUser.friendMap.get(ownerId);
        } else  {
            targetUser = ImpromptuUser.getUserById(userData.getString("ownerId"));
        }

        // Fill in the fields
        nameView.setText(targetUser.getName());
        emailView.setText(targetUser.getEmail());
        profileView.setImageBitmap(targetUser.getPicture());

        ParseQuery<Event> query = new ParseQuery<>("Event");
        query.whereEqualTo("owner", targetUser);
        // TODO - add some loading icon where events will be? Also, if we change this to only be the current user, we can cache these.
        // Initialize the adapter
        if (targetUser.ownedEventsHashList.isEmpty()) {
            // cache for later
            Log.d("Impromptu", "DAT list is empty");
            targetUser.getOwnedEvents();
        }
        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), targetUser.ownedEventsHashList,
                R.layout.template_stream_event_item, from, to);


        // Setting the list adapter for the ListFragment
        eventsList.setAdapter(adapter);

        // Update the list adapter
        adapter.notifyDataSetChanged();
        query.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> events, ParseException e) {
                if (e == null) {
                    posts = new ArrayList<>(events);
                    // Create the HashMap List
                    AsyncTaskPopulateOwnedEvents task = new AsyncTaskPopulateOwnedEvents();
                    task.setUpdateView(new ProfileUpdateView());
                    task.execute(posts);
//                    Iterator<Event> i = posts.iterator();
//                    HashMap<String, String> args = new HashMap<>();
//                    while (i.hasNext()) {
//                        Event event = i.next();
//                        long endMillis = event.getEventTime().getTime() + event.getDurationHour() * 3600 * 1000 + event.getDurationMinute() * 60 * 1000;
//                        long nowMillis = new Date().getTime();
//                        if (nowMillis > endMillis) {
//                            args.clear();
//                            args.put("eventId", event.getObjectId());
//                            ParseCloud.callFunctionInBackground("eventCleanup", args, null);
//                            i.remove();
//                        }
//                    }
//                    List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
//                    for (Event post : posts) {
//                        aList.add(post.getHashMap());
//
//                    }
//
//                    // Initialize the adapter
//                    SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList,
//                            R.layout.template_stream_event_item, from, to);
//
//
//                    // Setting the list adapter for the ListFragment
//                    eventsList.setAdapter(adapter);
//
//                    // Update the list adapter
//                    adapter.notifyDataSetChanged();

                } else {
                    // Error in query
                    e.printStackTrace();
                }

            }
        });


        return myInflatedView;
    }


    public void onListItemClick(ListView l, View v, int position, long id){

        // Get the selected event
        Event event = posts.get(position);

        // Get the selected user
        ImpromptuUser selectedUser = event.getOwner();

        // Bundle up the data getting passed
        Bundle eventData = new Bundle();

        eventData.putString("ownerKey", selectedUser.getObjectId());
        eventData.putString("eventKey", event.getObjectId());

        // Set up the fragment transaction
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        FragmentEventDetail fragment = new FragmentEventDetail();
        fragment.setArguments(eventData);
        transaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        transaction.addToBackStack(null).commit();
    }


}
