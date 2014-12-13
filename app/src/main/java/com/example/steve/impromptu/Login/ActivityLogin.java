package com.example.steve.impromptu.Login;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.FriendRequest;
import com.example.steve.impromptu.Entity.Group;
import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.R;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class ActivityLogin extends FragmentActivity {
    private Dialog progressDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    public void forwardToMainActivity() {
        Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
        startActivity(intent);
        finish();
    }


    public void forwardToLoginFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentLogin fragment = new FragmentLogin();
        fragmentTransaction.replace(R.id.loginShell, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseFacebookUtils.initialize("1512234555691131");

        //If user already logged in, forward them to main activity
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Send logged in users to main activity
            forwardToMainActivity();
        }
        setContentView(R.layout.activity_shell_login);

        forwardToLoginFragment();

    }

    public void facebookOnClick(View v) {
        // attempt to log in with facebook credentials
        ActivityLogin.this.progressDialog = ProgressDialog.show(
                ActivityLogin.this, "", "Logging in...", true);
        List<String> permissions = Arrays.asList("public_profile", "email", "user_friends");
        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (err != null) {
                    Log.e("Impromptu", "fb error: ", err);
                }
                ActivityLogin.this.progressDialog.dismiss();
                if (user == null) {
                    Log.d("Impromptu",
                            "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Request req = Request.newMeRequest(ParseFacebookUtils.getSession(),
                            new Request.GraphUserCallback() {
                                @Override
                                public void onCompleted(GraphUser user, Response response) {
                                    if (user != null) {
                                        ImpromptuUser currentUser = (ImpromptuUser) ParseUser.getCurrentUser();
                                        currentUser.setFacebookId(user.getId());
                                        currentUser.setName(user.getName());
                                        currentUser.persist();
                                    }
                                }
                            });
                    req.executeAsync();
                    ((ImpromptuUser)user).clearGroups();
                    ((ImpromptuUser)user).clearFriends();
                    ((ImpromptuUser)user).clearStreamEvents();
                    ((ImpromptuUser)user).persist();

                    forwardToMainActivity();
                } else {
                    forwardToMainActivity();
                }
            }
        });

    }


}
