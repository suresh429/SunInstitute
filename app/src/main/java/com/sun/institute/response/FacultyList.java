package com.sun.institute.response;

import com.google.gson.annotations.SerializedName;

public class FacultyList {
    @SerializedName("msg")
    private String msg;
    @SerializedName("info")
    private String info;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
