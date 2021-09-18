package com.sun.institute.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ThumbDataResponse {


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
        @SerializedName("id")
        private String id;
        @SerializedName("fname")
        private String fname;
        @SerializedName("last_name")
        private String lastName;
        @SerializedName("email")
        private String email;
        @SerializedName("mobile")
        private String mobile;
        @SerializedName("type")
        private String type;
        @SerializedName("thumb")
        private String thumb;
        @SerializedName("salary")
        private String salary;
        @SerializedName("join_date")
        private String joinDate;
        @SerializedName("create_on")
        private String createOn;
        @SerializedName("update_on")
        private String updateOn;
        @SerializedName("fstatus")
        private String fstatus;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String getSalary() {
            return salary;
        }

        public void setSalary(String salary) {
            this.salary = salary;
        }

        public String getJoinDate() {
            return joinDate;
        }

        public void setJoinDate(String joinDate) {
            this.joinDate = joinDate;
        }

        public String getCreateOn() {
            return createOn;
        }

        public void setCreateOn(String createOn) {
            this.createOn = createOn;
        }

        public String getUpdateOn() {
            return updateOn;
        }

        public void setUpdateOn(String updateOn) {
            this.updateOn = updateOn;
        }

        public String getFstatus() {
            return fstatus;
        }

        public void setFstatus(String fstatus) {
            this.fstatus = fstatus;
        }
    }
}
