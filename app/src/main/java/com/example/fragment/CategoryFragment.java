 package com.example.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.liveplanet_tv.AuthenticateParentFragment;
import com.app.liveplanet_tv.MainActivity;
import com.app.liveplanet_tv.R;
import com.app.liveplanet_tv.SearchActivity;
import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdLoadListener;
import com.applovin.sdk.AppLovinAdSize;
import com.applovin.sdk.AppLovinSdk;
import com.example.adapter.CategoryAdapter;
import com.example.db.DatabaseHelper;
import com.example.item.ItemCategory;
import com.example.util.Constant;
import com.example.util.ItemOffsetDecoration;
import com.example.util.JsonUtils;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by laxmi.
 */
public class CategoryFragment extends AuthenticateParentFragment {

    /* for chromecast*/
    private IntroductoryOverlay mIntroductoryOverlay;
    private CastStateListener mCastStateListener;
    private CastContext mCastContext;
    private MenuItem mediaRouteMenuItem;

    DatabaseHelper databaseHelper;
    ArrayList<ItemCategory> mListItem;
    public RecyclerView recyclerView;
    CategoryAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    private AppLovinAd loadedAd;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.row_recyclerview, container, false);

        /*AppLovinSdk.initializeSdk(getActivity());
        // For interstitials
        AppLovinInterstitialAd.isAdReadyToDisplay(getActivity());
        AppLovinInterstitialAd.show(getActivity());*/

        getActivity().invalidateOptionsMenu();
        setHasOptionsMenu(true);
        databaseHelper = new DatabaseHelper(getActivity());
        mListItem = new ArrayList<>();
        lyt_not_found = (LinearLayout) rootView.findViewById(R.id.lyt_not_found);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.vertical_courses_list);
        int columns = getResources().getInteger(R.integer.number_of_column);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columns));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new getCategory().execute(Constant.CATEGORY_URL+"?device_id="+getSharedPrefData(Constant.keyDeviceId));
        }

        mCastStateListener = new CastStateListener() {
            @Override
            public void onCastStateChanged(int newState) {
                if (newState != CastState.NO_DEVICES_AVAILABLE) {
                    showIntroductoryOverlay();
                }
            }
        };
        if(isGooglePlayServicesAvailable()) {
            mCastContext = CastContext.getSharedInstance(getActivity());
        }

        return rootView;
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
                    mIntroductoryOverlay = new IntroductoryOverlay.Builder(getActivity(), mediaRouteMenuItem)
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
                    JSONObject mainJsonObject=new JSONObject();
                    mainJsonObject=resultJson.getJSONObject("LIVETV");
                    if(mainJsonObject.has("category"))
                    {
                        JSONArray jsonArray = mainJsonObject.getJSONArray("category");
                        JSONObject objJson;
                        databaseHelper.deleteCategory();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            ItemCategory objItem = new ItemCategory();
                            objItem.setCategoryId(objJson.getInt(Constant.CATEGORY_CID));
                            objItem.setCategoryName(objJson.getString(Constant.CATEGORY_NAME));
                            objItem.setCategoryImage(objJson.getString(Constant.CATEGORY_IMAGE));
                            mListItem.add(objItem);

                            ContentValues contentValue = new ContentValues();
                            contentValue.put(DatabaseHelper.KEY_CATEGORY_ID,objItem.getCategoryId());
                            contentValue.put(DatabaseHelper.KEY_CATEGORY_IMAGE, objItem.getCategoryImage());
                            contentValue.put(DatabaseHelper.KEY_CATEGORY_NAME, objItem.getCategoryName());
                            databaseHelper.addCategory(DatabaseHelper.TABLE_CATEGORY,contentValue,null);

                        }
                    }else
                    {
                        lyt_not_found.setVisibility(View.VISIBLE);
                    }
                    if(mainJsonObject.has("token"))
                    {
                        JSONObject tokenJsonObject=new JSONObject();
                        tokenJsonObject=mainJsonObject.getJSONObject("token");
                        String tokenMessage=tokenJsonObject.optString("wmsAuthSignText");
                        String token=tokenJsonObject.optString("wmsAuthSign");
                        String mediaPlayerTokenApiUrl=tokenJsonObject.optString("media_token_url");
                        if(token.equalsIgnoreCase("0")||token.isEmpty())
                        {
                            //Toast.makeText(getActivity(),tokenMessage, Toast.LENGTH_SHORT).show();
                        }else
                        {
                            sharedPrefData(Constant.keyUserToken,token);
                        }
                        if(!mediaPlayerTokenApiUrl.isEmpty())
                        {
                            sharedPrefData(Constant.keyMediaPlayerTokenUrl,mediaPlayerTokenApiUrl);
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
        adapter = new CategoryAdapter(getActivity(), mListItem);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.search_fragment).setVisible(false);

        if(mCastContext != null) {
            mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getActivity().getApplicationContext(), menu,
                    R.id.media_route_menu_item);
        }

        final MenuItem searchMenuItem = menu.findItem(R.id.search);

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
                Intent intent = new Intent(getActivity(), SearchActivity.class);
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
        });

        //return true;
        //super.onCreateOptionsMenu(menu, inflater);
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.search:
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.replace(R.id.content_frame, new SettingFragment(),"Setting");
                transaction.addToBackStack("Setting");
                transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }*/
    public boolean isGooglePlayServicesAvailable() {
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
}
