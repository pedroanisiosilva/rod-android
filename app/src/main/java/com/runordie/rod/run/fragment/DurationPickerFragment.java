package com.runordie.rod.run.fragment;

import android.widget.EditText;

import com.runordie.rod.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobi.upod.timedurationpicker.TimeDurationPicker;
import mobi.upod.timedurationpicker.TimeDurationPickerDialogFragment;

/**
 * Created by wsouza on 7/18/16.
 */
public class DurationPickerFragment extends TimeDurationPickerDialogFragment {
    static Pattern pattern = Pattern.compile("(\\d{2}):(\\d{2}):(\\d{2})");

    public static long parseDuration(String period){
        Matcher matcher = pattern.matcher(period);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group(1)) * 3600000L
                    + Long.parseLong(matcher.group(2)) * 60000
                    + Long.parseLong(matcher.group(3)) * 1000;
        }
        return 0;
    }
    @Override
    protected long getInitialDuration() {
        String period = ((EditText)getActivity().findViewById(R.id.durationRun)).getText().toString();
        return parseDuration(period);
    }

    @Override
    public void onDurationSet(TimeDurationPicker view, long duration) {

        ((EditText)getActivity().findViewById(R.id.durationRun)).setText(parseDateToHours(duration));
        ((EditText)getActivity().findViewById(R.id.durationRun)).setError(null);
    }
    public static String parseDateToHours(long duration){
        duration = duration / 1000;
        long hours = duration / 3600;
        long mins = (duration / 60) % 60;
        long secs = duration % 60;
        return String.format("%02d:%02d:%02d", hours, mins, secs);
    }
}