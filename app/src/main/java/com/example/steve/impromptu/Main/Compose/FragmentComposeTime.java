package com.example.steve.impromptu.Main.Compose;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.steve.impromptu.R;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentComposeTime extends Fragment {

    SeekBar vSeekStartTime;
    SeekBar vSeekDuration;
    TextView vTextStartTime;
    TextView vTextDuration;
    TextView vTextEndTime;
    LinearLayout vOkay;
    LinearLayout vCancel;

    Date currentTime = new Date();
    Date startTime = new Date();
    int duration = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_compose_time, container, false);

        Calendar c = Calendar.getInstance();
//        currentTime.setTime(); = c.get(Calendar.MILLISECOND);

        vSeekStartTime = (SeekBar) fragmentView.findViewById(R.id.fragComposeTime_seekbar_startTime);
        vSeekDuration = (SeekBar) fragmentView.findViewById(R.id.fragComposeTime_seekbar_duration);
        vTextStartTime = (TextView) fragmentView.findViewById(R.id.fragComposeTime_textView_startTime);
        vTextDuration = (TextView) fragmentView.findViewById(R.id.fragComposeTime_textView_duration);
        vTextEndTime = (TextView) fragmentView.findViewById(R.id.fragComposeTime_textView_endTime);
        vOkay = (LinearLayout) fragmentView.findViewById(R.id.fragComposeTime_linearLayout_okay);
        vCancel = (LinearLayout) fragmentView.findViewById(R.id.fragComposeTime_linearLayout_cancel);

        vSeekStartTime.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {



            }
        });




        vCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO make sure enough info is filled out

                Toast.makeText(getActivity(), "Select cancel", Toast.LENGTH_SHORT).show();

            }
        });

        vOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO make sure enough info is filled out

                Toast.makeText(getActivity(), "Select Okay", Toast.LENGTH_SHORT).show();

            }
        });

        return fragmentView;
    }

}
