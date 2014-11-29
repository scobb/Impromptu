package com.example.steve.impromptu.Main;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.R;
import com.example.steve.impromptu.UI.ScrollableMapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentMap extends Fragment {

    private FragmentActivity myContext;
    private List<Event> posts;
    private LinearLayout listStream;
    private GoogleMap vMap;
    private ScrollView vScrollView;

    private static View myInflatedView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Receive data passed in
        //Bundle eventData = getArguments();

        // Get Views
        if (myInflatedView != null) {
            ViewGroup parent = (ViewGroup) myInflatedView.getParent();
            if (parent != null)
                parent.removeView(myInflatedView);
        }
        try {
            myInflatedView = inflater.inflate(R.layout.fragment_map, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        vScrollView = (ScrollView) myInflatedView.findViewById(R.id.fragMap_scrollView);

        ScrollableMapFragment mf = (ScrollableMapFragment) myContext.getSupportFragmentManager().findFragmentById(R.id.fragEventDetail_location_map);
        vMap = mf.getMap();

        ((ScrollableMapFragment) myContext.getSupportFragmentManager().findFragmentById(R.id.fragEventDetail_location_map))
                .setListener(new ScrollableMapFragment.OnTouchListener() {

                    @Override
                    public void onTouch() {
                        vScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });


        // Gets query for the event streams
        ImpromptuUser currentUser = (ImpromptuUser) ParseUser.getCurrentUser();
        List<Event> events = currentUser.getStreamEvents();
        listStream = (LinearLayout) myInflatedView.findViewById(R.id.fragMap_linearLayout_listStream);

        listStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: this might not be the best way. also, move this to a green bar on the bottom like FragmentStream
                getFragmentManager().popBackStackImmediate();
                //FragmentMap nextFrag = new FragmentMap();
                //getFragmentManager().beginTransaction().replace(R.id.activityMain_frameLayout_shell, nextFrag).addToBackStack(null).commit();
            }
        });



        ParseObject.fetchAllIfNeededInBackground(events, new FindCallback<Event>() {

            @Override
            public void done(List<Event> postsObjects, ParseException e) {
                if (e == null) {
                    List<Event> allPosts = new ArrayList<Event>(postsObjects);

                    clearPosts();


                    // Create the HashMap List
                    List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
                    for (Event post : allPosts) {

                        Log.d("Impromptu", post.getObjectId() + " " + post.getType());

                        // Check for the filters
                        if (ActivityMain.getFiltersMap().get(post.getType()) != null) {
                            if (ActivityMain.getFiltersMap().get(post.getType())) {
                                posts.add(post);
                            }
                        }
                    }

                    addMarkers();

                } else {
                    // Error in query
                    e.printStackTrace();
                }
            }
        });

        return myInflatedView;
    }

    private void clearPosts() {

        clearMarkers();

        if(posts != null) {
            posts.clear();
        }
        else {
            posts = new ArrayList<Event>();
        }
    }

    private void addMarkers() {
        clearMarkers();

        if(posts != null) {

        }
    }

    private void clearMarkers() {
        //TODO: implement
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }
}
