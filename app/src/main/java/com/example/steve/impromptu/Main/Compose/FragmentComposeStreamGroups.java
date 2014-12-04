package com.example.steve.impromptu.Main.Compose;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.Group;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterComposeStreamGroups;
import com.example.steve.impromptu.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by jonreynolds on 11/9/14.
 */
public class FragmentComposeStreamGroups extends Fragment {


    ListView vGroupsList;
    ImageView vOkay;
    ImageView vCancel;
    ArrayList<Group> eventStreamGroupsList;
    ArrayList<Group> userGroupsList;

    ArrayAdapterComposeStreamGroups groupsAdapter = null;

    OnComposeStreamChooseGroupsFinishedListener mCallback;

    // Container Activity must implement this interface
    public interface OnComposeStreamChooseGroupsFinishedListener {
        public void onComposeStreamChooseGroupsFinished();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_compose_streamgroups, container, false);
        vGroupsList = (ListView) fragmentView.findViewById(R.id.fragComposeStreamGroups_listView_groupsList);
        vOkay = (ImageView) fragmentView.findViewById(R.id.fragComposeStreamGroups_imageView_accept);
        vCancel = (ImageView) fragmentView.findViewById(R.id.fragComposeStreamGroups_imageView_cancel);
        ActivityMain myActivity = (ActivityMain) getActivity();
        Event myEvent = myActivity.getComposeEvent();

//        ImpromptuUser currentUser = (ImpromptuUser) ImpromptuUser.getCurrentUser();
        userGroupsList = (ArrayList<Group>) myEvent.getAllGroups();

        // initialize all groups to unselected
        for (Group group : userGroupsList) {
            group.setSelected(false);
        }

        eventStreamGroupsList = (ArrayList<Group>) myEvent.getStreamGroups();

        if (!eventStreamGroupsList.isEmpty()) {
            // there are groups to stream to && user has groups

            Iterator<Group> iterEventGroups = eventStreamGroupsList.iterator();
            Iterator<Group> iterUserGroups = userGroupsList.iterator();

            Group eventGroup;
            Group userGroup;

            while (iterEventGroups.hasNext() && iterUserGroups.hasNext()) {
                // Note: eventStreamGroupsList is a subset of userGroupsList

                eventGroup = iterEventGroups.next();
                userGroup = iterUserGroups.next();

                int comp = eventGroup.compareTo(userGroup);
                if (comp == 0) {
                    // they are the same

                    userGroup.setSelected(true);

                } else if (comp > 0) {
                    // eventGroup is "greater" than userGroup

                    while (((eventGroup.compareTo(userGroup)) != 0) && iterUserGroups.hasNext()) {
                        // keep looking for an equivalent group
                        userGroup = iterUserGroups.next();
                    }
                    userGroup.setSelected(true);

                } else {
                    // shouldn't get this case because both lists of groups are sorted alphabetically
                    // and eventStreamGroupsList is a subset of userGroupsList
                }
            }
        }

        groupsAdapter = new ArrayAdapterComposeStreamGroups(getActivity(), R.layout.template_friend_item, userGroupsList);
        vGroupsList.setAdapter(groupsAdapter);

        vOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityMain myActivity = (ActivityMain) getActivity();
                Event myEvent = myActivity.getComposeEvent();

                for (Group group : userGroupsList) {

                    Boolean contains = eventStreamGroupsList.contains(group);
                    if (contains && !group.isSelected()) {
                        // if this group was in the eventStreamGroupsList && is not selected, remove it if (!(group.isSelected())) {
                        eventStreamGroupsList.remove(group);
                        // remove friends that belong to group from eventStreamFriendsList
                        ArrayList<ImpromptuUser> friends = (ArrayList<ImpromptuUser>) group.getFriendsInGroup();
                        for (ImpromptuUser friend : friends) {
                            myEvent.removeStreamFriend(friend);
                        }

                    }

                    if (group.isSelected() && !contains) {
                        // if this is one of the selected groups && it was not already in the eventStreamGroupsList, add it
                        eventStreamGroupsList.add(group);
                    }
                }
                Collections.sort(eventStreamGroupsList); // make sure eventStreamGroupsList is sorted alphabetically
                myEvent.setStreamGroups(eventStreamGroupsList);

                ArrayList<ImpromptuUser> eventStreamFriendsList = (ArrayList<ImpromptuUser>) myEvent.getStreamFriends();
                for (Group group : eventStreamGroupsList) {
                    ArrayList<ImpromptuUser> friendsList = (ArrayList<ImpromptuUser>) group.getFriendsInGroup();

                    for (ImpromptuUser friend : friendsList) {

                        if (!eventStreamFriendsList.contains(friend)) {
                            myEvent.addStreamFriend(friend);
                        }

                    }
                }

                mCallback.onComposeStreamChooseGroupsFinished();
            }
        });

        vCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCallback.onComposeStreamChooseGroupsFinished();
            }
        });

        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnComposeStreamChooseGroupsFinishedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnComposeStreamFinishedListener");
        }
    }
}
