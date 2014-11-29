package com.example.steve.impromptu.Main.Friends;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    ArrayList<ImpromptuUser> fBFriends = new ArrayList<>();
    ArrayList<FriendAndRequestHolder> masterFriendsList = new ArrayList<>(); // holds all friend requests and friends
    ArrayList<FriendAndRequestHolder> masterFacebookFriendsList = new ArrayList<>(); // holds all of current user's FB friends in impromptu
    ArrayList<FriendAndRequestHolder> filteredList = new ArrayList<>(); // holds friend requests and friends that match search
    ImpromptuUser currentUser;
    ArrayAdapterFriendsList adapter;

    Boolean addingFriends = false;

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

//        new LoadData().execute();
        // TODO *****

        friends = (ArrayList<ImpromptuUser>) currentUser.getFriends();
        requests = (ArrayList<FriendRequest>) FriendRequest.getPendingRequestToUser(currentUser);
        fBFriends = currentUser.getFacebookFriends();

        // ******

        FriendAndRequestHolder holder;
        Boolean isFriend;
        Boolean hasSentRequest;
        Boolean isAddFBFriend = false;

        isAddFBFriend = false;
        isFriend = false;
        ImpromptuUser requestOwner;
        for (FriendRequest rqst : requests) {
            requestOwner = rqst.getFrom();
            if (!friends.contains(requestOwner)) { // TODO: won't be necessary when requests are made only from the app (i.e. not programatically)
                friendRequests.add(requestOwner);
                holder = new FriendAndRequestHolder(requestOwner, isFriend, isAddFBFriend);
                holder.setRequest(rqst);
                masterFriendsList.add(holder);
            }
        }

        isAddFBFriend = false;
        isFriend = true;
        for (ImpromptuUser user : friends) {
            holder = new FriendAndRequestHolder(user, isFriend, isAddFBFriend);
            masterFriendsList.add(holder);
        }

        isAddFBFriend = true;
        for (ImpromptuUser fBFriend : fBFriends) {
            isFriend = friends.contains(fBFriend); // is a current friend
            hasSentRequest = friendRequests.contains(fBFriend); // is sending a request

            if (!isFriend && !hasSentRequest) {
                holder = new FriendAndRequestHolder(fBFriend, isFriend, isAddFBFriend);
                masterFacebookFriendsList.add(holder);
            }
        }

        for (FriendAndRequestHolder hld : masterFriendsList) {
            filteredList.add(hld); // filteredList starts off with the same holders as masterFriendsList; will change with search
        }

        adapter = new ArrayAdapterFriendsList(getActivity(), R.layout.template_friend_or_request_item, filteredList, currentUser);
        vList.setAdapter(adapter);

        vSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                int textLength = charSequence.length();
                String text = charSequence.toString().toLowerCase();
                filteredList.clear();

                ArrayList<FriendAndRequestHolder> useThisList = new ArrayList<>();
                if (addingFriends) {
                    useThisList = masterFacebookFriendsList;
                } else {
                    useThisList = masterFriendsList;
                }
                for (FriendAndRequestHolder holder : useThisList) {
                    if (textLength <= holder.getName().length()) {
                        if (holder.getName().toLowerCase().contains(text)) {
                            filteredList.add(holder);
                        }
                    }
                }

//                adapter.updateAdapter(filteredList);
                adapter = new ArrayAdapterFriendsList(getActivity(), R.layout.template_friend_or_request_item, filteredList, currentUser);
                vList.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        vAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addingFriends) { // transition to finding FB friends on impromptu
                    vAddFriend.setImageResource(R.drawable.ic_action_add_person);
                    addingFriends = false;
                    initializeFilteredFriendsList();
                } else { // transition to current friends and requests
                    vAddFriend.setImageResource(R.drawable.ic_action_back);
                    generateMasterFBFriendsList();
                    initializeFilteredFBFriendsList();
                }
