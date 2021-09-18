package com.sun.institute.ui.fragments.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.sun.institute.databinding.ActivityLoginBinding;
import com.sun.institute.network.ApiInterface;
import com.sun.institute.network.NoConnectivityException;
import com.sun.institute.network.RetrofitService;
import com.sun.institute.response.FacultyList;
import com.sun.institute.sessions.UserSessionManager;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    ActivityLoginBinding binding;
    UserSessionManager userSessionManager;
    String currentTime,newTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // login session
         userSessionManager= new UserSessionManager(LoginActivity.this);
        if (userSessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        currentTime = dateFormat.format(new Date()).toString();

        Date d = null;
        try {
            d = dateFormat.parse(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MINUTE, 10);
        newTime = dateFormat.format(cal.getTime());

        Log.d(TAG, "onCreate: "+currentTime+"--------"+newTime);

        binding.btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });


        binding.btnLogin.setOnClickListener(v -> {

            String mobile = Objects.requireNonNull(binding.etMobile.getText()).toString().trim();
            if (mobile.isEmpty() || !isValidMobile(mobile) || binding.etMobile.getText().toString().length() < 10) {
                binding.txtInputLayout.setError("Enter Valid Mobile No.");

            } else {
                binding.txtInputLayout.setErrorEnabled(false);
                Intent intent = new Intent(LoginActivity.this, FingerLoginActivity.class);
                intent.putExtra("MOBILE",mobile);
                intent.putExtra("STATUS","Login");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

           // loginFinger("");

        });

        binding.btnAttendance.setOnClickListener(v -> {
            String mobile = Objects.requireNonNull(binding.etMobile.getText()).toString().trim();
            if (mobile.isEmpty() || !isValidMobile(mobile) || binding.etMobile.getText().toString().length() < 10) {
                binding.txtInputLayout.setError("Enter Valid Mobile No.");

            } else {
                binding.txtInputLayout.setErrorEnabled(false);
                Intent intent = new Intent(LoginActivity.this, FingerLoginActivity.class);
                intent.putExtra("MOBILE",mobile);
                intent.putExtra("STATUS","Attendance");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


    }



    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }


/*
    private void loginFinger(String stringT2){

        Call<FacultyList> call = RetrofitService.createService(ApiInterface.class, LoginActivity.this).loginFinger( "8465945100",currentTime,newTime);
        call.enqueue(new Callback<FacultyList>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<FacultyList> call, @NonNull Response<FacultyList> response) {

                Log.d(TAG, "onResponsestatus: "+response.isSuccessful());

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    FacultyList statusResponse = response.body();

                    if (statusResponse.getStatus().equalsIgnoreCase("true")){

                        String data =statusResponse.getInfo();

                    }else {
                        Toast.makeText(LoginActivity.this, "Invalid Mobile No", Toast.LENGTH_SHORT).show();

                    }


                } else if (response.errorBody() != null) {
                    Toast.makeText(LoginActivity.this, response.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<FacultyList> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Whenever you want to show toast use setValue.

                }

                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });
    }
*/
}