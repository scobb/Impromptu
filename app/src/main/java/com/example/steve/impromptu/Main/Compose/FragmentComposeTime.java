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

    public Event myEvent;
    Time currentTime = new Time();
    Time startTime = new Time();
    Time endTime = new Time();
    int durationHour = 0;
    int durationMinute = 0;
    int absoluteStartHour;
    boolean startMorning;
    boolean endTimeMorning;
    int durationSeekBarProgress;
    int startTimeSeekBarProgress;
    String newEndTime;
    String initialStartTime;

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
                if (startMorning) {
                    initialStartTime = Integer.toString(startTime.hour) + ":" + String.format("%02d", roundUpToNearest5(startTime.minute)) + "am";
                } else {
                    initialStartTime = Integer.toString(startTime.hour) + ":" + String.format("%02d", roundUpToNearest5(startTime.minute)) + "pm";
                }
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
            //need to find workaround for absolute start time
            String newEndTime = getEndTime();
            vTextEndTime.setText(newEndTime);

        } else {
            currentTime = new Time();
            startTime = new Time();
            endTime = new Time();

            currentTime.setToNow();
            startTime.hour = currentTime.hour;
            absoluteStartHour = startTime.hour;
            startTime.minute = currentTime.minute;
//used to have find views by ids


            startMorning = true;
            if (roundUpToNearest5(startTime.minute) >= 60) {
                startTime.hour += 1;
                // absoluteStartHour += 1;
                startTime.minute = 0;
            }
            if (startTime.hour == 12) {
                startMorning = false;
            }
            if (startTime.hour > 12) {
                startTime.hour = startTime.hour - 12;
                startMorning = false;
            }

            if (startTime.hour == 0) {
                startTime.hour = 12;
                startMorning = true;
            }
            //String correctMin = String.format("%02d", startTime.minute);

            if (startMorning) {
                initialStartTime = Integer.toString(startTime.hour) + ":" + String.format("%02d", roundUpToNearest5(startTime.minute)) + "am";
            } else {
                initialStartTime = Integer.toString(startTime.hour) + ":" + String.format("%02d", roundUpToNearest5(startTime.minute)) + "pm";
            }

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
                    int addHours = addTime / 60;
                    int addMinutes = addTime % 60;
                    boolean morning = true;
                    String newStartTime;
                    startTimeSeekBarProgress = progress;

                    startTime.hour = currentTime.hour + addHours;
                    absoluteStartHour = currentTime.hour + addHours;
                    startTime.minute = (currentTime.minute + addMinutes) % 60;


                    if (roundUpToNearest5(startTime.minute) >= 60) {
                        startTime.hour += 1;
                        // absoluteStartHour += 1;
                        startTime.minute = 0;
                    }

                    if (startTime.hour == 12) {
                        morning = false;
                    }
                    if (startTime.hour > 12) {
                        startTime.hour = startTime.hour - 12;
                        morning = false;
                    }

                    if (startTime.hour == 0) {
                        startTime.hour = 12;
                        morning = true;
                    }


                    if (morning) {

                        newStartTime = Integer.toString(startTime.hour) + ":" + String.format("%02d", roundUpToNearest5(startTime.minute)) + "am";
                    } else {
                        newStartTime = Integer.toString(startTime.hour) + ":" + String.format("%02d", roundUpToNearest5(startTime.minute)) + "pm";

                    }

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

        endTimeMorning = true;
        int absoluteEndHour = absoluteStartHour + durationHour;
        endTime.hour = startTime.hour + durationHour;
        endTime.minute = (startTime.minute + durationMinute) % 60;
        if(((startTime.minute + durationMinute) >= 60) || ((startTime.minute + roundUpToNearest5(durationMinute)) >= 60)) {
            absoluteEndHour += 1;
            endTime.hour += 1;
            endTime.minute = 0;
        }

        if(absoluteEndHour == 12 ) {
            endTimeMorning = false;
        }
        if(endTime.hour > 12) {
            endTime.hour = endTime.hour -12;
        }

        if(absoluteEndHour > 12) {
            endTimeMorning = false;
        }
        if(absoluteEndHour >= 24 ) {
            endTimeMorning = true;
        }
        if(absoluteEndHour == 0) {
            endTime.hour = 12;
            endTimeMorning = true;
        }

        if(endTimeMorning) {

            newEndTime = Integer.toString(endTime.hour) + ":" + String.format("%02d", roundUpToNearest5(endTime.minute)) + "am";
        }
        else {
            newEndTime = Integer.toString(endTime.hour) + ":" + String.format("%02d", roundUpToNearest5(endTime.minute)) + "pm";

        }

        return newEndTime;
    }

    int roundUpToNearest5(int n) {
        return (n + 4) / 5 * 5;
    }


}
