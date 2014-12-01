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
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.Profile.FragmentProfile;
import com.example.steve.impromptu.R;
import com.example.steve.impromptu.UI.ScrollableMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Stephen Arifin on 10/16/14.
 */
public class FragmentEventDetail extends Fragment {

    private static View myInflatedView;
    private ScrollView scrollView;
    private FragmentActivity myContext;

    private GoogleMap vMap;

    private ImpromptuUser owner;
    private Event event;
    private LinearLayout vOpenInGMaps;

    private static final LatLng defaultLocation = new LatLng(30.2864802, -97.74116620000001); //UT Austin ^___^

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
        scrollView = (ScrollView) myInflatedView.findViewById(R.id.fragEventDetail_scrollView);
        vOpenInGMaps = (LinearLayout) myInflatedView.findViewById(R.id.fragEventDetail_linearLayout_openInGMaps);

        //MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.fragEventDetail_location_map);

        // Replace map
        ScrollableMapFragment mf = (ScrollableMapFragment) myContext.getSupportFragmentManager().findFragmentById(R.id.fragEventDetail_location_map);
        vMap = mf.getMap();

        ((ScrollableMapFragment) myContext.getSupportFragmentManager().findFragmentById(R.id.fragEventDetail_location_map))
                .setListener(new ScrollableMapFragment.OnTouchListener() {

            @Override
            public void onTouch() {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        // Get the owner and event
        owner = ImpromptuUser.getUserById(eventData.getString("ownerKey"));
        event = Event.getEventById(eventData.getString("eventKey"));

        // Sets the data fields and picture
        titleTextView.setText(event.getTitle());
        ownerTextView.setText(owner.getName());
        descriptionTextView.setText(event.getDescription());
        profilePictureView.setImageBitmap(owner.getPicture());

        // Sets the map location
        vMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(event.getLatitude(), event.getLongitude()), 16.0f));

        // Places marker
        vMap.addMarker(new MarkerOptions().position(new LatLng(event.getLatitude(), event.getLongitude()))
            .title(event.getTitle()));

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



        return myInflatedView;

    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }
}
