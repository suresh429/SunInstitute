package com.sun.institute.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TimeTableResponse {


    @SerializedName("msg")
    private int msg;
    @SerializedName("info")
    private List<InfoBean> info;

    public int getMsg() {
        return msg;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public List<InfoBean> getInfo() {
        return info;
    }

    public void setInfo(List<InfoBean> info) {
        this.info = info;
    }

    public static class InfoBean {
        @SerializedName("dept_name")
        private String deptName;
        @SerializedName("sec_name")
        private String secName;
        @SerializedName("sem_name")
        private String semName;
        @SerializedName("sub_name")
        private String subName;
        @SerializedName("faculty_topic")
        private String facultyTopic;
        @SerializedName("create_time")
        private String createTime;
        @SerializedName("statu")
        private String statu;
        @SerializedName("sem_id")
        private String semId;

        public String getDeptName() {
            return deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }

        public String getSecName() {
            return secName;
        }

        public void setSecName(String secName) {
            this.secName = secName;
        }

        public String getSemName() {
            return semName;
        }

        public void setSemName(String semName) {
            this.semName = semName;
        }

        public String getSubName() {
            return subName;
        }

        public void setSubName(String subName) {
            this.subName = subName;
        }

        public String getFacultyTopic() {
            return facultyTopic;
        }

        public void setFacultyTopic(String facultyTopic) {
            this.facultyTopic = facultyTopic;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getStatu() {
            return statu;
        }

        public void setStatu(String statu) {
            this.statu = statu;
        }

        public String getSemId() {
            return semId;
        }

        public void setSemId(String semId) {
            this.semId = semId;
        }
    }
}
