package com.example.steve.impromptu.Main;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Entity.StreamPost;
import com.example.steve.impromptu.R;

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

    ArrayList<StreamPost> posts = new ArrayList<StreamPost>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Fill the list with content
        posts.add(testPost1);
        posts.add(testPost2);
        posts.add(testPost3);
        posts.add(testPost4);
        posts.add(testPost5);
        posts.add(testPost6);
        posts.add(testPost7);
        posts.add(testPost8);
        posts.add(testPost9);

        // Create the HashMap List
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
        for(StreamPost post : posts){
            aList.add(post.getHashMap());
        }

        // Initialize the adapter
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(),
//                android.R.layout.simple_list_item_1, people);
        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList,
                R.layout.fragment_stream_listview, from, to);


        // Setting the list adapter for the ListFragment
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

}
