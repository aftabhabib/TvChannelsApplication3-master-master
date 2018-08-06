package com.app.liveplanet_tv;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.app.liveplanet_tv.kinvey.startDataSetModel;
import com.app.liveplanet_tv.R;
import com.example.util.Constant;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyPingCallback;
import com.kinvey.android.model.User;
import com.kinvey.android.store.DataStore;
import com.kinvey.android.store.UserStore;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.store.StoreType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lolodev.permissionswrapper.callback.OnRequestPermissionsCallBack;
import lolodev.permissionswrapper.wrapper.PermissionWrapper;

import static com.example.util.Constant.keyDeviceId;


public class SplashActivity extends AuthenticateParentActivity {

    MyApplication App;
    private boolean mIsBackButtonPressed;
    private static final int SPLASH_DURATION = 1000;
    private Client mKinveyClient;
    DataStore<startDataSetModel> dataStore;
    List<startDataSetModel> dataList;
    startDataSetModel datamodel;
    final private int REQUEST_READ_PHONE_STATE_PERMISSIONS = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        dataList=new ArrayList<>();
        mKinveyClient = new Client.Builder(getResources().getString(R.string.appKey),getResources().getString(R.string.appSecrate), this.getApplicationContext()).build();
        mKinveyClient.ping(new KinveyPingCallback() {
            public void onFailure(Throwable t) {
                Log.e("Kinvey", "Kinvey Ping Failed", t);
                reqReadPhoneStatePermission();

            }
            public void onSuccess(Boolean b) {
                Log.d("Kinvey", "Kinvey Ping Success");
                if(mKinveyClient.isUserLoggedIn())
                {
                    Log.d("Kinvey", "already login");
                    getKinveyData();
                }else
                {
                    try {
                        UserStore.login(mKinveyClient, new KinveyClientCallback<User>() {
                            @Override
                            public void onFailure(Throwable t) {
                                Log.d("Kinvey", "login failed");
                                reqReadPhoneStatePermission();
                            }
                            @Override
                            public void onSuccess(User u) {
                                Log.d("Kinvey", "login successfully");
                                getKinveyData();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private  void getKinveyData()
    {
        dataStore = DataStore.collection("StartDataSet",startDataSetModel.class, StoreType.NETWORK, mKinveyClient);
        dataStore.find(new KinveyListCallback<startDataSetModel>(){
            @Override
            public void onSuccess(List<startDataSetModel> result) {
                // Place your code here
                Log.d("Kinvey", "data get successfully=="+result.size());
                dataList=result;
                if(dataList.size()>0)
                {
                    datamodel=new startDataSetModel();
                    datamodel=dataList.get(0);
                    sharedPrefData(Constant.keyAdmobPriority,datamodel.getAdmob()+"");
                    sharedPrefData(Constant.keyStartAppPriority,datamodel.getStartApp()+"");
                    sharedPrefData(Constant.keyBaseUrl,datamodel.getCurrentBase()+"");
                    sharedPrefData(Constant.keyFlurryPriority,datamodel.getFlurry()+"");
                    sharedPrefData(Constant.keyAppLovinPriority,datamodel.getAppLovin()+"");
                    sharedPrefData(Constant.keyIntertitialAdDelayTime,datamodel.getAdDelayTime()+"");
                    Constant.SERVER_URL=getSharedPrefData(Constant.keyBaseUrl);
                    Constant.refreshConstantsData();
                    reqReadPhoneStatePermission();
                }else
                {
                    reqReadPhoneStatePermission();
                }
            }
            @Override
            public void onFailure(Throwable error) {
                // Place your code here
                Log.d("Kinvey", "error on fetching data");
                reqReadPhoneStatePermission();
            }
        });



    }
private void moveToNextScreen()
{
    if(!isTextEmpty(getSharedPrefData(Constant.keyDeviceId)))
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                //  if (!mIsBackButtonPressed) {
                //  if (App.getIsLogin()) {
                try
                {
                    if(!isTextEmpty(getSharedPrefData(Constant.keyDeviceId)))
                    {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else
                    {
                        Toast.makeText(App, "Permission is required please allow to continue", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception c)
                {
                    c.printStackTrace();
                }

                //} else {
                       /* Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }*/
                //   }
            }

        }, SPLASH_DURATION);


    }else
    {
        Toast.makeText(this, "Please allow permission to continue", Toast.LENGTH_SHORT).show();

    }

}
    @Override
    public void onBackPressed() {
        // set the flag to true so the next activity won't start up
        mIsBackButtonPressed = true;
        super.onBackPressed();

    }
    private void reqReadPhoneStatePermission() {
        new PermissionWrapper.Builder(this)
                .addPermissions(new String[]{Manifest.permission.READ_PHONE_STATE})
                .addPermissionsGoSettings(true)
                .addRequestPermissionsCallBack(new OnRequestPermissionsCallBack() {
                    @Override
                    public void onGrant() {
                     getAndSaveDeviceId();
                    }
                    @Override
                    public void onDenied(String permission) {
                    }
                }).build().request();

    }
    public void getAndSaveDeviceId() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            String deviceId01 = telephonyManager.getDeviceId();
            if (isTextEmpty(deviceId01)) {
                deviceId01 = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            sharedPrefData(keyDeviceId, deviceId01);
            moveToNextScreen();
        } catch (Exception e) {
            e.printStackTrace();
            moveToNextScreen();
        }
    }

}
