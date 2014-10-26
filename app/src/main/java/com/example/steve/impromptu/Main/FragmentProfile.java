package com.example.steve.impromptu.Main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.steve.impromptu.Login.ActivityLogin;
import com.example.steve.impromptu.R;
import com.parse.ParseUser;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentProfile extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // get user's name, email, profile pic

        ParseUser currentUser = ParseUser.getCurrentUser();
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
        View myInflatedView = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView nameView = (TextView) myInflatedView.findViewById(R.id.fragProfile_textView_name);
        nameView.setText(currentUser.getUsername());
        TextView emailView = (TextView) myInflatedView.findViewById(R.id.fragProfile_textView_email);
        emailView.setText(currentUser.getEmail());

        return myInflatedView;
    }

}
