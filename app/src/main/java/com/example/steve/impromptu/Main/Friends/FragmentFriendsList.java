package com.example.steve.impromptu.Main.Friends;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.steve.impromptu.Entity.FriendRequest;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterFriendsList;
import com.example.steve.impromptu.R;

import java.util.ArrayList;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentFriendsList extends Fragment {

    ArrayList<FriendRequest> requests = new ArrayList<>();
    ArrayList<ImpromptuUser> friends = new ArrayList<>();
    ArrayList<ImpromptuUser> friendRequests = new ArrayList<>();
    ArrayList<FriendAndRequestHolder> masterList = new ArrayList<>(); // holds all friend requests and friends
    ImpromptuUser currentUser;
    static ArrayAdapterFriendsList adapter;

    Boolean flag = false;

    ListView vList;
    ImageView vAddFriend;
    EditText vSearchBar;
    ImageView vSearch;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_friendslist, container, false);

        vList = (ListView) fragmentView.findViewById(R.id.fragFriendsList_listView_friendsList);
        vAddFriend = (ImageView) fragmentView.findViewById(R.id.fragFriendsList_imageView_addFriend);
        vSearchBar = (EditText) fragmentView.findViewById(R.id.fragFriendsList_editText_search);
        vSearch = (ImageView) fragmentView.findViewById(R.id.fragFriendsList_imageView_search);

        currentUser = (ImpromptuUser) ImpromptuUser.getCurrentUser();
        friends = (ArrayList<ImpromptuUser>) currentUser.getFriends();
        requests = (ArrayList<FriendRequest>) FriendRequest.getPendingRequestToUser(currentUser);


        for (FriendRequest rqst : requests) {
            friendRequests.add(rqst.getFrom());
        }

        FriendAndRequestHolder holder = null;
        for (ImpromptuUser user : friendRequests) {
            holder = new FriendAndRequestHolder(user, false);
            masterList.add(holder);
        }

        for (ImpromptuUser user : friends) {
            holder = new FriendAndRequestHolder(user, true);
            masterList.add(holder);
        }

        adapter = new ArrayAdapterFriendsList(getActivity(), R.layout.template_friend_or_request_item, masterList, currentUser);
        vList.setAdapter(adapter);

        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // TODO
//        // This makes sure that the container activity has implemented
//        // the callback interface. If not, it throws an exception
//        try {
//            mCallback = (OnComposePushFinishedListener) activity;
//            mGroupsCallback = (OnComposePushChooseGroupsListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnComposePushFinishedListener");
//        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //TODO: does this save removed and added friends?
        currentUser.persist();
    }

    public class FriendAndRequestHolder {

        private ImpromptuUser user;
        private Boolean isFriend;
        private Bitmap picture;
        private String name;

        public FriendAndRequestHolder(ImpromptuUser user, Boolean isFriend) {
            this.user = user;
            this.isFriend = isFriend;
            this.picture = user.getPicture();
            this.name = user.getName();
        }

        public Boolean getIsFriend() {
            return isFriend;
        }

        public void setIsFriend(Boolean isFriend) {
            this.isFriend = isFriend;
        }

        public Bitmap getPicture() {
            return picture;
        }

        public String getName() {
            return name;
        }

        public ImpromptuUser getUser() {
            return user;
        }
    }

    public static void notifyChange() {
        adapter.notifyDataSetChanged();
    }

}
