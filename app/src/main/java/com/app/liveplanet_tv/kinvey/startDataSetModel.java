package com.app.liveplanet_tv.kinvey;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/**
 * Created by Nadeem Yousaf on 3/26/2018.
 */

public class startDataSetModel extends GenericJson {
    @Key("_id")
    private String id;
    @Key("admobPriority")
   private int admob;
    @Key("startAppPriotity")
   private int startApp;
    @Key("currentBaseUrl")
   private String currentBase;
    @Key("flurryAdsPriority")
   private int flurry;
    @Key("appLovinPriority")
   private int appLovin;

    public int getAdDelayTime() {
        return adDelayTime;
    }

    public void setAdDelayTime(int adDelayTime) {
        this.adDelayTime = adDelayTime;
    }

    @Key("IntertitialAdDelayTime")

    private int adDelayTime;

    public int getFlurry() {
        return flurry;
    }

    public void setFlurry(int flurry) {
        this.flurry = flurry;
    }

    public int getAppLovin() {
        return appLovin;
    }

    public void setAppLovin(int appLovin) {
        this.appLovin = appLovin;
    }

    public startDataSetModel(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAdmob() {
        return admob;
    }

    public void setAdmob(int admob) {
        this.admob = admob;
    }

    public int getStartApp() {
        return startApp;
    }

    public void setStartApp(int startApp) {
        this.startApp = startApp;
    }

    public String getCurrentBase() {
        return currentBase;
    }

    public void setCurrentBase(String currentBase) {
        this.currentBase = currentBase;
    }
    //GenericJson classes must have a public empty constructor

}
