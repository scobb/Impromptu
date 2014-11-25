package com.example.steve.impromptu.Main.Compose;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
    TextView vGroupsText;
    LinearLayout vGroups;
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
    OnComposePushChooseGroupsListener mGroupsCallback;

    // Container Activity must implement this interface
    public interface OnComposePushFinishedListener {
        public void onComposePushFinished();
    }

    public interface OnComposePushChooseGroupsListener {
        public void onComposePushChooseGroups();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_compose_push, container, false);

        vFriendsList = (ListView) fragmentView.findViewById(R.id.fragComposePush_listView_friendsList);
        vGroups = (LinearLayout) fragmentView.findViewById(R.id.fragComposePush_linearLayout_groups);
        vGroupsText = (TextView) fragmentView.findViewById(R.id.fragComposePush_textView_groupsList);
        vOkay = (LinearLayout) fragmentView.findViewById(R.id.fragComposePush_linearLayout_okay);
        vCancel = (LinearLayout) fragmentView.findViewById(R.id.fragComposePush_linearLayout_cancel);

        ImpromptuUser currentUser = (ImpromptuUser) ImpromptuUser.getCurrentUser();
        ActivityMain myActivity = (ActivityMain) getActivity();
        Event myEvent = myActivity.getComposeEvent();

        // TODO: remove when I have actual friends and groups
        if (myActivity.firstTime) {
            addTestFriends(currentUser);
            addTestGroups(currentUser);
            myActivity.firstTime = false;
        }

        userFriendsList = (ArrayList<ImpromptuUser>) currentUser.getFriends();
        userGroupsList = (ArrayList<Group>) currentUser.getGroups();


        // initialize all friends to unselected
        for (ImpromptuUser friend : userFriendsList) {
            friend.setSelected(false);
        }

        // initialize all groups to unselected
        for (Group group : userGroupsList) {
            group.setSelected(false);
        }

        eventPushFriendsList = (ArrayList<ImpromptuUser>) myEvent.getPushFriends();
//        eventPushGroupsList = (ArrayList<Group>) myEvent.getPushGroups();

        if (!eventPushFriendsList.isEmpty()) {

            Iterator<ImpromptuUser> iterEventFriends = eventPushFriendsList.iterator();
            Iterator<ImpromptuUser> iterUserFriends = userFriendsList.iterator();

            ImpromptuUser eventFriend;
            ImpromptuUser userFriend;

            while (iterEventFriends.hasNext() && iterUserFriends.hasNext()) {
                // Note: eventPushFriendsList is a subset of userFriendsList

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
                    // and eventPushFriendsList is a subset of userFriendsList
                }
            }
        }

        eventPushGroupsList = (ArrayList<Group>) myEvent.getPushGroups();
        if (!eventPushGroupsList.isEmpty() && !userGroupsList.isEmpty()) {
            // there are groups to push to && user has groups

            Iterator<Group> iterEventGroups = eventPushGroupsList.iterator();
            Iterator<Group> iterUserGroups = userGroupsList.iterator();

            Group eventGroup;
            Group userGroup;

            while (iterEventGroups.hasNext() && iterUserGroups.hasNext()) {
                // Note: eventPushGroupsList is a subset of userGroupsList

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
                    // and eventPushGroupsList is a subset of userGroupsList
                }
            }
        }

        // Create list of selected groups
        String groups = "";
        Group group;
        int length = eventPushGroupsList.size();
        for (int i = 0; i < length; i++) {
            group = eventPushGroupsList.get(i);
            groups = groups + group.getGroupName() + ", ";
        }

        vGroupsText.setText(groups);


        friendsAdapter = new ArrayAdapterComposePushFriends(getActivity(), R.layout.template_friend_item, userFriendsList, eventPushGroupsList);
        vFriendsList.setAdapter(friendsAdapter);

        vGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: figure out way to preserve friends selected between pressing okay and going to groups
