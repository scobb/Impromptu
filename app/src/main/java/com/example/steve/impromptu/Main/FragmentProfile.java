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
        final ImageView profileView = (ImageView) myInflatedView.findViewById(R.id.fragProfile_imageView_profilePic);



        // Get the user
        ImpromptuUser targetUser = ImpromptuUser.getUserById(userData.getString("ownerId"));

        if (targetUser == null) {
            //TODO - test this
            Log.e("Impromptu", "Current user is null");
            Intent intent = new Intent(getActivity(), ActivityLogin.class);
            startActivity(intent);
            getActivity().finish();
        }

        if (targetUser.getUsername() == null)
            Log.e("Impromptu", "Current user's username is null");
        if (targetUser.getEmail() == null)
            Log.e("Impromptu", "Current user's email is null");

        // Fill in the fields
        nameView.setText(targetUser.getName());
        emailView.setText(targetUser.getEmail());
        profileView.setImageBitmap(targetUser.getPicture());

        return myInflatedView;
    }

}
