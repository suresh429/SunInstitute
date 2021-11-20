package com.sun.institute.ui.fragments.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.sun.institute.databinding.ActivityLoginBinding;
import com.sun.institute.network.ApiInterface;
import com.sun.institute.network.NoConnectivityException;
import com.sun.institute.network.RetrofitService;
import com.sun.institute.response.DepartmentResponse;
import com.sun.institute.response.FacultyList;
import com.sun.institute.sessions.UserSessionManager;

import java.sql.Time;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    ActivityLoginBinding binding;
    UserSessionManager userSessionManager;
    String currentTime, newTime, dep_id;
    ArrayList<String> departmentNameList = new ArrayList<>();
    ArrayList<String> departmentIdList = new ArrayList<>();

    int mHour;
    int mMin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getDepartment();

        // login session
        userSessionManager = new UserSessionManager(LoginActivity.this);
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

        Log.d(TAG, "onCreate: " + currentTime + "--------" + newTime);

        binding.btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        binding.btnLogin.setOnClickListener(v -> {

            String mobile = Objects.requireNonNull(binding.etMobile.getText()).toString().trim();
            String startTime = Objects.requireNonNull(binding.etStartTime.getText()).toString().trim();
            String endTime = Objects.requireNonNull(binding.etEndTime.getText()).toString().trim();
            if (mobile.isEmpty() || !isValidMobile(mobile) || binding.etMobile.getText().toString().length() < 10) {
                binding.txtInputLayout.setError("Enter Valid Mobile No.");

            } else if (dep_id == "-1") {
                Toast.makeText(LoginActivity.this, "Please select Class", Toast.LENGTH_SHORT).show();
            } else if (startTime.isEmpty()) {
                binding.inputStartTime.setError("Select Start Time");
            } else if (endTime.isEmpty()) {
                binding.inputEndTime.setError("Select End Time");
            } else {
                binding.txtInputLayout.setErrorEnabled(false);
                binding.inputStartTime.setErrorEnabled(false);
                binding.inputEndTime.setErrorEnabled(false);
                Intent intent = new Intent(LoginActivity.this, FingerLoginActivity.class);
                intent.putExtra("MOBILE", mobile);
                intent.putExtra("dep_id", dep_id);
                intent.putExtra("startTime", startTime);
                intent.putExtra("endTime", endTime);
                intent.putExtra("STATUS", "Login");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            // loginFinger("");

        });

        binding.btnAttendance.setOnClickListener(v -> {

            Intent intent = new Intent(LoginActivity.this, FingerLoginActivity.class);
            intent.putExtra("MOBILE", "");
            intent.putExtra("STATUS", "Attendance");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

           /* String mobile = Objects.requireNonNull(binding.etMobile.getText()).toString().trim();
            if (mobile.isEmpty() || !isValidMobile(mobile) || binding.etMobile.getText().toString().length() < 10) {
                binding.txtInputLayout.setError("Enter Valid Mobile No.");

            } else {
                binding.txtInputLayout.setErrorEnabled(false);
                Intent intent = new Intent(LoginActivity.this, FingerLoginActivity.class);
                intent.putExtra("MOBILE",mobile);
                intent.putExtra("STATUS","Attendance");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }*/
        });

        binding.etStartTime.setOnClickListener(v -> {
            binding.etStartTime.setText("");
            setTimeEdittext(binding.etStartTime);
        });

        binding.etEndTime.setOnClickListener(v -> {
            binding.etEndTime.setText("");
            setTimeEdittext(binding.etEndTime);
        });


    }

    private void setTimeEdittext(TextInputEditText etTime) {
        StringBuilder stringBuilder = new StringBuilder();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {

            mHour = hourOfDay;
            mMin = minute;

            String am_pm;

            Calendar datetime = Calendar.getInstance();
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            datetime.set(Calendar.MINUTE, minute);

            am_pm = getTime(mHour, mMin);

            stringBuilder.append(" ");
            stringBuilder.append(am_pm);
            etTime.setText(stringBuilder);

        }, mHour, mMin, false);

        timePickerDialog.show();
    }

    @SuppressLint("SimpleDateFormat")
    public String getTime(int hr, int min) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hr);
        cal.set(Calendar.MINUTE, min);
        Format formatter;
        formatter = new SimpleDateFormat("h:mm a");
        return formatter.format(cal.getTime());
    }


    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public void getDepartment() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        Call<DepartmentResponse> call = RetrofitService.createService(ApiInterface.class, this).getDepartment("class");
        call.enqueue(new Callback<DepartmentResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<DepartmentResponse> call, @NonNull Response<DepartmentResponse> response) {
                binding.progressCircular.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<DepartmentResponse.InfoBean> department = response.body().getInfo();
                    showDepartmentinSpinner(department);

                } else if (response.errorBody() != null) {
                    Toast.makeText(LoginActivity.this, response.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<DepartmentResponse> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Whenever you want to show toast use setValue.

                }
                binding.progressCircular.setVisibility(View.GONE);

            }
        });
    }

    private void showDepartmentinSpinner(List<DepartmentResponse.InfoBean> department) {
        departmentNameList.add("Select Class");
        departmentIdList.add("-1");
        for (DepartmentResponse.InfoBean infoBean : department) {
            departmentNameList.add(infoBean.getDeptName());
            departmentIdList.add(infoBean.getDeptId());
        }

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(LoginActivity.this, android.R.layout.simple_list_item_1, departmentNameList);
        //setting adapter to spinner
        binding.spinner1.setAdapter(adapter);
        //Creating an array adapter for list view

        binding.spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    dep_id = "-1";
                } else {
                    dep_id = departmentIdList.get(position).toString();
                }

                Log.d(TAG, "onItemSelected: " + dep_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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