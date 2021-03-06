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

import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.Friends.FragmentFriendsList;
import com.example.steve.impromptu.R;

import java.util.ArrayList;

/**
 * Created by jonreynolds on 10/31/14.
 */
public class ArrayAdapterFriendsList extends ArrayAdapter<FragmentFriendsList.FriendAndRequestHolder> {

    private ArrayList<FragmentFriendsList.FriendAndRequestHolder> masterList;
    private ImpromptuUser currentUser;
    private Context context;

    public ArrayAdapterFriendsList(Context context, int textViewResourceId,
                                   ArrayList<FragmentFriendsList.FriendAndRequestHolder> masterList, ImpromptuUser currentUser) {
        super(context, textViewResourceId, masterList);
        this.context = context;
        this.masterList = masterList;
        this.currentUser = currentUser;
    }

    @Override
    public int getCount() {
        return masterList.size();
    }

    @Override
    public FragmentFriendsList.FriendAndRequestHolder getItem(int position) {
        return masterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    public void updateAdapter(ArrayList<FragmentFriendsList.FriendAndRequestHolder> updatedList) {
//        this.masterList = updatedList;
//        this.notifyDataSetChanged();
//    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.v("ConvertView", String.valueOf(position));

        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.template_friend_or_request_item, null);
            FragmentFriendsList.FriendAndRequestHolder holder = masterList.get(position);
            ImageView vPicture = (ImageView) view.findViewById(R.id.templateFriendOrRequestItem_imageView_picture);
            final ImageView vAccept = (ImageView) view.findViewById(R.id.templateFriendOrRequestItem_imageView_accept);
            final ImageView vDecline = (ImageView) view.findViewById(R.id.templateFriendOrRequestItem_imageView_decline);
            TextView vName = (TextView) view.findViewById(R.id.templateFriendOrRequestItem_textView_name);

            Bitmap picture = holder.getPicture();
            if (picture != null) {
                vPicture.setImageBitmap(holder.getPicture());
            }
            else {
                vPicture.setImageResource(R.drawable.ic_launcher);
            }

            vName.setText(holder.getName());

            if (holder.getIsAddFBFriend()) { // making list of people who are not my friends

                vDecline.setOnClickListener(null);
                vDecline.setVisibility(View.GONE);

                vAccept.setVisibility(View.VISIBLE);
                vAccept.setImageResource(R.drawable.ic_action_new);
                vAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ImpromptuUser newFriend = masterList.get(position).getUser();
                        Boolean sent = currentUser.createFriendRequest(newFriend);
                        if (!sent) {
                            Toast.makeText(context, "You have already sent a friend request to " + newFriend.getName(), Toast.LENGTH_LONG);
                        }
                        else {
                            int color = context.getResources().getColor(R.color.impromptu_add_green);
                            View parent = (View) view.getParent();
                            parent.setBackgroundColor(color);
                            vAccept.setVisibility(View.GONE);
                        }

                    }
                });

            } else { // making list of current friends and people requesting friendship
                if (holder.getIsFriend()) { // is current friend (means not requesting friendship)
                    vAccept.setOnClickListener(null);
                    vAccept.setVisibility(View.GONE);

                    vDecline.setVisibility(View.VISIBLE);
                    vDecline.setImageResource(R.drawable.ic_action_remove);
                    vDecline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            ImpromptuUser oldFriend = masterList.get(position).getUser();
                            currentUser.removeFriend(oldFriend);
                            currentUser.destroyFriendship(oldFriend);
                            int color = context.getResources().getColor(R.color.impromptu_remove_red);
                            View parent = (View) view.getParent();
                            parent.setBackgroundColor(color);
                            vDecline.setVisibility(View.GONE);

                        }
                    });
                } else { // is not a current friend (means seeking friendship)
                    vAccept.setVisibility(View.VISIBLE);
                    vAccept.setImageResource(R.drawable.ic_action_accept);
                    vAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            FragmentFriendsList.FriendAndRequestHolder holder = masterList.get(position);
                            holder.getRequest().accept();
                            int color = context.getResources().getColor(R.color.impromptu_add_green);
                            View parent = (View) view.getParent();
                            parent.setBackgroundColor(color);

                            vAccept.setVisibility(View.GONE);
                            vDecline.setVisibility(View.GONE);

                        }
                    });

                    vDecline.setVisibility((View.VISIBLE));
                    vDecline.setImageResource(R.drawable.ic_action_discard);
                    vDecline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            FragmentFriendsList.FriendAndRequestHolder holder = masterList.get(position);
                            holder.getRequest().decline();
                            int color = context.getResources().getColor(R.color.impromptu_remove_red);
                            View parent = (View) view.getParent();
                            parent.setBackgroundColor(color);

                            vAccept.setVisibility(View.GONE);
                            vDecline.setVisibility(View.GONE);

                        }
                    });
                }
            }
        } else {
            view = convertView;
        }
        return view;
    }
}
