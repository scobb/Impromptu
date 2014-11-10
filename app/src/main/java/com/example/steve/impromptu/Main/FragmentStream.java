package com.example.steve.impromptu.Main;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Entity.StreamPost;
import com.example.steve.impromptu.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Stephen Arifin on 10/16/14.
 */

public class FragmentStream extends ListFragment {

    // Keys used in HashMap
    String[] from = {"picture", "user", "content", "date"};

    // Ids of views in listview layout
    int[] to ={R.id.fragStream_imageView_picture, R.id.fragStream_textView_user,
            R.id.fragStream_textView_content, R.id.fragStream_textView_date};


    List<Event> posts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Gets query for the event streams
        ParseQuery<Event> postQuery = ParseQuery.getQuery("Event");
        postQuery.findInBackground(new FindCallback<Event>() {
                                       public void done(List<Event> postsObjects, ParseException e) {
                                           if (e == null) {
                                               posts = new ArrayList<Event>(postsObjects);

                                               // Create the HashMap List
                                               List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
                                               for(Event post : posts){
                                                   aList.add(post.getHashMap());
                                               }

                                               // Initialize the adapter
                                               SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList,
                                                       R.layout.fragment_stream_listview, from, to);


                                               // Setting the list adapter for the ListFragment
                                               setListAdapter(adapter);

                                               // Update the list adapter
                                               adapter.notifyDataSetChanged();

                                           } else {
                                               // Error in query
                                               e.printStackTrace();
                                           }
                                       }
                                   });

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id){

        // Get the selected event
        Event event = posts.get(position);

        // Get the selected user
        ImpromptuUser selectedUser = event.getOwner();

        // Bundle up the data getting passed
        Bundle eventData = new Bundle();

        eventData.putString("title", event.getTitle());
        eventData.putString("owner", selectedUser.getName());
        eventData.putString("email", selectedUser.getEmail());
        eventData.putBoolean("currentuser", false);

        // Set up the fragment transaction
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
//        FragmentProfile fragment = new FragmentProfile();
        FragmentEventDetail fragment = new FragmentEventDetail();
        fragment.setArguments(eventData);
        transaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        transaction.commit();
    }

}
