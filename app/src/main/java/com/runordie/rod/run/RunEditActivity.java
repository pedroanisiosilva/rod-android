package com.runordie.rod.run;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SquaringDrawable;
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
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.runordie.rod.run.views.RunEditViewActions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
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
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

/**
 * Created by wsouza on 7/9/16.
 */
public class RunEditActivity extends RunEditViewActions {

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
                Calendar cal = Calendar.getInstance();
                TimeZone tz = cal.getTimeZone();

                kmsOfRun().setText(runToUpdate.getDistance().toString());
                descriptionOfRun().setText(runToUpdate.getNote());
                durationOfRun().setText(DurationPickerFragment.parseDateToHours(runToUpdate.getDuration() * 1000));

                SimpleDateFormat sdf0 = new SimpleDateFormat("dd/MM/yyyy");
                sdf0.setTimeZone(tz);
                dateOfRun().setText(sdf0.format(runToUpdate.getDatetime()));

                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                sdf1.setTimeZone(tz);
                timeOfRun().setText(sdf1.format(runToUpdate.getDatetime()));

                if(runToUpdate.getImagePath() != null){
                    Log.i("IMAGE", Config.getHost(getBaseContext()) + runToUpdate.getImagePath());
                    Glide.with(this)
                            .load(Config.getHost(getBaseContext()) + runToUpdate.getImagePath())
                            .asBitmap().into(viewImage());
                }
                getRun().setId(runToUpdate.getId());
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    viewImage().setImageBitmap(image);

                } catch (IOException e) {
//                Log.e(TAG, e.getMessage(),e);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == 1) {
            Uri imageUri = data.getData();
//            Bitmap image = BitmapFactory.decodeFile(imageUri.getPath());

            try {
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setInitialCropWindowRectangle(this.getCropBox(imageUri))
                        .start(this);
                Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                viewImage().setImageBitmap(image);
            } catch (IOException e) {
//                Log.e(TAG, e.getMessage(),e);
            }


        }
    }

    @Override
    protected void doCreateRun() {
        if(new Validation().isValid()){
            try {
                BuildRun buildRun = new BuildRun().build();
                if(getRun().getId() != null){
                    new RunPost(buildRun).execute(Config.getRunDUUrl(this, getRun().getId()));
                }else{
                    new RunPost(buildRun).execute(Config.getRunPostUrl(this));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            getRun().setDatetime(data);
            getRun().setUserId(Integer.parseInt(userId));
            getRun().setDuration(DurationPickerFragment.parseDuration(duration));
            getRun().setDistance(kms);
            getRun().setNote(descriptionOfRun().getText().toString());
            if(viewImage().getDrawable() != null){
                bitmap = ((BitmapDrawable)viewImage().getDrawable()).getBitmap();
            }
            return this;
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
                if(getRun().getId() != null){
                    Toast.makeText(getBaseContext(), "Corrida criada com sucesso", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getBaseContext(), "Corrida atualizada com sucesso", Toast.LENGTH_LONG).show();
                }
                finish();
            }else{
                Toast.makeText(getBaseContext(), "Erro ao salvar corrida", Toast.LENGTH_SHORT).show();
            }
            ((RelativeLayout)findViewById(R.id.loadingPanel)).setVisibility(View.GONE);

        }

        @Override
        protected Integer doInBackground(String... urls) {
            HttpHeaders headers = Config.getHttpHeaders(getBaseContext());

            MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
            File f = null;
            if(buildRun.getBitmap() != null){
                // fazer refactory para buildRun ter uma instancia nova de Run
                f = new File(getBaseContext().getCacheDir(), getRun().getDatetime().getTime() + getRun().getUserId() + ".jpeg");
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    buildRun.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, bos);
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

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            multipartRequest.add("datetime", sdf.format(getRun().getDatetime()));
            multipartRequest.add("distance", getRun().getDistance());
            multipartRequest.add("duration", getRun().getDuration() / 1000);
            multipartRequest.add("note", getRun().getNote());

            HttpMessageConverter<Object> jackson = new MappingJackson2HttpMessageConverter();
            HttpMessageConverter<Resource> resource = new ResourceHttpMessageConverter();
            FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
            formHttpMessageConverter.addPartConverter(jackson);
            formHttpMessageConverter.addPartConverter(resource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest, headers);

            RestTemplate restTemplate = new RestTemplate(Arrays.asList(jackson, resource, formHttpMessageConverter));
            int code = 0;

            ResponseEntity<Object> result;
            try {
                if(getRun().getId() != null){
                    result = restTemplate.exchange(urls[0], HttpMethod.PUT, requestEntity, Object.class);
                }else{
                    result = restTemplate.exchange(urls[0], HttpMethod.POST, requestEntity, Object.class);
                }
                code = result.getStatusCode().value();
            }catch (ResourceAccessException e){
                e.printStackTrace();
            }

            if(f != null){
                f.delete();
            }

            return code;

        }
    }

}