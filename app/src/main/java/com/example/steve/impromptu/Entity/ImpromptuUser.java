package com.example.steve.impromptu.Entity;

import com.parse.ParseUser;

/**
 * ImpromptuUser object
 * Created by scobb on 10/19/14.
 */
public class ImpromptuUser extends ParseUser{
    public ImpromptuUser() {
        super();
    }

    public ImpromptuUser(String username) {
        super();
        this.setName(username);
    }

    public ImpromptuUser(String username, String pw, String email) {
        // use parse's interface to set basic info
        super();
        this.setUsername(username);
        this.setPassword(pw);
        this.setEmail(email);
    }

    public void setName(String name){
        this.put("name", name);
    }

    public String getName() {
        return this.get("name").toString();
    }

    public Integer getPicture() {
        return null;
    }

}
