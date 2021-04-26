package com.sun.institute.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DepartmentResponse {
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
        @SerializedName("dept_id")
        private String deptId;
        @SerializedName("course_id")
        private String courseId;
        @SerializedName("dept_name")
        private String deptName;
        @SerializedName("status")
        private String status;
        @SerializedName("create_date")
        private String createDate;
        @SerializedName("update_date")
        private String updateDate;



        public String getDeptId() {
            return deptId;
        }

        public void setDeptId(String deptId) {
            this.deptId = deptId;
        }

        public String getCourseId() {
            return courseId;
        }

        public void setCourseId(String courseId) {
            this.courseId = courseId;
        }

        public String getDeptName() {
            return deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }
    }
}
