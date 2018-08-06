package com.example.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.liveplanet_tv.R;

public class IntroFragment extends Fragment {

    final static String LAYOUT_ID = "layoutid";

    public static IntroFragment newInstance(int layoutId) {
        IntroFragment pane = new IntroFragment();
        Bundle args = new Bundle();
        args.putInt(LAYOUT_ID, layoutId);
        pane.setArguments(args);
        return pane;
    }

    //ImageView imageView;
    //Integer[] Images = {R.drawable.mobile_intro_1, R.drawable.mobile_intro_2, R.drawable.mobile_intro_3, R.drawable.mobile_intro_4};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_intro, container, false);
        //imageView = (ImageView) rootView.findViewById(R.id.image_intro);
        //imageView.setImageResource(Images[getArguments().getInt(LAYOUT_ID, -1)]);
        return rootView;
    }
}
