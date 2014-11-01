package com.example.steve.impromptu.Entity;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by stephen on 10/25/14.
 */
public class StreamPost {

    User user;
    Date date;
    String content;     // Might want to use the Text class instead

    public StreamPost(User user, Date date, String content){
        this.user = user;
        this.date = date;
        this.content = content;
    }

    public HashMap<String, String> getHashMap(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("user", user.getName());
        map.put("picture", Integer.toString(user.getPicture()));
        map.put("date", date.toString());
        map.put("content", content);
        return map;
    }
}
