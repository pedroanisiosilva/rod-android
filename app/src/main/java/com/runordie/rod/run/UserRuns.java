package com.runordie.rod.run;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.runordie.rod.helpers.Config;
import com.runordie.rod.login.Login;
import com.runordie.rod.login.LoginResult;

import org.springframework.http.ContentCodingType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by wsouza on 7/6/16.
 */
public class UserRuns extends AsyncTask<String, Void, Runs> {

    private RestTemplate restTemplate;
    private Context context;

    public Runs doInBackground(String... url) {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("X-User-Email", Login.getLoginInfo(getContext())[0]);
        requestHeaders.set("X-User-Token", Login.getLoginInfo(getContext())[1]);
        HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

        RestTemplate restTemplate = new RestTemplate();
        // tratar aqui na classe as exception, remover da atividade
        ResponseEntity<Runs> runs = restTemplate.exchange(url[0], HttpMethod.GET, requestEntity, Runs.class);

        return runs.getBody();
    }

    public UserRuns(Context context) {
        this.context = context;
    }

    public RestTemplate getRestTemplate() {
        if(this.restTemplate == null)
            this.restTemplate = new RestTemplate();
        return restTemplate;
    }

    public Context getContext() {
        return context;
    }

}
