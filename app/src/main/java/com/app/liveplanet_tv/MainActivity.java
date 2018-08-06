package com.app.liveplanet_tv;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.liveplanet_tv.R;
import com.example.db.DatabaseHelper;
import com.example.fragment.CategoryFragment;
import com.example.fragment.FavouriteFragment;
import com.example.fragment.LatestFragment;
import com.example.fragment.RecentFragment;
import com.example.item.ItemCategory;
import com.example.util.Utils;
import com.flurry.android.FlurryAgent;
import com.flurry.android.ads.FlurryAdBanner;
import com.flurry.android.ads.FlurryAdBannerListener;
import com.flurry.android.ads.FlurryAdErrorType;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.startapp.android.publish.ads.banner.Banner;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AuthenticateParentActivity {

    RecyclerView.Adapter adapterCategorySelection = null;
    List<ItemCategory> categoryListFromDatabase = new ArrayList<>();
    DatabaseHelper databaseHelper;
    private IntroductoryOverlay mIntroductoryOverlay;
    private CastStateListener mCastStateListener;
    private CastContext mCastContext;
    private MenuItem mediaRouteMenuItem;

    Context context;
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    MyApplication MyApp;
    NavigationView navigationView;
    AdView mAdView;
    View startAppAdBanner;
    Toolbar toolbar;
    private RelativeLayout mBanner;
    private FlurryAdBanner mFlurryAdBanner = null;
    private String mAdSpaceName = "BANNER_ADSPACE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseHelper(this);
        //MobileAds.initialize(this, getString(R.string.admob_app_id));

        if(isStartAppAddActive())
        {
            StartAppSDK.init(this, "202859694", true);
        }else
        {
            ((Banner) findViewById(R.id.startAppBanner)).hideBanner();
        }
        //////////////////////////flurry///////////////////////////
        mBanner = (RelativeLayout)findViewById(R.id.rl_flurry_banner);
        mFlurryAdBanner = new FlurryAdBanner(this, mBanner, mAdSpaceName);
        mFlurryAdBanner.setListener(bannerAdListener);
        //////////////////////////////////////////////////////////////////////
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCastStateListener = new CastStateListener() {
            @Override
            public void onCastStateChanged(int newState) {
                if (newState != CastState.NO_DEVICES_AVAILABLE) {
                    showIntroductoryOverlay();
                }
            }
        };
//        if(isGooglePlayServicesAvailable()) {
//            try{
//                mCastContext = CastContext.getSharedInstance(this);
//            }catch (Exception e){
//            }
//
//        }

        fragmentManager = getSupportFragmentManager();
        MyApp = MyApplication.getInstance();
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mAdView = (AdView) findViewById(R.id.adView);
        if(isAdmobActive())
        {
            mAdView.setVisibility(View.VISIBLE);
            mBanner.setVisibility(View.GONE);

            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    Log.e(" loaded "," on add load : ");
                    // Code to be executed when an ad finishes loading.
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    Log.e(" failed "," on add load : "+errorCode);
                    // Code to be executed when an ad request fails.
                }
                @Override
                public void onAdOpened() {
                    Log.e(" ad opened "," on add load : ");
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }

                @Override
                public void onAdLeftApplication() {
                    Log.e(" left applicaiton  "," on add load : ");
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    Log.e(" closed s "," on add load : ");
                    // Code to be executed when when the user is about to return
                    // to the app after tapping on an ad.
                }
            });
        }else if(isFlurryAddActive())
        {
            mAdView.setVisibility(View.GONE);
            mBanner.setVisibility(View.VISIBLE);
            showFlurryBannerAdd();
        }else if(isAppLovinAddActive())
        {
              showFlurryBannerAdd();
        }
        CategoryFragment currentCategory = new CategoryFragment();
        fragmentManager.beginTransaction().replace(R.id.Container, currentCategory).commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                drawerLayout.closeDrawers();
                    switch (menuItem.getItemId()) {

                    case R.id.menu_go_category:
                        toolbar.setTitle(getString(R.string.menu_category));
                        CategoryFragment currentCategory = new CategoryFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, currentCategory).commit();
                        return true;
                        case R.id.menu_favourite:
                            toolbar.setTitle(getString(R.string.menu_favourite));
                            FavouriteFragment favouriteFragment = new FavouriteFragment();
                            fragmentManager.beginTransaction().replace(R.id.Container, favouriteFragment).commit();
                            return true;
                    case R.id.menu_go_latest:
                        toolbar.setTitle(getString(R.string.menu_latest));
                        LatestFragment latestFragment = new LatestFragment();
                        fragmentManager.beginTransaction().replace(R.id.Container, latestFragment).commit();
                        return true;
                        case R.id.menu_play:
                            Utils.showDialogToChoosePlayer(MainActivity.this);
                            return true;
                   // case R.id.menu_go_favourite:
                        //toolbar.setTitle(getString(R.string.menu_favourite));
                       // FavouriteFragment favouriteFragment = new FavouriteFragment();
                      //  fragmentManager.beginTransaction().replace(R.id.Container, favouriteFragment).commit();
                      //  return true;
                        case R.id.menu_recent:
                            toolbar.setTitle(getString(R.string.menu_recent));
                            RecentFragment recentFragment = new RecentFragment();
                            fragmentManager.beginTransaction().replace(R.id.Container, recentFragment).commit();
                          return true;
                    case R.id.menu_go_profile:
                        Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(profile);
                        return true;
                    case R.id.menu_go_about:
                        Intent about = new Intent(MainActivity.this, AboutUsActivity.class);
                        startActivity(about);
                        return true;

                    case R.id.live_chat:
                        try {
                            Uri uri = Uri.parse("fb-messenger://user/133596044121862");
                            Intent toMessenger= new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(toMessenger);
                        }
                        catch (android.content.ActivityNotFoundException ex)
                        {
                            Toast.makeText(MainActivity.this, "Please Install Facebook Messenger",    Toast.LENGTH_LONG).show();
                        }

                        return true;
                    case R.id.menu_facebook:
                        Intent browserIntent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/liveplanet_tv.co/"));
                        startActivity(browserIntent);
                        return true;
                    case R.id.menu_web:
                        Intent browserIntent1=new Intent(Intent.ACTION_VIEW,Uri.parse("http://liveplanet_tv.com/"));
                        startActivity(browserIntent1);
                        return true;
                    case R.id.menu_go_privacy:
                        Intent privacy = new Intent(MainActivity.this, PrivacyActivity.class);
                        startActivity(privacy);
                        return true;
                    default:
                        return true;
                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        if (!MyApp.getIsLogin()) {
            navigationView.getMenu().findItem(R.id.menu_go_profile).setVisible(false);
          //  navigationView.getMenu().findItem(R.id.menu_go_logout).setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.clear();
        return true;
        /*if(mCastContext != null) {
            mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu,
                    R.id.media_route_menu_item);
        }

        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        final MenuItem searchFragmentMenuItem = menu.findItem(R.id.search_fragment);
        searchFragmentMenuItem.setVisible(false);


        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    MenuItemCompat.collapseActionView(searchMenuItem);
                    searchView.setQuery("", false);
                }
            }

        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("search", arg0);
                startActivity(intent);

                //searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }
        });*/
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    private void Logout() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.menu_logout))
                .setMessage(getString(R.string.logout_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MyApp.saveIsLogin(false);
                        Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.ic_logout)
                .show();
    }

    private void ShareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg) + getPackageName());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

   /* private void RateApp() {
        final String appName = getPackageName();//your application package name i.e play store application url
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id="
                            + appName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + appName)));
        }
    }*/
