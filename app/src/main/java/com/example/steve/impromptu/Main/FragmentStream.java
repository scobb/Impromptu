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

    ImpromptuUser testUser1 = new ImpromptuUser("Stevie Wonder");
    ImpromptuUser testUser2 = new ImpromptuUser("Nice Guy Thill");
    ImpromptuUser testUser3 = new ImpromptuUser("Ben Marsh the Darsh");
    ImpromptuUser testUser4 = new ImpromptuUser("Big Dog Billings");
    ImpromptuUser testUser5 = new ImpromptuUser("Papa Holley");
    ImpromptuUser testUser6 = new ImpromptuUser("Guilty Hilti");
    ImpromptuUser testUser7 = new ImpromptuUser("Haydensity");
    ImpromptuUser testUser8 = new ImpromptuUser("McCombs O`Neil");
    ImpromptuUser testUser9 = new ImpromptuUser("Baylor Taylor");

    Date date = new Date();

    StreamPost testPost1 = new StreamPost(testUser1, date, "Whoever programmed this must be amazing");
    StreamPost testPost2 = new StreamPost(testUser2, date, "I'm so nice");
    StreamPost testPost3 = new StreamPost(testUser3, date, "Our plan is to think of a plan");
    StreamPost testPost4 = new StreamPost(testUser4, date, "There comes a time when I have to big dog things");
    StreamPost testPost5 = new StreamPost(testUser5, date, "Destiny... destiny... destiny...");
    StreamPost testPost6 = new StreamPost(testUser6, date, "Have you ever seen a flailing giraffe?");
    StreamPost testPost7 = new StreamPost(testUser7, date, "You think you're good, but you're not");
    StreamPost testPost8 = new StreamPost(testUser8, date, "Study study study!");
    StreamPost testPost9 = new StreamPost(testUser9, date, "I go to OU");

    List<Event> posts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Fill the list with content
        /*
        posts.add(testPost1);
        posts.add(testPost2);
        posts.add(testPost3);
        posts.add(testPost4);
        posts.add(testPost5);
        posts.add(testPost6);
        posts.add(testPost7);
        posts.add(testPost8);
        posts.add(testPost9);
        */


        // Gets query for the event streams
        ParseQuery<Event> postQuery = ParseQuery.getQuery("Event");
        postQuery.findInBackground(new FindCallback<Event>() {
                                       public void done(List<Event> posts, ParseException e) {
                                           if (e == null) {
                                               // postsWereRetrievedSuccessfully(posts);
                                               posts = new ArrayList<Event>(posts);

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

                                               adapter.notifyDataSetChanged();

                                           //    posts = new ArrayList<Event>(objects);
                                           //    Toast.makeText(getActivity(), Integer.toString(posts.size()), Toast.LENGTH_SHORT).show();
                                               Log.d("TEST", Integer.toString(posts.size()));
                                           } else {
                                            //   objectRetrievalFailed();
                                               e.printStackTrace();

                                           }
                                       }
                                   });

        /*
        Log.d("TEST", Integer.toString(posts.size()));
        Log.d("TEST", "test");

        // Create the HashMap List
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
        for(Event post : posts){
            Log.d("POSTS", post.getDescription());
            aList.add(post.getHashMap());
        }

        // Initialize the adapter
        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList,
                R.layout.fragment_stream_listview, from, to);


        // Setting the list adapter for the ListFragment
        setListAdapter(adapter);
        */


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /*
    @Override
    public void onListItemClick(ListView l, View v, int position, long id){

        // Get the selected user
        ImpromptuUser selectedUser = posts.get(position).getUser();

        // Bundle up the data getting passed
        Bundle userData = new Bundle();
        userData.putString("username", selectedUser.getName());

        // Set up the fragment transaction
        FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
        FragmentProfile fragment = new FragmentProfile();
        fragment.setArguments(userData);
        transaction.replace(R.id.activityMain_frameLayout_shell, fragment);
        transaction.commit();
    }
    */

    public void postsWereRetrievedSuccessfully(LayoutInflater inflater, ViewGroup container,
                                               Bundle savedInstanceState, List<Event> posts){

    }

}
