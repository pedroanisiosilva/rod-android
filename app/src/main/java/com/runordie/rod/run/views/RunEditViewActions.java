package com.runordie.rod.run.views;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.runordie.rod.R;
import com.runordie.rod.run.Run;
import com.runordie.rod.run.fragment.DatePickerFragment;
import com.runordie.rod.run.fragment.DurationPickerFragment;
import com.runordie.rod.run.fragment.TimePickerFragment;

import java.io.File;

/**
 * Created by wsouza on 8/22/16.
 */
public abstract class RunEditViewActions extends AppCompatActivity {
    private EditText datePicker;
    private EditText timePicker;
    private EditText kmsRun;
    private EditText durationRun;
    private EditText descriptionRun;
    private ImageView photo;
    private Run run = new Run();
    private String TAG = "RunEdit";

    protected abstract void  doCreateRun();

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 1);

    }

    protected void setOnClicks(){

        findViewById(R.id.btnAddRun).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCreateRun();
            }
        });

        viewImage().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        dateOfRun().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new DatePickerFragment().show(getSupportFragmentManager(), "datePicker");
                }
            }
        });

        dateOfRun().setKeyListener(null);

        dateOfRun().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerFragment().show(getSupportFragmentManager(), "datePicker");
            }
        });

        timeOfRun().setKeyListener(null);

        timeOfRun().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerFragment().show(getSupportFragmentManager(), "timePicker");
            }
        });

        timeOfRun().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new TimePickerFragment().show(getSupportFragmentManager(), "timePicker");
                }
            }
        });

        durationOfRun().setKeyListener(null);

        durationOfRun().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new DurationPickerFragment().show(getFragmentManager(), "durationPicker");
                }
            }
        });

        durationOfRun().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DurationPickerFragment().show(getFragmentManager(), "durationPicker");
            }
        });

    }

    protected Rect getCropBox(Uri uri){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        int boxSize = Math.min(imageHeight, imageWidth);

        return new Rect(0, 0, boxSize, boxSize);

    }

    public class Validation {

        public boolean isValid(){
            boolean valid = true;

            if(descriptionOfRun().getText().toString().trim().isEmpty()){
                descriptionOfRun().setError("Description is required!!!");
                valid = false;
            }

            if(kmsOfRun().getText().toString().trim().isEmpty() || kmsOfRun().getText().toString().trim().matches("")){
                kmsOfRun().setError("Km`s is required!!!");
                valid = false;
            }

            if(durationOfRun().getText().toString().trim().isEmpty()){
                durationOfRun().setError("Duration is required!!!");
                valid = false;
            }

            if(dateOfRun().getText().toString().trim().isEmpty()){
                dateOfRun().setError("Date is required!!!");
                valid = false;
            }
            if(timeOfRun().getText().toString().trim().isEmpty()){
                timeOfRun().setError("Time is required!!!");
                valid = false;
            }

            return valid;
        }
    }

    protected EditText kmsOfRun(){
        if(kmsRun == null)
            kmsRun = (EditText) findViewById(R.id.kmsRun);
        return kmsRun;
    }

    protected EditText durationOfRun(){
        if(durationRun == null)
            durationRun = (EditText) findViewById(R.id.durationRun);
        return durationRun;
    }

    protected EditText descriptionOfRun(){
        if(descriptionRun == null)
            descriptionRun = (EditText) findViewById(R.id.descriptionRun);
        return descriptionRun;
    }

    protected EditText dateOfRun(){
        if(datePicker == null)
            datePicker = (EditText) findViewById(R.id.dateOfRun);
        return datePicker;
    }

    protected ImageView viewImage() {
        if(photo == null)
            photo = (ImageView) findViewById(R.id.runPhoto);
        return photo;
    }

    protected EditText timeOfRun(){
        if(timePicker == null)
            timePicker = (EditText) findViewById(R.id.timeOfRun);
        return timePicker;
    }

    protected Run getRun() {
        return run;
    }

    protected void setRun(Run run) {
        this.run = run;
    }
}
