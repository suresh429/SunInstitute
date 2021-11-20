package com.sun.institute.ui.fragments.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.acpl.access_computech_fm220_sdk.FM220_Scanner_Interface;
import com.acpl.access_computech_fm220_sdk.acpl_FM220_SDK;
import com.acpl.access_computech_fm220_sdk.fm220_Capture_Result;
import com.acpl.access_computech_fm220_sdk.fm220_Init_Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sun.institute.R;
import com.sun.institute.network.ApiInterface;
import com.sun.institute.network.NoConnectivityException;
import com.sun.institute.network.RetrofitService;
import com.sun.institute.response.FacultyList;
import com.sun.institute.response.StatusResponse;
import com.sun.institute.response.ThumbDataResponse;
import com.sun.institute.sessions.UserSessionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FingerLoginActivity extends AppCompatActivity implements FM220_Scanner_Interface {
    private static final int REQUEST = 112;
    private static final String TAG = "MainActivity";
    private acpl_FM220_SDK FM220SDK;
    private Button Capture_No_Preview, Capture_PreView, Capture_BackGround, Capture_Match;
    private Button Enroll_finger, abort_button;
    private TextView textMessage;
    private ImageView imageView;
    private ProgressBar progress_circular;
    private String currentTime, newTime;
    UserSessionManager userSessionManager;
    //  private Button btn_Release, btn_Clam, btn_RDCapture;
//  region Sen
    private static final String Telecom_Device_Key = "";
    private byte[] t1, t2;

    //region USB intent and functions
    private UsbManager manager;
    private PendingIntent mPermissionIntent;
    private UsbDevice usb_Dev;
    private static final String ACTION_USB_PERMISSION = "com.ACPL.FM220_Telecom.USB_PERMISSION";
    private static boolean isLocalConn = false;

    String mobileNo, status,dep_id,startTime,endTime;

    @SuppressLint("SetTextI18n")
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                int pid, vid;
                pid = device.getProductId();
                vid = device.getVendorId();
                if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
                    FM220SDK.stopCaptureFM220();
                    FM220SDK.unInitFM220();
                    usb_Dev = null;
                    textMessage.setText("FM220 disconnected");
                    isLocalConn = false;
                    DisableCapture();
                }
            }
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            // call method to set up device communication
                            int pid, vid;
                            pid = device.getProductId();
                            vid = device.getVendorId();
                            if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
                                fm220_Init_Result res = FM220SDK.InitScannerFM220(manager, device, Telecom_Device_Key);
                                if (res.getResult()) {
                                    textMessage.setText("FM220 ready. " + res.getSerialNo());
                                    EnableCapture();
                                    isLocalConn = true;
                                } else {
                                    textMessage.setText("Error :-" + res.getError());
                                    DisableCapture();
                                    isLocalConn = false;
                                }
                            }
                        }
                    } else {
                        textMessage.setText("User Blocked USB connection");
                        DisableCapture();
                    }
                }
            }
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null) {
                        // call method to set up device communication
                        int pid, vid;
                        pid = device.getProductId();
                        vid = device.getVendorId();
                        if ((pid == 0x8225) && (vid == 0x0bca) && !FM220SDK.FM220isTelecom()) {
                            Toast.makeText(context, "Wrong device type application restart required!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        if ((pid == 0x8220) && (vid == 0x0bca) && FM220SDK.FM220isTelecom()) {
                            Toast.makeText(context, "Wrong device type application restart required!", Toast.LENGTH_LONG).show();
                            finish();
                        }

                        if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
                            if (!manager.hasPermission(device)) {
                                textMessage.setText("FM220 requesting permission");
                                manager.requestPermission(device, mPermissionIntent);
                            } else {
                                fm220_Init_Result res = FM220SDK.InitScannerFM220(manager, device, Telecom_Device_Key);
                                if (res.getResult()) {
                                    textMessage.setText("FM220 ready. " + res.getSerialNo());
                                    EnableCapture();
                                    isLocalConn = true;
                                } else {
                                    textMessage.setText("Error :-" + res.getError());
                                    DisableCapture();
                                    isLocalConn = false;
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onNewIntent(Intent intent) {
        if (getIntent() != null) {
            return;
        }
        super.onNewIntent(intent);
        setIntent(intent);
        try {
            if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED) && usb_Dev == null) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null) {
                    // call method to set up device communication & Check pid
                    int pid, vid;
                    pid = device.getProductId();
                    vid = device.getVendorId();
                    if ((pid == 0x8225) && (vid == 0x0bca)) {
                        if (manager != null) {
                            if (!manager.hasPermission(device)) {
                                textMessage.setText("FM220 requesting permission");
                                manager.requestPermission(device, mPermissionIntent);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mUsbReceiver);
            FM220SDK.unInitFM220();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_close_24);
        requestStoragePermission();

        userSessionManager = new UserSessionManager(this);
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        currentTime = dateFormat.format(new Date()).toString();

        Date d = null;
        try {
            d = dateFormat.parse(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MINUTE, 10);
        newTime = dateFormat.format(cal.getTime());


        textMessage = findViewById(R.id.textMessage);
        Capture_PreView = findViewById(R.id.button2);
        Capture_No_Preview = findViewById(R.id.button);
        Capture_BackGround = findViewById(R.id.button3);
        Capture_Match = findViewById(R.id.button4);
        Enroll_finger = findViewById(R.id.button5);
        abort_button = findViewById(R.id.button6);
        imageView = findViewById(R.id.imageView);
        progress_circular = findViewById(R.id.progress_circular);

        Enroll_finger.setVisibility(View.GONE);
        Capture_Match.setVisibility(View.GONE);

        Capture_PreView.setText("Login With Finger");

        if (getIntent() != null) {
            mobileNo = getIntent().getStringExtra("MOBILE");
            dep_id = getIntent().getStringExtra("dep_id");
            startTime = getIntent().getStringExtra("startTime");
            endTime = getIntent().getStringExtra("endTime");
            status = getIntent().getStringExtra("STATUS");

            Log.d(TAG, "onCreate: "+startTime +"  "+endTime);

        }


//      btn_Release = findViewById(R.id.releasebutton);
//      btn_Clam = findViewById(R.id.clambutton);
//      btn_RDCapture = findViewById(R.id.rdcap);
//      Region USB initialisation and Scanning for device
        SharedPreferences sp = getSharedPreferences("last_FM220_type", Activity.MODE_PRIVATE);
        boolean oldDevType = sp.getBoolean("FM220type", true);


        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        final Intent piIntent = new Intent(ACTION_USB_PERMISSION);
        if (Build.VERSION.SDK_INT >= 16) piIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        mPermissionIntent = PendingIntent.getBroadcast(getBaseContext(), 1, piIntent, 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mUsbReceiver, filter);
        UsbDevice device = null;
        for (UsbDevice mdevice : manager.getDeviceList().values()) {
            int pid, vid;
            pid = mdevice.getProductId();
            vid = mdevice.getVendorId();
            boolean devType;
            if ((pid == 0x8225) && (vid == 0x0bca)) {
                FM220SDK = new acpl_FM220_SDK(getApplicationContext(), this, true);
                devType = true;
            } else if ((pid == 0x8220) && (vid == 0x0bca)) {
                FM220SDK = new acpl_FM220_SDK(getApplicationContext(), this, false);
                devType = false;
            } else {
                FM220SDK = new acpl_FM220_SDK(getApplicationContext(), this, oldDevType);
                devType = oldDevType;
            }
            if (oldDevType != devType) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("FM220type", devType);
                editor.apply();
            }
            if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
                device = mdevice;
                if (!manager.hasPermission(device)) {
                    textMessage.setText("FM220 requesting permission");
                    manager.requestPermission(device, mPermissionIntent);
                } else {
                    Intent intent = this.getIntent();
                    if (intent != null) {
                        /*if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
                            finishAffinity();
                        }*/

                        if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(intent.getAction())) {
                            finishAffinity();
                        } else {
                            Log.d(TAG, "onCreate: " + "error");
                        }
                        //
                    }
                    fm220_Init_Result res = FM220SDK.InitScannerFM220(manager, device, Telecom_Device_Key);
                    if (res.getResult()) {
                        textMessage.setText("FM220 ready. " + res.getSerialNo());
                        EnableCapture();
                        isLocalConn = true;
                    } else {
                        textMessage.setText("Error :-" + res.getError());
                        DisableCapture();
                        isLocalConn = false;
                    }
                }
                break;
            }
        }

        if (device == null) {
            textMessage.setText("Pl connect FM220");
            FM220SDK = new acpl_FM220_SDK(getApplicationContext(), this, oldDevType);
        }


        Capture_BackGround.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisableCapture();
                textMessage.setText("Pl wait..");
                imageView.setImageBitmap(null);
                FM220SDK.CaptureFM220(2);

//                if (isLocalConn) {
//                    DisableCapture();
//                    textMessage.setText("Pl wait..");
//                    imageView.setImageBitmap(null);
//                    FM220SDK.CaptureFM220(2);
//                } else {
//                    boolean isval = CheckUSBconn();
//                    if (isval) {
//                        textMessage.setText("Pl wait..");
//                        imageView.setImageBitmap(null);
//                        FM220SDK.CaptureFM220(2);
//                    }
//                }
            }
        });

        Capture_No_Preview.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisableCapture();
                textMessage.setText("Pl wait..");
                FM220SDK.CaptureFM220(2, true, false);

