package com.runordie.rod.login;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runordie.rod.R;
import com.runordie.rod.RodActivity;
import com.runordie.rod.helpers.Config;

import java.util.concurrent.ExecutionException;


/**
 * Created by wsouza on 7/2/16.
 */
public class RodLoginActivity extends AppCompatActivity {
    private static final String TAG = "RodLoginActivity";

    private static final int REQUEST_SIGNUP = 0;

    EditText emailText;
    EditText passwordText;
    Button loginButton;
    TextView signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if(!Login.isLogged(this)){
            setContentView(R.layout.activity_rod_login);
            loginButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doLogin();
                }
            });
//        }else{
//            startRod();
//        }
    }
    private void startRod(){
        startActivity(new Intent(this, RodActivity.class));
        this.finish();
    }
    public void doLogin() {
        if(Login.isValidParams(emailField(),  pwdField())){
            loginButton().setEnabled(false);
            String url = Config.getConfigValue(this, "rod_host") + Config.getConfigValue(this, "rod_session");

            try {
                RelativeLayout loading = (RelativeLayout) findViewById(R.id.loadingPanel);
                LoginResult loginResult = new RodAuth(loading, emailField().getText().toString(), pwdField().getText().toString()).execute(url).get();

                if(!loginResult.getSuccess()){
                    onLoginFailed();
                }else{
                    Log.i(TAG, loginResult.getAuthToken());
                    Login.saveLoginInfo(loginResult.getEmail(), loginResult.getAuthToken(),loginResult.getUserId().toString(), this);
                    startRod();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            loginButton().setEnabled(true);

        }
    }
    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed - Wrong User or PWD", Toast.LENGTH_LONG).show();
        loginButton().setEnabled(true);
    }
    private EditText pwdField() {
        if(passwordText == null)
            passwordText = (EditText) findViewById(R.id.input_password);
        return passwordText;
    }

    private EditText emailField() {
        if(emailText == null)
            emailText = (EditText) findViewById(R.id.input_email);

        return emailText;
    }

    private Button loginButton(){
        if(loginButton == null)
            loginButton = (Button) findViewById(R.id.btn_login);

        return loginButton;
    }
}
