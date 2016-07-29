package com.runordie.rod.run;

import android.widget.EditText;

import mobi.upod.timedurationpicker.TimeDurationPicker;

/**
 * Created by wsouza on 7/18/16.
 */
public class RunValidation {


    private final EditText description;
    private final EditText kms;
    private final EditText duration;
    private final EditText date;
    private final EditText time;

    public EditText getDescription() {
        return description;
    }

    public EditText getKms() { return kms; }

    public EditText getDuration() {
        return duration;
    }

    public EditText getDate() {
        return date;
    }

    public EditText getTime() {
        return time;
    }


    public RunValidation(EditText description, EditText kms, EditText duration, EditText date, EditText time){
        this.description = description;
        this.kms = kms;
        this.duration = duration;
        this.date = date;
        this.time = time;
    }

    public boolean isValid(){
        boolean valid = true;

        if(getDescription().getText().toString().trim().isEmpty()){
            getDescription().setError("Description is required!!!");
            valid = false;
        }

        if(getKms().getText().toString().trim().isEmpty()){
            getKms().setError("Km`s is required!!!");
            valid = false;
        }else if(getKms().getText().toString().trim().matches("")){

        }

        if(getDuration().getText().toString().trim().isEmpty()){
            getDuration().setError("Duration is required!!!");
            valid = false;
        }

        if(getDate().getText().toString().trim().isEmpty()){
            getDate().setError("Date is required!!!");
            valid = false;
        }
        if(getTime().getText().toString().trim().isEmpty()){
            getTime().setError("Time is required!!!");
            valid = false;
        }

        return valid;
    }
}
