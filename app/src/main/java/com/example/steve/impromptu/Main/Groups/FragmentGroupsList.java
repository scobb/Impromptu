//package com.example.steve.impromptu.Main.Groups;
//
//import android.app.Fragment;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ListView;
//
//import com.example.steve.impromptu.Entity.Group;
//import com.example.steve.impromptu.Entity.ImpromptuUser;
//import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterEditGroup;
//import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterGroupsList;
//import com.example.steve.impromptu.R;
//
//import java.util.ArrayList;
//
///**
// * Created by jonreynolds on 10/16/14.
// */
//public class FragmentGroupsList extends Fragment {
//
//
//    ArrayList<ImpromptuUser> friends = new ArrayList<>();
//    ArrayList<Group> groups = new ArrayList<>();
//    ArrayList<FriendHolder> masterFriendsList = new ArrayList<>(); // holds all friends
//    ArrayList<FriendHolder> filteredFriendsList = new ArrayList<>(); // holds friends that match search
//    ImpromptuUser currentUser;
//    ArrayAdapterGroupsList groupsAdapter;
//    ArrayAdapterEditGroup editGroupAdapter;
//
//    Boolean addingOrEditingGroups = false;
//
//    Group currentGroup = null;
//
//    ListView vFriendsInGroupsList;
//    ListView vGroupsList;
//    ImageView vAddGroup;
//    EditText vSearchBar;
//    ImageView vSearch;
//    EditText vEditName;
//    ImageView vEdit;
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        // Inflate the layout for this fragment
//        View fragmentView = inflater.inflate(R.layout.fragment_friendslist, container, false);
//
//        vFriendsInGroupsList = (ListView) fragmentView.findViewById(R.id.fragGroupsList_listView_friendsInGroupList);
//        vGroupsList = (ListView) fragmentView.findViewById(R.id.fragGroupsList_listView_groupsList);
//        vAddGroup = (ImageView) fragmentView.findViewById(R.id.fragGroupsList_imageView_addGroup);
//        vSearchBar = (EditText) fragmentView.findViewById(R.id.fragGroupsList_editText_search);
//        vSearch = (ImageView) fragmentView.findViewById(R.id.fragGroupsList_imageView_search);
//        vEditName = (EditText) fragmentView.findViewById(R.id.fragGroupsList_editText_name);
//        vEdit = (ImageView) fragmentView.findViewById(R.id.fragGroupsList_imageView_edit);
//
//        currentUser = (ImpromptuUser) ImpromptuUser.getCurrentUser();
//        friends = (ArrayList<ImpromptuUser>) currentUser.getFriends();
//        groups = (ArrayList<Group>) currentUser.getGroups();
//
//        FriendHolder holder;
//        for (ImpromptuUser user : friends) {
//            holder = new FriendHolder(user);
//            masterFriendsList.add(holder);
//        }
//
//        for (FriendHolder hld : masterFriendsList) {
//            filteredFriendsList.add(hld); // filteredFriendsList starts off with the same holders as masterFriendsList; will change with search
//        }
//
//        groupsAdapter = new ArrayAdapterGroupsList(getActivity(), R.layout.template_group_item, groups);
//        vGroupsList.setAdapter(groupsAdapter);
//
//        editGroupAdapter = new ArrayAdapterEditGroup(getActivity(), R.layout.template_add_to_group_item, filteredFriendsList, currentUser);
//        vFriendsInGroupsList.setAdapter(editGroupAdapter);
//
//        vSearchBar.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//
//                int textLength = charSequence.length();
//                String text = charSequence.toString().toLowerCase();
//                filteredFriendsList.clear();
//
//                for (FriendHolder holder : masterFriendsList) {
//                    if (textLength <= holder.getName().length()) {
//                        if (holder.getName().toLowerCase().contains(text)) {
//                            filteredFriendsList.add(holder);
//                        }
//                    }
//                }
//
////                adapter.updateAdapter(filteredFriendsList);
//                editGroupAdapter = new ArrayAdapterEditGroup(getActivity(), R.layout.template_add_to_group_item, filteredFriendsList, currentUser);
//                vFriendsInGroupsList.setAdapter(editGroupAdapter);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//
//        vAddGroup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!addingOrEditingGroups) { // transition to finding FB friends on impromptu
//                    vAddGroup.setImageResource(R.drawable.ic_action_back);
//                    addingOrEditingGroups = true;
//                    vSearch.setVisibility(View.VISIBLE);
//                    vSearchBar.setVisibility(View.VISIBLE);
//                    vEditName.setVisibility(View.VISIBLE);
//                    vEdit.setVisibility(View.VISIBLE);
//                    vGroupsList.setVisibility(View.GONE);
//                    vFriendsInGroupsList.setVisibility(View.VISIBLE);
////                    initializeFilteredGroupsList();
//                }
//                else { // transition to current groups
//                    vAddGroup.setImageResource(R.drawable.ic_action_add_group);
//                    addingOrEditingGroups = false;
//                    vSearch.setVisibility(View.GONE);
//                    vSearchBar.setVisibility(View.GONE);
//                    vEditName.setVisibility(View.GONE);
//                    vEdit.setVisibility(View.GONE);
//                    vGroupsList.setVisibility(View.VISIBLE);
//                    vFriendsInGroupsList.setVisibility(View.GONE);
////                    initializeFilteredFBGroupsList();
//                }
////                adapter.notifyDataSetChanged();
//            }
//        });
//
//        return fragmentView;
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        currentUser.persist();
//    }
//
//    public class FriendHolder {
//
//        private ImpromptuUser user;
//        private Bitmap picture;
//        private String name;
//        private Boolean inGroup;
//
//        public FriendHolder(ImpromptuUser user, Boolean inGroup) {
//            this.user = user;
//            this.picture = user.getPicture();
//            this.name = user.getName();
//            this.inGroup = inGroup;
//        }
//
//        public Bitmap getPicture() {
//            return picture;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public ImpromptuUser getUser() {
//            return user;
//        }
//    }
//
//}
