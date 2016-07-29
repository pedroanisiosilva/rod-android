package com.runordie.rod.login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by wsouza on 7/2/16.
 */
public class Login {
    public static final String USER_FIELD = "usremail";
    public static final String USER_PWD_FIELD = "usrpwd";
    public static final String USER_ID = "usrid";

    public static String KEY_STORE = "SESSION";

    public static void logOut(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(Login.KEY_STORE, Activity.MODE_PRIVATE).edit();
        editor.remove(USER_FIELD);
        editor.remove(USER_PWD_FIELD);
        editor.commit();
    }
    public static Boolean isLogged(Context context){
        String[] loginInfo  = getLoginInfo(context);
        return !loginInfo[0].equals("") && !loginInfo[1].equals("") && !loginInfo[2].equals("");
    }

    public static void saveLoginInfo(String email, String token, String userId, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Login.KEY_STORE, Activity.MODE_PRIVATE).edit();
        editor.putString(USER_FIELD, email);
        editor.putString(USER_PWD_FIELD, token);
        editor.putString(USER_ID, userId);
        editor.commit();
    }

    public  static String[] getLoginInfo(Context context) {
        SharedPreferences savedSession = context.getSharedPreferences(KEY_STORE, Activity.MODE_PRIVATE);
        return new String[]{savedSession.getString(USER_FIELD, ""), savedSession.getString(USER_PWD_FIELD, ""), savedSession.getString(USER_ID, "")};
    }

    public static Boolean isValidParams(EditText emailField, EditText pwdField){
        boolean valid = true;

        String email = emailField.getText().toString();
        String password = pwdField.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("enter a valid email address");
            emailField.requestFocus();
            valid = false;
        } else {
            emailField.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            pwdField.setError("between 4 and 20 alphanumeric characters");
            emailField.requestFocus();
            valid = false;
        } else {
            pwdField.setError(null);
        }

        return valid;

    }
}
