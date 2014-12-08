package com.example.steve.impromptu.Main;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.FriendRequest;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterPeopleAttending;
import com.example.steve.impromptu.Main.Profile.FragmentProfile;
import com.example.steve.impromptu.R;
import com.example.steve.impromptu.UI.ObservableScrollView;
import com.example.steve.impromptu.UI.ScrollableMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephen Arifin on 10/16/14.
 */
public class FragmentEventDetail extends Fragment{

    private ObservableScrollView mObservableScrollView;

    private static View myInflatedView;
    private FragmentActivity myContext;
    private ImageView vBack;

    ScrollableMapFragment mf;
    private GoogleMap vMap;
    private boolean mapVisibility = false;

    private ImpromptuUser owner;
    private Event event;
    private Marker marker;
    private LinearLayout vOpenInGMaps;

    private static final LatLng defaultLocation = new LatLng(30.2864802, -97.74116620000001); //UT Austin ^___^

    // Variables for people attending
    private ArrayAdapterPeopleAttending userAdapter = null;
    private ListView userAttendingList;
    private ArrayList<ImpromptuUser> usersAttending = new ArrayList<>();
    private boolean currentUserAttending;
    private ImpromptuUser currentUser;
    TextView joinTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Receive data passed in
        Bundle eventData = getArguments();

