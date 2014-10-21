package com.example.steve.impromptu.Login;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.steve.impromptu.R;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseUser;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class ActivityLogin extends FragmentActivity {
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.activity_shell_login);
        Parse.initialize(this, "sP5YdlJxg1WiwfKgSXX4KdrgpZzAV5g69dV8ryY0", "houV8Brg8oIuBKSLheR7qAW4AJfGq1QZmH62Spgk");

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FragmentLogin fragment = new FragmentLogin();
        fragmentTransaction.replace(R.id.loginShell, fragment).addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void loginOnClick(View v){
        // get username
        EditText usernameEntry = (EditText)findViewById(R.id.fragLogin_editText_user);
        String username = usernameEntry.getText().toString();

        // get password
        EditText passwordEntry = (EditText)findViewById(R.id.fragLogin_editText_password);
        String password = passwordEntry.getText().toString();

        // attempt to log in
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // forward user to main activity
                } else {
                    // display popup
                    Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT);
                }
            }
        });

    }
    public void newUserOnClick(View v){
        // switch fragment to FragmentNewUser

    }
    public void facebookOnClick(View v){
        // switch fragment to FragmentFacebookUser

    }



}
