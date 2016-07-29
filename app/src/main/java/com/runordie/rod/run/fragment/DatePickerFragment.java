package com.runordie.rod.run.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;

import com.runordie.rod.R;

import java.util.Calendar;

/**
 * Created by wsouza on 7/10/16.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private final String TAG = "DatePickerFragment";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {

        ((EditText)getActivity().findViewById(R.id.dateOfRun)).setText(day + "/" + (month + 1) + "/" + year);
        ((EditText)getActivity().findViewById(R.id.dateOfRun)).setError(null);

//        new TimePickerFragment().show(getActivity().getSupportFragmentManager(), "timePicker");

    }
}
