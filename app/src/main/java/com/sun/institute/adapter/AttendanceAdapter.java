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
import com.sun.institute.response.StudentsResponse;
import com.sun.institute.response.SubjectResponse;

import java.util.List;


public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {

    List<StudentsResponse.InfoBean> modelList;
    Context mContext;
    List<SubjectResponse.InfoBean> subject;
    String subjectId,subjectName;

    private AdapterCallback mListener;

    public AttendanceAdapter(List<StudentsResponse.InfoBean> modelList, Context mContext, List<SubjectResponse.InfoBean> subject, String subjectId, String subjectName, AdapterCallback mListener) {
        this.modelList = modelList;
        this.mContext = mContext;
        this.subject = subject;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.mListener = mListener;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AttendancelistItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentsResponse.InfoBean student = modelList.get(position);
        holder.rowItemBinding.txtName.setText(modelList.get(position).getStuName());
        holder.rowItemBinding.txtFatherName.setText("S/O " + modelList.get(position).getFatherName());
        holder.rowItemBinding.txtSubjectName.setText( subjectName);

       /* String[] items = new String[subject.size()];
        //Traversing through the whole list to get all the names
        for (int i = 0; i < subject.size(); i++) {
            //Storing names to string array
            items[i] = subject.get(i).getSubName();
        }

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        holder.rowItemBinding.spinner.setAdapter(aa);
        holder.rowItemBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjectId = subject.get(position).getSubId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        holder.rowItemBinding.btnPresent.setOnClickListener(v -> {
            mListener.presentClick(student, subjectId);

            holder.rowItemBinding.btnPresent.setAlpha(0.6f);
            holder.rowItemBinding.btnPresent.setEnabled(false);

            holder.rowItemBinding.btnAbsent.setAlpha(0.6f);
            holder.rowItemBinding.btnAbsent.setEnabled(false);
        });

        holder.rowItemBinding.btnAbsent.setOnClickListener(v -> {
            mListener.absentClick(student, subjectId);

            holder.rowItemBinding.btnPresent.setAlpha(0.6f);
            holder.rowItemBinding.btnPresent.setEnabled(false);

            holder.rowItemBinding.btnAbsent.setAlpha(0.6f);
            holder.rowItemBinding.btnAbsent.setEnabled(false);
        });

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
        AttendancelistItemBinding rowItemBinding;

        public ViewHolder(@NonNull AttendancelistItemBinding rowItemBinding) {
            super(rowItemBinding.getRoot());
            this.rowItemBinding = rowItemBinding;
        }
    }

    public interface AdapterCallback {
        void presentClick(StudentsResponse.InfoBean studentsResponse, String subjectId);

        void absentClick(StudentsResponse.InfoBean studentsResponse, String subjectId);
    }
}
