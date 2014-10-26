package com.example.steve.impromptu.Entity;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.steve.impromptu.Login.ActivityLogin;
import com.example.steve.impromptu.Main.ActivityMain;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * ImpromptuUser object
 * Created by scobb on 10/19/14.
 */
public class ImpromptuUser extends ParseUser{
    public ImpromptuUser() {
        super();
    }
    public ImpromptuUser(String username, String pw, String email) {
        // use parse's interface to set basic info
        super();
        this.setUsername(username);
        this.setPassword(pw);
        this.setEmail(email);
    }

}
