package com.sun.institute.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SectionResponse {

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
        @SerializedName("sec_id")
        private String secId;
        @SerializedName("dept_id")
        private String deptId;
        @SerializedName("sec_name")
        private String secName;
        @SerializedName("sec_status")
        private String secStatus;
        @SerializedName("create_date")
        private String createDate;
        @SerializedName("update_date")
        private String updateDate;

        public String getSecId() {
            return secId;
        }

        public void setSecId(String secId) {
            this.secId = secId;
        }

        public String getDeptId() {
            return deptId;
        }

        public void setDeptId(String deptId) {
            this.deptId = deptId;
        }

        public String getSecName() {
            return secName;
        }

        public void setSecName(String secName) {
            this.secName = secName;
        }

        public String getSecStatus() {
            return secStatus;
        }

        public void setSecStatus(String secStatus) {
            this.secStatus = secStatus;
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
