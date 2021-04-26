package com.sun.institute.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.sun.institute.adapter.AttendanceAdapter;

import com.sun.institute.databinding.FragmentDashboardBinding;
import com.sun.institute.network.ApiInterface;
import com.sun.institute.network.NoConnectivityException;
import com.sun.institute.network.RetrofitService;
import com.sun.institute.response.DepartmentResponse;
import com.sun.institute.response.SectionResponse;
import com.sun.institute.response.StatusResponse;
import com.sun.institute.response.StudentsResponse;
import com.sun.institute.response.SubjectResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {
    private static final String TAG = "DashboardFragment";
    FragmentDashboardBinding binding;
    ArrayList<String> departmentNameList = new ArrayList<>();
    ArrayList<String> departmentIdList = new ArrayList<>();
    ArrayList<String> sectionNameList = new ArrayList<>();
    ArrayList<String> sectionIdList = new ArrayList<>();
    AttendanceAdapter adapter;
    List<SubjectResponse.InfoBean> subject;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(getLayoutInflater());

        getSubject();
        getDepartment();

        return binding.getRoot();
    }

    public void getDepartment() {
        binding.progressCircular.setVisibility(View.VISIBLE);
        Call<DepartmentResponse> call = RetrofitService.createService(ApiInterface.class, requireContext()).getDepartment("class");
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
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<DepartmentResponse> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
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
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, departmentNameList);
        //setting adapter to spinner
        binding.spinner1.setAdapter(adapter);
        //Creating an array adapter for list view

        binding.spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sectionNameList.clear();
                sectionIdList.clear();

                if (position != 0) {
                    getSection(departmentIdList.get(position));
                    binding.recyclerView.setVisibility(View.GONE);
                } else {
                    binding.recyclerView.setVisibility(View.GONE);
                   
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void getSection(String id) {
        Call<SectionResponse> call = RetrofitService.createService(ApiInterface.class, requireContext()).getSection("section", id);
        call.enqueue(new Callback<SectionResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<SectionResponse> call, @NonNull Response<SectionResponse> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<SectionResponse.InfoBean> section = response.body().getInfo();
                    showSectioninSpinner(section);

                } else if (response.errorBody() != null) {

                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SectionResponse> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Whenever you want to show toast use setValue.

                }


            }
        });
    }

    private void showSectioninSpinner(List<SectionResponse.InfoBean> section) {
        sectionNameList.add("Select Section");
        sectionIdList.add("-1");
        for (SectionResponse.InfoBean infoBean : section) {
            sectionNameList.add(infoBean.getSecName());
            sectionIdList.add(infoBean.getSecId());
        }

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sectionNameList);
        //setting adapter to spinner
        binding.spinner2.setAdapter(adapter);
        //Creating an array adapter for list view

        binding.spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    getAllStudentsListData(sectionIdList.get(position));
                    binding.recyclerView.setVisibility(View.VISIBLE);
                } else {
                    binding.recyclerView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void getSubject() {

        Call<SubjectResponse> call = RetrofitService.createService(ApiInterface.class, requireContext()).getSubject("subject");
        call.enqueue(new Callback<SubjectResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<SubjectResponse> call, @NonNull Response<SubjectResponse> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    subject = response.body().getInfo();

                    // getAllStudentsListData(subject);

                } else if (response.errorBody() != null) {

                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SubjectResponse> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Whenever you want to show toast use setValue.

                }


            }
        });
    }

    public void getAllStudentsListData(String sectionId) {
        binding.progressCircular.setVisibility(View.VISIBLE);
        Call<StudentsResponse> call = RetrofitService.createService(ApiInterface.class, requireContext()).getAllStudents("student", sectionId);
        call.enqueue(new Callback<StudentsResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<StudentsResponse> call, @NonNull Response<StudentsResponse> response) {

                if (response.isSuccessful()) {
                    binding.progressCircular.setVisibility(View.GONE);
                    assert response.body() != null;
                    List<StudentsResponse.InfoBean> student = response.body().getInfo();

                    binding.recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
                    adapter = new AttendanceAdapter(student, requireContext(), subject, new AttendanceAdapter.AdapterCallback() {
                        @Override
                        public void presentClick(StudentsResponse.InfoBean studentsResponse, String subjectId) {
                            getAttendanceStatus(studentsResponse.getStuId(), subjectId, studentsResponse.getDeptId(), studentsResponse.getSecId(), "1", studentsResponse.getStuName());
                        }

                        @Override
                        public void absentClick(StudentsResponse.InfoBean studentsResponse, String subjectId) {
                            getAttendanceStatus(studentsResponse.getStuId(), subjectId, studentsResponse.getDeptId(), studentsResponse.getSecId(), "2", studentsResponse.getStuName());

                        }
                    });
                    binding.recyclerView.setAdapter(adapter);

                } else if (response.errorBody() != null) {
                    binding.progressCircular.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(@NonNull Call<StudentsResponse> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Whenever you want to show toast use setValue.

                }
                binding.progressCircular.setVisibility(View.GONE);

            }
        });
    }

    public void getAttendanceStatus(String stuId, String subjectId, String deptId, String secId, String status, String stuName) {
        Call<StatusResponse> call = RetrofitService.createService(ApiInterface.class, requireContext()).setAttendance("student_attendance", stuId, status, subjectId, deptId, secId);
        call.enqueue(new Callback<StatusResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    StatusResponse statusResponse = response.body();

                    if (status.equalsIgnoreCase("1")) {
                        Toast.makeText(getContext(), "" + stuName + " is Present", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "" + stuName + " is Absent", Toast.LENGTH_SHORT).show();
                    }

                } else if (response.errorBody() != null) {
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Whenever you want to show toast use setValue.

                }


            }
        });
    }

}