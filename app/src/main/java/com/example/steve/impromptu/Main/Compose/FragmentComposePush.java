package com.example.steve.impromptu.Main.Compose;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.Group;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterComposePushFriends;
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterComposePushGroups;
import com.example.steve.impromptu.R;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentComposePush extends Fragment {

    ListView vFriendsList;
    ListView vGroupsList;
    LinearLayout vOkay;
    LinearLayout vCancel;
    ArrayList<ImpromptuUser> eventPushFriendsList;
    ArrayList<Group> eventPushGroupsList;

    ArrayList<ImpromptuUser> userFriendsList;
    ArrayList<Group> userGroupsList;

    // List that holds all of the current user's friends that can be modified (change isSelected,etc.)
    ArrayList<ImpromptuUser> modifiableFriendsList;

    ArrayAdapterComposePushFriends friendsAdapter = null;
    ArrayAdapterComposePushGroups groupsAdapter = null;

    OnComposePushFinishedListener mCallback;

    // Container Activity must implement this interface
    public interface OnComposePushFinishedListener {
        public void onComposePushFinished();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_compose_push, container, false);

        vFriendsList = (ListView) fragmentView.findViewById(R.id.fragComposePush_listView_friendsList);
        vGroupsList = (ListView) fragmentView.findViewById(R.id.fragComposePush_listView_groupsList);
        vOkay = (LinearLayout) fragmentView.findViewById(R.id.fragComposePush_linearLayout_okay);
        vCancel = (LinearLayout) fragmentView.findViewById(R.id.fragComposePush_linearLayout_cancel);

        ImpromptuUser currentUser = (ImpromptuUser) ImpromptuUser.getCurrentUser();

        // TODO: remove when I have actual friends and groups
        addTestFriends(currentUser);
        addTestGroups(currentUser);

//        modifiableFriendsList = new ArrayList<ImpromptuUser>();
//        userFriendsList = new ArrayList<ImpromptuUser>();
//        userGroupsList = new ArrayList<Group>();
//        eventPushFriendsList = new ArrayList<ImpromptuUser>();
//        eventPushGroupsList = new ArrayList<Group>();

        userFriendsList = (ArrayList<ImpromptuUser>) currentUser.getFriends();
        userGroupsList = (ArrayList<Group>) currentUser.getGroups();

        for (ImpromptuUser friend : userFriendsList) {
            friend.setSelected(false);
        }

        ActivityMain myActivity = (ActivityMain) getActivity();
        Event myEvent = myActivity.getNewEvent();
        eventPushFriendsList = (ArrayList<ImpromptuUser>) myEvent.getPushFriends();
        eventPushGroupsList = myEvent.getPushGroups();

        if (!eventPushFriendsList.isEmpty()) {

            Iterator<ImpromptuUser> iterEventFriends = eventPushFriendsList.iterator();
            Iterator<ImpromptuUser> iterUserFriends = userFriendsList.iterator();

            ImpromptuUser eventFriend;
            ImpromptuUser userFriend;
            if (iterEventFriends.hasNext() && iterUserFriends.hasNext()) {

                eventFriend = iterEventFriends.next();
                userFriend = iterUserFriends.next();

                while (iterEventFriends.hasNext()) {

                    int comp = eventFriend.compareTo(userFriend);
                    if (comp == 0) {
                        // they are the same
                        userFriend.setSelected(true);
                        eventFriend = iterEventFriends.next();
                        userFriend = iterUserFriends.next();
                    }
                    else if (comp > 0) {
                        userFriend = iterUserFriends.next();
                    }
                    else {
                        // shouldn't get this case
                    }
                }
            }
            else {
                // Uh-oh
            }
        }

        if (!eventPushGroupsList.isEmpty()) {

            Iterator<Group> iterEventGroups = eventPushGroupsList.iterator();
            Iterator<Group> iterUserGroups = userGroupsList.iterator();

            Group eventGroup;
            Group userGroup;
            if (iterEventGroups.hasNext() && iterUserGroups.hasNext()) {

                eventGroup = iterEventGroups.next();
                userGroup = iterUserGroups.next();

                while (iterEventGroups.hasNext()) {

                    int comp = eventGroup.compareTo(userGroup);
                    if (comp == 0) {
                        // they are the same
                        userGroup.setSelected(true);
                        eventGroup = iterEventGroups.next();
                        userGroup = iterUserGroups.next();
                    }
                    else if (comp > 0) {
                        userGroup = iterUserGroups.next();
                    }
                    else {
                        // shouldn't get this case
                    }
                }
            }
            else {
                // Uh-oh
            }
        }

        friendsAdapter = new ArrayAdapterComposePushFriends(getActivity(), R.layout.template_friend_item, userFriendsList);
        vFriendsList.setAdapter(friendsAdapter);

        groupsAdapter = new ArrayAdapterComposePushGroups(getActivity(), R.layout.template_friend_item, userGroupsList);
        vGroupsList.setAdapter(groupsAdapter);

        vOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ArrayList<ImpromptuUser> pushTo = new ArrayList<ImpromptuUser>();
//
////                for (ImpromptuUser friend : friendsList) {
////
////                    if (friend.isSelected()) {
////                        pushTo.add(friend);
////                    }
////
////                }
//                // TODO: decide whether to pass just the selected friends, or the entire list and
//                // determine later which should be pushed to
//                pushTo.addAll(friendsList);
//
//                String test = "test: ";
//                for (ImpromptuUser friend : pushTo) {
//                    if (friend.isSelected())
//                        test = test + friend.getName() + " ";
//                }
//
//                Toast.makeText(getActivity(), test, Toast.LENGTH_SHORT).show();
//
//                ActivityMain myActivity = (ActivityMain) getActivity();
//                Event myEvent = myActivity.getNewEvent();
//                myEvent.setPushFriends(pushTo);

                mCallback.onComposePushFinished();
            }
        });

        vCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            // TODO:

//                mCallback.onComposePushFinished();
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
            mCallback = (OnComposePushFinishedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnComposePushFinishedListener");
        }
    }


    public void addTestGroups(ImpromptuUser currentUser) {
        Group myGroup = new Group();
        myGroup.clear();
        ImpromptuUser friend1 = new ImpromptuUser("Bob Newman");
        ImpromptuUser friend2 = new ImpromptuUser("Wolfgang Mozart");
        ImpromptuUser friend3 = new ImpromptuUser("John Williams");
//        ImpromptuUser friend4 = new ImpromptuUser("Alexandre Desplat");
//        ImpromptuUser friend5 = new ImpromptuUser("Hans Zimmer");

        myGroup.add(friend1);
        myGroup.add(friend2);
        myGroup.add(friend3);

        currentUser.addGroup(myGroup);
//        myGroup.add(friend4);
//        myGroup.add(friend5);

        return;
    }

    public void addTestFriends(ImpromptuUser currentUser) {
        ImpromptuUser friend1 = new ImpromptuUser("Bob Newman");
        ImpromptuUser friend2 = new ImpromptuUser("Wolfgang Mozart");
        ImpromptuUser friend3 = new ImpromptuUser("John Williams");
        ImpromptuUser friend4 = new ImpromptuUser("Alexandre Desplat");
        ImpromptuUser friend5 = new ImpromptuUser("Hans Zimmer");

        currentUser.addFriend(friend1);
        currentUser.addFriend(friend2);
        currentUser.addFriend(friend3);
        currentUser.addFriend(friend4);
        currentUser.addFriend(friend5);

        return;
    }

}
