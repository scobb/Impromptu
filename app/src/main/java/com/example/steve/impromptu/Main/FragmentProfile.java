package com.example.steve.impromptu.Main;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Login.ActivityLogin;
import com.example.steve.impromptu.R;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.games.GamesMetadata;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentProfile extends Fragment {

    private ImpromptuUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Receive data passed in
        Bundle userData = getArguments();


        // Get Views
        final View myInflatedView = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView nameView = (TextView) myInflatedView.findViewById(R.id.fragProfile_textView_name);
        final TextView emailView = (TextView) myInflatedView.findViewById(R.id.fragProfile_textView_email);
        final ProfilePictureView profileView = (ProfilePictureView) myInflatedView.findViewById(R.id.fragProfile_imageView_profilePic);



        // If the user is looking at his own profile
        if(userData.getBoolean("currentuser")){

            // Get the current user
            ImpromptuUser currentUser = (ImpromptuUser)ParseUser.getCurrentUser();
//            ParseUser currentUser = ParseUser.getCurrentUser();

            if (currentUser == null) {
                //TODO - test this
                Log.e("Impromptu", "Current user is null");
                Intent intent = new Intent(getActivity(), ActivityLogin.class);
                startActivity(intent);
                getActivity().finish();
            }

            if (currentUser.getUsername() == null)
                Log.e("Impromptu", "Current user's username is null");
            if (currentUser.getEmail() == null)
                Log.e("Impromptu", "Current user's email is null");

            // Get the profile picture
            /*
            Bitmap profilePic = currentUser.getPicture();
            if (profilePic != null) {
                ImageView picView = (ImageView) myInflatedView.findViewById(R.id.fragProfile_imageView_profilePic);
                picView.setImageBitmap(profilePic);
            }
            */




            Log.d("Impromptu", "Looking up facebook");
            if (ParseFacebookUtils.isLinked(currentUser)){

                // display facebook name
                Request req = Request.newMeRequest(ParseFacebookUtils.getSession(),
                        new Request.GraphUserCallback() {
                            @Override
                            public void onCompleted(GraphUser user, Response response) {
                                if (user != null) {
                                    // Create a JSON object to hold the profile info
                                    JSONObject userProfile = new JSONObject();
                                    try {
                                        // Populate the JSON object
                                        userProfile.put("facebookId", user.getId());
                                        userProfile.put("name", user.getName());
                                        nameView.setText(user.getName());

                                        // Save the user profile info in a user property
                                        ParseUser currentUser = ParseUser
                                                .getCurrentUser();
                                        currentUser.put("profile", userProfile);

                                        if(currentUser.get("profile") != null) {

                                            try {
                                                if (userProfile.getString("facebookId") != null) {
                                                    String facebookId = userProfile.get("facebookId").toString();
                                                    profileView.setProfileId(facebookId);
                                                } else {
                                                    profileView.setProfileId(null);
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
//                                        currentUser.saveInBackground();

                                        Log.d("Impromptu", "User's name: "+ userProfile.get("name") );
                                        myInflatedView.setVisibility(View.VISIBLE);
                                    } catch (JSONException e) {
                                        Log.d("Impromptu",
                                                "Error parsing returned user data.");
                                    } finally {

                                        ActivityMain actMain = (ActivityMain)getActivity();
                                        actMain.progressDialog.dismiss();
                                    }

                                } else if (response.getError() != null) {
                                    // handle error
                                    Log.d("Impromptu", "error getting fb: " + response.getError().toString() );
                                }
                            }
                        });
                myInflatedView.setVisibility(View.INVISIBLE);
                ActivityMain actMain = (ActivityMain)getActivity();
                actMain.progressDialog = ProgressDialog.show(
                        getActivity(), "", "Populating Profile...", true);
                req.executeAsync();

            }
            // If the account was not linked to Facebook
            else{
                ImpromptuUser currentUser2 = (ImpromptuUser)ParseUser.getCurrentUser();

                nameView.setText(currentUser2.getName());
                emailView.setText(currentUser2.getEmail());
            }
        }

        // If it is not the current user
        else {

            // Get the user
            user = ImpromptuUser.getUserById(userData.getString("ownerId"));

            // display parse username
    //      nameView.setText(currentUser.getUsername());
            nameView.setText(user.getName());
            emailView.setText(user.getEmail());
        }

        return myInflatedView;
    }

}
