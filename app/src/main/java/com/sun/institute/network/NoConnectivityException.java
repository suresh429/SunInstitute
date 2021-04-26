package com.sun.institute.network;

import java.io.IOException;

public class NoConnectivityException extends IOException {

    @Override
    public String getMessage() {
        return "No network available, please check your WiFi or Data connection";
        // You can send any message whatever you want from here.
    }
}
