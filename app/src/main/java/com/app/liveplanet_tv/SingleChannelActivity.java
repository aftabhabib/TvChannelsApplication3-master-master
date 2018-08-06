package com.app.liveplanet_tv;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.liveplanet_tv.R;
import com.example.db.DatabaseHelper;
import com.example.fragment.RelatedFragment;
import com.example.item.ItemChannel;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SingleChannelActivity extends AuthenticateParentActivity {

    Context context;

    ImageView imgChannel, imgPlay, imgShare, imgFavourite, imgReport;
    TextView txtChannelName;
    WebView webView;
    ArrayList<ItemChannel> mListItem;
    public static ArrayList<ItemChannel> mListItemRelated;
    ScrollView mScrollView;
    ProgressBar mProgressBar;
    ItemChannel objBean;
    String Id;
    MyApplication MyApp;
    DatabaseHelper databaseHelper;
    private FragmentManager fragmentManager;
    LinearLayout lyt_may_you;
    AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_details);
        fragmentManager = getSupportFragmentManager();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mAdView = (AdView) findViewById(R.id.adView);
        if(isAdmobActive())
        {
            mAdView.loadAd(new AdRequest.Builder().build());
        }
        databaseHelper = new DatabaseHelper(getApplicationContext());

        Intent i = getIntent();
        Id = i.getStringExtra("Id");

        imgChannel = (ImageView) findViewById(R.id.img_channel);
        imgPlay = (ImageView) findViewById(R.id.img_play);
        imgShare = (ImageView) findViewById(R.id.image_share);
        imgFavourite = (ImageView) findViewById(R.id.image_favourite);
        imgReport = (ImageView) findViewById(R.id.image_report);

        txtChannelName = (TextView) findViewById(R.id.txt_channelname);
        webView = (WebView) findViewById(R.id.txt_details);
        webView.setBackgroundColor(Color.TRANSPARENT);

        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);

        lyt_may_you = (LinearLayout) findViewById(R.id.lyt_may_you);

        mListItem = new ArrayList<>();
        mListItemRelated = new ArrayList<>();

        MyApp = MyApplication.getInstance();

        if (JsonUtils.isNetworkAvailable(SingleChannelActivity.this)) {
            new MyTaskChannel().execute(Constant.SINGLE_CHANNEL_URL + Id+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken));
        } else {


           // Toast.makeText(context, "single chnl activity l:104", Toast.LENGTH_SHORT).show();
            showToast(getString(R.string.conne_msg1));
        }

        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareApp();
            }
        });

        imgReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApp.getIsLogin()) {
                    Intent intent = new Intent(SingleChannelActivity.this, ReportChannelActivity.class);
                    intent.putExtra("Id", Id);
                    intent.putExtra("Name", objBean.getChannelName());
                    intent.putExtra("Image", objBean.getImage());
                    startActivity(intent);
                } else {
                    showToast(getString(R.string.login_msg));
                }
            }
        });

        imgFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues fav = new ContentValues();
                if (databaseHelper.getFavouriteById(Id)) {
                    databaseHelper.removeFavouriteById(Id);
                    imgFavourite.setImageResource(R.drawable.btn_favourite);
                    Toast.makeText(SingleChannelActivity.this, getString(R.string.favourite_remove), Toast.LENGTH_SHORT).show();
                } else {
                    fav.put(DatabaseHelper.KEY_ID, Id);
                    fav.put(DatabaseHelper.KEY_TITLE, objBean.getChannelName());
                    fav.put(DatabaseHelper.KEY_IMAGE, objBean.getImage());
                    databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                    imgFavourite.setImageResource(R.drawable.btn_favourite_hover);
                    Toast.makeText(SingleChannelActivity.this, getString(R.string.favourite_add), Toast.LENGTH_SHORT).show();
                }
            }
        });
        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


           //     Toast.makeText(context, "img play listner ", Toast.LENGTH_SHORT).show();
                if (objBean.isTv()) {

                    //my added code.
//                    File file = new File("fileUri");
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.putExtra("url", objBean.getChannelUrl()+"?"+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken));
//                    intent.setDataAndType(Uri.fromFile(file), "video/*");
//                    startActivity(intent);

                    //orignal code.
                    Intent intent = new Intent(SingleChannelActivity.this, TVPlayActivity.class);
                    intent.putExtra("url", objBean.getChannelUrl()+"?"+Constant.TOKEN_PARAMETER+getSharedPrefData(Constant.keyUserToken));
                    startActivity(intent);

                } else {
                    String videoId = JsonUtils.getVideoId(objBean.getChannelUrl());
                    Intent intent = new Intent(SingleChannelActivity.this, YtPlayActivity.class);
                    intent.putExtra("id", videoId);
                    startActivity(intent);
                }

            }
        });

        firstFavourite();
    }

    private class MyTaskChannel extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressBar.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            if (null == result || result.length() == 0) {
                showToast(getString(R.string.nodata));
            } else {

                try {
                    JSONObject resultJson = new JSONObject(result);
                    JSONObject mainJsonObject=resultJson.getJSONObject("LIVETV");
                    JSONArray jsonArray = mainJsonObject.getJSONArray("channel");
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        ItemChannel itemChannel = new ItemChannel();
                        itemChannel.setId(objJson.getInt(Constant.CHANNEL_ID));
                        itemChannel.setChannelName(objJson.getString(Constant.CHANNEL_TITLE));
                        itemChannel.setImage(objJson.getString(Constant.CHANNEL_IMAGE));
                        itemChannel.setChannelUrl(objJson.getString(Constant.CHANNEL_URL));
                        itemChannel.setDescription(objJson.getString(Constant.CHANNEL_DESC));
                        itemChannel.setIsTv(objJson.getString(Constant.CHANNEL_TYPE).equals("live_url"));
                        mListItem.add(itemChannel);

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
                                Toast.makeText(SingleChannelActivity.this,tokenMessage, Toast.LENGTH_SHORT).show();
                            }else
                            {
                                sharedPrefData(Constant.keyUserToken,token);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    private void setResult() {
        objBean = mListItem.get(0);
        txtChannelName.setText(objBean.getChannelName());

        String mimeType = "text/html";
        String encoding = "utf-8";
        String htmlText = objBean.getDescription();

        String text = "<html><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/custom.ttf\")}body{font-family: MyFont;color: #000000;text-align:left;font-size:15px;margin-left:0px}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

        Picasso.with(SingleChannelActivity.this).load(Constant.IMAGE_PATH + objBean.getImage()).into(imgChannel);
        if (!mListItemRelated.isEmpty()) {
            RelatedFragment storeFragment = new RelatedFragment();
            fragmentManager.beginTransaction().replace(R.id.Container, storeFragment).commitAllowingStateLoss();
        } else {
            lyt_may_you.setVisibility(View.GONE);
        }
    }

    public void showToast(String msg) {
        Toast.makeText(SingleChannelActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    private void ShareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg) + getPackageName());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
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

    private void firstFavourite() {
        if (databaseHelper.getFavouriteById(Id)) {
            imgFavourite.setImageResource(R.drawable.btn_favourite_hover);
        } else {
            imgFavourite.setImageResource(R.drawable.btn_favourite);
        }
    }

}