//                adapter.notifyDataSetChanged();
            }
        });

        return fragmentView;
    }

    private void initializeFilteredFriendsList() {
        filteredList.clear();
        for (FriendAndRequestHolder hld : masterFriendsList) {
            filteredList.add(hld); // filteredList starts off with the same holders as masterFriendsList; will change with search
        }
        adapter = new ArrayAdapterFriendsList(getActivity(), R.layout.template_friend_or_request_item, filteredList, currentUser);
        vList.setAdapter(adapter);
    }

    private void initializeFilteredFBFriendsList() {
        filteredList.clear();
        for (FriendAndRequestHolder hld : masterFacebookFriendsList) {
            filteredList.add(hld); // filteredList starts off with the same holders as masterFBFriendsList; will change with search
        }
        adapter = new ArrayAdapterFriendsList(getActivity(), R.layout.template_friend_or_request_item, filteredList, currentUser);
        vList.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        currentUser.persist();
    }

    public class FriendAndRequestHolder {

        private ImpromptuUser user;
        private Boolean isFriend;
        private Bitmap picture;
        private String name;
        private Boolean isAddFBFriend;
        private FriendRequest request = null;

        public FriendAndRequestHolder(ImpromptuUser user, Boolean isFriend, Boolean isAddFBFriend) {
            this.user = user;
            this.isFriend = isFriend;
            this.picture = user.getPicture();
            this.name = user.getName();
            this.isAddFBFriend = isAddFBFriend;
        }

        public Boolean getIsFriend() {
            return isFriend;
        }

//        public void setIsFriend(Boolean isFriend) {
//            this.isFriend = isFriend;
//        }

        public Bitmap getPicture() {
            return picture;
        }

        public String getName() {
            return name;
        }

        public ImpromptuUser getUser() {
            return user;
        }

        public Boolean getIsAddFBFriend() {
            return isAddFBFriend;
        }

        public FriendRequest getRequest() {
            return request;
        }

        public void setRequest(FriendRequest request) {
            this.request = request;
        }
    }

//    private class LoadData extends AsyncTask<Void, Void, Void> {
//
//        ProgressDialog progressDialog;
//        ImpromptuUser me;
//        ArrayList<ImpromptuUser> asyncFriends;
//        ArrayList<FriendRequest> asyncRequests;
//        ArrayList<ImpromptuUser> asyncFBFriends;
//
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            me = currentUser;
//            progressDialog = new ProgressDialog(getActivity());
//            progressDialog.setTitle("Retrieving your friends...");
//            progressDialog.setIndeterminate(false);
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progressDialog.setCancelable(true);
//            progressDialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... vd) {
//
//            asyncFriends = (ArrayList<ImpromptuUser>) me.getFriends();
//            asyncRequests = (ArrayList<FriendRequest>) FriendRequest.getPendingRequestToUser(me);
//            asyncFBFriends = me.getFacebookFriends();
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void vd) {
//
//            friends = this.asyncFriends;
//            requests = this.asyncRequests;
//            fBFriends = this.asyncFBFriends;
//
//            FriendAndRequestHolder holder;
//            Boolean isFriend;
//            Boolean hasSentRequest;
//            Boolean isAddFBFriend = false;
//
//            isAddFBFriend = false;
//            isFriend = false;
//            ImpromptuUser requestOwner;
//            for (FriendRequest rqst : requests) {
//                requestOwner = rqst.getFrom();
//                if (!friends.contains(requestOwner)) { // TODO: won't be necessary when requests are made only from the app (i.e. not programatically)
//                    friendRequests.add(requestOwner);
//                    holder = new FriendAndRequestHolder(requestOwner, isFriend, isAddFBFriend);
//                    holder.setRequest(rqst);
//                    masterFriendsList.add(holder);
//                }
//            }
//
//            isAddFBFriend = false;
//            isFriend = true;
//            for (ImpromptuUser user : friends) {
//                holder = new FriendAndRequestHolder(user, isFriend, isAddFBFriend);
//                masterFriendsList.add(holder);
//            }
//
//            isAddFBFriend = true;
//            for (ImpromptuUser fBFriend : fBFriends) {
//                isFriend = friends.contains(fBFriend); // is a current friend
//                hasSentRequest = friendRequests.contains(fBFriend); // is sending a request
//
//                if (!isFriend && !hasSentRequest) {
//                    holder = new FriendAndRequestHolder(fBFriend, isFriend, isAddFBFriend);
//                    masterFacebookFriendsList.add(holder);
//                }
//            }
//
//            for (FriendAndRequestHolder hld : masterFriendsList) {
//                filteredList.add(hld); // filteredList starts off with the same holders as masterFriendsList; will change with search
//            }
//
//            adapter = new ArrayAdapterFriendsList(getActivity(), R.layout.template_friend_or_request_item, filteredList, currentUser);
//            vList.setAdapter(adapter);
//
//            progressDialog.dismiss();
//        }
//
//        @Override
//        protected void onCancelled() {
//            // TODO
//
//            progressDialog.dismiss();
//        }
//    }

    public void generateMasterFBFriendsList() {

        Boolean isAddFBFriend = true;
        Boolean isFriend;
        Boolean hasSentRequest;
        FriendAndRequestHolder holder;
        masterFacebookFriendsList.clear();
        for (ImpromptuUser fBFriend : fBFriends) {
            isFriend = friends.contains(fBFriend); // is a current friend
            hasSentRequest = friendRequests.contains(fBFriend); // is sending a request

            if (!isFriend && !hasSentRequest) {
                holder = new FriendAndRequestHolder(fBFriend, isFriend, isAddFBFriend);
                masterFacebookFriendsList.add(holder);
            }
        }
    }

}
