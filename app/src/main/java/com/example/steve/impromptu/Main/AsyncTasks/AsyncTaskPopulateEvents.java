package com.example.steve.impromptu.Main.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Entity.UpdateView;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Steve on 12/7/2014.
 */
public class AsyncTaskPopulateEvents extends AsyncTask<ImpromptuUser, Void, List<Event>> {

    private UpdateView updateView;

    public void setUpdateView(UpdateView updateView) {
        this.updateView = updateView;
    }


    @Override
    protected List<Event> doInBackground(ImpromptuUser... users) {
        // grab user to populate
        ImpromptuUser user = users[0];
        Log.d("Impromptu", "User id: " + user.getObjectId());
        try {
            user.fetch();
            Log.d("Impromptu", "Fetch successful.");
        } catch (ParseException exc) {
            Log.e("Impromptu", "Parse exception refreshing.", exc);
        }
        List<Event> events = user.getList("events");
        Log.d("Impromptu", "num events before validation: " + events.size());
        long nowMillis = System.currentTimeMillis();
        Iterator<Event> i = events.iterator();
        user.eventMap.clear();
        while (i.hasNext()) {
            Event event = i.next();
            HashMap<String, String> args = new HashMap<>();
            long endMillis = event.getEventTime().getTime() + event.getDurationHour() * 3600 * 1000 + event.getDurationMinute() * 60 * 1000;
            Log.d("Impromptu", "nowMillis: " + nowMillis + "\nendMillis: " + endMillis);
            if (endMillis < nowMillis) {
                Log.d("Impromptu", "Would remove " + event.getObjectId());
                args.clear();
                args.put("eventId", event.getObjectId());
                ParseCloud.callFunctionInBackground("eventCleanup", args, null);
                i.remove();
            } else {
                // load this list for use later
                event.getUsersGoing();
                event.getLocationName();
                user.eventMap.put(event.getObjectId(), event);
            }
        }
        Collections.sort(events);
        user.streamEvents = events;
        Log.d("Impromptu", "num events: " + events.size());
        return events;

    }
    protected void onPostExecute(List<Event> events) {
        Log.d("Impromptu", "Updating dat view");
        updateView.update(events);
        updateView.clearLoad();
    }

}
