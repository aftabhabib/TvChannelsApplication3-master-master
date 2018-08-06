package com.app.liveplanet_tv;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.liveplanet_tv.R;
import com.example.util.Constant;

public class AuthenticateParentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate_parent);
    }
    /**
     * ******************************** getSharedPrefData ******************************************
     */
    protected String getSharedPrefData(String tag) {
        String res = "";
        try {
            String pfName =getResources().getString(R.string.pref_name);
            SharedPreferences prefs =getSharedPreferences(pfName,MODE_PRIVATE);
            res = prefs.getString(tag, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    /**
     * *********************************** SharedPrefData ******************************************
     */
    protected void sharedPrefData(String tag, String value) {
        try {
            String pfName =getResources().getString(R.string.pref_name);
            SharedPreferences prefs =getSharedPreferences(pfName,MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(tag, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
  public Boolean isAdmobActive()
    {
        Boolean status=false;
        if(getSharedPrefData(Constant.keyAdmobPriority).equalsIgnoreCase("1"))
        {
            status=true;
        }else if(getSharedPrefData(Constant.keyAdmobPriority).equalsIgnoreCase("0"))
        {
            status=false;
        }
        return status;
    }
    public Boolean isStartAppAddActive()
    {
        Boolean status=false;
        if(getSharedPrefData(Constant.keyStartAppPriority).equalsIgnoreCase("1"))
        {
            status=true;
        }else if(getSharedPrefData(Constant.keyStartAppPriority).equalsIgnoreCase("0"))
        {
            status=false;
        }
        return status;
    }
    public Boolean isFlurryAddActive()
    {
        Boolean status=false;
        if(getSharedPrefData(Constant.keyFlurryPriority).equalsIgnoreCase("1"))
        {
            status=true;
        }else if(getSharedPrefData(Constant.keyFlurryPriority).equalsIgnoreCase("0"))
        {
            status=false;
        }
        return status;
    }
    public Boolean isAppLovinAddActive()
    {
        Boolean status=false;
        if(getSharedPrefData(Constant.keyAppLovinPriority).equalsIgnoreCase("1"))
        {
            status=true;
        }else if(getSharedPrefData(Constant.keyAppLovinPriority).equalsIgnoreCase("0"))
        {
            status=false;
        }
        return status;
    }
    /***********************************************************************************************
     * getAndSaveDeviceId
     **********************************************************************************************/

    /****************************isTextEmpty***************************************************/
    protected boolean isTextEmpty(String text) {
        String result = "";
        try {
            if (text != null) {
                result = text.trim();
                if (result.isEmpty() || result.equalsIgnoreCase("null")) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
