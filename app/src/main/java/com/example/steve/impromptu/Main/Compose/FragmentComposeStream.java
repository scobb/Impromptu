package com.example.steve.impromptu.Main.Compose;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.Group;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterComposeStreamFriends;
import com.example.steve.impromptu.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentComposeStream extends Fragment {

    ListView vFriendsList;
    TextView vGroupsText;
    LinearLayout vGroups;
    ImageView vOkay;
    ImageView vCancel;
    ArrayList<ImpromptuUser> eventStreamFriendsList;
    ArrayList<Group> eventStreamGroupsList;

    ArrayList<ImpromptuUser> userFriendsList;
    ArrayList<Group> userGroupsList;

    ArrayAdapterComposeStreamFriends friendsAdapter = null;
    ImpromptuUser currentUser;

    OnComposeStreamFinishedListener mCallback;
    OnComposeStreamChooseGroupsListener mGroupsCallback;

    // Container Activity must implement this interface
    public interface OnComposeStreamFinishedListener {
        public void onComposeStreamFinished();
    }

    public interface OnComposeStreamChooseGroupsListener {
        public void onComposeStreamChooseGroups();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_compose_stream, container, false);

        currentUser = (ImpromptuUser) ImpromptuUser.getCurrentUser();

        vFriendsList = (ListView) fragmentView.findViewById(R.id.fragComposeStream_listView_friendsList);
        vGroups = (LinearLayout) fragmentView.findViewById(R.id.fragComposeStream_linearLayout_groups);
        vGroupsText = (TextView) fragmentView.findViewById(R.id.fragComposeStream_textView_groupsList);
        vOkay = (ImageView) fragmentView.findViewById(R.id.fragComposeStream_imageView_accept);
        vCancel = (ImageView) fragmentView.findViewById(R.id.fragComposeStream_imageView_cancel);

//        ImpromptuUser currentUser = (ImpromptuUser) ImpromptuUser.getCurrentUser();
        ActivityMain myActivity = (ActivityMain) getActivity();
        Event myEvent = myActivity.getComposeEvent();

//        test();

        userFriendsList = (ArrayList<ImpromptuUser>) currentUser.getFriends();
        userGroupsList = (ArrayList<Group>) myEvent.getAllGroups();


        // initialize all friends to unselected
        for (ImpromptuUser friend : userFriendsList) {
            friend.setSelected(false);
        }

        // initialize all groups to unselected
        for (Group group : userGroupsList) {
            group.setSelected(false);
        }

        eventStreamFriendsList = (ArrayList<ImpromptuUser>) myEvent.getStreamFriends();
//        eventStreamGroupsList = (ArrayList<Group>) myEvent.getStreamGroups();

        if (!eventStreamFriendsList.isEmpty()) {
            // eventStreamGroupsList and userFriendsList sorted by parse object id

            Iterator<ImpromptuUser> iterEventFriends = eventStreamFriendsList.iterator();
            Iterator<ImpromptuUser> iterUserFriends = userFriendsList.iterator();

            ImpromptuUser eventFriend;
            ImpromptuUser userFriend;

            while (iterEventFriends.hasNext() && iterUserFriends.hasNext()) {
                // Note: eventStreamFriendsList is a subset of userFriendsList

                eventFriend = iterEventFriends.next();
                userFriend = iterUserFriends.next();

                int comp = eventFriend.compareTo(userFriend);
                if (comp == 0) {
                    // they are the same

                    userFriend.setSelected(true);

                } else if (comp > 0) {
                    // eventFriend is "greater" than userFriend

                    while ((eventFriend.compareTo(userFriend)) != 0) {
                        // keep looking for an equivalent friend
                        userFriend = iterUserFriends.next();
                    }
                    userFriend.setSelected(true);

                } else {
                    // shouldn't get this case because both lists of friends are sorted alphabetically
                    // and eventStreamFriendsList is a subset of userFriendsList
                }
            }
        }

        eventStreamGroupsList = (ArrayList<Group>) myEvent.getStreamGroups();
        if (!eventStreamGroupsList.isEmpty() && !userGroupsList.isEmpty()) {
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

        // Create list of selected groups
        String groups = "";
        Group group;
        int length = eventStreamGroupsList.size();
        for (int i = 0; i < length; i++) {
            group = eventStreamGroupsList.get(i);
            groups = groups + group.getGroupName() + ", ";
        }

        vGroupsText.setText(groups);


        friendsAdapter = new ArrayAdapterComposeStreamFriends(getActivity(), R.layout.template_friend_item, userFriendsList, eventStreamGroupsList);
        vFriendsList.setAdapter(friendsAdapter);

        vGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: figure out way to preserve friends selected between pressing okay and going to groups
//                for (ImpromptuUser friend : userFriendsList) {
//
//                    if (friend.isSelected() && !(eventStreamFriendsList.contains(friend))) {
//                        eventStreamFriendsList.add(friend);
//                    }
//                }
//                Collections.sort(eventStreamFriendsList);
//
//                ActivityMain myActivity = (ActivityMain) getActivity();
//                Event myEvent = myActivity.getComposeEvent();
//                myEvent.setStreamFriends(eventStreamFriendsList);
//
                mGroupsCallback.onComposeStreamChooseGroups();

            }
        });

        vOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityMain myActivity = (ActivityMain) getActivity();
                Event myEvent = myActivity.getComposeEvent();

                for (ImpromptuUser friend : userFriendsList) {

                    Boolean contains = eventStreamFriendsList.contains(friend);
                    if (contains && !friend.isSelected()) {
                        myEvent.removeStreamFriend(friend);
                    }

                    if (friend.isSelected() && !contains) {
                        myEvent.addStreamFriend(friend);
                    }
                }

                for (Group group : userGroupsList) {

                    Boolean contains = eventStreamGroupsList.contains(group);

                    if (contains && !group.isSelected()) {
                        eventStreamGroupsList.remove(group);
                    }

                    if (group.isSelected() && !contains) {
                        eventStreamGroupsList.add(group);
                    }

                }
                Collections.sort(eventStreamGroupsList); // make sure eventStreamGroupsList is sorted alphabetically
                myEvent.setStreamGroups(eventStreamGroupsList);

                mCallback.onComposeStreamFinished();
            }
        });

        vCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCallback.onComposeStreamFinished();

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
            mCallback = (OnComposeStreamFinishedListener) activity;
            mGroupsCallback = (OnComposeStreamChooseGroupsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnComposeStreamFinishedListener");
        }
    }

//    public void test () {
//        ImpromptuUser me = (ImpromptuUser) ImpromptuUser.getCurrentUser();
//        ArrayList<ImpromptuUser> friends = (ArrayList<ImpromptuUser>) me.getFriends();
//
//        Group everyone = new Group("everyone");
//
//        for (ImpromptuUser frd : friends) {
//            everyone.add(frd);
//        }
//
//        everyone.persist();
//
//        me.addGroup(everyone);
//        me.persist();
//    }
}
