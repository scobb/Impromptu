package com.example.steve.impromptu.Main.Groups;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Group;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterEditGroup;
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterGroupsList;
import com.example.steve.impromptu.R;

import java.util.ArrayList;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentGroupsList extends Fragment {


    ArrayList<ImpromptuUser> friends = new ArrayList<>();
    ArrayList<Group> groups = new ArrayList<>();
    ArrayList<FriendHolder> masterFriendsList = new ArrayList<>(); // holds all friends
    ArrayList<FriendHolder> masterFriendsInGroupList = new ArrayList<>(); // holds all friends in current group
    ArrayList<FriendHolder> filteredFriendsList = new ArrayList<>(); // holds friends that match search
    ImpromptuUser currentUser;
    ArrayAdapterGroupsList groupsAdapter;
    ArrayAdapterEditGroup editGroupAdapter;

    Boolean addingOrEditingGroups = false;
    Boolean addToGroup = false;

    Group currentGroup = null;

    ListView vFriendsInGroupList;
    ListView vGroupsList;
    ImageView vAddGroup;
    EditText vSearchBar;
    ImageView vSearch;
    EditText vEditName;
    ImageView vEdit;
    ImageView vAddToGroup;

    //TODO: add X to remove groups, add done & cancel, demolishFriendship() to remove friend and me from groups, add cancel to compose type

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_groupslist, container, false);

        vFriendsInGroupList = (ListView) fragmentView.findViewById(R.id.fragGroupsList_listView_friendsInGroupList);
        vGroupsList = (ListView) fragmentView.findViewById(R.id.fragGroupsList_listView_groupsList);
        vAddGroup = (ImageView) fragmentView.findViewById(R.id.fragGroupsList_imageView_addGroup);
        vSearchBar = (EditText) fragmentView.findViewById(R.id.fragGroupsList_editText_search);
        vSearch = (ImageView) fragmentView.findViewById(R.id.fragGroupsList_imageView_search);
        vEditName = (EditText) fragmentView.findViewById(R.id.fragGroupsList_editText_name);
        vEdit = (ImageView) fragmentView.findViewById(R.id.fragGroupsList_imageView_edit);
        vAddToGroup = (ImageView) fragmentView.findViewById(R.id.fragGroupsList_imageView_addFriend);

        currentUser = (ImpromptuUser) ImpromptuUser.getCurrentUser();
        friends = (ArrayList<ImpromptuUser>) currentUser.getFriends();
        groups = (ArrayList<Group>) currentUser.getGroups();

        FriendHolder holder;
        for (ImpromptuUser user : friends) {
            holder = new FriendHolder(user);
            masterFriendsList.add(holder);
        }

        for (FriendHolder hld : masterFriendsList) {
            filteredFriendsList.add(hld); // filteredFriendsList starts off with the same holders as masterFriendsList; will change with search
        }

        groupsAdapter = new ArrayAdapterGroupsList(getActivity(), R.layout.template_group_item, groups);
        vGroupsList.setAdapter(groupsAdapter);

        vFriendsInGroupList.setAdapter(null);

        vSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                int textLength = charSequence.length();
                String text = charSequence.toString().toLowerCase();
                filteredFriendsList.clear();

                if (addToGroup) { // searching through masterFriendsList
                    for (FriendHolder holder : masterFriendsList) {
                        if (textLength <= holder.getName().length()) {
                            if (holder.getName().toLowerCase().contains(text)) {
                                filteredFriendsList.add(holder);
                            }
                        }
                    }
                } else { // searching through masterFriendsInListGroup
                    for (FriendHolder holder : masterFriendsInGroupList) {
                        if (textLength <= holder.getName().length()) {
                            if (holder.getName().toLowerCase().contains(text)) {
                                filteredFriendsList.add(holder);
                            }
                        }
                    }
                }

                editGroupAdapter = new ArrayAdapterEditGroup(getActivity(), R.layout.template_add_to_group_item, filteredFriendsList, addToGroup, currentGroup);
                vFriendsInGroupList.setAdapter(editGroupAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        vAddGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!addingOrEditingGroups) { // transition to finding FB friends on impromptu
                    switchToAddOrEditGroup();
                } else { // transition to current groups
                    // TODO
                    currentGroup.setGroupName(vEditName.getText().toString());
                    if (currentGroup.getGroupName() != null && currentGroup.getFriendsInGroup() != null) {

                        currentGroup.persist();
                        if (!groups.contains(currentGroup)) {

                            groups.add(currentGroup);
                            currentUser.persist();
                        }
                        switchToAllGroups();

                    } else {
                        Toast.makeText(getActivity(), "You must give the group a title and at least one member.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        vGroupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentGroup = groups.get(i);
                switchToAddOrEditGroup();
            }
        });

        vAddToGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filteredFriendsList.clear();

                if (addToGroup) {
                    vAddToGroup.setImageResource(R.drawable.ic_action_new);
                    addToGroup = false;
                    buildFriendsInGroupList();
                    filteredFriendsList.addAll(masterFriendsInGroupList);
                } else {
                    vAddToGroup.setImageResource(R.drawable.ic_action_accept);
                    addToGroup = true;
                    buildFriendsList();
                    filteredFriendsList.addAll(masterFriendsList);
                }
                editGroupAdapter = new ArrayAdapterEditGroup(getActivity(), R.layout.template_add_to_group_item, filteredFriendsList, addToGroup, currentGroup);
                vFriendsInGroupList.setAdapter(editGroupAdapter);
            }
        });

        return fragmentView;
    }

    private void switchToAllGroups() {
        currentGroup = null;
        vAddGroup.setImageResource(R.drawable.ic_action_add_group);
        addingOrEditingGroups = false;
        vAddToGroup.setVisibility(View.GONE);
        vSearch.setVisibility(View.GONE);
        vSearchBar.setVisibility(View.GONE);
        vEditName.setVisibility(View.GONE);
        vEdit.setVisibility(View.GONE);
        vGroupsList.setVisibility(View.VISIBLE);
        vFriendsInGroupList.setVisibility(View.GONE);
        vFriendsInGroupList.setAdapter(null);
        groups = (ArrayList<Group>) currentUser.getGroups();
        groupsAdapter = new ArrayAdapterGroupsList(getActivity(), R.layout.template_group_item, groups);
        vGroupsList.setAdapter(groupsAdapter);
    }

    private void switchToAddOrEditGroup() {
        buildFriendsInGroupList();
        filteredFriendsList.clear();
        filteredFriendsList.addAll(masterFriendsInGroupList);
        editGroupAdapter = new ArrayAdapterEditGroup(getActivity(), R.layout.template_add_to_group_item, filteredFriendsList, addToGroup, currentGroup);
        vFriendsInGroupList.setAdapter(editGroupAdapter);
        vAddGroup.setImageResource(R.drawable.ic_action_back);
        addingOrEditingGroups = true;
        vAddToGroup.setVisibility(View.VISIBLE);
        vSearch.setVisibility(View.VISIBLE);
        vSearchBar.setVisibility(View.VISIBLE);
        vEditName.setVisibility(View.VISIBLE);
        vEdit.setVisibility(View.VISIBLE);
        vGroupsList.setVisibility(View.GONE);
        vGroupsList.setAdapter(null);
        vFriendsInGroupList.setVisibility(View.VISIBLE);
        vEditName.setText(currentGroup.getGroupName());
    }

    @Override
    public void onPause() {
        super.onPause();
        currentUser.persist();
    }

    public class FriendHolder {

        private ImpromptuUser user;
        private Bitmap picture;
        private String name;

        public FriendHolder(ImpromptuUser user) {
            this.user = user;
            this.picture = user.getPicture();
            this.name = user.getName();
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

    public void buildFriendsInGroupList() {
        masterFriendsInGroupList.clear();

        if (currentGroup != null) {
            FriendHolder holder;
            ArrayList<ImpromptuUser> friendsInGroup = (ArrayList<ImpromptuUser>) currentGroup.getFriendsInGroup();
            for (ImpromptuUser frd : friendsInGroup) {
                holder = new FriendHolder(frd);
                masterFriendsInGroupList.add(holder);
            }
        } else {
            currentGroup = new Group();
            currentGroup.clear();
        }

    }

    public void buildFriendsList() {
        masterFriendsList.clear();
        FriendHolder holder;
        ArrayList<ImpromptuUser> friendsInGroup = (ArrayList<ImpromptuUser>) currentGroup.getFriendsInGroup();
        for (ImpromptuUser friend : friends) {
            if (!friendsInGroup.contains(friend)) {
                holder = new FriendHolder(friend);
                masterFriendsList.add(holder);
            }
        }
    }
}
