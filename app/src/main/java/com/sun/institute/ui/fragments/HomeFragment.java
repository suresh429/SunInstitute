package com.sun.institute.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sun.institute.BuildConfig;
import com.sun.institute.R;
import com.sun.institute.adapter.TimeTableAdapter;
import com.sun.institute.databinding.FragmentHomeBinding;
import com.sun.institute.network.ApiInterface;
import com.sun.institute.network.NoConnectivityException;
import com.sun.institute.network.RetrofitService;
import com.sun.institute.response.HomeResponse;
import com.sun.institute.response.TimeTableResponse;
import com.sun.institute.sessions.FileCompressor;
import com.sun.institute.sessions.UriUtils;
import com.sun.institute.sessions.UserSessionManager;
import com.sun.institute.ui.fragments.activities.LoginActivity;
import com.sun.institute.ui.fragments.activities.SplashActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY_PHOTO = 2;
    File mPhotoFile;
    FileCompressor mCompressor;
    Uri fileUri;

    private static final int PICK_IMAGES_CODE = 100;
    FragmentHomeBinding binding;
    Uri uri;
    private String timetableId,id;

    UserSessionManager userSessionManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        mCompressor = new FileCompressor(requireContext());

        userSessionManager = new UserSessionManager(getContext());
        timetableId = userSessionManager.getUserDetails().get("time_table_id");
        id = userSessionManager.getUserDetails().get("id");



        dashBoard();

        binding.btnChoose.setOnClickListener(v -> selectImage());

        binding.btnSubmit.setOnClickListener(v -> {
            if (mPhotoFile != null && !Objects.requireNonNull(binding.etNote.getText()).toString().isEmpty()){
                uploadCplData(mPhotoFile,binding.etNote.getText().toString());
            }else {
                Toast.makeText(getActivity(), "Fill All Details", Toast.LENGTH_SHORT).show();
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


   /* @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode ==PICK_IMAGES_CODE){
            uri = Objects.requireNonNull(data).getData();
            binding.txtFilepath.setText(uri.toString());
        }


    }*/

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

    public void uploadCplData(File uri, String noteData) {
       /* String path = UriUtils.getPathFromUri(getActivity(), uri);
        File file = new File(path);*/

        RequestBody requestFile = RequestBody.create(mPhotoFile,MediaType.parse("multipart/form-data"));

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", mPhotoFile.getName(), requestFile);

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

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        Intent intent = new Intent(requireActivity(),LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        Toast.makeText(getContext(), "Session Expired !", Toast.LENGTH_LONG).show();

                        userSessionManager.clearSession();
                    },2000);

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


    /**
     * Alert dialog for capture or select from galley
     */
    private void selectImage() {
        final CharSequence[] items = {
                "Take Photo", "Choose from Library",
                "Cancel"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Take Photo")) {
                requestStoragePermission(true);
            } else if (items[item].equals("Choose from Library")) {
                requestStoragePermission(false);
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Capture image from camera
     */
    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireActivity(),
                        BuildConfig.APPLICATION_ID ,
                        photoFile);
                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

        /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }*/
    }

    /**
     * Select image fro gallery
     */
    private void dispatchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK ) {
            if (requestCode == REQUEST_TAKE_PHOTO ) {

                //  fileUri = data.getData();
                try {
                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                    binding.txtFilepath.setText(mPhotoFile.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                fileUri = data.getData();
                try {
                    mPhotoFile = mCompressor.compressToFile(new File(getRealPathFromUri(fileUri)));
                    binding.txtFilepath.setText(mPhotoFile.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }



    /**
     * Requesting multiple permissions (storage and camera) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission(boolean isCamera) {
        Dexter.withActivity(requireActivity())
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                dispatchTakePictureIntent();
                            } else {
                                dispatchGalleryIntent();
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                                   PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(
                        error -> Toast.makeText(requireActivity(), "Error occurred! ", Toast.LENGTH_SHORT)
                                .show())
                .onSameThread()
                .check();
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage(
                "This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    /**
     * Create file with current timestamp name
     *
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(mFileName, ".jpg", storageDir);
    }

    /**
     * Get real file path from URI
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = requireActivity().getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}