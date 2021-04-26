package com.sun.institute.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sun.institute.R;
import com.sun.institute.databinding.FragmentDashboardBinding;
import com.sun.institute.databinding.FragmentHomeBinding;
import com.sun.institute.databinding.FragmentTimeTableBinding;

public class TimeTableFragment extends Fragment {
    FragmentTimeTableBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTimeTableBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }
}