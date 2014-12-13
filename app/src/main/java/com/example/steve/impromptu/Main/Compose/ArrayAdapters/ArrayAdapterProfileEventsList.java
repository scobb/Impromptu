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
import com.example.steve.impromptu.R;
import com.parse.ParseCloud;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jonreynolds on 10/31/14.
 */
public class ArrayAdapterProfileEventsList extends ArrayAdapter<Event> {

    private ArrayList<Event> eventList;
    private ImpromptuUser currentUser;
    private Context context;

    public ArrayAdapterProfileEventsList(Context context, int textViewResourceId,
                                         ArrayList<Event> eventList, ImpromptuUser currentUser) {
        super(context, textViewResourceId, eventList);
        this.context = context;
        this.eventList = eventList;
        this.currentUser = currentUser;
    }

    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Event getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    public void updateAdapter(ArrayList<FragmentFriendsList.FriendAndRequestHolder> updatedList) {
//        this.eventList = updatedList;
//        this.notifyDataSetChanged();
//    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.v("ConvertView", String.valueOf(position));

        View view = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.template_profile_event_item, null);
            Event event = eventList.get(position);

            ImageView vPicture = (ImageView) view.findViewById(R.id.templateProfileEventItem_imageView_picture);
            final ImageView vDiscard = (ImageView) view.findViewById(R.id.templateProfileEventItem_imageView_discard);
            TextView vName = (TextView) view.findViewById(R.id.templateProfileEventItem_textView_eventTitle);

            String eventType = event.getType();

            if (eventType != null) {
                switch (eventType) {
                    case "Sports":
                        vPicture.setImageResource(R.drawable.sport_icon);
                        break;
                    case "Drinking":
                        vPicture.setImageResource(R.drawable.drinking);
                        break;
                    case "Eating":
                        vPicture.setImageResource(R.drawable.food);
                        break;
                    case "TV":
                        vPicture.setImageResource(R.drawable.tv);
                        break;
                    case "Studying":
                        vPicture.setImageResource(R.drawable.studying);
                        break;
                    case "Working Out":
                        vPicture.setImageResource(R.drawable.working_out);
                        break;
                    default:
                        vPicture.setImageResource(R.drawable.ic_launcher);
                }
            }

            vName.setText(event.getTitle());


            vDiscard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Event deleteEvent = eventList.get(position);
                    eventList.remove(deleteEvent);
//
                    HashMap<String, String> args = new HashMap<>();
                    args.clear();
                    args.put("eventId", deleteEvent.getObjectId());
                    ParseCloud.callFunctionInBackground("eventCleanup", args, null);

                    int color = context.getResources().getColor(R.color.impromptu_remove_red);
                    View parent = (View) view.getParent();
                    parent.setBackgroundColor(color);
                    vDiscard.setVisibility(View.GONE);

                }
            });


        } else {
            view = convertView;
        }
        return view;
    }

}
