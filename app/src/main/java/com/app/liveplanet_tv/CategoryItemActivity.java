package com.app.liveplanet_tv;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.liveplanet_tv.R;
import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinSdk;
import com.example.adapter.ChannelAdapter;
import com.example.db.DatabaseHelper;
import com.example.item.ItemChannel;
import com.example.item.PlayerListModel;
import com.example.util.Constant;
import com.example.util.ItemClickSupport;
import com.example.util.ItemOffsetDecoration;
import com.example.util.JsonUtils;
import com.example.util.MediaItem;
import com.example.util.PopUpAds;
import com.example.util.Utils;
import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdBanner;
import com.flurry.android.ads.FlurryAdBannerListener;
import com.flurry.android.ads.FlurryAdErrorType;
import com.flurry.android.ads.FlurryAdInterstitial;
import com.flurry.android.ads.FlurryAdInterstitialListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.vov.vitamio.utils.Log;

/**
 * Created by laxmi.
 */
public class CategoryItemActivity extends AuthenticateParentActivity {

    DatabaseHelper databaseHelper;
    private MenuItem mediaRouteMenuItem;
    private final Handler mHandler = new Handler();
    private CastContext mCastContext;
    private CastSession mCastSession;
    private SessionManagerListener<CastSession> mSessionManagerListener;

    String packageName="";
    boolean isAppInstalled=false;
    String videoUrl="";
    String videoUrlToken="";
    int channelId;
    private ArrayList<PlayerListModel> arrayListPlayerList = new ArrayList<>();
    private RecyclerView.Adapter adapterPlayerlist;
    Context context;
    ArrayList<ItemChannel> mListItem;
    public RecyclerView recyclerView;
    ChannelAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    String Id,Name;
    public static ArrayList<ItemChannel> mListItemRelated;
    ItemChannel objBean;

