package com.runordie.rod.run;

import android.content.Context;
import android.os.AsyncTask;

import com.runordie.rod.login.Login;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by wsouza on 7/23/16.
 */
public class RunPost extends AsyncTask<String, Void, Run> {
    private Run run;
    private Context context;

    public RunPost(Run run, Context context){
        this.run = run;
        this.context = context;
    }
    @Override
    protected Run doInBackground(String... urls) {
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-User-Email", Login.getLoginInfo(context)[0]);
        headers.set("X-User-Token", Login.getLoginInfo(context)[1]);

        HttpEntity<Run> requestEntity = new HttpEntity<Run>(run, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Run> result = restTemplate.exchange(urls[0], HttpMethod.POST, requestEntity, Run.class);

        return result.getBody();

    }
}
