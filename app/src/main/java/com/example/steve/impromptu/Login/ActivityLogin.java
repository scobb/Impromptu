package com.example.steve.impromptu.Login;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.R;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;
import com.parse.ParseException;
import com.parse.SignUpCallback;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class ActivityLogin extends FragmentActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parse.initialize(this, "sP5YdlJxg1WiwfKgSXX4KdrgpZzAV5g69dV8ryY0", "houV8Brg8oIuBKSLheR7qAW4AJfGq1QZmH62Spgk");

        //TODO remove this after testing
        ParseUser.logOut();

        //If user already logged in, forward them to main activity
        if (ParseUser.getCurrentUser() != null) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                // Send logged in users to main activity
                Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
                startActivity(intent);
                finish();
            }
        }

        setContentView(R.layout.activity_shell_login);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentLogin fragment = new FragmentLogin();
        fragmentTransaction.replace(R.id.loginShell, fragment).addToBackStack(null);
        fragmentTransaction.commit();

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
                    Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
                    startActivity(intent);
                    finish();
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

        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Show a simple Toast message upon successful registration
                    Toast.makeText(getApplicationContext(),
                            "Successfully Signed up, please log in.",
                            Toast.LENGTH_LONG).show();
                            // switch fragment to FragmentLogin
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            FragmentLogin fragment = new FragmentLogin();
                            fragmentTransaction.replace(R.id.loginShell, fragment).addToBackStack(null);
                            fragmentTransaction.commit();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Sign up Error", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }


    public void newUserOnClick(View v) {
        // switch fragment to FragmentNewUser
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentNewUser fragment = new FragmentNewUser();
        fragmentTransaction.replace(R.id.loginShell, fragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void facebookOnClick(View v) {
        // switch fragment to FragmentFacebookUser

    }


}