        // Get Views
        if (myInflatedView != null) {
            ViewGroup parent = (ViewGroup) myInflatedView.getParent();
            if (parent != null)
                parent.removeView(myInflatedView);
        }
        try {
            myInflatedView = inflater.inflate(R.layout.fragment_event_detail, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }


        LinearLayout ownerLayout = (LinearLayout) myInflatedView.findViewById(R.id.fragEventDetail_linearLayout_owner);
        TextView titleTextView = (TextView) myInflatedView.findViewById(R.id.fragEventDetail_textView_title);
        TextView ownerTextView = (TextView) myInflatedView.findViewById(R.id.fragEventDetail_textView_owner);
        TextView descriptionTextView = (TextView) myInflatedView.findViewById(R.id.fragEventDetail_textView_description);
        ImageView profilePictureView = (ImageView) myInflatedView.findViewById(R.id.fragEventDetail_imageView_profilePic);
        vBack = (ImageView) myInflatedView.findViewById(R.id.fragEventDetail_imageView_back);

        vOpenInGMaps = (LinearLayout) myInflatedView.findViewById(R.id.fragEventDetail_linearLayout_openInGMaps);

        userAttendingList = (ListView) myInflatedView.findViewById(R.id.fragEventDetail_listView_peopleAttending);
        LinearLayout joinLayout = (LinearLayout) myInflatedView.findViewById(R.id.fragEventDetail_linearLayout_join);
        joinTextView = (TextView) myInflatedView.findViewById(R.id.fragEventDetail_textView_join);

        TextView timeTextView = (TextView) myInflatedView.findViewById(R.id.fragEventDetail_textView_time);
        TextView locationTextView = (TextView) myInflatedView.findViewById(R.id.fragEventDetail_textView_location);
        LinearLayout locationLayout = (LinearLayout) myInflatedView.findViewById(R.id.fragEventDetail_linearLayout_location);

        // Set up the quick return layout bar
        mObservableScrollView = (ObservableScrollView) myInflatedView.findViewById(R.id.scroll_view);


        currentUser = ((ActivityMain)getActivity()).currentUser;
        // Get the owner and event
        String ownerKey = eventData.getString("ownerKey");
        String eventKey = eventData.getString("eventKey");
        Log.d("Impromptu", "ownerKey: " + ownerKey);
        if (currentUser.friendMap.containsKey(ownerKey)) {
            Log.d("Impromptu", "Found the owner.");
            owner = currentUser.friendMap.get(ownerKey);
        } else {
            Log.d("Impromptu", "Didn't find the owner");
            owner = ImpromptuUser.getUserById(ownerKey);
        }

        if (currentUser.eventMap.containsKey(eventKey)) {
            event = currentUser.eventMap.get(eventKey);
        } else {
            event = Event.getEventById(eventKey);
        }

        // Sets the data fields and picture
        titleTextView.setText(event.getTitle());
        ownerTextView.setText(owner.getName());
        descriptionTextView.setText(event.getDescription());
        profilePictureView.setImageBitmap(owner.getPicture());
        Log.d("Impromptu", "Got owner's pic.");
        timeTextView.setText(event.getEventDate().toString());
        locationTextView.setText(event.getLocationName());

        // Is the current user attending the event?
        currentUserAttending = event.userIsGoing(currentUser);

        // Initialize the join button
        if(currentUserAttending){
            joinTextView.setText("Leave");
        }
        else{
            joinTextView.setText("Join");
        }


        initiateMap();
        refreshPeopleAttendingList();


        ownerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Bundle up the data getting passed
                Bundle userData = new Bundle();
                userData.putString("ownerId", owner.getObjectId());


                // Set up the fragment transaction
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                FragmentProfile fragment = new FragmentProfile();
                fragment.setArguments(userData);
                transaction.replace(R.id.activityMain_frameLayout_shell, fragment);
                transaction.addToBackStack(null).commit();
            }
        });

        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mapVisibility){
                    // if not visibile
                    // Sets the map location
                    vMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(event.getLatitude(), event.getLongitude()), 16.0f));

                    // Show map
                    mf.getView().setVisibility(View.VISIBLE);
                    vOpenInGMaps.setVisibility(View.VISIBLE);

                    mapVisibility = true;
                }
                else{
                    // Hide map
                    mf.getView().setVisibility(View.GONE);
                    vOpenInGMaps.setVisibility(View.GONE);
                    mapVisibility = false;
                }

            }
        });

        vOpenInGMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: implement

                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                String locationProvider = LocationManager.NETWORK_PROVIDER;
                //Or use LocationManager.GPS_PROVIDER
                Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
                LatLng myLoc;

                if(lastKnownLocation == null)
                {
                    myLoc = defaultLocation;
                }
                else
                {
                    myLoc = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                }

                String url = "http://maps.google.com/maps?saddr=" + myLoc.latitude + "," + myLoc.longitude
                        + "&daddr=" + event.getLatitude() + "," + event.getLongitude() + "&mode=driving";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });

        vBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // TODO implement!!!!

            }
        });

        userAttendingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // Bundle up the data getting passed
                Bundle userData = new Bundle();

                // TODO need to make this check if it is the current user
                userData.putString("ownerId", usersAttending.get(i).getObjectId());


                // Set up the fragment transaction
                FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
                FragmentProfile fragment = new FragmentProfile();
                fragment.setArguments(userData);
                transaction.replace(R.id.activityMain_frameLayout_shell, fragment);
                transaction.addToBackStack(null).commit();

            }
        });



        joinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImpromptuUser currentUser = (ImpromptuUser)ParseUser.getCurrentUser();

                if(currentUserAttending){
                    event.removeUserGoing(currentUser);
                    refreshPeopleAttendingList();
                    joinTextView.setText("Join");
                    currentUserAttending = false;
                }
                else{
                    event.addUserGoing(currentUser);
                    refreshPeopleAttendingList();
                    joinTextView.setText("Leave");
                    currentUserAttending = true;
                }

            }
        });


        return myInflatedView;

    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }



    public void initiateMap(){

        // Replace map
        mf = (ScrollableMapFragment) myContext.getSupportFragmentManager()
                .findFragmentById(R.id.fragEventDetail_location_map);
        vMap = mf.getMap();

        mf.getView().setVisibility(View.GONE);

        ((ScrollableMapFragment) myContext.getSupportFragmentManager().findFragmentById(R.id.fragEventDetail_location_map))
                .setListener(new ScrollableMapFragment.OnTouchListener() {

                    @Override
                    public void onTouch() {
                        mObservableScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();

        marker.remove();
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean flag = true;

            // Places marker
            marker = vMap.addMarker(new MarkerOptions().position(new LatLng(event.getLatitude(), event.getLongitude()))
                    .title(event.getTitle()).snippet(event.getDescription()));
    }

    private void refreshPeopleAttendingList(){
        // Set up the people attending the event
        Log.d("Impromptu", "Getting users going");
        List<ImpromptuUser> users = event.getUsersGoing();
        Log.d("Impromptu", "Starting fetch all if needed");
        // Parse query shit
        ParseObject.fetchAllIfNeededInBackground(users, new FindCallback<ImpromptuUser>() {

            @Override
            public void done(List<ImpromptuUser> users, ParseException e) {
                if (e == null) {
                    usersAttending = new ArrayList<>();
                    for (ImpromptuUser user: users) {
                        if (currentUser.friendMap.containsKey(user.getObjectId())){
                            // get version with picture already fetched.
                            Log.d("Impromptu", "attendee is in the map.");
                            usersAttending.add(currentUser.friendMap.get(user.getObjectId()));
                        } else {
                            Log.d("Impromptu", "attendee not in the map.");
                            usersAttending.add(user);
                        }
                    }
                    usersAttending = new ArrayList<ImpromptuUser>(users);
                    userAdapter = new ArrayAdapterPeopleAttending(getActivity(),
                            R.layout.template_friend_attending_item, users);
                    userAttendingList.setAdapter(userAdapter);

                    Log.d("Impromptu", "notifying that data set has changed");
                    // Update the list adapter
                    userAdapter.notifyDataSetChanged();

                } else {
                    // Error in query
                    e.printStackTrace();
                }
            }
        });
        Log.d("Impromptu", "Done with this method.");
    }

}
