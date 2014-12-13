package com.example.steve.impromptu.Main.Profile;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterProfileEventsList;
import com.example.steve.impromptu.R;

import java.util.ArrayList;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentProfile extends ListFragment{
    ListView eventsList;
    private LinearLayout progressContainer;


    ArrayList<Event> events;
    ImpromptuUser currentUser;
    // Keys used in HashMap
//    private String[] from = {"picture", "user", "title", "content", "date"};

    // Ids of views in listview layout
//    private int[] to ={R.id.fragStream_imageView_picture, R.id.fragStream_textView_user, R.id.fragStream_textView_title,
//            R.id.fragStream_textView_content, R.id.fragStream_textView_date};

//    public class ProfileUpdateView extends UpdateView {
//        public void update(List<Event> events) {
//            List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
//            for (Event post : events) {
//                // TODO - why are these titles not displaying properly?
//                aList.add(post.getHashMap());
//
//            }
//
//            if (getActivity() != null) {
//                // If view is still active, initialize the adapter
//                SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList,
//                        R.layout.template_stream_event_item, from, to);
//
//
//                // Setting the list adapter for the ListFragment
//                eventsList.setAdapter(adapter);
//
//                // Update the list adapter
//                adapter.notifyDataSetChanged();
//                Log.d("Impromptu", "Profile view updated.");
//            }
//        }
//
//        public void clearLoad() {
//            progressContainer.setVisibility(View.INVISIBLE);
//
//        }
//
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Receive data passed in
//        Bundle userData = getArguments();

        currentUser = ((ActivityMain)getActivity()).currentUser;

        // Get Views
        final View myInflatedView = inflater.inflate(R.layout.fragment_profile, container, false);
        final LinearLayout vLogOut = (LinearLayout) myInflatedView.findViewById(R.id.fragProfile_linearLayout_logout);
        final TextView nameView = (TextView) myInflatedView.findViewById(R.id.fragProfile_textView_name);
        final ImageView profileView = (ImageView) myInflatedView.findViewById(R.id.fragProfile_imageView_profilePic);

        eventsList = (ListView) myInflatedView.findViewById(android.R.id.list);

//

//        final ImpromptuUser targetUser;
        // Get the user
//        String ownerId = userData.getString("ownerId");
//        if (ownerId.equals(currentUser.getObjectId())) {
//            Log.d("Impromptu", "Looking at current user's profile.");
//            targetUser = currentUser;
//        } else if (currentUser.friendMap.containsKey(ownerId)) {
//            Log.d("Impromptu", "User in map.");
//            targetUser = currentUser.friendMap.get(ownerId);
//        } else  {
//            targetUser = ImpromptuUser.getUserById(userData.getString("ownerId"));
//        }
        // fill cache
//        targetUser.getOwnedEvents();

        events = (ArrayList<Event>)currentUser.getOwnedEvents();

        // Fill in the fields
        nameView.setText(currentUser.getName());
        profileView.setImageBitmap(currentUser.getPicture());

        // TODO: not sure what this does
//        ParseQuery<Event> query = new ParseQuery<>("Event");
//        query.whereEqualTo("owner", currentUser);
        // Initialize the adapter

        // TODO: change to custom adapter
//        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), targetUser.ownedEventsHashList,
//                R.layout.template_stream_event_item, from, to);

        ArrayAdapterProfileEventsList adapter = new ArrayAdapterProfileEventsList(getActivity(), R.layout.template_profile_event_item, events, currentUser);

        // Setting the list adapter for the ListFragment
        eventsList.setAdapter(adapter);

        // Update the list adapter
        adapter.notifyDataSetChanged();
//        query.findInBackground(new FindCallback<Event>() {
//            @Override
//            public void done(List<Event> events, ParseException e) {
//                if (e == null) {
//                    posts = new ArrayList<>(events);
//                    // Create the HashMap List
//                    AsyncTaskPopulateOwnedEvents task = new AsyncTaskPopulateOwnedEvents();
//                    task.setUpdateView(new ProfileUpdateView());
//                    task.execute(posts);
//
//                } else {
//                    // Error in query
//                    e.printStackTrace();
//                }
//
//            }
//        });

        vLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActivityMain) getActivity()).onLogoutClicked(view);
            }
        });


        return myInflatedView;
    }


//    public void onListItemClick(ListView l, View v, int position, long id){
//
//        // Get the selected event
//        Event event = posts.get(position);
//
//        // Get the selected user
//        ImpromptuUser selectedUser = event.getOwner();
//
//        // Bundle up the data getting passed
//        Bundle eventData = new Bundle();
//
//        eventData.putString("ownerKey", selectedUser.getObjectId());
//        eventData.putString("eventKey", event.getObjectId());
//
//        // Set up the fragment transaction
//        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
//        FragmentEventDetail fragment = new FragmentEventDetail();
//        fragment.setArguments(eventData);
//        transaction.replace(R.id.activityMain_frameLayout_shell, fragment);
//        transaction.addToBackStack(null).commit();
//    }


}
