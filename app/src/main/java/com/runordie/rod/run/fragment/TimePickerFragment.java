package com.runordie.rod.run.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

import com.runordie.rod.R;

import java.util.Calendar;

/**
 * Created by wsouza on 7/10/16.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private final String TAG = "TimePickerFragment";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        ((EditText)getActivity().findViewById(R.id.timeOfRun)).setText(hourOfDay + ":" + minute);
        ((EditText)getActivity().findViewById(R.id.timeOfRun)).setError(null);
    }
}
