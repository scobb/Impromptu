package com.example.steve.impromptu.Main.Compose.ArrayAdapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Group;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.Groups.FragmentGroupsList;
import com.example.steve.impromptu.R;

import java.util.ArrayList;

/**
* Created by jonreynolds on 10/31/14.
*/
public class ArrayAdapterEditGroup extends ArrayAdapter<FragmentGroupsList.FriendHolder> {

    private ArrayList<FragmentGroupsList.FriendHolder> masterList;
    private Context context;
    private Boolean isAddingToGroup = false;
    Group currentGroup = null;

    public ArrayAdapterEditGroup (Context context, int textViewResourceId,
                                  ArrayList<FragmentGroupsList.FriendHolder> masterList, Boolean isAddingToGroup, Group currentGroup) {
        super(context, textViewResourceId, masterList);
        this.context = context;
        this.masterList = masterList;
        this.isAddingToGroup = isAddingToGroup;
        this.currentGroup = currentGroup;
    }

    @Override
    public int getCount() {
        return masterList.size();
    }

    @Override
    public FragmentGroupsList.FriendHolder getItem(int position) {
        return masterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.v("ConvertView", String.valueOf(position));

        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.template_add_to_group_item, null);
            FragmentGroupsList.FriendHolder holder = masterList.get(position);
            ImageView vPicture = (ImageView) view.findViewById(R.id.templateAddToGroupItem_imageView_picture);
            final ImageView vChange = (ImageView) view.findViewById(R.id.templateAddToGroupItem_imageView_change);
            TextView vName = (TextView) view.findViewById(R.id.templateAddToGroupItem_textView_name);

            Bitmap picture = holder.getPicture();
            if (picture != null) {
                vPicture.setImageBitmap(holder.getPicture());
            }
            else {
                vPicture.setImageResource(R.drawable.ic_launcher);
            }

            vName.setText(holder.getName());

            if (isAddingToGroup) { // plus sign next to someone in friends list

                vChange.setImageResource(R.drawable.ic_action_new);
                vChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ImpromptuUser newGroupMember = masterList.get(position).getUser();
                        ArrayList<ImpromptuUser> friendsInGroup = (ArrayList<ImpromptuUser>) currentGroup.getFriendsInGroup();
                        if (!friendsInGroup.isEmpty()) {
                            if (!currentGroup.getFriendsInGroup().contains(newGroupMember)) {
                                currentGroup.add(newGroupMember);

                                int color = context.getResources().getColor(R.color.impromptu_add_green);
                                View parent = (View) view.getParent();
                                parent.setBackgroundColor(color);
                                vChange.setVisibility(View.GONE);
                            }
                            else {
                                Toast.makeText(context, newGroupMember.getName() + " is already in your group.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            currentGroup.add(newGroupMember);

                            int color = context.getResources().getColor(R.color.impromptu_add_green);
                            View parent = (View) view.getParent();
                            parent.setBackgroundColor(color);
                            vChange.setVisibility(View.GONE);
                        }
                    }
                });
            }
            else { // X next to someone currently in group
                vChange.setImageResource(R.drawable.ic_action_remove);
                vChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ImpromptuUser removeGroupMember = masterList.get(position).getUser();
                        currentGroup.remove(removeGroupMember);

                        int color = context.getResources().getColor(R.color.impromptu_remove_red);
                        View parent = (View) view.getParent();
                        parent.setBackgroundColor(color);
                        vChange.setVisibility(View.GONE);

                    }
                });
            }

        } else {
            view = convertView;
        }
        return view;
    }
}
