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
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.Group;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterComposePushFriends;
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterComposePushGroups;
import com.example.steve.impromptu.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentComposePush extends Fragment {

    //TODO: remove
    static Boolean firstTime = true;

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

        // TODO: remove when I have actual friends and groups
        if (firstTime) {
            addTestFriends(currentUser);
            addTestGroups(currentUser);
            firstTime = false;
        }

        userFriendsList = (ArrayList<ImpromptuUser>) currentUser.getFriends();
        userGroupsList = (ArrayList<Group>) currentUser.getGroups();

        ActivityMain myActivity = (ActivityMain) getActivity();
        Event myEvent = myActivity.getComposeEvent();

        // initialize all friends to unselected
        for (ImpromptuUser friend : userFriendsList) {
            friend.setSelected(false);
        }

        // initialize all groups to unselected
        for (Group group : userGroupsList) {
            group.setSelected(false);
        }

        eventPushFriendsList = (ArrayList<ImpromptuUser>) myEvent.getPushFriends();
        eventPushGroupsList = (ArrayList<Group>) myEvent.getPushGroups();

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

                    if (listContainsFriend(eventPushFriendsList, friend)) {
                        // if this friend was in the eventPushFriendsList && is not selected, remove it
                        if (!(friend.isSelected())) {
                            eventPushFriendsList.remove(friend);
                        }
                    }

                    if (friend.isSelected()) {
                        // if this is one of the selected friends && isn't already in the eventPushFriendsList, add it
                        if (!listContainsFriend(eventPushFriendsList, friend)) {
                            eventPushFriendsList.add(friend);
                        }
                    }

                }
                Collections.sort(eventPushFriendsList); // make sure eventPushFriendsList is sorted alphabetically
                myEvent.setPushFriends(eventPushFriendsList);

                // TODO: remove this, isn't necessary
                for (Group group : userGroupsList) {

                    if (listContainsGroup(eventPushGroupsList, group)) {
                        // if this group was in the eventPushGroupsList && is not selected, remove it
                        if (!(group.isSelected())) {
                            eventPushGroupsList.remove(group);
                        }
                    }

                    if (group.isSelected()) {
                        // if this one of the selected groups && isn't already in the eventPushFriendsList, add it
                        if (!listContainsGroup(eventPushGroupsList, group)) {
                            eventPushGroupsList.add(group);
                        }
                    }

                }
                Collections.sort(eventPushGroupsList); // make sure eventPushGroupsList is sorted alphabetically
                myEvent.setPushGroups(eventPushGroupsList);

                String test = "Friends: ";
                for (ImpromptuUser friend : eventPushFriendsList) {
                    if (friend.isSelected())
                        test = test + friend.getName() + " ";
                }
                test = test + "\n Groups: ";
                for (Group group : eventPushGroupsList) {
                    if (group.isSelected())
                        test = test + group.getGroupName();
                }

                Toast.makeText(getActivity(), test, Toast.LENGTH_SHORT).show();

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
        Group myGroup = new Group();
        myGroup.clear();
        myGroup.setGroupName("Composers");
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

    public Boolean listContainsFriend(ArrayList<ImpromptuUser> list, ImpromptuUser friend) {
        Boolean contains = false;

        for (ImpromptuUser frd : list) {
            if (frd.getName().equals(friend.getName())) {
                contains = true;
                break;
            }
        }

        return contains;
    }

    public Boolean listContainsGroup(ArrayList<Group> list, Group group) {
        Boolean contains = false;

        for (Group grp : list) {
            if (group.getGroupName().equals(grp.getGroupName())) {
                contains = true;
                break;
            }
        }

        return contains;
    }
}
