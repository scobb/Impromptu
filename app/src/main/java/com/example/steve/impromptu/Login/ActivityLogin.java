package com.example.steve.impromptu.Login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Event;
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
import com.parse.SignUpCallback;

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

    public void forwardToNewUserFragment() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentNewUser fragment = new FragmentNewUser();
        fragmentTransaction.replace(R.id.loginShell, fragment).addToBackStack(null);
        fragmentTransaction.commit();
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
        ParseUser.registerSubclass(ImpromptuUser.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Group.class);
        Parse.initialize(this, "sP5YdlJxg1WiwfKgSXX4KdrgpZzAV5g69dV8ryY0", "houV8Brg8oIuBKSLheR7qAW4AJfGq1QZmH62Spgk");
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

    public void loginOnClick(View v) {
        Log.w("Impromptu", "in loginOnClick");
        // get username
        EditText usernameEntry = (EditText) findViewById(R.id.fragLogin_editText_user);
        String username = usernameEntry.getText().toString();
        Log.w("Impromptu", "username: " + username);

        // get password
        EditText passwordEntry = (EditText) findViewById(R.id.fragLogin_editText_password);
        String password = passwordEntry.getText().toString();
        Log.w("Impromptu", "password: " + password);

        // attempt to log in
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Log.w("Impromptu", "got a user ...somehow...: " + user.toString());

                    // forward user to main activity
                    forwardToMainActivity();

                } else {
                    // display popup
                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                    Log.e("Impromptu", "exception: ", e);
                }
            }
        });

    }

    public void newUserSubmitOnClick(View v) {

        EditText usernameEntry = (EditText) findViewById(R.id.fragNewUser_editText_user);
        String username = usernameEntry.getText().toString();

        EditText passwordEntry = (EditText) findViewById(R.id.fragNewUser_editText_password);
        String password = passwordEntry.getText().toString();

        EditText emailEntry = (EditText) findViewById(R.id.fragNewUser_editText_email);
        String email = emailEntry.getText().toString();

        ImpromptuUser user = new ImpromptuUser(username, password, email);
        ActivityLogin.this.progressDialog = ProgressDialog.show(
                ActivityLogin.this, "", "Creating profile...", true);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                ActivityLogin.this.progressDialog.dismiss();

                if (e == null) {
                    // Show an alert on successful sign in
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
                    builder.setMessage("Successfully Signed up, please log in.");
                    builder.setPositiveButton("OK", null);
                    builder.show();

                    // switch fragment to FragmentLogin
                    forwardToLoginFragment();

                } else if (e.getCode() == ParseException.USERNAME_TAKEN) {
                    // Show an alert that username is taken
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
                    builder.setMessage("Username is taken. Please try a different one.");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                } else if (e.getCode() == ParseException.INVALID_EMAIL_ADDRESS) {
                    // Show an alert that email is invalid
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
                    builder.setMessage("Please enter a valid email.");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Sign up Error", Toast.LENGTH_LONG)
                            .show();
                    Log.e("Impromptu", "exception:", e);
                    Log.e("Impromptu", "e.getCode():" + e.getCode());
                }
            }
        });
    }

    public void newUserOnClick(View v) {
        // switch fragment to FragmentNewUser
        forwardToNewUserFragment();
    }

    public void facebookOnClick(View v) {
        // attempt to log in with facebook credentials
        ActivityLogin.this.progressDialog = ProgressDialog.show(
                ActivityLogin.this, "", "Logging in...", true);
        List<String> permissions = Arrays.asList("public_profile", "email");
        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
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
                                        currentUser.persist();
                                    }
                                }
                            });
                    req.executeAsync();
                    Log.d("Impromptu",
                            "User signed up and logged in through Facebook!");
                    forwardToMainActivity();
                } else {
                    Request req = Request.newMeRequest(ParseFacebookUtils.getSession(),
                            new Request.GraphUserCallback() {
                                @Override
                                public void onCompleted(GraphUser user, Response response) {
                                    if (user != null) {
                                        ImpromptuUser currentUser = (ImpromptuUser) ParseUser.getCurrentUser();
                                        currentUser.setFacebookId(user.getId());
                                        currentUser.persist();
                                    }
                                }
                            });
                    req.executeAsync();
                    Log.d("Impromptu",
                            "User logged in through Facebook!");
                    forwardToMainActivity();
                }
            }
        });

    }


}
