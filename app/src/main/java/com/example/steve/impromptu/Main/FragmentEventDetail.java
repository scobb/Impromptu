package com.example.steve.impromptu.Main;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    private GoogleMap vMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Receive data passed in
        Bundle eventData = getArguments();

        // Get Views
        View myInflatedView = inflater.inflate(R.layout.fragment_event_detail, container, false);

        TextView titleTextView = (TextView) myInflatedView.findViewById(R.id.fragEventDetail_textView_title);
        TextView ownerTextView = (TextView) myInflatedView.findViewById(R.id.fragEventDetail_textView_owner);
        ProfilePictureView profilePictureView = (ProfilePictureView) myInflatedView.findViewById(R.id.fragProfile_imageView_profilePic);

        MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.fragEventDetail_location_map);
        vMap = mf.getMap();

        // not sure what this does yet
        int hasGooglePlay = GooglePlayServicesUtil.isGooglePlayServicesAvailable(myInflatedView.getContext());



        titleTextView.setText(eventData.getString("title"));
        ownerTextView.setText(eventData.getString("owner"));



        return myInflatedView;

    }

}