//                for (ImpromptuUser friend : userFriendsList) {
//
//                    if (friend.isSelected() && !(eventPushFriendsList.contains(friend))) {
//                        eventPushFriendsList.add(friend);
//                    }
//                }
//                Collections.sort(eventPushFriendsList);
//
//                ActivityMain myActivity = (ActivityMain) getActivity();
//                Event myEvent = myActivity.getComposeEvent();
//                myEvent.setPushFriends(eventPushFriendsList);
//
                mGroupsCallback.onComposePushChooseGroups();

            }
        });

        vOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityMain myActivity = (ActivityMain) getActivity();
                Event myEvent = myActivity.getComposeEvent();

                for (ImpromptuUser friend : userFriendsList) {

                    ImpromptuUser frd = listContainsFriend(eventPushFriendsList, friend);

                    if (frd != null) {
                        // if this friend was in the eventPushFriendsList && is not selected, remove it
                        if (!(friend.isSelected())) {
//                            eventPushFriendsList.remove(frd);
                            myEvent.removePushFriend(frd);
                        }
                    }

                    if (friend.isSelected()) {
                        // if this is one of the selected friends && isn't already in the eventPushFriendsList, add it
                        if (frd == null) {
//                            eventPushFriendsList.add(friend);
                            myEvent.addPushFriend(friend);
                        }
                    }
                }
//                Collections.sort(eventPushFriendsList); // make sure eventPushFriendsList is sorted alphabetically
                // TODO fix this
//                myEvent.setPushFriends(eventPushFriendsList);


                for (Group group : userGroupsList) {

                    Group grp = listContainsGroup(eventPushGroupsList, group);

                    if (grp != null) {
                        // if this group was in the eventPushGroupsList && is not selected, remove it
                        if (!(grp.isSelected())) {
                            eventPushGroupsList.remove(grp);
                        }
                    }

                    if (group.isSelected()) {
                        // if this one of the selected groups && isn't already in the eventPushFriendsList, add it
                        if (grp == null) {
                            eventPushGroupsList.add(group);
                        }
                    }

                }
//                Collections.sort(eventPushGroupsList); // make sure eventPushGroupsList is sorted alphabetically

                myEvent.setPushGroups(eventPushGroupsList);

                mCallback.onComposePushFinished();
            }
        });

        vCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCallback.onComposePushFinished();

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
            mGroupsCallback = (OnComposePushChooseGroupsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnComposePushFinishedListener");
        }
    }


    public void addTestGroups(ImpromptuUser currentUser) {
        Group myGroup1 = new Group();
        myGroup1.clear();
        myGroup1.setGroupName("Composers");
        ImpromptuUser friend1 = new ImpromptuUser("Bob Newman");
        ImpromptuUser friend2 = new ImpromptuUser("Wolfgang Mozart");
        ImpromptuUser friend3 = new ImpromptuUser("John Williams");

        myGroup1.add(friend1);
        myGroup1.add(friend2);
        myGroup1.add(friend3);

        Group myGroup2 = new Group();
        myGroup2.clear();
        myGroup2.setGroupName("Small Composers");
        ImpromptuUser friend4 = new ImpromptuUser("Alexandre Desplat");
        ImpromptuUser friend5 = new ImpromptuUser("Hans Zimmer");

        myGroup2.add(friend4);
        myGroup2.add(friend5);

        currentUser.addGroup(myGroup1);
        currentUser.addGroup(myGroup2);

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

    public ImpromptuUser listContainsFriend(ArrayList<ImpromptuUser> list, ImpromptuUser friend) {

        for (ImpromptuUser frd : list) {
            if (frd.getName().equals(friend.getName())) {
                return frd;
            }
        }

        return null;
    }

    public Group listContainsGroup(ArrayList<Group> list, Group group) {

        for (Group grp : list) {
            if (group.getGroupName().equals(grp.getGroupName())) {
                return grp;
            }
        }

        return null;
    }
}
