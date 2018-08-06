/*
 * Copyright (C) 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.liveplanet_tv.R;

/**
 * A collection of utility methods, all static.
 */
public class Utils {

    public static String packageName="";
    public static boolean isAppInstalled=false;

    public static String packageNameMxPlayer="com.mxtech.videoplayer.ad";
    public static String packageNameXmtvPlayer="";
    public static String packageName321Player="spr.hd.video.player";
    public static String packageNameVideoPlayer="org.videolan.vlc";
    public static String packageNameWebviewCast="com.instantbits.cast.webvideo";
    public static String packageNameLocalCast="de.stefanpledl.localcast";
    private static final String TAG = "Utils";

    /*
     * Making sure public utility methods remain static
     */
    private Utils() {
    }

    @SuppressWarnings("deprecation")
    /**
     * Returns the screen/display size
     *
     */
    public static Point getDisplaySize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        return new Point(width, height);
    }

    /**
     * Returns {@code true} if and only if the screen orientation is portrait.
     */
    public static boolean isOrientationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * Shows an error dialog with a given text message.
     */
    public static void showErrorDialog(Context context, String errorString) {
        new AlertDialog.Builder(context).setTitle(R.string.error)
                .setMessage(errorString)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    /**
     * Shows an "Oops" error dialog with a text provided by a resource ID
     */
    public static void showOopsDialog(Context context, int resourceId) {
        new AlertDialog.Builder(context).setTitle(R.string.oops)
                .setMessage(context.getString(resourceId))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.ic_action_alerts_and_states_warning)
                .create()
                .show();
    }

    /**
     * Gets the version of app.
     */
    public static String getAppVersionName(Context context) {
        String versionString = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0 /* basic info */);
            versionString = info.versionName;
        } catch (Exception e) {
            // do nothing
        }
        return versionString;
    }

    /**
     * Shows a (long) toast.
     */
    public static void showToast(Context context, int resourceId) {
        Toast.makeText(context, context.getString(resourceId), Toast.LENGTH_LONG).show();
    }

    /**
     * Formats time from milliseconds to hh:mm:ss string format.
     */
    public static String formatMillis(int millisec) {
        int seconds = (int) (millisec / 1000);
        int hours = seconds / (60 * 60);
        seconds %= (60 * 60);
        int minutes = seconds / 60;
        seconds %= 60;

        String time;
        if (hours > 0) {
            time = String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            time = String.format("%d:%02d", minutes, seconds);
        }
        return time;
    }
    public static void showDialogToChoosePlayer(final Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_choose_player);

        final TextView textViewOk = (TextView)dialog.findViewById(R.id.ok);
        textViewOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(getSharedPrefData(Constant.keySelectedPlayer,activity))){
                    if(getSharedPrefData(Constant.keySelectedPlayer,activity).equalsIgnoreCase(activity.getString(R.string.mx_player))){
                        isAppInstalled = appInstalledOrNot(packageNameMxPlayer,activity);
                        packageName = packageNameMxPlayer;
                    }
                    else if(getSharedPrefData(Constant.keySelectedPlayer,activity).equalsIgnoreCase(activity.getString(R.string.three_twenty_one_player))){
                        isAppInstalled = appInstalledOrNot(packageName321Player,activity);
                        packageName = packageName321Player;
                    }
                    else if(getSharedPrefData(Constant.keySelectedPlayer,activity).equalsIgnoreCase(activity.getString(R.string.vlc_player))){
                        isAppInstalled = appInstalledOrNot(packageNameVideoPlayer,activity);
                        packageName = packageNameVideoPlayer;
                    }
                    else if(getSharedPrefData(Constant.keySelectedPlayer,activity).equalsIgnoreCase(activity.getString(R.string.local_cast_player))){
                        isAppInstalled = appInstalledOrNot(packageNameWebviewCast,activity);
                        packageName = packageNameWebviewCast;
                    }
                }

                dialog.cancel();
            }
        });

        LinearLayout linearDefaultPlayer = (LinearLayout)dialog.findViewById(R.id.linear_default_player);
        final TextView textViewDefaultPlayer = (TextView)dialog.findViewById(R.id.textview_default_player);

        LinearLayout linearMxPlayer = (LinearLayout)dialog.findViewById(R.id.linear_mx_player);
        final TextView textViewMxPlayer = (TextView)dialog.findViewById(R.id.textview_mx_player);

        LinearLayout linearVlcPlayer = (LinearLayout)dialog.findViewById(R.id.linear_vlc_player);
        final TextView textViewVlcPlayer = (TextView)dialog.findViewById(R.id.textview_vlc_player);

        LinearLayout linear321Player = (LinearLayout)dialog.findViewById(R.id.linear_321_player);
        final TextView textView321Player = (TextView)dialog.findViewById(R.id.textview_321_player);

        LinearLayout linearLocalCastPlayer = (LinearLayout)dialog.findViewById(R.id.linear_localcast_player);
        final TextView textViewLocalCastPlayer = (TextView)dialog.findViewById(R.id.textview_localcast_player);

        if(!TextUtils.isEmpty(getSharedPrefData(Constant.keySelectedPlayer,activity))){
            if(getSharedPrefData(Constant.keySelectedPlayer,activity).equalsIgnoreCase(activity.getString(R.string.mx_player))){
                textViewMxPlayer.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
            }
            else if(getSharedPrefData(Constant.keySelectedPlayer,activity).equalsIgnoreCase(activity.getString(R.string.three_twenty_one_player))){
                textView321Player.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
            }
            else if(getSharedPrefData(Constant.keySelectedPlayer,activity).equalsIgnoreCase(activity.getString(R.string.vlc_player))){
                textViewVlcPlayer.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
            }
            else if(getSharedPrefData(Constant.keySelectedPlayer,activity).equalsIgnoreCase(activity.getString(R.string.local_cast_player))){
                textViewLocalCastPlayer.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
            }
        }else{
            textViewDefaultPlayer.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
        }
        linearDefaultPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textViewDefaultPlayer.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
                textViewMxPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textViewVlcPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textView321Player.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textViewLocalCastPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                sharedPrefData(Constant.keySelectedPlayer,activity.getString(R.string.default_player),activity);
            }
        });
        linearMxPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewDefaultPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textViewMxPlayer.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
                textViewVlcPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textView321Player.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textViewLocalCastPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                sharedPrefData(Constant.keySelectedPlayer,activity.getString(R.string.mx_player),activity);
            }
        });
        linear321Player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewDefaultPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textViewMxPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textViewVlcPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textView321Player.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
                textViewLocalCastPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                sharedPrefData(Constant.keySelectedPlayer,activity.getString(R.string.three_twenty_one_player),activity);
            }
        });
        linearVlcPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewDefaultPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textViewMxPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textViewVlcPlayer.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
                textView321Player.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textViewLocalCastPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                sharedPrefData(Constant.keySelectedPlayer,activity.getString(R.string.vlc_player),activity);
            }
        });
        linearLocalCastPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewDefaultPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textViewMxPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textViewVlcPlayer.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textView321Player.setBackgroundColor(activity.getResources().getColor(R.color.white));
                textViewLocalCastPlayer.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
                sharedPrefData(Constant.keySelectedPlayer,activity.getString(R.string.local_cast_player),activity);

            }
        });
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);
        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    public static boolean appInstalledOrNot(String uri,Activity activity) {
        PackageManager pm = activity.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
    public static String getSharedPrefData(String tag,Activity activity) {
        String res = "";
        try {
            String pfName =activity.getResources().getString(R.string.pref_name);
            SharedPreferences prefs =activity.getSharedPreferences(pfName,activity.MODE_PRIVATE);
            res = prefs.getString(tag, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    public static void sharedPrefData(String tag, String value,Activity activity) {
        try {
            String pfName =activity.getResources().getString(R.string.pref_name);
            SharedPreferences prefs =activity.getSharedPreferences(pfName,activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(tag, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
