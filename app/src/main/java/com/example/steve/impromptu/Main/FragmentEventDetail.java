package com.example.steve.impromptu.Main;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Login.ActivityLogin;
import com.example.steve.impromptu.R;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.games.GamesMetadata;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Stephen Arifin on 10/16/14.
 */
public class FragmentEventDetail extends Fragment {

    private static View myInflatedView;

    private GoogleMap vMap;

    private ImpromptuUser owner;

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


        RelativeLayout ownerLayout = (RelativeLayout) myInflatedView.findViewById(R.id.fragEventDetail_relativeLayout_owner);
        TextView titleTextView = (TextView) myInflatedView.findViewById(R.id.fragEventDetail_textView_title);
        TextView ownerTextView = (TextView) myInflatedView.findViewById(R.id.fragEventDetail_textView_owner);
//        ProfilePictureView profilePictureView = (ProfilePictureView) myInflatedView.findViewById(R.id.fragProfile_imageView_profilePic);
        ImageView profilePictureView = (ImageView) myInflatedView.findViewById(R.id.fragProfile_imageView_profilePic);


        MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.fragEventDetail_location_map);
        vMap = mf.getMap();

        // Get the owner
        owner = ImpromptuUser.getUserById(eventData.getString("ownerKey"));


        // not sure what this does yet
        int hasGooglePlay = GooglePlayServicesUtil.isGooglePlayServicesAvailable(myInflatedView.getContext());


        // Sets the data fields and picture
        titleTextView.setText(eventData.getString("title"));
        ownerTextView.setText(owner.getName());
        profilePictureView.setImageBitmap(owner.getPicture());

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
                transaction.commit();
            }
        });



        return myInflatedView;

    }

}
