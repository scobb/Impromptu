package com.example.steve.impromptu.Entity;

import android.util.Log;

import com.example.steve.impromptu.R;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by stephen on 10/25/14.
 */
@ParseClassName("StreamPost")
public class StreamPost extends ParseObject{

    public StreamPost() {
        super();
    }

    public StreamPost(ImpromptuUser user, Date date, String content){
        super();
        this.setUser(user);
        this.setDate(date);
        this.setContent(content);
    }


    public HashMap<String, String> getHashMap(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("user", this.getUser().getName());
        map.put("picture", Integer.toString(R.drawable.ic_launcher));
        map.put("date", this.getDate().toString());
        map.put("content", this.getContent());
        return map;
    }

    public String getContent(){
        return this.getString("content");
    }
    public Date getDate() {
        return this.getDate("date");
    }
    public ImpromptuUser getUser() {
        return (ImpromptuUser)this.get("user");
    }
    public void setContent(String content) {
        this.put("content", content);
    }
    public void setUser(ImpromptuUser user) {
        this.put("user", user);
    }
    public void setDate(Date date) {
        this.put("date", date);
    }
    public void persist() {
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("Impromptu", "exception in persisting streampost", e);
                }
            }
        });
//        d8ae159fe156f6e7b833e712c005e463ac4733cf
    }
}
