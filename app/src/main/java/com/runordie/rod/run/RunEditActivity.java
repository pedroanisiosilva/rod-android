package com.runordie.rod.run;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.runordie.rod.R;
import com.runordie.rod.helpers.Config;
import com.runordie.rod.login.Login;
import com.runordie.rod.run.fragment.DatePickerFragment;
import com.runordie.rod.run.fragment.DurationPickerFragment;
import com.runordie.rod.run.fragment.TimePickerFragment;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

/**
 * Created by wsouza on 7/9/16.
 */
public class RunEditActivity extends AppCompatActivity {
    private FloatingActionButton btnAddRun;
    private EditText datePicker;
    private EditText timePicker;
    private EditText kmsRun;
    private EditText durationRun;
    private EditText descriptionRun;
    private ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        setContentView(R.layout.run_form);
        setOnClicks();

        Bundle extras = getIntent().getExtras();


        if(extras != null){
            Integer runId = extras.getInt(RunEnum.RUN_ID, 0);
            if(runId > 0){
                Long runDateTime = extras.getLong(RunEnum.RUN_DATETIME, 0);
                Double runDistance = extras.getDouble(RunEnum.RUN_DISTANCE, 0);
                Long runDuration= extras.getLong(RunEnum.RUN_DURATION, 0);
                kmsOfRun().setText(runDistance.toString());
                durationOfRun().setText(DurationPickerFragment.parseDateToHours(runDuration.longValue()));

            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap runPhoro = extras.getParcelable("data");
                viewImage().setImageBitmap(runPhoro);
            }
        }
    }
    private void doCreateRun() {
        if(new RunValidation(descriptionOfRun(), durationOfRun(),kmsOfRun(),dateOfRun(),timeOfRun()).isValid()){

            try {
                String description = descriptionOfRun().getText().toString();
                Double kms = Double.parseDouble(kmsOfRun().getText().toString());
                String duration = durationOfRun().getText().toString();
                String date = dateOfRun().getText().toString();
                String time = timeOfRun().getText().toString();
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                Date data = format.parse(date + " " + time);

                long parsedDuration = DurationPickerFragment.parseDuration(duration);
                String userId = Login.getLoginInfo(this)[2];
                Run r = new Run();
                r.setDatetime(data);
                r.setUserId(Integer.parseInt(userId));
                r.setDuration(DurationPickerFragment.parseDuration(duration));
                r.setDistance(kms);
                Bitmap bitmap = null;
                if(viewImage().getDrawable() != null){
                    bitmap = ((BitmapDrawable)viewImage().getDrawable()).getBitmap();
                }

                Integer statusCode = new RunPost(r, this, bitmap).execute(Config.getRunPostUrl(this)).get();
                if(statusCode == 200){
                    this.finish();
                }else{

                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 1);
    }

    private FloatingActionButton btnAddRun(){
        if(btnAddRun == null)
            btnAddRun = (FloatingActionButton) findViewById(R.id.btnAddRun);

        return btnAddRun;
    }

    private EditText kmsOfRun(){
        if(kmsRun == null)
            kmsRun = (EditText) findViewById(R.id.kmsRun);
        return kmsRun;
    }

    private EditText durationOfRun(){
        if(durationRun == null)
            durationRun = (EditText) findViewById(R.id.durationRun);
        return durationRun;
    }

    private EditText descriptionOfRun(){
        if(descriptionRun == null)
            descriptionRun = (EditText) findViewById(R.id.descriptionRun);
        return descriptionRun;
    }

    private EditText dateOfRun(){
        if(datePicker == null)
            datePicker = (EditText) findViewById(R.id.dateOfRun);
        return datePicker;
    }

    private ImageView viewImage() {
        if(photo == null)
            photo = (ImageView) findViewById(R.id.runPhoto);
        return photo;
    }

    private EditText timeOfRun(){
        if(timePicker == null)
            timePicker = (EditText) findViewById(R.id.timeOfRun);
        return timePicker;
    }

    private void setOnClicks(){

        btnAddRun().setOnClickListener(new View.OnClickListener() {
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

        timeOfRun().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new TimePickerFragment().show(getSupportFragmentManager(), "timePicker");
                }
            }
        });

        durationOfRun().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    new DurationPickerFragment().show(getFragmentManager(), "dialog");
                }
            }
        });


    }

}