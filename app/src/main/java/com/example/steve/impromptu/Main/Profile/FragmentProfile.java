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
public class FragmentProfile extends Fragment {

    List<Event> posts;
    ListView eventsList;
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


        // Get Views
        final View myInflatedView = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView nameView = (TextView) myInflatedView.findViewById(R.id.fragProfile_textView_name);
        final TextView emailView = (TextView) myInflatedView.findViewById(R.id.fragProfile_textView_email);
        final ImageView profileView = (ImageView) myInflatedView.findViewById(R.id.fragProfile_imageView_profilePic);
        eventsList = (ListView) myInflatedView.findViewById(R.id.fragProfile_listView_eventsList);

        // Get the user
        ImpromptuUser targetUser = ImpromptuUser.getUserById(userData.getString("ownerId"));

        List<Event> events = targetUser.getStreamEvents();

        if (targetUser == null) {
            //TODO - test this
            Log.e("Impromptu", "Current user is null");
            Intent intent = new Intent(getActivity(), ActivityLogin.class);
            startActivity(intent);
            getActivity().finish();
        }

        if (targetUser.getUsername() == null)
            Log.e("Impromptu", "Current user's username is null");
        if (targetUser.getEmail() == null)
            Log.e("Impromptu", "Current user's email is null");

        // Fill in the fields
        nameView.setText(targetUser.getName());
        emailView.setText(targetUser.getEmail());
        profileView.setImageBitmap(targetUser.getPicture());
        ParseObject.fetchAllIfNeededInBackground(events, new FindCallback<Event>() {

            @Override
            public void done(List<Event> postsObjects, ParseException e) {
                if (e == null) {
                    posts = new ArrayList<Event>(postsObjects);


                    // Create the HashMap List
                    List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
                    for(Event post : posts){

                        // Check for the filters
                        if(ActivityMain.getFiltersMap().get(post.getType()) != null) {
                            if (ActivityMain.getFiltersMap().get(post.getType())) {

                                // Check the time if it has passed already
                                if(new Date().after(post.getEventDate())){
                                    aList.add(post.getHashMap());
                                }

                            }
                        }
                    }

                    // Initialize the adapter
                    SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList,
                            R.layout.fragment_stream_listview, from, to);


                    // Setting the list adapter for the ListFragment
                    eventsList.setAdapter(adapter);

                    // Update the list adapter
                    adapter.notifyDataSetChanged();

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