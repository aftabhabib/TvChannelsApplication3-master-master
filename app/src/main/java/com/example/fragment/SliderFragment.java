package com.example.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.liveplanet_tv.R;
import com.app.liveplanet_tv.TVPlayActivity;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.item.ItemSlider;
import com.example.util.Constant;

import java.util.ArrayList;

public class SliderFragment extends Fragment implements BaseSliderView.OnSliderClickListener {

    ArrayList<ItemSlider> objects;
    private SliderLayout mDemoSlider;
    ItemSlider itemSlider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_slider, container, false);
        mDemoSlider = (SliderLayout) rootView.findViewById(R.id.slider);
        objects=HomeFragment.mSliderList;
        for (int i = 0; i < objects.size(); i++) {
            itemSlider = objects.get(i);
            TextSliderView textSliderView = new TextSliderView(getActivity());
            textSliderView.description(itemSlider.getName());
            textSliderView.image(Constant.IMAGE_PATH + itemSlider.getImage());
            textSliderView.setScaleType(BaseSliderView.ScaleType.Fit);
            textSliderView.getBundle().putString("extra", itemSlider.getLink());
            textSliderView.setOnSliderClickListener(this);
            mDemoSlider.addSlider(textSliderView);
        }

        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());

        return rootView;
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Intent intent = new Intent(getActivity(), TVPlayActivity.class);
        intent.putExtra("url", slider.getBundle().getString("extra"));
        startActivity(intent);
    }
}
