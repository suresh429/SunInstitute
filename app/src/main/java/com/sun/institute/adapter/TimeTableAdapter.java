package com.sun.institute.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sun.institute.databinding.AttendancelistItemBinding;
import com.sun.institute.databinding.TimetablelistItemBinding;
import com.sun.institute.response.StudentsResponse;
import com.sun.institute.response.SubjectResponse;
import com.sun.institute.response.TimeTableResponse;

import java.util.List;


public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.ViewHolder> {

    List<TimeTableResponse.InfoBean> modelList;
    Context mContext;

    public TimeTableAdapter(List<TimeTableResponse.InfoBean> modelList, Context mContext) {
        this.modelList = modelList;
        this.mContext = mContext;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(TimetablelistItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimeTableResponse.InfoBean table = modelList.get(position);
        holder.rowItemBinding.txtFacultyTopic.setText("Topic : "+table.getFacultyTopic().trim());
        holder.rowItemBinding.txtClass.setText("Class : " + table.getDeptName());
        holder.rowItemBinding.txtSemister.setText("Semister : " + table.getSemName());
        holder.rowItemBinding.txtSubject.setText("Subject : " + table.getSubName());
        holder.rowItemBinding.txtLoginTime.setText("Login Time : " + table.getCreateTime());
       // holder.rowItemBinding.txtLogoutTime.setText("Logout Time : " + table.getUpdateTime());

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TimetablelistItemBinding rowItemBinding;

        public ViewHolder(@NonNull TimetablelistItemBinding rowItemBinding) {
            super(rowItemBinding.getRoot());
            this.rowItemBinding = rowItemBinding;
        }
    }


}
