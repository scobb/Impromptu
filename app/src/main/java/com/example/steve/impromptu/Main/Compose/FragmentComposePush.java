package com.example.steve.impromptu.Main.Compose;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.steve.impromptu.Entity.Friend;
import com.example.steve.impromptu.Entity.Group;
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

    ArrayAdapterComposePush myAdapter = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_compose_push, container, false);

        vFriendsList = (ListView) fragmentView.findViewById(R.id.fragComposePush_listView_list);
        vOkay = (LinearLayout) fragmentView.findViewById(R.id.fragComposePush_linearLayout_okay);
        vCancel = (LinearLayout) fragmentView.findViewById(R.id.fragComposePush_linearLayout_cancel);

        Group myGroup = createFakeGroup();
        ArrayList<Friend> friendsList = myGroup.getFriends();
        myAdapter = new ArrayAdapterComposePush(getActivity(), R.layout.template_friend_item, friendsList);

        return fragmentView;
    }

    public Group createFakeGroup() {
        Group myGroup = new Group();
        Friend friend1 = new Friend("Bob Newman");
        Friend friend2 = new Friend("Wolfgang Mozart");
        Friend friend3 = new Friend("John Williams");
        Friend friend4 = new Friend("Alexandre Desplat");
        Friend friend5 = new Friend("Hans Zimmer");

        myGroup.add(friend1);
        myGroup.add(friend2);
        myGroup.add(friend3);
        myGroup.add(friend4);
        myGroup.add(friend5);

        return myGroup;
    }

}
