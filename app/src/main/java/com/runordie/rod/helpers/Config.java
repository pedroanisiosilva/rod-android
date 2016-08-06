package com.runordie.rod.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;

import com.runordie.rod.R;
import com.runordie.rod.login.Login;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by wsouza on 7/2/16.
 */
public class Config {
    private static final String TAG = "Helper";
    private static Properties properties;

    public static String getConfigValue(Context context, String name) {
        try {
            return properties(context).getProperty(name);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        }

        return null;
    }


    private static Properties properties(Context context) throws IOException {
        Resources resources = context.getResources();
        if(properties == null){
            InputStream rawResource = resources.openRawResource(R.raw.config);
            properties = new Properties();
            properties.load(rawResource);
        }
        return properties;
    }

    @NonNull
    public static HttpHeaders getHttpHeaders(Context context) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Email", Login.getLoginInfo(context)[0]);
        headers.set("X-User-Token", Login.getLoginInfo(context)[1]);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }
    public static String getHost(Context context){
        return Config.getConfigValue(context, "rod_host");
    }
    private static String uriWithUserID(Context context, String key){
        return getConfigValue(context, key).replace("user_id", Login.getLoginInfo(context)[2]);
    }
    public static String getRunPostUrl(Context context) {
        return getHost(context) + uriWithUserID(context, "run_create_path");
    }

    public static String getRunDUUrl(Context context, Integer id) {
        return getHost(context) + Config.getConfigValue(context, "run_du_path").replace("run_id", id.toString());
    }

    public static String getRunsUrl(Context context) {
        return getHost(context) + uriWithUserID(context, "runs_path");
    }

}