/*public void facebook(View view){
    Intent browserIntent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/liveplanet_tv.co/"));
    startActivity(browserIntent);

}
    public void web(View view){
        Intent browserIntent=new Intent(Intent.ACTION_VIEW,Uri.parse("http://liveplanet_tv.com/"));
        startActivity(browserIntent);

    }*/
    @Override
    public void onBackPressed() {
        if(isStartAppAddActive())
        {
            StartAppAd.onBackPressed(this);
        }
        super.onBackPressed();
       // ExitApp();

        //super.onBackPressed();
    }

    private void ExitApp() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.exit_msg))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
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
    public void showFlurryBannerAdd()
    {
        FlurryAgent.onStartSession(this);
        // fetch and display ad for this ad space as soon as it is ready.
        mFlurryAdBanner.fetchAndDisplayAd();
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
    @Override
    public void onResume() {
        super.onResume();
        if(mCastContext != null) {
            mCastContext.addCastStateListener(mCastStateListener);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(mCastContext != null) {
            mCastContext.removeCastStateListener(mCastStateListener);
        }
    }
    private void showIntroductoryOverlay() {
        if (mIntroductoryOverlay != null) {
            mIntroductoryOverlay.remove();
        }
        if ((mediaRouteMenuItem != null) && mediaRouteMenuItem.isVisible()) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mIntroductoryOverlay = new IntroductoryOverlay.Builder(
                            MainActivity.this, mediaRouteMenuItem)
                            .setTitleText("Introducing Cast")
                            .setSingleTime()
                            .setOnOverlayDismissedListener(
                                    new IntroductoryOverlay.OnOverlayDismissedListener() {
                                        @Override
                                        public void onOverlayDismissed() {
                                            mIntroductoryOverlay = null;
                                        }
                                    })
                            .build();
                    mIntroductoryOverlay.show();
                }
            });
        }
    }
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(MainActivity.this, status, 2404).show();
            }
            return false;
        }
        return true;
    }
}
