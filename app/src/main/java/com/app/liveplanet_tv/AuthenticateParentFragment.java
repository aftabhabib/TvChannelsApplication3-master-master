package com.app.liveplanet_tv;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.liveplanet_tv.R;
import com.example.util.Constant;


public class AuthenticateParentFragment extends Fragment {


    public AuthenticateParentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        return textView;
    }
    /**
     * ******************************** getSharedPrefData ******************************************
     */
    protected String getSharedPrefData(String tag) {
        String res = "";
        try {
            String pfName = getActivity().getResources().getString(R.string.pref_name);
            SharedPreferences prefs = getActivity().getSharedPreferences(pfName, getActivity().MODE_PRIVATE);
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
            String pfName = getActivity().getResources().getString(R.string.pref_name);
            SharedPreferences prefs = getActivity().getSharedPreferences(pfName, getActivity().MODE_PRIVATE);
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
}