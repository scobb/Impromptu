package com.example.steve.impromptu.Main.Compose;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.Group;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterComposePushGroups;
import com.example.steve.impromptu.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by jonreynolds on 11/9/14.
 */
public class FragmentComposePushGroups extends Fragment {


    ListView vGroupsList;
    LinearLayout vOkay;
    LinearLayout vCancel;
    ArrayList<Group> eventPushGroupsList;
    ArrayList<Group> userGroupsList;

    ArrayAdapterComposePushGroups groupsAdapter = null;

    OnComposePushChooseGroupsFinishedListener mCallback;

    // Container Activity must implement this interface
    public interface OnComposePushChooseGroupsFinishedListener {
        public void onComposePushChooseGroupsFinished();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_compose_pushgroups, container, false);
        vGroupsList = (ListView) fragmentView.findViewById(R.id.fragComposePushGroups_listView_groupsList);
        vOkay = (LinearLayout) fragmentView.findViewById(R.id.fragComposePushGroups_linearLayout_okay);
        vCancel = (LinearLayout) fragmentView.findViewById(R.id.fragComposePushGroups_linearLayout_cancel);

        ImpromptuUser currentUser = (ImpromptuUser) ImpromptuUser.getCurrentUser();
        userGroupsList = (ArrayList<Group>) currentUser.getGroups();

        ActivityMain myActivity = (ActivityMain) getActivity();
        Event myEvent = myActivity.getComposeEvent();

        // initialize all groups to unselected
        for (Group group : userGroupsList) {
            group.setSelected(false);
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

        groupsAdapter = new ArrayAdapterComposePushGroups(getActivity(), R.layout.template_friend_item, userGroupsList);
        vGroupsList.setAdapter(groupsAdapter);

        vOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityMain myActivity = (ActivityMain) getActivity();
                Event myEvent = myActivity.getComposeEvent();
                ArrayList<ImpromptuUser> eventPushFriendsList = (ArrayList<ImpromptuUser>) myEvent.getPushFriends();

                for (Group group : userGroupsList) {

                    if (listContainsGroup(eventPushGroupsList, group)) {
                        // if this group was in the eventPushGroupsList && is not selected, remove it
                        if (!(group.isSelected())) {
                            eventPushGroupsList.remove(group);
                            // remove friends that belong to group from eventPushFriendsList
                            ArrayList<ImpromptuUser> friends = (ArrayList<ImpromptuUser>)group.getFriendsInGroup();
                            for (ImpromptuUser friend : friends) {
                                eventPushFriendsList.remove(friend);
                            }
                        }
                    }

                    if (group.isSelected()) {
                        // if this is one of the selected groups && it was not already in the eventPushGroupsList, add it
                        if (!(listContainsGroup(eventPushGroupsList, group)))
                            eventPushGroupsList.add(group);
                    }
                }
                Collections.sort(eventPushGroupsList); // make sure eventPushGroupsList is sorted alphabetically
                myEvent.setPushGroups(eventPushGroupsList);

                for (Group group : eventPushGroupsList) {
                    ArrayList<ImpromptuUser> friendsList = (ArrayList<ImpromptuUser>) group.getFriendsInGroup();

                    for (ImpromptuUser friend : friendsList) {

                        if (!(listContainsFriend(eventPushFriendsList, friend))) {
                            eventPushFriendsList.add(friend);
                        }

                    }
                }
                Collections.sort(eventPushFriendsList);
                myEvent.setPushFriends(eventPushFriendsList);

                String test = "";
                test = test + "Groups: ";
                for (Group group : eventPushGroupsList) {
                    if (group.isSelected())
                        test = test + group.getGroupName();
                }

                Toast.makeText(getActivity(), test, Toast.LENGTH_SHORT).show();


                mCallback.onComposePushChooseGroupsFinished();
            }
        });

        vCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCallback.onComposePushChooseGroupsFinished();
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
            mCallback = (OnComposePushChooseGroupsFinishedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnComposePushFinishedListener");
        }
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
