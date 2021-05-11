package com.sun.institute.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sun.institute.R;
import com.sun.institute.adapter.TimeTableAdapter;
import com.sun.institute.databinding.FragmentDashboardBinding;
import com.sun.institute.databinding.FragmentHomeBinding;
import com.sun.institute.databinding.FragmentTimeTableBinding;
import com.sun.institute.network.ApiInterface;
import com.sun.institute.network.NoConnectivityException;
import com.sun.institute.network.RetrofitService;
import com.sun.institute.response.DepartmentResponse;
import com.sun.institute.response.TimeTableResponse;
import com.sun.institute.sessions.UserSessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimeTableFragment extends Fragment {
    FragmentTimeTableBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentTimeTableBinding.inflate(getLayoutInflater());
        UserSessionManager userSessionManager = new UserSessionManager(getContext());
       String timetableId = userSessionManager.getUserDetails().get("time_table_id");
       String id = userSessionManager.getUserDetails().get("id");

        getTimeTable(id);

        return binding.getRoot();
    }

    public void getTimeTable(String id) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        Call<TimeTableResponse> call = RetrofitService.createService(ApiInterface.class, requireContext()).timeTable(id);
        call.enqueue(new Callback<TimeTableResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<TimeTableResponse> call, @NonNull Response<TimeTableResponse> response) {
                binding.progressCircular.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<TimeTableResponse.InfoBean> timeTable = response.body().getInfo();
                    binding.timeTableRecycler.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
                    TimeTableAdapter adapter = new TimeTableAdapter(timeTable,getActivity());
                    binding.timeTableRecycler.setAdapter(adapter);

                } else if (response.errorBody() != null) {
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<TimeTableResponse> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Whenever you want to show toast use setValue.

                }
                binding.progressCircular.setVisibility(View.GONE);

            }
        });
    }
}