package com.example.steve.impromptu.Main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.example.steve.impromptu.R;

import java.util.Hashtable;

/**
 * Created by stephen on 11/23/14.
 */
public class FragmentFilterDialog extends DialogFragment{

    ImageView drinkingFilter;
    ImageView foodFilter;
    ImageView sportFilter;
    ImageView studyingFilter;
    ImageView tvFilter;
    ImageView workingOutFilter;

    Hashtable<String, Boolean> currentFilters = ActivityMain.getFiltersMap();

    public static FragmentFilterDialog newInstance() {
        FragmentFilterDialog frag = new FragmentFilterDialog();
        Bundle args = new Bundle();
//        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        View view = inflater.inflate(R.layout.fragment_dialog_filter, null);

        // Get ImageViews
        drinkingFilter = (ImageView)view.findViewById(R.id.filter_drinking_imageView);
        foodFilter = (ImageView)view.findViewById(R.id.filter_food_imageView);
        sportFilter = (ImageView)view.findViewById(R.id.filter_sport_imageView);
        studyingFilter = (ImageView)view.findViewById(R.id.filter_studying_imageView);
        tvFilter = (ImageView)view.findViewById(R.id.filter_tv_imageView);
        workingOutFilter = (ImageView)view.findViewById(R.id.filter_workingOut_imageView);

        // Set onClickListeners
        drinkingFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean currentState = currentFilters.get("Drinking");
                if(currentState){
                    currentFilters.put("Drinking", false);
                    fadeFilterOut(drinkingFilter);
                }
                else{
                    currentFilters.put("Drinking", true);
                    fadeFilterIn(drinkingFilter);
                }
            }
        });

        foodFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean currentState = currentFilters.get("Eating");
                if(currentState){
                    currentFilters.put("Eating", false);
                    fadeFilterOut(foodFilter);
                }
                else{
                    currentFilters.put("Eating", true);
                    fadeFilterIn(foodFilter);
                }
            }
        });

        sportFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean currentState = currentFilters.get("Sports");
                if(currentState){
                    currentFilters.put("Sports", false);
                    fadeFilterOut(sportFilter);
                }
                else{
                    currentFilters.put("Sports", true);
                    fadeFilterIn(sportFilter);
                }
            }
        });

        studyingFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean currentState = currentFilters.get("Studying");
                if(currentState){
                    currentFilters.put("Studying", false);
                    fadeFilterOut(studyingFilter);
                }
                else{
                    currentFilters.put("Studying", true);
                    fadeFilterIn(studyingFilter);
                }
            }
        });

        tvFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean currentState = currentFilters.get("TV");
                if(currentState){
                    currentFilters.put("TV", false);
                    fadeFilterOut(tvFilter);
                }
                else{
                    currentFilters.put("TV", true);
                    fadeFilterIn(tvFilter);
                }
            }
        });

        workingOutFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean currentState = currentFilters.get("Working Out");
                if(currentState){
                    currentFilters.put("Working Out", false);
                    fadeFilterOut(workingOutFilter);
                }
                else{
                    currentFilters.put("Working Out", true);
                    fadeFilterIn(workingOutFilter);
                }
            }
        });

        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);
        return builder.create();
    }

    private void fadeFilterOut(ImageView filter){
        AlphaAnimation alpha = new AlphaAnimation(0.5F, 0.5F); // change values as you want
        alpha.setDuration(0); // Make animation instant
        alpha.setFillAfter(true); // Tell it to persist after the animation ends

        // And then on your imageview
        filter.startAnimation(alpha);
    }

    private void fadeFilterIn(ImageView filter){
        AlphaAnimation alpha = new AlphaAnimation(1.0F, 1.0F); // change values as you want
        alpha.setDuration(0); // Make animation instant
        alpha.setFillAfter(true); // Tell it to persist after the animation ends

        // And then on your imageview
        filter.startAnimation(alpha);
    }

}
