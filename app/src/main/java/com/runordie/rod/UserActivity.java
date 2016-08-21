package com.runordie.rod;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by wsouza on 8/8/16.
 */
public class UserActivity  extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings);
        ((EditText) findViewById(R.id.userCellphone)).addTextChangedListener(new PhoneNumberFormattingTextWatcher("BR"));

    }

}
