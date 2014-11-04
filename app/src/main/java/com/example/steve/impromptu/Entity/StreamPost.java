package com.example.steve.impromptu.Entity;

import com.example.steve.impromptu.R;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by stephen on 10/25/14.
 */
public class StreamPost {

    ImpromptuUser user;
    Date date;
    String content;     // Might want to use the Text class instead

    public StreamPost(ImpromptuUser user, Date date, String content){
        this.user = user;
        this.date = date;
        this.content = content;
    }

    public HashMap<String, String> getHashMap(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("user", user.getName());
        map.put("picture", Integer.toString(R.drawable.ic_launcher));
        map.put("date", date.toString());
        map.put("content", content);
        return map;
    }

    public ImpromptuUser getUser(){
        return user;
    }
}
