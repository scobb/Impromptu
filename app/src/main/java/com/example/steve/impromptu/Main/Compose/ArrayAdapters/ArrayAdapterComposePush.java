package com.example.steve.impromptu.Main.Compose.ArrayAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Friend;
import com.example.steve.impromptu.R;

import java.util.ArrayList;

/**
 * Created by jonreynolds on 10/31/14.
 */
public class ArrayAdapterComposePush extends ArrayAdapter<Friend> {

    private ArrayList<Friend> friendList;
    private Context context;

    public ArrayAdapterComposePush(Context context, int textViewResourceId,
                           ArrayList<Friend> friendList) {
        super(context, textViewResourceId, friendList);
        this.context = context;
        this.friendList = new ArrayList<Friend>();
        this.friendList.addAll(friendList);
    }

    private class ViewHolder {
        TextView friendName;
        CheckBox check;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.template_friend_item, null);

            holder = new ViewHolder();
            holder.friendName = (TextView) convertView.findViewById(R.id.templateFriendItem_textView_friendName);
            holder.check = (CheckBox) convertView.findViewById(R.id.templateFriendItem_checkBox_check);
            convertView.setTag(holder);

            holder.check.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    Friend friend = (Friend) cb.getTag();
                    Toast.makeText(context,
                            "Clicked on Checkbox: " + cb.getText() +
                                    " is " + cb.isChecked(),
                            Toast.LENGTH_LONG).show();
                    friend.setSelected(cb.isChecked());
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Friend friend = friendList.get(position);
        holder.friendName.setText(friend.getName());
        holder.check.setChecked(friend.isSelected());
        holder.check.setTag(friend);

        return convertView;

    }
}
