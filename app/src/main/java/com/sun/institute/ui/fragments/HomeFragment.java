package com.sun.institute.ui.fragments;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sun.institute.databinding.FragmentHomeBinding;

import java.io.FileDescriptor;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    private static final int PICK_IMAGES_CODE = 100;
    FragmentHomeBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater());

        binding.btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
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
            Uri uri = data.getData();
            binding.txtFilepath.setText(uri.toString());
        }


    }


}