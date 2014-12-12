package com.example.steve.impromptu.Main.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.UpdateView;
import com.parse.ParseCloud;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Steve on 12/8/2014.
 */
public class AsyncTaskPopulateOwnedEvents extends AsyncTask<List<Event>, Void, List<Event>> {

    private UpdateView updateView;

    public void setUpdateView(UpdateView updateView) {
        this.updateView = updateView;
    }


    @Override
    protected List<Event> doInBackground(List<Event>... events) {
        Log.d("Impromptu", "In doInBackground");
        List<Event> posts = events[0];

        Iterator<Event> i = posts.iterator();
        HashMap<String, String> args = new HashMap<>();
        while (i.hasNext()) {
            Event event = i.next();
            long endMillis = event.getEventTime().getTime() + event.getDurationHour() * 3600 * 1000 + event.getDurationMinute() * 60 * 1000;
            long nowMillis = new Date().getTime();
            if (nowMillis > endMillis) {
                args.clear();
                args.put("eventId", event.getObjectId());
                ParseCloud.callFunctionInBackground("eventCleanup", args, null);
                i.remove();
            }
        }
        Log.d("Impromptu", "Done with doInBackground");
        return posts;
    }
    protected void onPostExecute(List<Event> events) {
        Log.d("Impromptu", "Updating dat view");
        updateView.update(events);
        updateView.clearLoad();
    }
}
