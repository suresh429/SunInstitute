package com.sun.institute.response;

import com.google.gson.annotations.SerializedName;

public class StatusResponse {

    @SerializedName("msg")
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
