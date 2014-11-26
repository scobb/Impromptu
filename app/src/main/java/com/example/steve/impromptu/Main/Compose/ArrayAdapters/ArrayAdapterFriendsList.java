package com.example.steve.impromptu.Main.Compose.ArrayAdapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

    static class ViewHolder {
        protected TextView name;
        protected Picture profilePicture;
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
            ImageView vAccept = (ImageView) view.findViewById(R.id.templateFriendOrRequestItem_imageView_accept);
            ImageView vDecline = (ImageView) view.findViewById(R.id.templateFriendOrRequestItem_imageView_decline);
            TextView vName = (TextView) view.findViewById(R.id.templateFriendOrRequestItem_textView_name);

            Bitmap picture = holder.getPicture();
            if (picture != null) {
                vPicture.setImageBitmap(holder.getPicture());
            }

            vName.setText(holder.getName());

            if (holder.getIsFriend()) {
                vAccept.setOnClickListener(null);

                vDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        masterList.remove(position);
                        currentUser.removeFriend(masterList.get(position).getUser());
                        int color = context.getResources().getColor(android.R.color.holo_red_light);
                        View parent = (View) view.getParent();
                        parent.setBackgroundColor(color);

                    }
                });

                vAccept.setVisibility(View.INVISIBLE);
            } else {
                vAccept.setVisibility(View.VISIBLE);

                vAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        FragmentFriendsList.FriendAndRequestHolder holder = masterList.get(position);
                        currentUser.addFriend(holder.getUser());
                        int color = context.getResources().getColor(android.R.color.holo_green_light);
                        View parent = (View) view.getParent();
                        parent.setBackgroundColor(color);//                        holder.setIsFriend(true);
//                        masterList.add(holder);
//                        masterList.remove(position);

                    }
                });

                vDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        masterList.remove(position);
                        int color = context.getResources().getColor(android.R.color.holo_red_light);
                        View parent = (View) view.getParent();
                        parent.setBackgroundColor(color);
                    }
                });
            }
        } else {
            view = convertView;
        }
        return view;
    }
}
