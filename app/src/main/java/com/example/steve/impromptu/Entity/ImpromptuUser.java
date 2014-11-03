package com.example.steve.impromptu.Entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

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
        this.setUsername(name);
    }

    public String getName() {
        return this.get("name").toString();
    }

    /**
     * method - setPicture - saves user's profile picture to parse. Note, filename must end in .png
     */
    public void setPicture(Bitmap pic) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();

        final ParseFile parseImage = new ParseFile(data);

        parseImage.saveInBackground();
        this.put("profilePic", parseImage);
        this.saveInBackground();
    }

    /**
     * method - getPicture - returns bitmap for profile picture, null if user doesn't have one.
     */
    public Bitmap getPicture() {
        ParseFile picFile = (ParseFile)this.get("profilePic");
        if (picFile == null) {
            return null;
        }
        byte[] picBlock = null;
        try {
            picBlock = picFile.getData();
        }
        catch (ParseException exc) {
            Log.e("Impromptu", "exception in getPicture", exc);

        }
        return BitmapFactory.decodeByteArray(picBlock, 0, picBlock.length);
    }


}
