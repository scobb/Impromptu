package com.example.steve.impromptu.Main;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.ImpromptuLocation;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.R;
import com.example.steve.impromptu.UI.ObservableScrollView;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Stephen Arifin on 10/16/14.
 */

public class FragmentStream extends ListFragment implements AbsListView.OnScrollListener{

    // Keys used in HashMap
    private String[] from = {"picture", "user", "content", "date"};

    // Ids of views in listview layout
    private int[] to ={R.id.fragStream_imageView_picture, R.id.fragStream_textView_user,
            R.id.fragStream_textView_content, R.id.fragStream_textView_date};


    // New things for the quick return thing
    private ObjectAnimator filterBarReturnAnimator;
    private ObjectAnimator filterBarHideAnimator;

    private QuickReturnState state = QuickReturnState.ON_SCREEN;

    /** Tracks the last seen y-position of the first visible child */
    private int _last_y;
    /** Tracks the last seen first visible child */
    private int _last_first_child;

    List<Event> posts;
    LinearLayout mapStream;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get the root view
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_stream, container, false);
        View fragmentView = inflater.inflate(R.layout.fragment_stream, container, false);


        // Gets query for the event streams
        ImpromptuUser currentUser = (ImpromptuUser) ParseUser.getCurrentUser();
        List<Event> events = currentUser.getStreamEvents();


        ParseObject.fetchAllIfNeededInBackground(events, new FindCallback<Event>() {

                                       @Override
                                       public void done(List<Event> postsObjects, ParseException e) {
                                           if (e == null) {
                                               posts = new ArrayList<Event>(postsObjects);


                                               // Create the HashMap List
                                               List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
                                               for(Event post : posts){

                                                   Log.d("Impromptu", post.getObjectId() + " " + post.getType());

                                                   // Check for the filters
                                                   if(ActivityMain.getFiltersMap().get(post.getType()) != null) {
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
                                   });


        // Customize the stream layout

        // Get the quick return for the filters
        final View filtersBar = rootView.findViewById(R.id.filter_bar);

        // Set up animations
        filterBarReturnAnimator = ObjectAnimator.ofFloat(filtersBar,
                "translationY",
                0);
        filterBarReturnAnimator.addListener(
                new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        state = QuickReturnState.RETURNING;
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        state = QuickReturnState.ON_SCREEN;
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        state = QuickReturnState.OFF_SCREEN;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                }
        );
        filterBarHideAnimator = ObjectAnimator.ofFloat(filtersBar,
                "translationY",
                getResources().getDimension(R.dimen.filter_bar_height));
        filterBarHideAnimator.addListener(
                new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        state = QuickReturnState.HIDING;
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        state = QuickReturnState.OFF_SCREEN;
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        state = QuickReturnState.ON_SCREEN;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) { }
                }
        );

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        final ListView list_view = getListView();
        list_view.setOnScrollListener(this);
        _last_first_child = list_view.getFirstVisiblePosition();
        final View first_child = list_view.getChildAt(_last_first_child);
        _last_y = first_child == null ? 0 : first_child.getTop();
    }

    /* AbsListView.OnScrollListener */
    @Override
    public void onScroll(AbsListView list_view, int first_visible_child, int _, int __) {

        // Computes the scroll Y position
        final View first_child = list_view.getChildAt(first_visible_child);
        int y_position = first_child == null? 0 : first_child.getTop();
        switch (state) {
            case OFF_SCREEN:

                // * Return quick return bar if first visible child is the same AND it's Y position increases
                // * Return quick return bar if first visible child changes to a previous sibling
                // * AND only if the quick return bar isn't already returning

                if (wasPreviousFirstVisibleChildScrolledDownAndQuickReturnBarIsNotReturning(first_visible_child, y_position))
                    filterBarReturnAnimator.start();
                break;

            case ON_SCREEN:

                // * Hide quick return bar if first visible child is the same AND it's Y position decreases
                // * Hide quick return bar if first visible child changes to a later sibling
                // * AND only if the quick return bar isn't already going away

                if (wasPreviousFirstVisibleChildScrolledUpAndQuickReturnBarIsNotGoingAway(first_visible_child, y_position))
                    filterBarHideAnimator.start();
                break;
            case RETURNING:

                // * Cancel return of quick return bar if first visible child is the same AND it's Y position decreases
                // * Cancel return of quick return bar if first visible child changes to a later sibling

                if (wasPreviousFirstVisibleChildScrolledUp(first_visible_child, y_position)) {
                    filterBarReturnAnimator.cancel();
                    filterBarHideAnimator.start();
                }
                break;
            case HIDING:

                // * Cancel hide of quick return bar if first visible child is the same AND it's Y position increases
                // * Cancel hide of quick return bar if first visible child changes to a previous sibling
                if (wasPreviousFirstVisibleChildScrolledDown(first_visible_child, y_position)) {
                    filterBarHideAnimator.cancel();
                    filterBarReturnAnimator.start();
                }
                break;
        }
        _last_first_child = first_visible_child;
        _last_y = y_position;
    }


    @Override
    public void onScrollStateChanged(AbsListView _, int __) { }


    /* Transition status checks */
    /**
     * <p>Checks if the quick return bar is transitioning back onto the screen.</p>
     * @return {@code true} indicates that the quick return bar is returning
     */

    private boolean quickReturnBarIsReturning() {

        // This should be equivalent to checking that the quick return bar is RETURNING
        return filterBarReturnAnimator.isRunning() || filterBarReturnAnimator.isStarted();
    }

    /**
     * <p>Checks if the quick return bar is transitioning off of the screen.</p>
     * @return {@code true} indicates that the quick return bar is going away
     */

    private boolean quickReturnBarIsGoingAway() {
        return filterBarHideAnimator.isRunning() || filterBarHideAnimator.isStarted();
    }

    private boolean wasPreviousFirstVisibleChildScrolledUp(int first_visible_child, int y_position) {
        return first_visible_child == _last_first_child && y_position < _last_y
                || first_visible_child > _last_first_child;
    }

    private boolean wasPreviousFirstVisibleChildScrolledDown(int first_visible_child, int y_position) {
        return first_visible_child == _last_first_child && y_position > _last_y
                || first_visible_child < _last_first_child;
    }

    private boolean wasPreviousFirstVisibleChildScrolledDownAndQuickReturnBarIsNotReturning(int first_visible_child,
                                                                                            int y_position) {
        return !quickReturnBarIsReturning() && wasPreviousFirstVisibleChildScrolledDown(first_visible_child, y_position);
    }

    private boolean wasPreviousFirstVisibleChildScrolledUpAndQuickReturnBarIsNotGoingAway(int first_visible_child,
                                                                                          int y_position) {
        return !quickReturnBarIsGoingAway() && wasPreviousFirstVisibleChildScrolledUp(first_visible_child, y_position);
    }

    @Override
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
        transaction.commit();
    }


    private static enum QuickReturnState {
        /** Stable state indicating that the quick return bar is visible on screen */
        ON_SCREEN,
        /** Stable state indicating that the quick return bar is hidden off screen */
        OFF_SCREEN,
        /** Transitive state indicating that the quick return bar is coming onto the screen */
        RETURNING,
        /** Transitive state indicating that the quick return bar is going off of the screen */
        HIDING
    }

}
