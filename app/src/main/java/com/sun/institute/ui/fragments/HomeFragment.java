package com.sun.institute.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.sun.institute.adapter.TimeTableAdapter;
import com.sun.institute.databinding.FragmentHomeBinding;
import com.sun.institute.network.ApiInterface;
import com.sun.institute.network.NoConnectivityException;
import com.sun.institute.network.RetrofitService;
import com.sun.institute.response.HomeResponse;
import com.sun.institute.response.TimeTableResponse;
import com.sun.institute.sessions.UriUtils;
import com.sun.institute.sessions.UserSessionManager;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    private static final int PICK_IMAGES_CODE = 100;
    FragmentHomeBinding binding;
    Uri uri;
    private String timetableId,id;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        UserSessionManager userSessionManager = new UserSessionManager(getContext());
        timetableId = userSessionManager.getUserDetails().get("time_table_id");
        id = userSessionManager.getUserDetails().get("id");

        dashBoard();

        binding.btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri != null && !Objects.requireNonNull(binding.etNote.getText()).toString().isEmpty()){
                    uploadCplData(uri,binding.etNote.getText().toString());
                }else {
                    Toast.makeText(getActivity(), "Fill All Details", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return binding.getRoot();
    }

    private void pickImage() {
        /*Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.ACTION_PICK, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_IMAGES_CODE);
*/


        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
       // Ask specifically for something that can be opened:
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("image/*");
        startActivityForResult(
                Intent.createChooser(chooseFile, "Choose a file"),
                PICK_IMAGES_CODE
        );

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode ==PICK_IMAGES_CODE){
            uri = Objects.requireNonNull(data).getData();
            binding.txtFilepath.setText(uri.toString());
        }


    }

    public void dashBoard() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        Call<HomeResponse> call = RetrofitService.createService(ApiInterface.class, requireContext()).dashBoard(timetableId);
        call.enqueue(new Callback<HomeResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<HomeResponse> call, @NonNull Response<HomeResponse> response) {
                binding.progressCircular.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<HomeResponse.InfoBean> data = response.body().getInfo();

                    binding.txtClass.setText(data.get(0).getDeptName());
                    binding.txtSection.setText(data.get(0).getSecName());
                    binding.txtSemister.setText(data.get(0).getSemName());
                    binding.txtFacultyName.setText(data.get(0).getFname());
                    binding.txtSubject.setText(data.get(0).getSubName());
                    binding.txtFacultyTopic.setText(data.get(0).getFacultyTopic());



                } else if (response.errorBody() != null) {
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<HomeResponse> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Whenever you want to show toast use setValue.

                }
                binding.progressCircular.setVisibility(View.GONE);

            }
        });
    }

    public void uploadCplData(Uri uri,String noteData) {
        String path = UriUtils.getPathFromUri(getActivity(), uri);
        File file = new File(path);
        RequestBody requestFile = RequestBody.create(file,MediaType.parse("multipart/form-data"));

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        RequestBody note = RequestBody.create(noteData,MediaType.parse("text/plain"));
        RequestBody id = RequestBody.create(timetableId,MediaType.parse("text/plain"));

        binding.progressCircular.setVisibility(View.VISIBLE);
        Call<ResponseBody> call = RetrofitService.createService(ApiInterface.class, requireContext()).cplData(id,note,body);
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                binding.progressCircular.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    assert response.body() != null;

                    Toast.makeText(getContext(), "Cpl Data Upload Successfully", Toast.LENGTH_SHORT).show();


                } else if (response.errorBody() != null) {
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Whenever you want to show toast use setValue.

                }
                Log.d(TAG, "onFailure: "+t.getMessage());
                binding.progressCircular.setVisibility(View.GONE);

            }
        });
    }

}