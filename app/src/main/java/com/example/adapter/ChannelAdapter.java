package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.liveplanet_tv.R;
import com.app.liveplanet_tv.SingleChannelActivity;
import com.example.item.ItemChannel;
import com.example.util.Constant;
import com.example.util.PopUpAds;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by laxmi.
 */
public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ItemRowHolder> {

    private ArrayList<ItemChannel> dataList;
    private ArrayList<ItemChannel> dataListFilterd = new ArrayList<>();
    private Context mContext;

    public ChannelAdapter(Context context, ArrayList<ItemChannel> dataList) {
        this.dataList = dataList;
        this.dataListFilterd.addAll(this.dataList);
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder holder, final int position) {
        final ItemChannel singleItem = dataList.get(position);
        holder.text.setText(singleItem.getChannelName());
        Picasso.with(mContext).load(Constant.IMAGE_PATH + singleItem.getImage()).placeholder(R.drawable.header_top_logo).into(holder.image);
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    PopUpAds.ShowInterstitialAds(mContext);
                Intent intent = new Intent(mContext, SingleChannelActivity.class);
                intent.putExtra("Id", String.valueOf(singleItem.getId()));
                mContext.startActivity(intent);*/
            }
        });
        holder.lyt_parent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    holder.lyt_parent.setBackgroundResource(R.drawable.outline_button_blue);
                }
                else {
                    holder.lyt_parent.setBackgroundResource(0);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView text;
        public LinearLayout lyt_parent;

        public ItemRowHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            text = (TextView) itemView.findViewById(R.id.text);
            lyt_parent = (LinearLayout) itemView.findViewById(R.id.rootLayout);
        }
    }
    public void filter(String text) {
        dataList.clear();
        if(text.isEmpty()){
            dataList.addAll(dataListFilterd);
        } else{
            text = text.toLowerCase();
            for(ItemChannel item: dataListFilterd){
                if(item.getChannelName().toLowerCase().contains(text)){
                    dataList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
