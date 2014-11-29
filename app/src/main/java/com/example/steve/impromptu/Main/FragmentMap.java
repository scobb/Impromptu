package com.example.steve.impromptu.Main;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.R;
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

    List<Event> posts;
    LinearLayout listStream;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_map, container, false);


        // Gets query for the event streams
        ImpromptuUser currentUser = (ImpromptuUser) ParseUser.getCurrentUser();
        List<Event> events = currentUser.getStreamEvents();
        listStream = (LinearLayout) fragmentView.findViewById(R.id.fragMap_linearLayout_listStream);

        listStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getFragmentManager().popBackStackImmediate();
                //FragmentMap nextFrag = new FragmentMap();
                //getFragmentManager().beginTransaction().replace(R.id.activityMain_frameLayout_shell, nextFrag).addToBackStack(null).commit();
            }
        });

        /*

        ParseObject.fetchAllIfNeededInBackground(events, new FindCallback<Event>() {

            @Override
            public void done(List<Event> postsObjects, ParseException e) {
                if (e == null) {
                    posts = new ArrayList<Event>(postsObjects);


                    // Create the HashMap List
                    List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
                    for (Event post : posts) {

                        Log.d("Impromptu", post.getObjectId() + " " + post.getType());

                        // Check for the filters
                        if (ActivityMain.getFiltersMap().get(post.getType()) != null) {
                            if (ActivityMain.getFiltersMap().get(post.getType())) {
                                Log.d("Impromptu", post.getType());
                                aList.add(post.getHashMap());
                            }
                        }
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
        }); */

        return fragmentView;
    }

}
