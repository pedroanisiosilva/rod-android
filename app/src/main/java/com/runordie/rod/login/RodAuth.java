package com.runordie.rod.login;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.RelativeLayout;

import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wsouza on 7/3/16.
 */
public class RodAuth extends AsyncTask<String, Void, LoginResult> {
    private static final String TAG = "RodAuth";

    private String email;
    private String pwd;

    public RodAuth(String email, String pwd){
        this.email = email;
        this.pwd = pwd;
    }
    protected LoginResult doInBackground(String... url) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("password", getPwd());
        params.add("email", getEmail());

        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        try {
            return restTemplate.postForObject(url[0], params, LoginResult.class);
        }catch (HttpClientErrorException e){
            Log.e(TAG, e.getLocalizedMessage());
            if(e.getStatusCode().value() == 401){
                return new LoginResult(false);
            }
        }

        return new LoginResult(true);

    }

    public String getEmail() {
        return email;
    }

    public String getPwd() {
        return pwd;
    }
}