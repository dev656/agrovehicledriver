package com.jaats.agrovehicledriver.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jaats.agrovehicledriver.R;
import com.jaats.agrovehicledriver.app.App;
import com.jaats.agrovehicledriver.config.Config;
import com.jaats.agrovehicledriver.listeners.LoginListener;
import com.jaats.agrovehicledriver.model.AuthBean;
import com.jaats.agrovehicledriver.net.DataManager;
import com.jaats.agrovehicledriver.util.AppConstants;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseAppCompatNoDrawerActivity {

    private EditText etxtEmail;
    private EditText etxtPassword;
    private String email;
    private String password;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);

        if (SharedPrefManager.getInstans(this).isLogin()) {
            finish();/*
            startActivity(new Intent(this, HOME_ACTIVITY.class));*/
            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intent);
            return;
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            lytBase.setPadding(0, App.getStatusBarHeight(), 0, 0);
//            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }

        initViews();

        getSupportActionBar().hide();
        swipeView.setPadding(0, 0, 0, 0);
        lytBase.setFitsSystemWindows(false);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                lytContent.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
//                mVibrator.vibrate(25);

                startActivity(new Intent(this, WelcomeActivity.class));
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, WelcomeActivity.class));
        super.onBackPressed();
    }

    private void initViews() {




        etxtEmail = (EditText) findViewById(R.id.etxt_login_email);
        etxtPassword = (EditText) findViewById(R.id.etxt_login_password);

        Button btnLogin = (Button) findViewById(R.id.btn_login_submit);
        Button btnForgotPassword = (Button) findViewById(R.id.btn_login_forgot_password);

        btnLogin.setTypeface(typeface);
        btnForgotPassword.setTypeface(typefaceBold);

        etxtEmail.setTypeface(typeface);
        etxtPassword.setTypeface(typeface);
        etxtPassword.setTransformationMethod(new PasswordTransformationMethod());

    }


    public void onLoginSubmitClick(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        //mVibrator.vibrate(25);

        if (collectLoginData()) {
            if (App.isNetworkAvailable()) {
                performLogin();
            } else {
                Snackbar.make(coordinatorLayout, AppConstants.NO_NETWORK_AVAILABLE, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            }
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    private boolean collectLoginData() {
        if (!etxtEmail.getText().toString().equals("")) {
            email = etxtEmail.getText().toString();
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Snackbar.make(coordinatorLayout, R.string.message_enter_a_valid_email_address, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
                return false;
            }
        } else {
            Snackbar.make(coordinatorLayout, R.string.message_email_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }
        if (!etxtPassword.getText().toString().equals(""))
            password = etxtPassword.getText().toString();
        else {
            Snackbar.make(coordinatorLayout, R.string.message_password_is_required, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();
            return false;
        }

        return true;
    }


    public void performLogin() {
        swipeView.setRefreshing(true);
        final String Mobileholder =  etxtEmail.getText().toString().trim();
        final String passwordHolder =etxtPassword.getText().toString().trim();

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        String Url="https://nkploggy.com/api/provider/oauth/token";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {
                        try {

                            JSONObject jsonObject=new JSONObject(ServerResponse);
                            JSONObject jsonObject1=jsonObject.getJSONObject("result");

                            String id= jsonObject1.getString("id");

                            String name= jsonObject1.getString("first_name");

                            String ln =jsonObject1.getString("last_name");


                            SharedPrefManager.getInstans(getApplicationContext()).userLogin(
                                 id,name,ln);

                            Toast.makeText(LoginActivity.this, "Login Successfully ", Toast.LENGTH_SHORT).show();


                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                            //Toast.makeText(LoginActivity.this,id+name+ln+"", Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


/*

*/




                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        // Hiding the progress dialog after all task complete.


                        // Showing error message if something goes wrong.
                        Toast.makeText(LoginActivity.this, "Retry", Toast.LENGTH_SHORT).show();
                        //  Toast.makeText(LoginActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                    }


                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.


                params.put("email",Mobileholder);
                params.put("password",passwordHolder);
                return params;
            }

        };

        // Creating RequestQueue.


        // Adding the StringRequest object into requestQueue.
        queue.add(stringRequest);


    }

    /*private void performLogin() {
        swipeView.setRefreshing(true);

        JSONObject postData = getLoginJSObj();

        DataManager.performLogin(postData, new LoginListener() {
            @Override
            public void onLoadCompleted(AuthBean authBean) {
                swipeView.setRefreshing(false);
                App.saveToken(authBean);
                if (!Config.getInstance().isPhoneVerified()) {
                    startActivity(new Intent(LoginActivity.this, OtpVerificationActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
            }

            @Override
            public void onLoadFailed(String error) {
                swipeView.setRefreshing(false);
                Snackbar.make(coordinatorLayout, error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.btn_dismiss, snackBarDismissOnClickListener).show();

                   *//* To Be Removed....*//*
                if (App.getInstance().isDemo()) {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });

    }*/

    private JSONObject getLoginJSObj() {
        JSONObject postData = new JSONObject();
        try {
            postData.put("email", email);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return postData;
    }

    public void onLoginForgotPasswordClick(View view) {

        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        //mVibrator.vibrate(25);

        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }
}
