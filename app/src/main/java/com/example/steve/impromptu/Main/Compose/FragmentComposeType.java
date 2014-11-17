package com.example.steve.impromptu.Main.Compose;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterComposeType;
import com.example.steve.impromptu.R;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentComposeType extends Fragment {
    LinearLayout vSports;
    LinearLayout vDrinking;
    LinearLayout vEating;
    LinearLayout vTv;
    LinearLayout vStudying;
    LinearLayout vWorkingOut;
    LinearLayout vOkay;
    LinearLayout vCancel;
    ListView vTypeList;
    public Event myEvent;
    ArrayAdapterComposeType typeAdapter = null;

   ArrayList<Type> types = new ArrayList<Type>(Arrays.asList(new Type("Drinking", false),
           new Type("Eating", false), new Type("Sports", false), new Type("Studying", false),
                   new Type("TV", false), new Type("Working Out", false)));

    OnComposeTypeFinishedListener composeTypeFinishedCallback;

    // Container Activity must implement this interface
    public interface OnComposeTypeFinishedListener {
        public void onComposeTypeFinished();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View fragmentView = inflater.inflate(R.layout.fragment_compose_type, container, false);

        ActivityMain myActivity = (ActivityMain) getActivity();
        myEvent = myActivity.getComposeEvent();

        // get references to all the necessary GUI widgets
        vTypeList = (ListView) fragmentView.findViewById(R.id.fragComposeType_listView);
//        vSports = (LinearLayout) fragmentView.findViewById(R.id.fragComposeType_linearLayout_sports);
//        vDrinking = (LinearLayout) fragmentView.findViewById(R.id.fragComposeType_linearLayout_drinking);
//        vEating = (LinearLayout) fragmentView.findViewById(R.id.fragComposeType_linearLayout_eating);
//        vTv = (LinearLayout) fragmentView.findViewById(R.id.fragComposeType_linearLayout_tv);
//        vStudying = (LinearLayout) fragmentView.findViewById(R.id.fragComposeType_linearLayout_studying);
//        vWorkingOut = (LinearLayout) fragmentView.findViewById(R.id.fragComposeType_linearLayout_working_out);
        vOkay = (LinearLayout) fragmentView.findViewById(R.id.fragComposeType_linearLayout_okay);
        vCancel = (LinearLayout) fragmentView.findViewById(R.id.fragComposeType_linearLayout_cancel);


       typeAdapter = new ArrayAdapterComposeType(getActivity(), R.layout.template_type_item, types, myEvent );
        vTypeList.setAdapter(typeAdapter);


        vSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getActivity(), "Select location", Toast.LENGTH_SHORT).show();
                myEvent.setType("sports");
                String attribute = "sports";
                composeTypeFinishedCallback.onComposeTypeFinished();

            }
        });
        vDrinking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getActivity(), "Select location", Toast.LENGTH_SHORT).show();
                myEvent.setType("drinking");
                String attribute = "drinking";
                composeTypeFinishedCallback.onComposeTypeFinished();

            }
        });
        vEating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getActivity(), "Select location", Toast.LENGTH_SHORT).show();
                myEvent.setType("eating");

                composeTypeFinishedCallback.onComposeTypeFinished();
            }
        });
        vTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getActivity(), "Select location", Toast.LENGTH_SHORT).show();
                myEvent.setType("tv");
                String attribute = "tv";
                composeTypeFinishedCallback.onComposeTypeFinished();

            }
        });
        vStudying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getActivity(), "Select location", Toast.LENGTH_SHORT).show();
                myEvent.setType("studying");
                String attribute = "studying";
                composeTypeFinishedCallback.onComposeTypeFinished();

            }
        });
        vWorkingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getActivity(), "Select location", Toast.LENGTH_SHORT).show();
                myEvent.setType("working out");
                String attribute = "working out";
                composeTypeFinishedCallback.onComposeTypeFinished();

            }
        });

        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            composeTypeFinishedCallback = (OnComposeTypeFinishedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnComposeTypeFinishedListener");
        }
    }

    public class Type {
        String type = "";
        Boolean selected = false;

        public Type(String type, Boolean selected) {
            this.type = type;
            this.selected = selected;
        }

        public String getName() {
            return this.type;
        }
        public void setName(String name) {
            this.type = name;
        }
        public Boolean getSelected() {
            return this.selected;
        }
        public void setSelected(Boolean selected) {
            this.selected = selected;
        }
    }
}
