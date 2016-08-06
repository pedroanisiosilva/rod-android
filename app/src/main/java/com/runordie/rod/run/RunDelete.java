package com.runordie.rod.run;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.runordie.rod.login.Login;

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
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * Created by wsouza on 7/23/16.
 */
public class RunDelete extends AsyncTask<String, Void, Boolean> {
    private Run run;
    private Context context;

    public RunDelete(Run run, Context context){
        this.run = run;
        this.context = context;

    }

    @Override
    protected Boolean doInBackground(String... urls) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Email", Login.getLoginInfo(context)[0]);
        headers.set("X-User-Token", Login.getLoginInfo(context)[1]);

        HttpEntity<?> requestEntity = new HttpEntity<Object>(headers);

        RestTemplate restTemplate = new RestTemplate();

        try{
            ResponseEntity<String> response = restTemplate.exchange(urls[0], HttpMethod.DELETE, requestEntity, String.class);
            if(response.getStatusCode().value() == 204){
                return true;
            }
        }catch (Exception e){
            //tratar status code
            e.printStackTrace();
        }
        return false;

    }
}
