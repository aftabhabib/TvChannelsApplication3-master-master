package com.example.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.liveplanet_tv.MoreActivity;
import com.app.liveplanet_tv.R;
import com.example.adapter.ChannelAdapter;
import com.example.item.ItemChannel;
import com.example.item.ItemSlider;
import com.example.util.Constant;
import com.example.util.ItemOffsetDecoration;
import com.example.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    ScrollView mScrollView;
    ProgressBar mProgressBar;
    Button btnLatest, btnFeatured;
    TextView txtLatest, txtFeatured;
    RecyclerView mLatestView, mFeaturedView;
    ChannelAdapter mLatestAdapter, mFeaturedAdapter;
    ArrayList<ItemChannel> mLatestList, mFeaturedList;
    static ArrayList<ItemSlider> mSliderList;
    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mScrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        btnLatest = (Button) rootView.findViewById(R.id.btn_latest);
        btnFeatured = (Button) rootView.findViewById(R.id.btn_featured);
        txtLatest = (TextView) rootView.findViewById(R.id.txt_latest_home_size);
        txtFeatured = (TextView) rootView.findViewById(R.id.txt_featured_home_size);
        mLatestView = (RecyclerView) rootView.findViewById(R.id.rv_latest);
        mFeaturedView = (RecyclerView) rootView.findViewById(R.id.rv_featured);
        mLatestList = new ArrayList<>();
        mFeaturedList = new ArrayList<>();
        mSliderList = new ArrayList<>();

        fragmentManager = getActivity().getSupportFragmentManager();
        mLatestView.setHasFixedSize(false);
        mLatestView.setNestedScrollingEnabled(false);
        mLatestView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        mLatestView.addItemDecoration(itemDecoration);

        mFeaturedView.setHasFixedSize(false);
        mFeaturedView.setNestedScrollingEnabled(false);
        mFeaturedView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mFeaturedView.addItemDecoration(itemDecoration);

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new Home().execute(Constant.HOME_URL);
        } else {
            showToast(getString(R.string.conne_msg1));
        }

        btnLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoreActivity.class);
                intent.putExtra("which", "0");
                startActivity(intent);
            }
        });

        btnFeatured.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MoreActivity.class);
                intent.putExtra("which", "1");
                startActivity(intent);
            }
        });

        return rootView;
    }

    private class Home extends AsyncTask<String, Void, String> {

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
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject jsonArray = mainJson.getJSONObject(Constant.ARRAY_NAME);

                    JSONArray jsonSlider = jsonArray.getJSONArray(Constant.SLIDER_ARRAY);
                    JSONObject objJsonSlider;
                    for (int i = 0; i < jsonSlider.length(); i++) {
                        objJsonSlider = jsonSlider.getJSONObject(i);
                        ItemSlider objItem = new ItemSlider();
                        objItem.setName(objJsonSlider.getString(Constant.SLIDER_NAME));
                        objItem.setImage(objJsonSlider.getString(Constant.SLIDER_IMAGE));
                        objItem.setLink(objJsonSlider.getString(Constant.SLIDER_LINK));
                        mSliderList.add(objItem);
                    }

                    JSONArray jsonLatest = jsonArray.getJSONArray(Constant.HOME_LATEST_ARRAY);
                    JSONObject objJson;
                    for (int i = 0; i < jsonLatest.length(); i++) {
                        objJson = jsonLatest.getJSONObject(i);
                        ItemChannel objItem = new ItemChannel();
                        objItem.setId(objJson.getInt(Constant.CHANNEL_ID));
                        objItem.setChannelName(objJson.getString(Constant.CHANNEL_TITLE));
                        objItem.setImage(objJson.getString(Constant.CHANNEL_IMAGE));
                        mLatestList.add(objItem);
                    }

                    JSONArray jsonFeatured = jsonArray.getJSONArray(Constant.HOME_FEATURED_ARRAY);
                    JSONObject objJsonFeature;
                    for (int i = 0; i < jsonFeatured.length(); i++) {
                        objJsonFeature = jsonFeatured.getJSONObject(i);
                        ItemChannel objItem = new ItemChannel();
                        objItem.setId(objJsonFeature.getInt(Constant.CHANNEL_ID));
                        objItem.setChannelName(objJsonFeature.getString(Constant.CHANNEL_TITLE));
                        objItem.setImage(objJsonFeature.getString(Constant.CHANNEL_IMAGE));
                        mFeaturedList.add(objItem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setResult();
            }
        }
    }

    private void setResult() {

        mLatestAdapter = new ChannelAdapter(getActivity(), mLatestList);
        mLatestView.setAdapter(mLatestAdapter);

        mFeaturedAdapter = new ChannelAdapter(getActivity(), mFeaturedList);
        mFeaturedView.setAdapter(mFeaturedAdapter);

        txtLatest.setText(mLatestList.size() + " Channel");
        txtFeatured.setText(mFeaturedList.size() + " Channel");

        if (!mSliderList.isEmpty()) {
            SliderFragment sliderFragment = new SliderFragment();
            fragmentManager.beginTransaction().replace(R.id.ContainerSlider, sliderFragment).commit();
        }

    }

    public void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }


}