//                if (isLocalConn) {
//                    DisableCapture();
//                    textMessage.setText("Pl wait..");
//                    FM220SDK.CaptureFM220(2, true, false);
//                } else {
//                    boolean isval = CheckUSBconn();
//                    if (isval) {
//                        textMessage.setText("Pl wait..");
//                        imageView.setImageBitmap(null);
//                        FM220SDK.CaptureFM220(2,true,false);
//                    }
//                }
            }
        });

        Capture_PreView.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                DisableCapture();
                FM220SDK.Settimeout(8);       // set the timeout value for fingerprint capture set valuse as a secound .
                int ti = FM220SDK.Gettimeout();     // get timeout value it return milisecound of timeout.
                FM220SDK.CaptureFM220(2, true, true);

//                if (isLocalConn) {
//                    DisableCapture();
//                    FM220SDK.Settimeout(8);       // set the timeout value for fingerprint capture set valuse as a secound .
//                    int ti = FM220SDK.Gettimeout();     // get timeout value it return milisecound of timeout.
//                    FM220SDK.CaptureFM220(2, true, true);
//                } else {
//                    boolean isval = CheckUSBconn();
//                    if (isval) {
//                        textMessage.setText("Pl wait..");
//                        imageView.setImageBitmap(null);
//                        FM220SDK.CaptureFM220(2,true,true);
//                    }
//                }
            }
        });

        Enroll_finger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DisableCapture();
                FM220SDK.EnrollFM220(true, true);
