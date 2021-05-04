package com.sun.institute.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("msg")
    private String msg;
    @SerializedName("info")
    private InfoBean info;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public static class InfoBean {
        @SerializedName("0")
        private String $0;
        @SerializedName("id")
        private String id;
        @SerializedName("1")
        private String $1;
        @SerializedName("fname")
        private String fname;
        @SerializedName("2")
        private String $2;
        @SerializedName("last_name")
        private String lastName;
        @SerializedName("3")
        private String $3;
        @SerializedName("email")
        private String email;
        @SerializedName("4")
        private String $4;
        @SerializedName("mobile")
        private String mobile;
        @SerializedName("5")
        private String $5;
        @SerializedName("type")
        private String type;
        @SerializedName("6")
        private String $6;
        @SerializedName("thumb")
        private String thumb;
        @SerializedName("7")
        private String $7;
        @SerializedName("create_on")
        private String createOn;
        @SerializedName("8")
        private String $8;
        @SerializedName("update_on")
        private String updateOn;
        @SerializedName("9")
        private String $9;
        @SerializedName("create_time")
        private String createTime;
        @SerializedName("10")
        private String $10;
        @SerializedName("update_time")
        private String updateTime;
        @SerializedName("11")
        private String $11;
        @SerializedName("fstatus")
        private String fstatus;

        public String get$0() {
            return $0;
        }

        public void set$0(String $0) {
            this.$0 = $0;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String get$1() {
            return $1;
        }

        public void set$1(String $1) {
            this.$1 = $1;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public String get$2() {
            return $2;
        }

        public void set$2(String $2) {
            this.$2 = $2;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String get$3() {
            return $3;
        }

        public void set$3(String $3) {
            this.$3 = $3;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String get$4() {
            return $4;
        }

        public void set$4(String $4) {
            this.$4 = $4;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String get$5() {
            return $5;
        }

        public void set$5(String $5) {
            this.$5 = $5;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String get$6() {
            return $6;
        }

        public void set$6(String $6) {
            this.$6 = $6;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String get$7() {
            return $7;
        }

        public void set$7(String $7) {
            this.$7 = $7;
        }

        public String getCreateOn() {
            return createOn;
        }

        public void setCreateOn(String createOn) {
            this.createOn = createOn;
        }

        public String get$8() {
            return $8;
        }

        public void set$8(String $8) {
            this.$8 = $8;
        }

        public String getUpdateOn() {
            return updateOn;
        }

        public void setUpdateOn(String updateOn) {
            this.updateOn = updateOn;
        }

        public String get$9() {
            return $9;
        }

        public void set$9(String $9) {
            this.$9 = $9;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String get$10() {
            return $10;
        }

        public void set$10(String $10) {
            this.$10 = $10;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public String get$11() {
            return $11;
        }

        public void set$11(String $11) {
            this.$11 = $11;
        }

        public String getFstatus() {
            return fstatus;
        }

        public void setFstatus(String fstatus) {
            this.fstatus = fstatus;
        }
    }
}
