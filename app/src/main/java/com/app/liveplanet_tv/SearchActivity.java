package com.app.liveplanet_tv;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.liveplanet_tv.R;
import com.example.adapter.ChannelAdapter;
import com.example.item.ItemChannel;
import com.example.util.Constant;
import com.example.util.ItemOffsetDecoration;
import com.example.util.JsonUtils;
import com.example.util.PopUpAds;
import com.example.util.RecyclerItemClickListener;
import com.example.util.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AuthenticateParentActivity {
    String packageName="";
    boolean isAppInstalled=false;
    String videoUrl="";
    String videoUrlToken="";
    public static ArrayList<ItemChannel> mListItemRelated;
    int channelId;
    ArrayList<ItemChannel> mListItem;
    public RecyclerView recyclerView;
    ChannelAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    String Search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.menu_search));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mListItemRelated = new ArrayList<>();
        Intent intent = getIntent();
        Search = intent.getStringExtra("search");
        mListItem = new ArrayList<>();
        lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.vertical_courses_list);
        int columns = getResources().getInteger(R.integer.number_of_column);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(SearchActivity.this, columns));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(SearchActivity.this, R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        if (JsonUtils.isNetworkAvailable(SearchActivity.this)) {
            new getSearch().execute(Constant.SEARCH_URL + Search);
        }
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        final ItemChannel singleItem = mListItem.get(position);
                        channelId=singleItem.getId();

                        if (JsonUtils.isNetworkAvailable(SearchActivity.this)) {
                            String tokenApiUrl=getSharedPrefData(Constant.keyMediaPlayerTokenUrl);
                            if(!tokenApiUrl.isEmpty())
                            {
                                new GetChannelToken().execute(tokenApiUrl+"?"+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken)+Constant.DEVICE_ID_PARAMETER+getSharedPrefData(Constant.keyDeviceId));
                            }else
                            {
                                Toast.makeText(SearchActivity.this, "MediaPlayer Url token not found", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else {
                            //orignal code.
                            //showToast(getString(R.string.ctg_itm));
                            //my added code.
                            // showToast(getString(R.string.conne_msg1));
                            // Toast.makeText(context, "category line 123 ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


        AdView mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());
    }

    private class getSearch extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showProgress(false);
            if (null == result || result.length() == 0) {
                lyt_not_found.setVisibility(View.VISIBLE);
            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        ItemChannel objItem = new ItemChannel();
                        objItem.setId(objJson.getInt(Constant.CHANNEL_ID));
                        objItem.setChannelName(objJson.getString(Constant.CHANNEL_TITLE));
                        objItem.setImage(objJson.getString(Constant.CHANNEL_IMAGE));
                        mListItem.add(objItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }
        }
    }


    private void displayData() {
        adapter = new ChannelAdapter(SearchActivity.this, mListItem);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);
        }
    }


    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
    private class MyTaskChannel extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            ItemChannel objectChannel;
            if (null == result || result.length() == 0) {
                showToast(getString(R.string.nodata));
            } else {

                try {
                    JSONObject resultJson = new JSONObject(result);
                    JSONObject mainJsonObject=resultJson.getJSONObject("LIVETV");
                    JSONArray jsonArray = mainJsonObject.getJSONArray("channel");
                    JSONObject objJson=new JSONObject();
                    objJson = jsonArray.getJSONObject(0);
                    objectChannel = new ItemChannel();
                    objectChannel.setId(objJson.getInt(Constant.CHANNEL_ID));
                    objectChannel.setChannelName(objJson.getString(Constant.CHANNEL_TITLE));
                    objectChannel.setImage(objJson.getString(Constant.CHANNEL_IMAGE));
                    objectChannel.setChannelUrl(objJson.getString(Constant.CHANNEL_URL));
                    objectChannel.setDescription(objJson.getString(Constant.CHANNEL_DESC));
                    objectChannel.setIsTv(objJson.getString(Constant.CHANNEL_TYPE).equals("live_url"));


                    JSONArray jsonArrayChild = objJson.getJSONArray(Constant.RELATED_ITEM_ARRAY_NAME);
                    if (jsonArrayChild.length() != 0) {
                        for (int j = 0; j < jsonArrayChild.length(); j++) {
                            JSONObject objChild = jsonArrayChild.getJSONObject(j);
                            ItemChannel item = new ItemChannel();
                            item.setId(objChild.getInt(Constant.RELATED_ITEM_CHANNEL_ID));
                            item.setChannelName(objChild.getString(Constant.RELATED_ITEM_CHANNEL_NAME));
                            item.setImage(objChild.getString(Constant.RELATED_ITEM_CHANNEL_THUMB));
                            mListItemRelated.add(item);
                        }
                    }
                    if(mainJsonObject.has("token"))
                    {
                        JSONObject tokenJsonObject=new JSONObject();
                        tokenJsonObject=mainJsonObject.getJSONObject("token");
                        String tokenMessage=tokenJsonObject.optString("wmsAuthSignText");
                        String token=tokenJsonObject.optString("wmsAuthSign");
                        if(token.equalsIgnoreCase("0")||token.isEmpty())
                        {
                            //Toast.makeText(CategoryItemActivity.this,tokenMessage, Toast.LENGTH_SHORT).show();
                        }else
                        {
                            sharedPrefData(Constant.keyUserToken,token);
                        }
                    }
                    moveToNext(objectChannel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private class GetChannelToken extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            if (null == result || result.length() == 0) {
                showToast(getString(R.string.nodata));
            } else {

                try {
                    JSONObject resultJson = new JSONObject(result);
                    if(resultJson.has("authToken"))
                    {
                        JSONObject tokenJsonObject=new JSONObject();
                        tokenJsonObject=resultJson.getJSONObject("authToken");
                        String tokenMessage=tokenJsonObject.optString("wmsAuthSignText");
                        String token=tokenJsonObject.optString("wmsAuthSign");
                        if(token.equalsIgnoreCase("0")||token.isEmpty())
                        {
                            Toast.makeText(SearchActivity.this,tokenMessage, Toast.LENGTH_SHORT).show();
                        }else
                        {
                            sharedPrefData(Constant.keyUserToken,token);
                        }
                    }
                    if(resultJson.has("videoToken"))
                    {
                        videoUrlToken=resultJson.optString("videoToken");
                    }
                    makeSingleChannelApiCall();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void makeSingleChannelApiCall()
    {
        if (JsonUtils.isNetworkAvailable(SearchActivity.this)) {
            new MyTaskChannel().execute(Constant.SINGLE_CHANNEL_URL +channelId+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken)+Constant.DEVICE_ID_PARAMETER+getSharedPrefData(Constant.keyDeviceId));
        }else
        {
            Toast.makeText(SearchActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
    private void  moveToNext(ItemChannel obj)
    {
        if(isAdmobActive())
        {
            PopUpAds.ShowInterstitialAds(SearchActivity.this,getSharedPrefData(Constant.keyIntertitialAdDelayTime));
        }else if(isFlurryAddActive())
        {
            //showFlurryIntertitial();
        }
        if (obj.isTv()) {
            videoUrl = obj.getChannelUrl()+videoUrlToken;
            if(!TextUtils.isEmpty(getSharedPrefData(Constant.keySelectedPlayer))){
                    if(getSharedPrefData(Constant.keySelectedPlayer).equalsIgnoreCase(getString(R.string.mx_player))){
                        isAppInstalled = appInstalledOrNot(Utils.packageNameMxPlayer);
                        packageName = Utils.packageNameMxPlayer;
                    }
                    else if(getSharedPrefData(Constant.keySelectedPlayer).equalsIgnoreCase(getString(R.string.three_twenty_one_player))){
                        isAppInstalled = appInstalledOrNot(Utils.packageName321Player);
                        packageName = Utils.packageName321Player;
                    }
                    else if(getSharedPrefData(Constant.keySelectedPlayer).equalsIgnoreCase(getString(R.string.vlc_player))){
                        isAppInstalled = appInstalledOrNot(Utils.packageNameVideoPlayer);
                        packageName = Utils.packageNameVideoPlayer;
                    }
                    else if(getSharedPrefData(Constant.keySelectedPlayer).equalsIgnoreCase(getString(R.string.local_cast_player))){
                        isAppInstalled = appInstalledOrNot(Utils.packageNameLocalCast);
                        packageName = Utils.packageNameLocalCast;
                    }
                    else if(getSharedPrefData(Constant.keySelectedPlayer).equalsIgnoreCase(getString(R.string.webview_player))){
                        isAppInstalled = appInstalledOrNot(Utils.packageNameWebviewCast);
                        packageName = Utils.packageNameWebviewCast;
                    }
                    else if(getSharedPrefData(Constant.keySelectedPlayer).equalsIgnoreCase(getString(R.string.default_player))){
                        Intent intent = new Intent(SearchActivity.this, TVPlayActivity.class);
                        intent.putExtra("url",videoUrl);
                        //intent.putExtra("url", obj.getChannelUrl()+"?"+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken)+Constant.DEVICE_ID_PARAMETER+getSharedPrefData(Constant.keyDeviceId));
                        startActivity(intent);
                    }
                    if(isAppInstalled) {
                        try{
                            //This intent will help you to launch if the package is already installed
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setPackage(packageName);
                            Uri videoUri = Uri.parse(videoUrl);
                            intent.setDataAndType( videoUri, "application/x-mpegURL" );
                            intent.setPackage(packageName);
                            startActivity(intent);
                        }catch (Exception e){

                        }

                        //Log.i("Application is already installed.");
                    } else {
                        // Do whatever we want to do if application not installed
                        // For example, Redirect to play store
                        //Log.i("Application is not currently installed.");
                        //final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                        }
                    }
                }
                else{
                    Intent intent = new Intent(SearchActivity.this, TVPlayActivity.class);
                    intent.putExtra("url",videoUrl);
                    //intent.putExtra("url", obj.getChannelUrl()+"?"+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken)+Constant.DEVICE_ID_PARAMETER+getSharedPrefData(Constant.keyDeviceId));
                    startActivity(intent);
                }
        } else {
            String videoId = JsonUtils.getVideoId(obj.getChannelUrl());
            Intent intent = new Intent(SearchActivity.this, YtPlayActivity.class);
            intent.putExtra("id", videoId);
            startActivity(intent);
        }
    }
    public void showToast(String msg) {
        Toast.makeText(SearchActivity.this, msg, Toast.LENGTH_LONG).show();
    }
    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}
