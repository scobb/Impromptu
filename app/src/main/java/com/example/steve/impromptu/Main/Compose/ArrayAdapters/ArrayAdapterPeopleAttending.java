package com.example.steve.impromptu.Main.Compose.ArrayAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.Main.Compose.FragmentComposeType;
import com.example.steve.impromptu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephen on 12/1/14.
 */


public class ArrayAdapterPeopleAttending extends ArrayAdapter<ImpromptuUser> {

    private ArrayList<ImpromptuUser> userList;
    private Context context;
    private ImpromptuUser currentUser;

    private LayoutInflater l_Inflater;

    public ArrayAdapterPeopleAttending(Context context, int layoutResourceId,
                                       List<ImpromptuUser> users) {

        super(context, layoutResourceId, users);
        this.context = context;
        this.userList = new ArrayList<ImpromptuUser>();
        this.userList.addAll(users);
        l_Inflater = LayoutInflater.from(context);
        currentUser = ((ActivityMain)context).currentUser;

    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public ImpromptuUser getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("ConvertView", String.valueOf(position));

        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.template_friend_attending_item, null);

            ImageView vImage = (ImageView) view.findViewById(R.id.templateFriendAttendingItem_imageView_profilePic);
            TextView vText = (TextView) view.findViewById(R.id.templateFriendAttendingItem_textView_name);

            Log.d("Impromptu", "getting pic in adapter");
            ImpromptuUser user = userList.get(position);
            if (currentUser.friendMap.containsKey(user.getObjectId())) {
                user = currentUser.friendMap.get(user.getObjectId());
            }
            vImage.setImageBitmap(user.getPicture());
            vText.setText(user.getName());
        }
        else {
            view = convertView;
        }
        return view;
    }

}