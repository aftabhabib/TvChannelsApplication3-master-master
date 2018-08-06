package com.example.fragment;


import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.liveplanet_tv.AuthenticateParentFragment;
import com.app.liveplanet_tv.LocalPlayerActivity;
import com.app.liveplanet_tv.R;
import com.app.liveplanet_tv.TVPlayActivity;
import com.app.liveplanet_tv.YtPlayActivity;
import com.example.adapter.ChannelAdapter;
import com.example.db.DatabaseHelper;
import com.example.item.ItemChannel;
import com.example.util.Constant;
import com.example.util.ItemClickSupport;
import com.example.util.ItemOffsetDecoration;
import com.example.util.JsonUtils;
import com.example.util.MediaItem;
import com.example.util.PopUpAds;
import com.example.util.Utils;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavouriteFragment extends AuthenticateParentFragment{

    int channelId;
    ArrayList<ItemChannel> mListItem;
    private RecyclerView recyclerView;
    ChannelAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    DatabaseHelper databaseHelper;
    String packageName="";
    boolean isAppInstalled=false;
    String videoUrl="";
    String videoUrlToken="";
    public static ArrayList<ItemChannel> mListItemRelated;

    private CastContext mCastContext;
    private CastSession mCastSession;
    private SessionManagerListener<CastSession> mSessionManagerListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.row_recyclerview, container, false);
        getActivity().invalidateOptionsMenu();
        setHasOptionsMenu(true);
        mListItem = new ArrayList<>();
        mListItemRelated = new ArrayList<>();
        databaseHelper = new DatabaseHelper(getActivity());

        setupCastListener();
        if(isGooglePlayServicesAvailable()) {
            try {
                mCastContext = CastContext.getSharedInstance(getActivity());
                //mCastContext.registerLifecycleCallbacksBeforeIceCreamSandwich(this, savedInstanceState);
                mCastSession = mCastContext.getSessionManager().getCurrentCastSession();
            }catch (Exception e){
            }

        }

        lyt_not_found = (LinearLayout) rootView.findViewById(R.id.lyt_not_found);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.vertical_courses_list);
        int columns = getResources().getInteger(R.integer.number_of_column);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columns));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {

                final ItemChannel singleItem = mListItem.get(position);
                showDialogDeleteFavourite(singleItem);
                return true;
            }
        });

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override

            public void onItemClicked(RecyclerView recyclerView, int position, View v)
            {

                final ItemChannel singleItem = mListItem.get(position);
                channelId=singleItem.getId();

                if (JsonUtils.isNetworkAvailable(getActivity())) {
                    String tokenApiUrl= getSharedPrefData(Constant.keyMediaPlayerTokenUrl);
                    if(!tokenApiUrl.isEmpty())
                    {
                        new GetChannelToken().execute(tokenApiUrl+"?"+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken)+Constant.DEVICE_ID_PARAMETER+getSharedPrefData(Constant.keyDeviceId));
                    }else
                    {
                        Toast.makeText(getActivity(), "MediaPlayer Url token not found", Toast.LENGTH_SHORT).show();
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


        return rootView;
    }
    private void displayData() {

        adapter = new ChannelAdapter(getActivity(), mListItem);
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
    public void onResume() {
        super.onResume();
        mListItem = databaseHelper.getFavouriteChannle();
        displayData();
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
                            Toast.makeText(getActivity(),tokenMessage, Toast.LENGTH_SHORT).show();
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
        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new MyTaskChannel().execute(Constant.SINGLE_CHANNEL_URL +channelId+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken)+Constant.DEVICE_ID_PARAMETER+getSharedPrefData(Constant.keyDeviceId));
        }else
        {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
    private void  moveToNext(ItemChannel obj)
    {
        if(isAdmobActive())
        {
            PopUpAds.ShowInterstitialAds(getActivity(),getSharedPrefData(Constant.keyIntertitialAdDelayTime));
        }
        if (obj.isTv()) {
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

                Intent intent = new Intent(getActivity(), LocalPlayerActivity.class);
                intent.putExtra("media", item.toBundle());
                intent.putExtra("shouldStart", false);
                startActivity(intent);
                //ActivityCompat.startActivity(CategoryItemActivity.this, intent, options.toBundle());

            }else {
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
                        Intent intent = new Intent(getActivity(), TVPlayActivity.class);
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
                    Intent intent = new Intent(getActivity(), TVPlayActivity.class);
                    intent.putExtra("url",videoUrl);
                    //intent.putExtra("url", obj.getChannelUrl()+"?"+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken)+Constant.DEVICE_ID_PARAMETER+getSharedPrefData(Constant.keyDeviceId));
                    startActivity(intent);
                }
            }
        } else {
            String videoId = JsonUtils.getVideoId(obj.getChannelUrl());
            Intent intent = new Intent(getActivity(), YtPlayActivity.class);
            intent.putExtra("id", videoId);
            startActivity(intent);
        }
    }
    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
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
    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
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
            }

            private void onApplicationDisconnected() {
            }
        };
    }
    public  boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(getActivity());
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(getActivity(), status, 2404).show();
            }
            return false;
        }
        return true;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        inflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.media_route_menu_item).setVisible(false);
        menu.findItem(R.id.search_fragment).setVisible(true);

        final MenuItem searchMenuItem = menu.findItem(R.id.search_fragment);
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
    }
    private void showDialogDeleteFavourite(final ItemChannel singleItem){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_favourite);

        final TextView textviewYes = (TextView)dialog.findViewById(R.id.txt_yes);
        final TextView textviewTitle = (TextView)dialog.findViewById(R.id.txt_title);

        final TextView textviewNo = (TextView)dialog.findViewById(R.id.txt_no);
        final TextView textviewAddFavourite = (TextView)dialog.findViewById(R.id.txt_add_favourite);
        textviewAddFavourite.setText("Are you sure to delete this channle from favourite?");
        textviewTitle.setText("Remove Favourite");

        textviewYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.removeFavouriteChannleById(String.valueOf(singleItem.getId()));
                mListItem.remove(singleItem);
                adapter.notifyDataSetChanged();
                dialog.cancel();
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
}
