package com.app.liveplanet_tv;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.app.liveplanet_tv.kinvey.startDataSetModel;
import com.app.liveplanet_tv.R;
import com.crashlytics.android.Crashlytics;
import com.example.util.TypefaceUtil;
import com.flurry.android.FlurryAgent;
import com.onesignal.OneSignal;

import io.fabric.sdk.android.Fabric;

import static android.util.Log.VERBOSE;


public class MyApplication extends MultiDexApplication {

    private static MyApplication mInstance;
    public SharedPreferences preferences;
    public String prefName = "androidlivetv";


    startDataSetModel customStartDataSetModel;
    public MyApplication() {
        mInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        try{
            MultiDex.install(this);
        }catch (Exception e){

        }
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();
        mInstance = this;
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/custom.ttf");
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .withCaptureUncaughtExceptions(true)
                .withContinueSessionMillis(10)
                .withLogLevel(VERBOSE)
                .build(this,getResources().getString(R.string.flurry_api_key));
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void saveIsLogin(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putBoolean("IsLoggedIn", flag);
        editor.apply();
    }

    public boolean getIsLogin() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsLoggedIn", false);
    }


    public void saveIsRemember(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putBoolean("IsLoggedRemember", flag);
        editor.apply();
    }

    public boolean getIsRemember() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsLoggedRemember", false);
    }


    public void saveRemember(String email, String password) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putString("remember_email", email);
        editor.putString("remember_password", password);
        editor.apply();
    }

    public String getRememberEmail() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("remember_email", "");
    }

    public String getRememberPassword() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("remember_password", "");
    }

    public void saveLogin(String user_id, String user_name, String email) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putString("user_id", user_id);
        editor.putString("user_name", user_name);
        editor.putString("email", email);
        editor.apply();
    }

    public String getUserId() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("user_id", "");
    }

    public String getUserName() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("user_name", "");
    }

    public String getUserEmail() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("email", "");
    }
    /**
     * *********************************** SharedPrefData ******************************************
     */
    protected void sharedPrefData(String tag, String value) {
        try {
            String pfName =getResources().getString(R.string.pref_name);
            SharedPreferences prefs =getSharedPreferences(pfName,MODE_PRIVATE);
            Editor editor = prefs.edit();
            editor.putString(tag, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}