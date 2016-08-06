package com.runordie.rod.run;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runordie.rod.R;
import com.runordie.rod.helpers.Config;
import com.runordie.rod.login.Login;
import com.runordie.rod.run.fragment.DatePickerFragment;
import com.runordie.rod.run.fragment.DurationPickerFragment;
import com.runordie.rod.run.fragment.TimePickerFragment;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
    private Run run = new Run();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        setContentView(R.layout.run_form);
        setOnClicks();

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            Run runToUpdate = (Run) extras.get(RunEnum.RUN);
            if(runToUpdate != null){
                kmsOfRun().setText(runToUpdate.getDistance().toString());
                descriptionOfRun().setText(runToUpdate.getNote());
                durationOfRun().setText(DurationPickerFragment.parseDateToHours(runToUpdate.getDuration() * 1000));
                dateOfRun().setText(new SimpleDateFormat("dd/MM/yyyy").format(runToUpdate.getDatetime()));
                timeOfRun().setText(new SimpleDateFormat("hh:mm").format(runToUpdate.getDatetime()));
                if(runToUpdate.getImagePath() != null){
                    new DownloadImageTask().execute(Config.getHost(getBaseContext()) + runToUpdate.getImagePath());
                }
                run.setId(runToUpdate.getId());
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
        if(new Validation().isValid()){
            try {
                BuildRun buildRun = new BuildRun().build();
                if(run.getId() != null){
                    new RunPost(buildRun).execute(Config.getRunDUUrl(this, run.getId()));
                }else{
                    new RunPost(buildRun).execute(Config.getRunPostUrl(this));
                }
            } catch (Exception e) {
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

    private void setOnClicks(){

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

    private class BuildRun {
        private Bitmap bitmap = null;

        public Bitmap getBitmap() {
            return bitmap;
        }

        public BuildRun build() throws ParseException {

            String description = descriptionOfRun().getText().toString();
            TextView kmsText = (TextView)findViewById(R.id.kmsRun);
            Double kms = Double.parseDouble(kmsText.getText().toString());
            String duration = durationOfRun().getText().toString();
            String date = dateOfRun().getText().toString();
            String time = timeOfRun().getText().toString();
            DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            Date data = format.parse(date + " " + time);

            long parsedDuration = DurationPickerFragment.parseDuration(duration);
            String userId = Login.getLoginInfo(getBaseContext())[2];
            run.setDatetime(data);
            run.setUserId(Integer.parseInt(userId));
            run.setDuration(DurationPickerFragment.parseDuration(duration));
            run.setDistance(kms);
            run.setNote(descriptionOfRun().getText().toString());
            if(viewImage().getDrawable() != null){
                bitmap = ((BitmapDrawable)viewImage().getDrawable()).getBitmap();
            }
            return this;
        }
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

    public class RunPost extends AsyncTask<String, Void, Integer> {
        private BuildRun buildRun;

        public RunPost(BuildRun buildRun) {
            this.buildRun = buildRun;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((RelativeLayout)findViewById(R.id.loadingPanel)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Integer code) {
            super.onPostExecute(code);
            if(code == 200){
                Toast.makeText(getBaseContext(), "Corrida criada com sucesso", Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(getBaseContext(), "Erro ao salvar corrida ", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Integer doInBackground(String... urls) {
            HttpHeaders headers = Config.getHttpHeaders(getBaseContext());

            MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
            File f = null;
            if(buildRun.getBitmap() != null){
                f = new File(getBaseContext().getCacheDir(), run.getDatetime().getTime() + run.getUserId() + "");
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    buildRun.getBitmap().compress(Bitmap.CompressFormat.JPEG, 20, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Resource resourceImg = new FileSystemResource(f);
                HttpHeaders pictureHeader = new HttpHeaders();
                pictureHeader.setContentType(MediaType.IMAGE_JPEG);
                HttpEntity<Resource> picturePart = new HttpEntity<>(resourceImg, pictureHeader);
                multipartRequest.add("rod_images_attributes[0][image]", picturePart);
            }

            multipartRequest.add("datetime", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(run.getDatetime()));
            multipartRequest.add("distance", run.getDistance());
            multipartRequest.add("duration", run.getDuration() / 1000);
            multipartRequest.add("note", run.getNote());

            HttpMessageConverter<Object> jackson = new MappingJackson2HttpMessageConverter();
            HttpMessageConverter<Resource> resource = new ResourceHttpMessageConverter();
            FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
            formHttpMessageConverter.addPartConverter(jackson);
            formHttpMessageConverter.addPartConverter(resource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest, headers);

            RestTemplate restTemplate = new RestTemplate(Arrays.asList(jackson, resource, formHttpMessageConverter));
            int code = 0;

            ResponseEntity<Object> result;

            if(run.getId() != null){
                result = restTemplate.exchange(urls[0], HttpMethod.PUT, requestEntity, Object.class);
            }else{
                result = restTemplate.exchange(urls[0], HttpMethod.POST, requestEntity, Object.class);
            }

            code = result.getStatusCode().value();

            if(f != null){
                f.delete();
            }

            return code;

        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            Bitmap runImage = null;
            try {
                InputStream in = new java.net.URL(urls[0]).openStream();
                runImage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return runImage;
        }

        protected void onPostExecute(Bitmap result) {
            viewImage().setImageBitmap(result);
        }
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

}