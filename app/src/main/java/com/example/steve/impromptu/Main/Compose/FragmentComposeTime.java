package com.example.steve.impromptu.Main.Compose;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
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

import java.text.SimpleDateFormat;
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

    public Event myEvent;
    Date currentTime = new Date();
    Date startTime = new Date();
    Date endTime = new Date();
    int durationHour = 0;
    int durationMinute = 0;
    int absoluteStartHour;
    boolean startMorning;
    boolean endTimeMorning;
    int durationSeekBarProgress;
    int startTimeSeekBarProgress;
    String newEndTime;
    String initialStartTime;
    SimpleDateFormat dateString = new SimpleDateFormat("h:mm a");

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

        ActivityMain myActivity = (ActivityMain) getActivity();
        myEvent = myActivity.getComposeEvent();

        vSeekStartTime = (SeekBar) fragmentView.findViewById(R.id.fragComposeTime_seekbar_startTime);
        vSeekDuration = (SeekBar) fragmentView.findViewById(R.id.fragComposeTime_seekbar_duration);
        vTextStartTime = (TextView) fragmentView.findViewById(R.id.fragComposeTime_textView_startTime);
        vTextDuration = (TextView) fragmentView.findViewById(R.id.fragComposeTime_textView_duration);
        vTextEndTime = (TextView) fragmentView.findViewById(R.id.fragComposeTime_textView_endTime);
        vOkay = (LinearLayout) fragmentView.findViewById(R.id.fragComposeTime_linearLayout_okay);
        vCancel = (LinearLayout) fragmentView.findViewById(R.id.fragComposeTime_linearLayout_cancel);

        int durationTest = myEvent.getDurationHour();

        if (durationTest != -1) {
            startMorning = myEvent.getEventTimeMorning();
            startTime = myEvent.getEventTime();
            durationHour = myEvent.getDurationHour();
            durationMinute = myEvent.getDurationMinute();
            startTimeSeekBarProgress = myEvent.getSeekStart();
            durationSeekBarProgress = myEvent.getSeekDuration();

            if (startTime != null) {

                initialStartTime = dateString.format(startTime);
                vTextStartTime.setText(initialStartTime);
                vSeekStartTime.setProgress(startTimeSeekBarProgress);
            }
            if (durationHour != 0 || durationMinute != 0) {
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
                vSeekDuration.setProgress(durationSeekBarProgress);
            }

            String newEndTime = getEndTime();
            vTextEndTime.setText(newEndTime);

        } else {
            startTime = new Date();
            endTime = new Date();

            Log.d("Impromptu", "startTime: " + startTime);
            initialStartTime = dateString.format(startTime);
            vTextStartTime.setText(initialStartTime);

            String initialEndTime = getEndTime();
            vTextEndTime.setText(initialEndTime);

        }
            vSeekStartTime.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int addTime = progress;
                    addTime *= 5;
                    currentTime.setMinutes(roundUpToNearest5(currentTime.getMinutes()));
                    startTime.setTime(currentTime.getTime() + (addTime * 60000));
                    SimpleDateFormat dateString = new SimpleDateFormat("h:mm a");
                    String newStartTime = dateString.format(startTime);
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
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    int addTime = progress * 15;
                    durationHour = addTime / 60;
                    durationMinute = addTime % 60;
                    durationSeekBarProgress = progress;
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

                    mCallback.onComposeTimeFinished();

                }
            });

            vOkay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ActivityMain myActivity = (ActivityMain) getActivity();
                    myEvent = myActivity.getComposeEvent();
                    Log.d("Impromptu", "Onclick listener! startTime: " + startTime);
                    myEvent.setEventTime(startTime);
                    myEvent.setEventTimeMorning(startMorning);
                    myEvent.setDurationHour(durationHour);
                    myEvent.setDurationMinute(durationMinute);
                    myEvent.setSeekStart(startTimeSeekBarProgress);
                    myEvent.setSeekDuration(durationSeekBarProgress);
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
        endTime.setTime(startTime.getTime() + (durationMinute * 60000) + (durationHour * 3600000));
        SimpleDateFormat dateString = new SimpleDateFormat("h:mm a");
        String newEndTime = dateString.format(endTime);
        return newEndTime;
    }

    int roundUpToNearest5(int n) {
        return (n + 4) / 5 * 5;
    }


}
