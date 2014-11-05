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
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterComposePush;
import com.example.steve.impromptu.R;

import java.util.ArrayList;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentComposePush extends Fragment {

    ListView vFriendsList;
    LinearLayout vOkay;
    LinearLayout vCancel;
    ArrayList<ImpromptuUser> friendsList;

            ArrayAdapterComposePush myAdapter = null;

    OnComposePushFinishedListener mCallback;

    // Container Activity must implement this interface
    public interface OnComposePushFinishedListener {
        public void onComposePushFinished();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_compose_push, container, false);

        vFriendsList = (ListView) fragmentView.findViewById(R.id.fragComposePush_listView_list);
        vOkay = (LinearLayout) fragmentView.findViewById(R.id.fragComposePush_linearLayout_okay);
        vCancel = (LinearLayout) fragmentView.findViewById(R.id.fragComposePush_linearLayout_cancel);

        ActivityMain myActivity = (ActivityMain) getActivity();
        Event myEvent = myActivity.getNewEvent();
        friendsList = (ArrayList<ImpromptuUser>) myEvent.getPushFriends();

        if (friendsList.isEmpty()) {
            Group myGroup = createFakeGroup();
            friendsList = myGroup.getFriends();
        }

        myAdapter = new ArrayAdapterComposePush(getActivity(), R.layout.template_friend_item, friendsList);
        vFriendsList.setAdapter(myAdapter);

        vOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<ImpromptuUser> pushTo = new ArrayList<ImpromptuUser>();

//                for (ImpromptuUser friend : friendsList) {
//
//                    if (friend.isSelected()) {
//                        pushTo.add(friend);
//                    }
//
//                }
                // TODO: decide whether to pass just the selected friends, or the entire list and
                // determine later which should be pushed to
                pushTo.addAll(friendsList);

                String test = "test: ";
                for (ImpromptuUser friend : pushTo) {
                    if (friend.isSelected())
                        test = test + friend.getName() + " ";
                }

                Toast.makeText(getActivity(), test, Toast.LENGTH_SHORT).show();

                ActivityMain myActivity = (ActivityMain) getActivity();
                Event myEvent = myActivity.getNewEvent();
                myEvent.setPushFriends(pushTo);

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
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnComposePushFinishedListener");
        }
    }


    public Group createFakeGroup() {
        Group myGroup = new Group();
        ImpromptuUser friend1 = new ImpromptuUser("Bob Newman");
        ImpromptuUser friend2 = new ImpromptuUser("Wolfgang Mozart");
        ImpromptuUser friend3 = new ImpromptuUser("John Williams");
        ImpromptuUser friend4 = new ImpromptuUser("Alexandre Desplat");
        ImpromptuUser friend5 = new ImpromptuUser("Hans Zimmer");

        myGroup.add(friend1);
        myGroup.add(friend2);
        myGroup.add(friend3);
        myGroup.add(friend4);
        myGroup.add(friend5);

        return myGroup;
    }

}
