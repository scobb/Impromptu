package com.example.steve.impromptu.Main.Compose.ArrayAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.steve.impromptu.Entity.Group;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.R;

import java.util.ArrayList;

/**
 * Created by jonreynolds on 10/31/14.
 */
public class ArrayAdapterComposePushFriends extends ArrayAdapter<ImpromptuUser> {

    private ArrayList<ImpromptuUser> friendList;
    private ArrayList<Group> groupsList;
    private Context context;

    public ArrayAdapterComposePushFriends(Context context, int textViewResourceId,
                                          ArrayList<ImpromptuUser> friendList, ArrayList<Group> groupsList) {
        super(context, textViewResourceId, friendList);
        this.context = context;
        this.friendList = new ArrayList<ImpromptuUser>();
        this.groupsList = new ArrayList<Group>();
        this.friendList.addAll(friendList);
        this.groupsList.addAll(groupsList);
    }

    static class ViewHolder {
        protected TextView text;
        protected ImageView image;
        protected CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.template_friend_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.templateFriendItem_textView_friendName);
            viewHolder.image = (ImageView) view.findViewById(R.id.templateFriendItem_imageView_picture);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.templateFriendItem_checkBox_check);
            viewHolder.checkbox
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            ImpromptuUser friend = (ImpromptuUser) viewHolder.checkbox
                                    .getTag();
                            if (isChecked) {
                                friend.setSelected(true);
                            }

                            else {
                                friend.setSelected(false);

                                for (Group group : groupsList) {
                                    ArrayList<ImpromptuUser> friends = (ArrayList<ImpromptuUser>) group.getFriendsInGroup();
                                    if (friends.contains(friend)) {
                                        group.setSelected(false);
                                    }
                                }
                            }
                        }
                    });
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(friendList.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(friendList.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(friendList.get(position).getName());
        holder.image.setImageBitmap(friendList.get(position).getPicture());
        holder.checkbox.setChecked(friendList.get(position).isSelected());
        return view;
    }
}
