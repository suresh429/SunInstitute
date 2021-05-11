package com.sun.institute.ui.fragments.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    ActivityRegistrationBinding binding;
    private static final String TAG = "RegistrationActivity";
    String requiredThumb, selectedRbText,type;
    RadioButton selectedRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registration");

        binding.btnFinger.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, FingerEnrollActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        });

        binding.btnSubmit.setOnClickListener(v -> {

            int selectedRadioButtonId = binding.rgGroup.getCheckedRadioButtonId();
            if (selectedRadioButtonId != -1) {
                selectedRadioButton = findViewById(selectedRadioButtonId);
                selectedRbText = selectedRadioButton.getText().toString();

                if (selectedRbText.equalsIgnoreCase("Teaching")){
                    type= "1";
                }else {
                    type ="2";
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
        Call<StatusResponse> call = RetrofitService.createService(ApiInterface.class, RegistrationActivity.this).registerFinger(binding.etFirstName.getText().toString(), binding.etLastName.getText().toString(), binding.etEmailId.getText().toString(), binding.etMobile.getText().toString(), type, requiredThumb);
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
                        Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
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