//                if (isLocalConn) {
//                    DisableCapture();
//                    FM220SDK.EnrollFM220(true, true);
//                } else {
//                    textMessage.setText("Pl Press Release Button");
//                }
            }
        });

        abort_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FM220SDK.stopCaptureFM220();
            }
        });

        Capture_Match.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    /*
                     * if t1 and t2 is byte so you can use MatchFM220(byte[],baye[]) function
                     and its String so you can use MatchFm220String(StringTmp1,StringTmp2) function
                     for your Matching templets. and you want at time match your finger with scaning process use
                     FM220SDK.MatchFM220(2, true, true, oldfingerprintisovale) function with pass old templet as perameter.
                     */
                    if (t1 != null) {
                        // SetNFIQ == 2
                        DisableCapture();  // alwasy set 2 here..
                        FM220SDK.MatchFM220(2, true, true, t1);

                    }

                   /* if (t1 != null && t2 != null) {

                        if (FM220SDK.MatchFM220(t1, t2)) {
                            textMessage.setText("Finger matched");
                        } else {
                            textMessage.setText("Finger not matched");
                        }
                        // String teamplet match example using FunctionBAse64 function .....
                        String t1base64, t2base64;
                        t1base64 = Base64.encodeToString(t1, Base64.NO_WRAP);
                        t2base64 = Base64.encodeToString(t2, Base64.NO_WRAP);
                        boolean matchval = FunctionBase64(t1base64, t2base64);
                        if (matchval) {
                            Toast toast = Toast.makeText(getBaseContext(), "Finger matched", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(getBaseContext(), "Finger not matched", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                        *//*Finger match using MatchFM220Score fuction its is return score value and check this return value is greterthan or equals *//*
                        int MatchScr_val = FM220SDK.MatchFM220Score(t1, t2);
                        if (MatchScr_val >= 4000) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Finger is Matched 'Score' :-" + String.valueOf(MatchScr_val), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Finger not Matched 'Score' :-" + String.valueOf(MatchScr_val), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        t1 = null;
                        t2 = null;
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


//        try {
//            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
//            // if external memory exists and folder with name Notes
//            if (!root.exists()) {
//                root.mkdirs(); // this will create folder.
//            }
//            File filepath = new File(root, "test" + ".txt");  // file pa
//            if (filepath.exists()) filepath.delete();
//            filepath.createNewFile();
//            try {
//                //BufferedWriter for performance, true to set append to file flag
//                BufferedWriter buf = new BufferedWriter(new FileWriter(filepath, true));
//                buf.append("Application start");
//                buf.newLine();
//                buf.close();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


    }

//    private boolean CheckUSBconn() {
//        try {
//            manager = (UsbManager) getSystemService(Context.USB_SERVICE);
//            final Intent piIntent = new Intent(ACTION_USB_PERMISSION);
//            if (Build.VERSION.SDK_INT >= 16) piIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
//            mPermissionIntent = PendingIntent.getBroadcast(getBaseContext(), 1, piIntent, 0);
//            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//            filter.addAction(ACTION_USB_PERMISSION);
//            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
//            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//            registerReceiver(mUsbReceiver, filter);
//            UsbDevice device = null;
//            for (UsbDevice mdevice : manager.getDeviceList().values()) {
//                int pid, vid;
//                pid = mdevice.getProductId();
//                vid = mdevice.getVendorId();
//                boolean devType;
//                if ((pid == 0x8225) && (vid == 0x0bca)) {
//                    FM220SDK = new acpl_FM220_SDK(getApplicationContext(), this, true);
//                    devType = true;
//                } else if ((pid == 0x8220) && (vid == 0x0bca)) {
//                    FM220SDK = new acpl_FM220_SDK(getApplicationContext(), this, false);
//                    devType = false;
//                }
//                if ((pid == 0x8225 || pid == 0x8220) && (vid == 0x0bca)) {
//                    device = mdevice;
//                    if (!manager.hasPermission(device)) {
//                        textMessage.setText("FM220 requesting permission");
//                        manager.requestPermission(device, mPermissionIntent);
//                    } else {
//                        Intent intent = this.getIntent();
//                        if (intent != null) {
//                            if (intent.getAction().equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
//                                finishAffinity();
//                            }
//                        }
//                        fm220_Init_Result res = FM220SDK.InitScannerFM220(manager, device, Telecom_Device_Key);
//                        if (res.getResult()) {
//                            textMessage.setText("FM220 ready. " + res.getSerialNo());
//                            EnableCapture();
//                            isLocalConn = true;
//                            return true;
//                        } else {
//                            textMessage.setText("Error :-" + res.getError());
//                            DisableCapture();
//                            isLocalConn = false;
//                            return false;
//                        }
//                    }
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return false;
//    }


    // region To crete optxml for RD Service capture ..
//    private String createPidOptXML() {
//        String tmpOptXml;
//        try {
//            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//            docFactory.setNamespaceAware(true);
//            DocumentBuilder docBuilder;
//
//            docBuilder = docFactory.newDocumentBuilder();
//            Document doc = docBuilder.newDocument();
//            doc.setXmlStandalone(true);
//
//            Element rootElement = doc.createElement("PidOptions");
//            doc.appendChild(rootElement);
//
//            Element opts = doc.createElement("Opts");
//            rootElement.appendChild(opts);
//
//            Attr attr = doc.createAttribute("fCount");
//            attr.setValue("1");
//            opts.setAttributeNode(attr);
//
//            attr = doc.createAttribute("fType");
//            attr.setValue("0");
//            opts.setAttributeNode(attr);
//
//            attr = doc.createAttribute("iCount");
//            attr.setValue("0");
//            opts.setAttributeNode(attr);
//
//            attr = doc.createAttribute("iType");
//            attr.setValue("");
//            opts.setAttributeNode(attr);
//
//            attr = doc.createAttribute("pCount");
//            attr.setValue("0");
//            opts.setAttributeNode(attr);
//
//            attr = doc.createAttribute("pType");
//            attr.setValue("");
//            opts.setAttributeNode(attr);
//
//            attr = doc.createAttribute("format");
//            attr.setValue("0");
//            opts.setAttributeNode(attr);
//
//            attr = doc.createAttribute("pidVer");
//            attr.setValue("2.0");
//            opts.setAttributeNode(attr);
//
//            attr = doc.createAttribute("timeout");
//            attr.setValue("10000");
//            opts.setAttributeNode(attr);
//
//            attr = doc.createAttribute("otp");
//            attr.setValue("");
//            opts.setAttributeNode(attr);
//
//            attr = doc.createAttribute("env");
//            attr.setValue("PP");
//            opts.setAttributeNode(attr);
//
//            attr = doc.createAttribute("wadh");
//            attr.setValue("");
//            opts.setAttributeNode(attr);
//
//            attr = doc.createAttribute("posh");
//            attr.setValue("UNKNOWN");
//            opts.setAttributeNode(attr);
//
//            Element demo = doc.createElement("Demo");
//            demo.setTextContent("");
//            rootElement.appendChild(demo);
//
//            Element custotp = doc.createElement("CustOpts");
//            rootElement.appendChild(custotp);
//
//            Element param = doc.createElement("Param");
//            custotp.appendChild(param);
//
//            attr = doc.createAttribute("name");
//            attr.setValue("ValidationKey");
//            param.setAttributeNode(attr);
//
//            attr = doc.createAttribute("value");
//            attr.setValue("");
//            param.setAttributeNode(attr);
//
//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//            Transformer transformer = transformerFactory.newTransformer();
//            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
//            DOMSource source = new DOMSource(doc);
//            StringWriter writer = new StringWriter();
//            StreamResult result = new StreamResult(writer);
//            transformer.transform(source, result);
//
//            tmpOptXml = writer.getBuffer().toString().replaceAll("\n|\r", "");
//            tmpOptXml = tmpOptXml.replaceAll("&lt;", "<").replaceAll("&gt;", ">");
//
//            return tmpOptXml;
//        } catch (Exception ex) {
//            showMessageDialogue("EXCEPTION- " + ex.getMessage(), "EXCEPTION");
//            return "";
//        }
//    }
    //endregion

    private boolean FunctionBase64(String temp1, String temp2) {
        try {
            if (FM220SDK.MatchFM220String(temp1, temp2)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void DisableCapture() {
        Capture_BackGround.setEnabled(false);
        Capture_No_Preview.setEnabled(false);
        Capture_PreView.setEnabled(false);
        Capture_Match.setEnabled(false);
        Enroll_finger.setEnabled(false);
        imageView.setImageBitmap(null);
        abort_button.setEnabled(true);

    }

    private void EnableCapture() {
        Capture_BackGround.setEnabled(true);
        Capture_No_Preview.setEnabled(true);
        Capture_PreView.setEnabled(true);
        Capture_Match.setEnabled(true);
        Enroll_finger.setEnabled(true);
        abort_button.setEnabled(false);
    }

    @Override
    public void ScannerProgressFM220(final boolean DisplayImage, final Bitmap ScanImage, final boolean DisplayText, final String statusMessage) {
        FingerLoginActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (DisplayText) {
                    textMessage.setText(statusMessage);
                    textMessage.invalidate();
                }
                if (DisplayImage) {
                    imageView.setImageBitmap(ScanImage);
                    imageView.invalidate();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void ScanCompleteFM220(final fm220_Capture_Result result) {
        FingerLoginActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (FM220SDK.FM220Initialized()) EnableCapture();
                if (result.getResult()) {
                    imageView.setImageBitmap(result.getScanImage());

                    Log.d(TAG, "run: " + result.getFingermatchScore());


                    if (result.isEnroll()) {  // if isEnroll return true then result.getISO_Template() return enrolled finger data .
                        textMessage.setText("Finger Enroll Success");
                    } else {
                        textMessage.setText("");
                    }
                    if (t1 == null) {
                        t1 = result.getISO_Template();
                        Log.d(TAG, "t1Value: " + t1);
                        String stringT2 = Base64.encodeToString(t1, Base64.NO_WRAP);

                        if (status.equalsIgnoreCase("Login")) {
                            loginFinger(stringT2);
                        } else {
                            allLoginFinger(stringT2);
                        }


                    } else {
                        t2 = result.getISO_Template();
                        Log.d(TAG, "t2Value: " + t2);
                        String stringT2 = Base64.encodeToString(t2, Base64.NO_WRAP);

                        if (status.equalsIgnoreCase("Login")) {
                            loginFinger(stringT2);
                        } else {
                            allLoginFinger(stringT2);
                        }

                    }
//                    Bitmap mb = result.getScanImage();
//                    System.out.print((mb));
//                    byte[] isosy = result.getISO_Template();   // ISO TEMPLET of FingerPrint.....
//                    System.out.print(Arrays.toString(isosy));
                } else {
                    imageView.setImageBitmap(null);
                    textMessage.setText(result.getError());
                }
                imageView.invalidate();
                textMessage.invalidate();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void ScanMatchFM220(final fm220_Capture_Result result) {
        FingerLoginActivity.this.runOnUiThread(() -> {
            if (FM220SDK.FM220Initialized()) EnableCapture();
            if (result.getResult()) {
                //saveFinger(BitMapToString(result.getScanImage()));
                //loginFinger(BitMapToString(result.getScanImage()));
                imageView.setImageBitmap(result.getScanImage());
                Log.d(TAG, "run: " + result.getFingermatchScore());
                //  textMessage.setText("Finger matched\n" + "Success NFIQ:" + result.getNFIQ() + "Score:- " + result.getFingermatchScore());
            } else {
                imageView.setImageBitmap(null);
                // textMessage.setText("Finger not matched\n" + result.getError());
            }
            imageView.invalidate();
            textMessage.invalidate();
        });
    }


    private void loginFinger(String stringT2) {

        Call<FacultyList> call = RetrofitService.createService(ApiInterface.class, FingerLoginActivity.this).loginFinger(mobileNo,dep_id,startTime,endTime);
        call.enqueue(new Callback<FacultyList>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<FacultyList> call, @NonNull Response<FacultyList> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    FacultyList statusResponse = response.body();

                    if (statusResponse.getStatus().equalsIgnoreCase("true")) {

                        if (FM220SDK.MatchFM220String(statusResponse.getInfo(), stringT2)) {
                            Log.d(TAG, "Fingermatched: " + "Finger matched");
                            textMessage.setText("Finger matched");
                            textMessage.setTextColor(Color.GREEN);

                            // Faculty Attandance
                            facultyAttendance(statusResponse.getTimetable(), statusResponse.getFacultyId(), statusResponse.getSubjectId(), statusResponse.getSubjectName());

                        } else {
                            textMessage.setText("Finger not matched");
                            textMessage.setTextColor(Color.RED);
                            Log.d(TAG, "Fingernotmatched: " + "Finger not matched");

                            Toast.makeText(FingerLoginActivity.this, "Finger not matched", Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Toast.makeText(FingerLoginActivity.this, "Please Check Your Mobile no / Time Slots ! ", Toast.LENGTH_SHORT).show();

                    }





                  /*  byte [] primary = Base64.decode(stringT2,Base64.URL_SAFE);
                    Log.d(TAG, "primary: "+primary);
                    byte [] secondry = Base64.decode(String.valueOf(statusResponse.getInfo()),Base64.URL_SAFE);
                    Log.d(TAG, "secondry: "+secondry);*/

                  /*  if(primary!=null && secondry!=null)
                    {
                        if (FM220SDK.MatchFM220(primary, secondry)) {

                            Log.d(TAG, "Fingermatched: "+"Finger matched");
                            textMessage.setText("Finger matched");
                        } else {
                            textMessage.setText("Finger not matched");
                            Log.d(TAG, "Fingernotmatched: "+"Finger not matched");

                        }
                    }
                    else
                    {

                    }*/


                } else if (response.errorBody() != null) {
                    Toast.makeText(FingerLoginActivity.this, response.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<FacultyList> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    Toast.makeText(FingerLoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Whenever you want to show toast use setValue.

                }


            }
        });
    }

    private void allLoginFinger(String stringT2) {

        Call<ThumbDataResponse> call = RetrofitService.createService(ApiInterface.class, FingerLoginActivity.this).allLogins();
        call.enqueue(new Callback<ThumbDataResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ThumbDataResponse> call, @NonNull Response<ThumbDataResponse> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ThumbDataResponse statusResponse = response.body();

                    if (statusResponse.getMsg() == 1) {

                        for (ThumbDataResponse.InfoBean infoBean : statusResponse.getInfo()) {

                            if (FM220SDK.MatchFM220String(infoBean.getThumb(), stringT2)) {
                                Log.d(TAG, "Fingermatched: " + "Finger matched");
                                textMessage.setText("Finger matched");
                                textMessage.setTextColor(Color.GREEN);

                                payslip(infoBean.getId());

                                break;

                            } else {
                                textMessage.setText("Finger not matched");
                                textMessage.setTextColor(Color.RED);
                                Log.d(TAG, "Fingernotmatched: " + "Finger not matched");

                                //  Toast.makeText(FingerLoginActivity.this, "Finger not matched", Toast.LENGTH_LONG).show();

                            }
                        }
                    } else {
                        Toast.makeText(FingerLoginActivity.this, "Please Check Your Mobile no / Time Slots ! ", Toast.LENGTH_SHORT).show();

                    }


                } else if (response.errorBody() != null) {
                    Toast.makeText(FingerLoginActivity.this, response.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ThumbDataResponse> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    Toast.makeText(FingerLoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Whenever you want to show toast use setValue.

                }


            }
        });
    }


    private void payslip(String id) {

        Call<ResponseBody> call = RetrofitService.createService(ApiInterface.class, FingerLoginActivity.this).paySlip(id);
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ResponseBody statusResponse = response.body();

                    Toast.makeText(FingerLoginActivity.this, "Your Attendance Added Successfully", Toast.LENGTH_SHORT).show();


                } else if (response.errorBody() != null) {
                    Toast.makeText(FingerLoginActivity.this, response.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    Toast.makeText(FingerLoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Whenever you want to show toast use setValue.

                }


            }
        });
    }


    private void facultyAttendance(String timeTableId, String userId, String subjectId, String subjectName) {
        Call<ResponseBody> call = RetrofitService.createService(ApiInterface.class, FingerLoginActivity.this).facultyAtt(userId);
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;


                    userSessionManager.createLogin(userId, timeTableId, subjectId, subjectName);
                    Intent intent = new Intent(FingerLoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } else if (response.errorBody() != null) {
                    Toast.makeText(FingerLoginActivity.this, response.message(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (t instanceof NoConnectivityException) {
                    // show No Connectivity message to user or do whatever you want.
                    Toast.makeText(FingerLoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    // Whenever you want to show toast use setValue.

                }


            }
        });
    }

    public boolean bitmapEquals(Bitmap bitmap1, Bitmap bitmap2) {
        ByteBuffer buffer1 = ByteBuffer.allocate(bitmap1.getHeight() * bitmap1.getRowBytes());
        bitmap1.copyPixelsToBuffer(buffer1);

        ByteBuffer buffer2 = ByteBuffer.allocate(bitmap2.getHeight() * bitmap2.getRowBytes());
        bitmap2.copyPixelsToBuffer(buffer2);

        return Arrays.equals(buffer1.array(), buffer2.array());
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static File bitmapToFile(Context context, Bitmap bitmap, String fileNameToSave) { // File name like "image.png"
        //create a file to write bitmap data
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory() + File.separator + fileNameToSave);
            file.createNewFile();

//Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return file; // it will return null
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FingerLoginActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}

