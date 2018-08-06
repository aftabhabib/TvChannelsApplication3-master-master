package com.example.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.liveplanet_tv.R;
import com.app.liveplanet_tv.SingleChannelActivity;
import com.example.adapter.ChannelAdapter;
import com.example.item.ItemChannel;
import com.example.util.ItemOffsetDecoration;

import java.util.ArrayList;

public class RelatedFragment extends Fragment {

    ArrayList<ItemChannel> objects;
    public RecyclerView recyclerView;
    ChannelAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_related, container, false);
        objects= SingleChannelActivity.mListItemRelated;
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        adapter = new ChannelAdapter(getActivity(), objects);
        recyclerView.setAdapter(adapter);
        return rootView;
    }
}
