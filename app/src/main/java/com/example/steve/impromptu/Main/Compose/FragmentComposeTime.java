package com.example.steve.impromptu.Main.Compose;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.R;


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

    Time currentTime;
    Time startTime;
    Time endTime;
    int durationHour = 0;
    int durationMinute = 0;

    OnComposeTimeFinishedListener mCallback;

    // Container Activity must implement this interface
    public interface OnComposeTimeFinishedListener {
        public void onComposeTimeFinished();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_compose_time, container, false);

        currentTime = new Time();
        startTime = new Time();
        endTime = new Time();

        currentTime.setToNow();
        startTime.hour = currentTime.hour;
        startTime.minute = currentTime.minute;

        vSeekStartTime = (SeekBar) fragmentView.findViewById(R.id.fragComposeTime_seekbar_startTime);
        vSeekDuration = (SeekBar) fragmentView.findViewById(R.id.fragComposeTime_seekbar_duration);
        vTextStartTime = (TextView) fragmentView.findViewById(R.id.fragComposeTime_textView_startTime);
        vTextDuration = (TextView) fragmentView.findViewById(R.id.fragComposeTime_textView_duration);
        vTextEndTime = (TextView) fragmentView.findViewById(R.id.fragComposeTime_textView_endTime);
        vOkay = (LinearLayout) fragmentView.findViewById(R.id.fragComposeTime_linearLayout_okay);
        vCancel = (LinearLayout) fragmentView.findViewById(R.id.fragComposeTime_linearLayout_cancel);

        String initialStartTime = Integer.toString(currentTime.hour) + ":" + Integer.toString(currentTime.minute);
        vTextStartTime.setText(initialStartTime);

        String initialEndTime = getEndTime();
        vTextEndTime.setText(initialEndTime);

        vSeekStartTime.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {

                int addTime = progress;
                addTime *= 5;
                int addHours = addTime / 60;
                int addMinutes = addTime % 60;

                currentTime.setToNow();
                startTime.hour = currentTime.hour + addHours;
                startTime.minute = currentTime.minute + addMinutes;
                String newStartTime = Integer.toString(startTime.hour) + ":" + Integer.toString(startTime.minute);
                vTextStartTime.setText(newStartTime);

                String newEndTime = getEndTime();
                vTextEndTime.setText(newEndTime);
            }
        });

        vSeekDuration.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {

                int addTime = progress * 15;
                durationHour = addTime / 60;
                durationMinute = addTime % 60;

                String hourString = "hrs";
                String minString = "mins";

                if (durationHour == 1) {
                    hourString = "hr";
                }
                if (durationMinute == 1) {
                    minString = "min";
                }

                String newDuration = Integer.toString(durationHour) + " " + hourString + " " + Integer.toString(durationMinute) + " " + minString;
                vTextDuration.setText(newDuration);

                String newEndTime = getEndTime();
                vTextEndTime.setText(newEndTime);

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

                ActivityMain myActivity = (ActivityMain) getActivity();
                Event myEvent = myActivity.getNewEvent();
                myEvent.setEventTime(startTime);
                myEvent.setDurationHour(durationHour);
                myEvent.setDurationMinute(durationMinute);

                mCallback.onComposeTimeFinished();
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
            mCallback = (OnComposeTimeFinishedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnComposeTimeFinishedListener");
        }
    }

    public String getEndTime() {
        endTime.hour = startTime.hour + durationHour;
        endTime.minute = startTime.minute + durationMinute;

        String newEndTime = Integer.toString(endTime.hour) + ":" + Integer.toString(endTime.minute);
        return newEndTime;
    }

    int roundUpToNearest5(int n) {
        return (n + 4) / 5 * 5;
    }
}
