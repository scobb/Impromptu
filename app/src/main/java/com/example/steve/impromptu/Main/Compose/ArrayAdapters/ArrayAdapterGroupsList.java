package com.example.steve.impromptu.Main.Compose.ArrayAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.steve.impromptu.Entity.Group;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.R;

import java.util.ArrayList;

/**
 * Created by jonreynolds on 10/31/14.
 */
public class ArrayAdapterGroupsList extends ArrayAdapter<Group> {

    private ArrayList<Group> masterList;
    private ImpromptuUser currentUser;
    private Context context;

    public ArrayAdapterGroupsList(Context context, int textViewResourceId,
                                   ArrayList<Group> masterList, ImpromptuUser currentUser) {
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
    public Group getItem(int position) {
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
            view = inflater.inflate(R.layout.template_group_item, null);
            Group group = masterList.get(position);
//            ImageView vPicture = (ImageView) view.findViewById(R.id.templateFriendOrRequestItem_imageView_picture);
            TextView vName = (TextView) view.findViewById(R.id.templateGroupItem_textView_name);
            final ImageView vRemove = (ImageView) view.findViewById(R.id.templateGroupItem_imageView_remove);

            vName.setText(group.getGroupName());
            vRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Group group = masterList.get(position);

                    ArrayList<Group> groups = (ArrayList<Group>) currentUser.getGroups();
                    groups.remove(group);
                    currentUser.persist();

                    int color = context.getResources().getColor(R.color.impromptu_remove_red);
                    View parent = (View) view.getParent();
                    parent.setBackgroundColor(color);
                    vRemove.setVisibility(View.GONE);
                }
            });

        } else {
            view = convertView;
        }
        return view;
    }
}
