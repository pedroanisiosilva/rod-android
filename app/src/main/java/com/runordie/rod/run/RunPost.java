package com.runordie.rod.run;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.runordie.rod.login.Login;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wsouza on 7/23/16.
 */
public class RunPost extends AsyncTask<String, Void, Integer> {
    private Run run;
    private Context context;
    private Bitmap img;

    public RunPost(Run run, Context context, Bitmap img){
        this.run = run;
        this.context = context;
        this.img = img;
    }
    @Override
    protected Integer doInBackground(String... urls) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Email", Login.getLoginInfo(context)[0]);
        headers.set("X-User-Token", Login.getLoginInfo(context)[1]);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
        File f = null;
        if(img != null){
            f = new File(context.getCacheDir(), "aaa1.jpg");
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.JPEG, 60, bos);
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
        multipartRequest.add("duration", run.getDuration() / 1000 / 60);
        multipartRequest.add("note", run.getNote());

        HttpMessageConverter<Object> jackson = new MappingJackson2HttpMessageConverter();
        HttpMessageConverter<Resource> resource = new ResourceHttpMessageConverter();
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        formHttpMessageConverter.addPartConverter(jackson);
        formHttpMessageConverter.addPartConverter(resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest, headers);

        RestTemplate restTemplate = new RestTemplate(Arrays.asList(jackson, resource, formHttpMessageConverter));
        int code = 0;
        try{
            ResponseEntity<Object> result = restTemplate.exchange(urls[0], HttpMethod.POST, requestEntity, Object.class);
            code = result.getStatusCode().value();
        }catch (Exception e){
            e.printStackTrace();
        }

        if(f != null){
            f.delete();
        }

        return code;

    }
}
