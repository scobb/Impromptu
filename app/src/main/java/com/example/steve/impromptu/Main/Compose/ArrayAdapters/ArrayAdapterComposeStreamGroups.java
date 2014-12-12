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
import com.example.steve.impromptu.R;

import java.util.ArrayList;

/**
 * Created by jonreynolds on 10/31/14.
 */
public class ArrayAdapterComposeStreamGroups extends ArrayAdapter<Group> {

    private ArrayList<Group> groupList;
    private Context context;

    public ArrayAdapterComposeStreamGroups(Context context, int textViewResourceId,
                                         ArrayList<Group> groupList) {
        super(context, textViewResourceId, groupList);
        this.context = context;
        this.groupList = new ArrayList<Group>();
        this.groupList.addAll(groupList);
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
                            Group group = (Group) viewHolder.checkbox
                                    .getTag();
                            group.setSelected(buttonView.isChecked());

                        }
                    });
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(groupList.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(groupList.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(groupList.get(position).getGroupName());
        holder.image.setVisibility(View.GONE);
        holder.checkbox.setChecked(groupList.get(position).isSelected());
        return view;
    }
}
