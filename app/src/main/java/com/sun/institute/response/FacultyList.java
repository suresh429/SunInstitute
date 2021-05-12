package com.sun.institute.response;

import com.google.gson.annotations.SerializedName;

public class FacultyList {


    @SerializedName("msg")
    private String msg;
    @SerializedName("status")
    private String status;
    @SerializedName("info")
    private String info;
    @SerializedName("faculty_id")
    private String facultyId;
    @SerializedName("timetable")
    private String timetable;
    @SerializedName("subject_id")
    private String subjectId;
    @SerializedName("subject_name")
    private String subjectName;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId;
    }

    public String getTimetable() {
        return timetable;
    }

    public void setTimetable(String timetable) {
        this.timetable = timetable;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
