package com.app.liveplanet_tv;


import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.liveplanet_tv.R;
import com.example.util.Constant;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


public class TVPlayActivity extends AuthenticateParentActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    private InterstitialAd interstitialAd;
    private CountDownTimer countDownTimer;
    private long timerMilliseconds;

    private VideoView mVideoView;
    private String url;
    private ProgressBar load;
    private TextView empty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(this);
        /*this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_tv_play);
        url = getIntent().getStringExtra("url");
        init();
    }

    public void init() {
        MobileAds.initialize(this,  "ca-app-pub-2590720503901919~4508282964");
// Create the InterstitialAd and set the adUnitId.
        interstitialAd = new InterstitialAd(this);
        // Defined in res/values/strings.xml
        interstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                //loadAdd();
            }

            @Override
            public void onAdLoaded() {
                //Toast.makeText(TVPlayActivity.this," loaded !!!!!!!!!!!!!!! ",Toast.LENGTH_SHORT).show();
                showInterstitial();
            }
        });
        loadAdd();
        load = (ProgressBar) this.findViewById(R.id.load);
        empty = (TextView) this.findViewById(R.id.empty);
        mVideoView = (VideoView) this.findViewById(R.id.surface_view);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnErrorListener(this);
        if(!TextUtils.isEmpty(url)) {
            Uri videoUri = Uri.parse(url);
            mVideoView.setVideoURI(videoUri);
            mVideoView.requestFocus();
            loading();
        }

    }

    private void loading() {
        load.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
    }

    private void loadComplete(MediaPlayer arg0) {
        load.setVisibility(View.GONE);
        // vv.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
        mVideoView.start();
        mVideoView.resume();
    }

    private void error(String msg) {
        load.setVisibility(View.GONE);
        mVideoView.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
        if (msg != null)
            empty.setText(msg);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // TODO Auto-generated method stub
        Log.d("ONLINE TV", "Prepared");
        loadComplete(mp);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        Log.d("ONLINE TV", "Error");
        error("Unable to play this channel.");
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub
        Log.d("ONLINE TV", "Complete");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mVideoView.stopPlayback();
        //       finish();
    }
    private void showInterstitial() {
        if(!TextUtils.isEmpty(getSharedPrefData(Constant.keyIntertitialAdDelayTime))){
            Constant.AD_COUNT_SHOW = Integer.parseInt(getSharedPrefData(Constant.keyIntertitialAdDelayTime));
        }
        if(Constant.isFirstTime){
            if (interstitialAd != null && interstitialAd.isLoaded()) {
                interstitialAd.show();
                Constant.isFirstTime=false;
            }

        }else {
            Constant.AD_COUNT += 1;
            if (Constant.AD_COUNT == Constant.AD_COUNT_SHOW) {
                Constant.AD_COUNT = 0;
                if (interstitialAd != null && interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                }
            }
        }
    }
    private void createTimer(final long milliseconds) {
        // Create the game timer, which counts down to the end of the level
        // and shows the "retry" button.
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(milliseconds, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                timerMilliseconds = millisUnitFinished;
            }

            @Override
            public void onFinish() {
                showInterstitial();
            }
        };
    }
    private void loadAdd(){
        if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
        }

    }

}