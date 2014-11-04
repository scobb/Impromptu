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

import com.example.steve.impromptu.Entity.ImpromptuUser;
import com.example.steve.impromptu.Login.ActivityLogin;
import com.example.steve.impromptu.R;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.google.android.gms.games.GamesMetadata;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentProfile extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Receive the arguments
        Bundle userData = getArguments();


        // get user's name, email, profile pic
        Log.d("Impromptu", "In onCreateView");
        ImpromptuUser currentUser = (ImpromptuUser)ParseUser.getCurrentUser();

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




        // Inflate the layout for this fragment
        final View myInflatedView = inflater.inflate(R.layout.fragment_profile, container, false);

        // grab profile pic
        Bitmap profilePic = currentUser.getPicture();
        if (profilePic != null) {
            ImageView picView = (ImageView) myInflatedView.findViewById(R.id.fragProfile_imageView_profilePic);
            picView.setImageBitmap(profilePic);
        }

        final TextView nameView = (TextView) myInflatedView.findViewById(R.id.fragProfile_textView_name);
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
        else {
            // display parse username
//            nameView.setText(currentUser.getUsername());
            nameView.setText(userData.getString("username"));
        }
        TextView emailView = (TextView) myInflatedView.findViewById(R.id.fragProfile_textView_email);
        emailView.setText(currentUser.getEmail());

        return myInflatedView;
    }

}
