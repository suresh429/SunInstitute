package com.sun.institute.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubjectResponse {

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
        @SerializedName("sub_id")
        private String subId;
        @SerializedName("sub_name")
        private String subName;
        @SerializedName("status")
        private String status;
        @SerializedName("create_date")
        private String createDate;
        @SerializedName("update_date")
        private String updateDate;

        public String getSubId() {
            return subId;
        }

        public void setSubId(String subId) {
            this.subId = subId;
        }

        public String getSubName() {
            return subName;
        }

        public void setSubName(String subName) {
            this.subName = subName;
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
