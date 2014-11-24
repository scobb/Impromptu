package com.example.steve.impromptu.Main.Compose;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterComposeType;
import com.example.steve.impromptu.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentComposeType extends Fragment {

    LinearLayout vOkay;
    LinearLayout vCancel;
    ListView vTypeList;
    public Event myEvent;
    ArrayAdapterComposeType typeAdapter = null;
    String type = null;

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
        vOkay = (LinearLayout) fragmentView.findViewById(R.id.fragComposeType_linearLayout_okay);
        vCancel = (LinearLayout) fragmentView.findViewById(R.id.fragComposeType_linearLayout_cancel);

        typeAdapter = new ArrayAdapterComposeType(getActivity(), R.layout.template_type_item, types, myEvent);
        vTypeList.setAdapter(typeAdapter);

        vTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                type = types.get(i).getName();
                Toast.makeText(getActivity(), type, Toast.LENGTH_SHORT).show();
            }
        });

        vOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                view.setSelected(true);

                ActivityMain myActivity = (ActivityMain) getActivity();
                Event myEvent = myActivity.getComposeEvent();

                if (type != null) {
                    myEvent.setType(type);
                    composeTypeFinishedCallback.onComposeTypeFinished();
                }
                else {
                    Toast.makeText(getActivity(), "Please select a type.", Toast.LENGTH_SHORT).show();
                }
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