    private RelativeLayout mBanner;
    private FlurryAdBanner mFlurryAdBanner = null;
    private String mAdSpaceName = "BANNER_ADSPACE";
    private FlurryAdInterstitial mFlurryAdInterstitial = null;
    private String mAdSpaceNameIntertial = "intertitialAd";
    //private AppLovinIncentivizedInterstitial myIncent;
    //AppLovinAdDisplayListener objectImplementingAdLoadListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*AppLovinSdk.initializeSdk(this);
        myIncent = AppLovinIncentivizedInterstitial.create(this);
        myIncent.preload(null);
        myIncent.preload(new AppLovinAdLoadListener() {
            @Override
            public void adReceived(AppLovinAd appLovinAd) {
                if(myIncent.isAdReadyToDisplay()){
                    // An ad is ready.  Display the ad with one of the available show methods.
                    if(!TextUtils.isEmpty(getSharedPrefData(Constant.keyIntertitialAdDelayTime))){
                        Constant.AD_COUNT_SHOW = Integer.parseInt(getSharedPrefData(Constant.keyIntertitialAdDelayTime));
                    }
                    if(Constant.isFirstTime2){
                        myIncent.show(CategoryItemActivity.this);
                        Constant.isFirstTime2 = false;

                    }else {
                        Constant.AD_COUNT += 1;
                        if (Constant.AD_COUNT == Constant.AD_COUNT_SHOW) {
                            Constant.AD_COUNT = 0;
                            myIncent.show(CategoryItemActivity.this);
                        }
                    }
                }
                else{
                    // No rewarded ad is currently available.
                    myIncent.preload(null);
                }
            }

            @Override
            public void failedToReceiveAd(int i) {
                Log.e(" @@@ "," failed video : "+i);
            }
        });*/

// Preload call using a new load listener
        /*if(myIncent.isAdReadyToDisplay()){
            myIncent.show(this);
        }
        else{
            // Perform fallback logic
        }*/
        try
        {
            setContentView(R.layout.activity_category_item);
            databaseHelper = new DatabaseHelper(this);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            fillArraylist();

            setupCastListener();
            if(isGooglePlayServicesAvailable()) {
                try {
                    mCastContext = CastContext.getSharedInstance(this);
                    //mCastContext.registerLifecycleCallbacksBeforeIceCreamSandwich(this, savedInstanceState);
                    mCastSession = mCastContext.getSessionManager().getCurrentCastSession();
                }catch (Exception e){
                }

            }
            //////////////////////////////flurry////////////////////////////////
            mBanner = (RelativeLayout)findViewById(R.id.rl_flurry_banner);
            mFlurryAdBanner = new FlurryAdBanner(this, mBanner, mAdSpaceName);
            mFlurryAdBanner.setListener(bannerAdListener);

            mFlurryAdInterstitial = new FlurryAdInterstitial(this, mAdSpaceNameIntertial);

            // allow us to get callbacks for ad events
            mFlurryAdInterstitial.setListener(interstitialAdListener);
            /////////////////////////////////////////////////////////////////////
            Intent intent = getIntent();
            Id = intent.getStringExtra("Id");
            Name = intent.getStringExtra("name");
            setTitle(Name);
            mListItem = new ArrayList<>();
            mListItemRelated = new ArrayList<>();
            lyt_not_found = (LinearLayout) findViewById(R.id.lyt_not_found);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            recyclerView = (RecyclerView) findViewById(R.id.vertical_courses_list);
            int columns = getResources().getInteger(R.integer.number_of_column);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(CategoryItemActivity.this, columns));
            ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(CategoryItemActivity.this, R.dimen.item_offset);
            recyclerView.addItemDecoration(itemDecoration);
            if (JsonUtils.isNetworkAvailable(CategoryItemActivity.this)) {
                new getCategory().execute(Constant.CATEGORY_ITEM_URL +Id+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken)+Constant.DEVICE_ID_PARAMETER+getSharedPrefData(Constant.keyDeviceId));
            }
            AdView mAdView = (AdView) findViewById(R.id.adView);
            if(isAdmobActive())
            {
                mAdView.setVisibility(View.VISIBLE);
                mBanner.setVisibility(View.GONE);
                mAdView.loadAd(new AdRequest.Builder().build());
            }else if(isFlurryAddActive())
            {
                mAdView.setVisibility(View.GONE);
                mBanner.setVisibility(View.VISIBLE);
                showFlurryBannerAdd();
            }else if(isAppLovinAddActive())
            {
                /*  showFlurryBannerAdd(); */
            }
            ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {

                    final ItemChannel singleItem = mListItem.get(position);
                        showDialogFavourite(singleItem.getChannelName(),singleItem);
                    return true;
                }
            });
            ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override

                public void onItemClicked(RecyclerView recyclerView, int position, View v)
                {

                    final ItemChannel singleItem = mListItem.get(position);
                    channelId=singleItem.getId();

                    if (JsonUtils.isNetworkAvailable(CategoryItemActivity.this)) {
                        String tokenApiUrl=getSharedPrefData(Constant.keyMediaPlayerTokenUrl);
                        if(!tokenApiUrl.isEmpty())
                        {
                            new GetChannelToken().execute(tokenApiUrl+"?"+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken)+Constant.DEVICE_ID_PARAMETER+getSharedPrefData(Constant.keyDeviceId));
                        }else
                        {
                            Toast.makeText(context, "MediaPlayer Url token not found", Toast.LENGTH_SHORT).show();
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
            });


        }catch (Exception c)
        {
            c.printStackTrace();
        }
    }
    private class getCategory extends AsyncTask<String, Void, String> {

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
                    JSONObject resultJson = new JSONObject(result);
                   JSONObject mainJsonObject=resultJson.getJSONObject("LIVETV");
                    JSONArray jsonArray = mainJsonObject.getJSONArray("category_detail");
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        ItemChannel objItem = new ItemChannel();
                        objItem.setId(objJson.getInt(Constant.CHANNEL_ID));
                        objItem.setChannelName(objJson.getString(Constant.CHANNEL_TITLE));
                        objItem.setImage(objJson.getString(Constant.CHANNEL_IMAGE));
                        mListItem.add(objItem);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }
        }
    }

    private void displayData() {
        adapter = new ChannelAdapter(CategoryItemActivity.this, mListItem);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_category_item, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.item_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                /*if (!hasFocus) {
                    MenuItemCompat.collapseActionView(searchMenuItem);
                    searchView.setQuery("", false);
                }*/
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub
                //searchView.clearFocus();
                //if(!TextUtils.isEmpty(query)) {
                    adapter.filter(query);
                //}
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                //if(!TextUtils.isEmpty(newText)) {
                    adapter.filter(newText);
                //}
                return true;
            }
        });

        final MenuItem playMenuItem = menu.findItem(R.id.item_play);

        playMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Utils.showDialogToChoosePlayer(CategoryItemActivity.this);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
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
                            Toast.makeText(CategoryItemActivity.this,tokenMessage, Toast.LENGTH_SHORT).show();
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
    if (JsonUtils.isNetworkAvailable(CategoryItemActivity.this)) {
         new MyTaskChannel().execute(Constant.SINGLE_CHANNEL_URL +channelId+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken)+Constant.DEVICE_ID_PARAMETER+getSharedPrefData(Constant.keyDeviceId));
    }else
    {
        Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
    }
}
private void  moveToNext(ItemChannel obj)
{
    if (obj.isTv()) {

       // File file = new File("fileUri");
      //  URL videoUrl= new URL();
        //Intent intent = new Intent(Intent.ACTION_VIEW);
        //intent.setDataAndType(Uri.fromFile(file), "video/*");
        //intent.putExtra("url", obj.getChannelUrl()+"?"+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken));
        //intent.setSelector(intent.setAction("video/*"));
        //startActivity(intent);


//        Intent intent = new Intent(CategoryItemActivity.this, TVPlayActivity.class);
  //      intent.putExtra("url", obj.getChannelUrl()+"?"+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken));
    //    startActivity(intent);

        if (databaseHelper.getRecentChannelById(String.valueOf(obj.getId()))) {
        }else {
            ContentValues contentValue = new ContentValues();
            contentValue.put(DatabaseHelper.KEY_RECENT_ID, obj.getId());
            contentValue.put(DatabaseHelper.KEY_RECENT_URL, obj.getChannelUrl());
            contentValue.put(DatabaseHelper.KEY_RECENT_IMAGE, obj.getImage());
            contentValue.put(DatabaseHelper.KEY_RECENT_NAME, obj.getChannelName());
            contentValue.put(DatabaseHelper.KEY_RECENT_TIMESTAMP, System.currentTimeMillis());
            contentValue.put(DatabaseHelper.KEY_RECENT_DESC, obj.getDescription());
            databaseHelper.addRecentChannel(DatabaseHelper.TABLE_RECENT_CHANNEL, contentValue, null);
        }
        videoUrl = obj.getChannelUrl()+videoUrlToken;
        if (mCastSession != null && mCastSession.isConnected()) {
            MediaItem item = new MediaItem();
            item.setUrl(videoUrl);
            item.setTitle("");

            Intent intent = new Intent(CategoryItemActivity.this, LocalPlayerActivity.class);
            intent.putExtra("media", item.toBundle());
            intent.putExtra("shouldStart", false);
            startActivity(intent);
            //ActivityCompat.startActivity(CategoryItemActivity.this, intent, options.toBundle());

        }else {
            if(!TextUtils.isEmpty(getSharedPrefData(Constant.keySelectedPlayer))){
                if(isAdmobActive())
                {
                    PopUpAds.ShowInterstitialAds(CategoryItemActivity.this,getSharedPrefData(Constant.keyIntertitialAdDelayTime));
                }else if(isFlurryAddActive())
                {
                    showFlurryIntertitial();
                }

                if(getSharedPrefData(Constant.keySelectedPlayer).equalsIgnoreCase(getString(R.string.mx_player))){
                    isAppInstalled =Utils.appInstalledOrNot(Utils.packageNameMxPlayer,this);
                    packageName = Utils.packageNameMxPlayer;
                }
                else if(getSharedPrefData(Constant.keySelectedPlayer).equalsIgnoreCase(getString(R.string.three_twenty_one_player))){
                    isAppInstalled = Utils.appInstalledOrNot(Utils.packageName321Player,this);
                    packageName = Utils.packageName321Player;
                }
                else if(getSharedPrefData(Constant.keySelectedPlayer).equalsIgnoreCase(getString(R.string.vlc_player))){
                    isAppInstalled = Utils.appInstalledOrNot(Utils.packageNameVideoPlayer,this);
                    packageName = Utils.packageNameVideoPlayer;
                }
                else if(getSharedPrefData(Constant.keySelectedPlayer).equalsIgnoreCase(getString(R.string.local_cast_player))){
                    isAppInstalled = Utils.appInstalledOrNot(Utils.packageNameLocalCast,this);
                    packageName = Utils.packageNameLocalCast;
                }
                else if(getSharedPrefData(Constant.keySelectedPlayer).equalsIgnoreCase(getString(R.string.webview_player))){
                    isAppInstalled = Utils.appInstalledOrNot(Utils.packageNameWebviewCast,this);
                    packageName = Utils.packageNameWebviewCast;
                }
                else if(getSharedPrefData(Constant.keySelectedPlayer).equalsIgnoreCase(getString(R.string.default_player))){
                    Intent intent = new Intent(CategoryItemActivity.this, TVPlayActivity.class);
                    intent.putExtra("url",videoUrl);
                    //intent.putExtra("url", obj.getChannelUrl()+"?"+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken)+Constant.DEVICE_ID_PARAMETER+getSharedPrefData(Constant.keyDeviceId));
                    startActivity(intent);
                }
                if(isAppInstalled) {
                    //This intent will help you to launch if the package is already installed
                    try{
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
                Intent intent = new Intent(CategoryItemActivity.this, TVPlayActivity.class);
                intent.putExtra("url",videoUrl);
                //intent.putExtra("url", obj.getChannelUrl()+"?"+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken)+Constant.DEVICE_ID_PARAMETER+getSharedPrefData(Constant.keyDeviceId));
                startActivity(intent);
            }
        }
        /*Intent i = new Intent(Intent.ACTION_VIEW);
        i.setClassName("com.mxtech.videoplayer.ad ","com.mxtech.videoplayer.ActivityScreen");
                i.setDataAndType(Uri.parse(videoUrl),"video/mp4");
                i.setPackage("com.mxtech.videoplayer.ad");
         startActivity(i);*/
        // mxplayer
        /*Intent intent = new Intent(Intent.ACTION_VIEW);
        intent .setPackage("com.mxtech.videoplayer.ad");
        Uri videoUri = Uri.parse(videoUrl);
        intent.setDataAndType( videoUri, "application/x-mpegURL" );
        intent.setPackage( "com.mxtech.videoplayer.ad" );
        startActivity( intent );*/
        // vlc player
        /*Intent intent = new Intent(Intent.ACTION_VIEW);
        intent .setPackage("org.videolan.vlc");
        Uri videoUri = Uri.parse(videoUrl);
        intent.setDataAndType( videoUri, "application/x-mpegURL" );
        intent.setPackage( "org.videolan.vlc" );
        startActivity( intent );*/

        //showDialogToChoosePlayer(videoUrl);

        //openApp(this,"com.mxtech.videoplayer.ad",videoUrl);
        //String videoUrl = obj.getChannelUrl()+"?"+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken);
        //Intent playVideo = new Intent(Intent.ACTION_VIEW);
        //playVideo.setDataAndType(Uri.parse(videoUrl), "video/mp4");
        //Intent chooser= Intent.createChooser(playVideo, "Choose Player");
        //Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.mxtech.videoplayer.ad");
        //LaunchIntent.setDataAndType(Uri.parse(videoUrl), "video/mp4");
       //if (playVideo.resolveActivity(getPackageManager()) != null){
           //startActivity(LaunchIntent);
       //}
        // Use package name which we want to check
        /*boolean isAppInstalled = appInstalledOrNot("org.videolan.vlc");

        if(isAppInstalled) {
            //This intent will help you to launch if the package is already installed
            Intent LaunchIntent = getPackageManager()
                    .getLaunchIntentForPackage("org.videolan.vlc");
            LaunchIntent.setData(Uri.parse(videoUrl));
            LaunchIntent.setV
            //LaunchIntent.setDataAndType(Uri.parse(videoUrl), "video/mp4");
            startActivity(LaunchIntent);

            //Log.i("Application is already installed.");
        } else {
            String appPackageName = "org.videolan.vlc";
            // Do whatever we want to do if application not installed
            // For example, Redirect to play store

            //Log.i("Application is not currently installed.");
            //final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
*/

    } else {
        String videoId = JsonUtils.getVideoId(obj.getChannelUrl());
        Intent intent = new Intent(CategoryItemActivity.this, YtPlayActivity.class);
        intent.putExtra("id", videoId);
        startActivity(intent);
    }
    }
    private void showDialogFavourite(String title, final ItemChannel singleItem){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_favourite);

        final TextView textviewYes = (TextView)dialog.findViewById(R.id.txt_yes);
        final TextView textviewNo = (TextView)dialog.findViewById(R.id.txt_no);
        final TextView textviewAddFavourite = (TextView)dialog.findViewById(R.id.txt_add_favourite);
        textviewAddFavourite.setText("Do you want to add "+title+" to favourites?");

        textviewYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (databaseHelper.getFavouriteChannelById(String.valueOf(singleItem.getId()))) {
                    dialog.dismiss();
                    Toast.makeText(CategoryItemActivity.this, getString(R.string.favourite_already_exist), Toast.LENGTH_SHORT).show();
                }else {
                    dialog.dismiss();
                    ContentValues contentValue = new ContentValues();
                    contentValue.put(DatabaseHelper.KEY_CHANNLE_ID, singleItem.getId());
                    contentValue.put(DatabaseHelper.KEY_CHANNLE_URL, singleItem.getChannelUrl());
                    contentValue.put(DatabaseHelper.KEY_CHANNLE_IMAGE, singleItem.getImage());
                    contentValue.put(DatabaseHelper.KEY_CHANNLE_NAME, singleItem.getChannelName());
                    contentValue.put(DatabaseHelper.KEY_CHANNLE_DESC, singleItem.getDescription());
                    databaseHelper.addFavouriteChannel(DatabaseHelper.TABLE_FAVOURITE_CHANNLE, contentValue, null);
                    Toast.makeText(CategoryItemActivity.this, getString(R.string.favourite_add), Toast.LENGTH_SHORT).show();
                }
            }
        });
        textviewNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);
        //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void fillArraylist(){
        String[] array ={getString(R.string.mx_player),getString(R.string.three_twenty_one_player)};
        for (int i = 0; i < array.length; i++) {
            PlayerListModel model = new PlayerListModel();
            model.setName(array[i]);
            arrayListPlayerList.add(model);
        }

    }
    public void onStart() {
        super.onStart();

    }

    public void onStop() {
        super.onStop();
        try
        {
            FlurryAgent.onEndSession(this);
            mFlurryAdBanner.destroy();

        }catch (Exception c)
        {

        }
    }

    public void onDestroy() {
        super.onDestroy();
        try
        {
            mFlurryAdInterstitial.destroy();

        }catch (Exception c)
        {

        }
    }
    public void showToast(String msg) {
        Toast.makeText(CategoryItemActivity.this, msg, Toast.LENGTH_LONG).show();
    }
    public void showFlurryBannerAdd()
    {
        FlurryAgent.onStartSession(this);
        // fetch and display ad for this ad space as soon as it is ready.
        mFlurryAdBanner.fetchAndDisplayAd();
    }
    FlurryAdBannerListener bannerAdListener = new FlurryAdBannerListener() {

        @Override
        public void onFetched(FlurryAdBanner adBanner) {
            adBanner.displayAd();
        }

        @Override
        public void onRendered(FlurryAdBanner flurryAdBanner) {

        }

        @Override
        public void onShowFullscreen(FlurryAdBanner flurryAdBanner) {

        }

        @Override
        public void onCloseFullscreen(FlurryAdBanner flurryAdBanner) {

        }

        @Override
        public void onAppExit(FlurryAdBanner flurryAdBanner) {

        }

        @Override
        public void onClicked(FlurryAdBanner flurryAdBanner) {

        }

        @Override
        public void onVideoCompleted(FlurryAdBanner flurryAdBanner) {

        }

        @Override
        public void onError(FlurryAdBanner adBanner, FlurryAdErrorType adErrorType, int errorCode) {
            adBanner.destroy();
        }
        //..
        //the remainder of the listener callback methods
    };
    FlurryAdInterstitialListener interstitialAdListener = new FlurryAdInterstitialListener() {

        @Override
        public void onFetched(FlurryAdInterstitial adInterstitial) {
            adInterstitial.displayAd();
        }

        @Override
        public void onRendered(FlurryAdInterstitial flurryAdInterstitial) {

        }

        @Override
        public void onDisplay(FlurryAdInterstitial flurryAdInterstitial) {

        }

        @Override
        public void onClose(FlurryAdInterstitial flurryAdInterstitial) {

        }

        @Override
        public void onAppExit(FlurryAdInterstitial flurryAdInterstitial) {

        }

        @Override
        public void onClicked(FlurryAdInterstitial flurryAdInterstitial) {

        }

        @Override
        public void onVideoCompleted(FlurryAdInterstitial flurryAdInterstitial) {

        }

        @Override
        public void onError(FlurryAdInterstitial adInterstitial, FlurryAdErrorType adErrorType, int errorCode) {
            adInterstitial.destroy();
        }
        //..
        //the remainder of listener callbacks
    };
    private void showFlurryIntertitial()
    {
        FlurryAgent.onStartSession(this,getResources().getString(R.string.flurry_api_key));
        // fetch and prepare ad for this ad space. won’t render one yet
        mFlurryAdInterstitial.fetchAd();
    }
    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarting(CastSession session) {
            }

            @Override
            public void onSessionEnding(CastSession session) {
            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {
            }

            private void onApplicationConnected(CastSession castSession) {
                mCastSession = castSession;
                invalidateOptionsMenu();
            }

            private void onApplicationDisconnected() {
                invalidateOptionsMenu();
            }
        };
    }
    public  boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(CategoryItemActivity.this, status, 2404).show();
            }
            return false;
        }
        return true;
    }
}
