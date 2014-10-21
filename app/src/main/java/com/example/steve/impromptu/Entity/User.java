package com.example.steve.impromptu.Entity;

import com.parse.ParseUser;

/**
 * User object
 * Created by scobb on 10/19/14.
 */
public class User extends ParseUser{
    public User(String username, String pw, String email) {
        // use parse's interface to set basic info
        super();
        this.setUsername(username);
        this.setPassword(pw);
        this.setEmail(email);
    }

}
