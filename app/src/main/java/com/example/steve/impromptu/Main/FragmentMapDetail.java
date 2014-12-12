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
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.R;
import com.example.steve.impromptu.UI.ScrollableMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Sean on 12/11/2014.
 */
public class FragmentMapDetail extends Fragment {


    private TextView vTitle;
    private ImageView vBackButton;
    private GoogleMap vMap;
    private ScrollView vScrollView;

    private boolean markerDisplayed;
    private Event event;
    private Marker marker;

    private static View myInflatedView;
    private FragmentActivity myContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //inflate views:

        // Get Views
        if (myInflatedView != null) {
            ViewGroup parent = (ViewGroup) myInflatedView.getParent();
            if (parent != null)
                parent.removeView(myInflatedView);
        }
        try {
            myInflatedView = inflater.inflate(R.layout.fragment_map_detail, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }


        //get event from bundle:

        Bundle eventData = getArguments();
        String eventKey = eventData.getString("eventKey");
        event = Event.getEventById(eventKey);


        //get references to everything:

        vTitle = (TextView) myInflatedView.findViewById(R.id.fragMapDetail_textView_title);
        vScrollView = (ScrollView) myInflatedView.findViewById(R.id.fragMapDetail_scrollView);
        vBackButton = (ImageView) myInflatedView.findViewById(R.id.fragMapDetail_imageView_back);
        ScrollableMapFragment mf = (ScrollableMapFragment) myContext.getSupportFragmentManager().findFragmentById(R.id.fragMapDetail_map);
        vMap = mf.getMap();


        //set on-click listeners

        vBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Entered OnClick.", Toast.LENGTH_SHORT).show();

                getFragmentManager().popBackStack();
            }
        });

        mf.setListener(new ScrollableMapFragment.OnTouchListener() {

                    @Override
                    public void onTouch() {
                        vScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });


        //intitializations:

        if(event != null)
        {
            vTitle.setText(event.getTitle());
            vMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(event.getLatitude(), event.getLongitude()), 16.0f));
        }
        else {
            vTitle.setText("Error: No Event found:");
        }


        return myInflatedView;
    }












    private void removeMarker() {

        if(markerDisplayed) {

            if(marker != null) {
                marker.remove();
            }

            markerDisplayed = false;
        }
    }

    private void plotMarker() {

        if(!markerDisplayed && event != null) {
            marker = vMap.addMarker(new MarkerOptions().position(new LatLng(event.getLatitude(), event.getLongitude()))
                    .title(event.getTitle()).snippet(event.getDescription()));

            markerDisplayed = true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        removeMarker();
    }

    @Override
    public void onResume() {
        super.onResume();

        plotMarker();
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

}
