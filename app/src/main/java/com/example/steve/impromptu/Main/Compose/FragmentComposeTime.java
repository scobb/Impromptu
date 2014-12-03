package com.example.steve.impromptu.Main.Compose;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import java.util.ArrayList;
import java.util.List;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.R;
import com.example.steve.impromptu.Main.Compose.ArrayAdapters.ArrayAdapterComposeTime;


/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentComposeTime extends Fragment {

    // SeekBar vSeekStartTime;
    EditText vEditStartHour;
    EditText vEditStartMinute;
    EditText vEditEndHour;
    TextView vTextViewEndHourCheck;
    TextView vTextViewStartHourCheck;
    EditText vEditEndMinute;
    Spinner vSpinnerStartTime, vSpinnerEndTime;

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

        vEditStartHour = (EditText) fragmentView.findViewById(R.id.fragComposeTime_editText_startHour);
        vTextViewStartHourCheck = (TextView) fragmentView.findViewById(R.id.fragComposeTime_textViewStartHourCheck);
        vEditStartMinute = (EditText) fragmentView.findViewById(R.id.fragComposeTime_editText_startMinute);
        vEditEndHour = (EditText) fragmentView.findViewById(R.id.fragComposeTime_editText_endHour);
        vTextViewEndHourCheck = (TextView) fragmentView.findViewById(R.id.fragComposeTime_textViewEndHourCheck);
        vEditEndMinute = (EditText) fragmentView.findViewById(R.id.fragComposeTime_editText_endMinute);
        vSpinnerStartTime = (Spinner) fragmentView.findViewById(R.id.spinnerStartTime);
        vSpinnerEndTime = (Spinner) fragmentView.findViewById(R.id.spinnerEndTime);
        vOkay = (LinearLayout) fragmentView.findViewById(R.id.fragComposeTime_linearLayout_okay);
        vCancel = (LinearLayout) fragmentView.findViewById(R.id.fragComposeTime_linearLayout_cancel);

        addListenerOnSpinnerItemSelection();

        int durationTest = myEvent.getDurationHour();

        if (durationTest != -1) {
            startMorning = myEvent.getEventTimeMorning();
            startTime = myEvent.getEventTime();
            endTime = myEvent.getEventEndTime();
            vEditStartHour.setText(startTime.hour);
            vEditStartMinute.setText(startTime.minute);
            vEditEndHour.setText(endTime.hour);
            vEditEndMinute.setText(endTime.minute);

        }

        vEditStartHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                startTime.hour = Integer.parseInt(editable.toString());
                if(startTime.hour <= 0 || startTime.hour > 12) {
                    vTextViewStartHourCheck.setText("Please enter a valid hour");
                }
                if(startTime.hour > ((currentTime.hour + 5) % 12)) {
                    vTextViewStartHourCheck.setText("Events cannot begin more than 5 hours in the future");
                }
                if(startTime.hour < currentTime.hour) {
                    vTextViewStartHourCheck.setText("Events must begin in the future");
                }


            }
        });

        vEditStartMinute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                startTime.minute = Integer.parseInt(editable.toString());

            }
        });

        vEditEndHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                endTime.hour = Integer.parseInt(editable.toString());
                if(endTime.hour <= 0 || endTime.hour > 12) {
                    vTextViewEndHourCheck.setText("Please enter a valid hour");
                }
                if(endTime.hour < ((startTime.hour + 5) % 12)) {
                    vTextViewEndHourCheck.setText("Events cannot last more than 5 hours");
                }

            }
        });

        vEditEndMinute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                endTime.minute = Integer.parseInt(editable.toString());

            }
        });


        vCancel.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

        // TODO make sure enough info is filled out

        Toast.makeText(getActivity(), "Select cancel", Toast.LENGTH_SHORT).show();

        mCallback.onComposeTimeFinished();

        }
        });

        vOkay.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

        ActivityMain myActivity = (ActivityMain) getActivity();
        myEvent = myActivity.getComposeEvent();
        Log.d("Impromptu", "Onclick listener! startTime: " + startTime.toMillis(false));
        myEvent.setEventTime(startTime);
        myEvent.setEventEndTime(endTime);
        myEvent.setEventTimeMorning(startMorning);
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

    public void addListenerOnSpinnerItemSelection() {
        vSpinnerStartTime.setOnItemSelectedListener(new ArrayAdapterComposeTime());
        vSpinnerEndTime.setOnItemSelectedListener(new ArrayAdapterComposeTime());
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
