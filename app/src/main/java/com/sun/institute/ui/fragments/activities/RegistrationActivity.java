package com.sun.institute.ui.fragments.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.sun.institute.databinding.ActivityRegistrationBinding;
import com.sun.institute.network.ApiInterface;
import com.sun.institute.network.NoConnectivityException;
import com.sun.institute.network.RetrofitService;
import com.sun.institute.response.StatusResponse;

import java.util.Calendar;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    ActivityRegistrationBinding binding;
    private static final String TAG = "RegistrationActivity";
    String requiredThumb, selectedRbText, type;
    RadioButton selectedRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registration");

        binding.etDateOfJoin.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        if(mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon-1);

                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    binding.etDateOfJoin.setText(current);
                    binding.etDateOfJoin.setSelection(sel < current.length() ? sel : current.length());



                }
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.btnFinger.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, FingerEnrollActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        });

        binding.btnSubmit.setOnClickListener(v -> {

            int selectedRadioButtonId = binding.rgGroup.getCheckedRadioButtonId();
            if (selectedRadioButtonId != -1) {
                selectedRadioButton = findViewById(selectedRadioButtonId);
                selectedRbText = selectedRadioButton.getText().toString();

                if (selectedRbText.equalsIgnoreCase("Teaching")) {
                    type = "1";
                } else {
                    type = "2";
                }
                // textView.setText(selectedRbText + " is Selected");
            }

            if (Objects.requireNonNull(binding.etFirstName.getText()).toString().isEmpty()) {

                Toast.makeText(RegistrationActivity.this, "Enter FirstName", Toast.LENGTH_SHORT).show();

            } else if (Objects.requireNonNull(binding.etLastName.getText()).toString().isEmpty()) {
                Toast.makeText(RegistrationActivity.this, "Enter LastName", Toast.LENGTH_SHORT).show();

            } else if (Objects.requireNonNull(binding.etEmailId.getText()).toString().isEmpty()) {
                Toast.makeText(RegistrationActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();

            } else if (Objects.requireNonNull(binding.etMobile.getText()).toString().isEmpty()) {
                Toast.makeText(RegistrationActivity.this, "Enter Mobile", Toast.LENGTH_SHORT).show();

            } else if (Objects.requireNonNull(binding.etSalary.getText()).toString().isEmpty()) {
                Toast.makeText(RegistrationActivity.this, "Enter Salary", Toast.LENGTH_SHORT).show();

            } else if (Objects.requireNonNull(binding.etDateOfJoin.getText()).toString().isEmpty()) {
                Toast.makeText(RegistrationActivity.this, "Enter Date Of Join", Toast.LENGTH_SHORT).show();

            } else if (selectedRadioButtonId == -1) {
                Toast.makeText(RegistrationActivity.this, "Nothing selected from the radio group", Toast.LENGTH_SHORT).show();
            } else if (requiredThumb == null) {
                Toast.makeText(RegistrationActivity.this, "Please Enroll Finger First", Toast.LENGTH_SHORT).show();
            } else {
                registerFinger();

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            requiredThumb = Objects.requireNonNull(data).getStringExtra("THUMB");

        }
    }

    private void registerFinger() {

        binding.progressCircular.setVisibility(View.VISIBLE);
        Call<StatusResponse> call = RetrofitService.createService(ApiInterface.class, RegistrationActivity.this).registerFinger(binding.etFirstName.getText().toString(), binding.etLastName.getText().toString(), binding.etEmailId.getText().toString(), binding.etMobile.getText().toString(),binding.etSalary.getText().toString(),binding.etDateOfJoin.getText().toString(), type, requiredThumb);
        call.enqueue(new Callback<StatusResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {

                if (response.isSuccessful()) {
                    binding.progressCircular.setVisibility(View.GONE);

                    assert response.body() != null;
                    StatusResponse statusResponse = response.body();

                    Log.d(TAG, "onResponse: " + statusResponse.getMsg());

                    if (statusResponse.getMsg().equalsIgnoreCase("success")) {
                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Toast.makeText(RegistrationActivity.this, "" + statusResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "" + statusResponse.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                } else if (response.errorBody() != null) {
                    Toast.makeText(RegistrationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    binding.progressCircular.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    Toast.makeText(RegistrationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Whenever you want to show toast use setValue.

                }

                binding.progressCircular.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}