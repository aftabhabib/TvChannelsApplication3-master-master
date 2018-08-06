package com.example.adapter;

/**
 * Created by S Soft on 04-Aug-17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.app.liveplanet_tv.R;
import com.example.item.PlayerListModel;

import java.util.List;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.ViewHolder> {
    private List<PlayerListModel> arrayListPlayer;
    private int selectedPosition = -1;
    public PlayerListAdapter(List<PlayerListModel> arrayListPlayer) {
        this.arrayListPlayer = arrayListPlayer;
    }
    @Override
    public PlayerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_row_playerlist, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final int pos = position;
        PlayerListModel model = arrayListPlayer.get(position);
        viewHolder.tvName.setText(model.getName());


        /* for multiple selection*/
        /*viewHolder.chkSelected.setChecked(model.isSelected());
        viewHolder.chkSelected.setTag(model.get(position));*/

        /* for single selection */
        viewHolder.chkSelected.setTag(position);
        if (position == selectedPosition) {
            viewHolder.chkSelected.setChecked(true);
        } else {
            viewHolder.chkSelected.setChecked(false);
        }
        /* for single selection */

        viewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                /* single selection for checkbox*/
                CheckBox cb = (CheckBox) v;
                if(cb.isChecked()){
                    selectedPosition = (int) cb.getTag();
                }else {
                    selectedPosition = -1;
                }
                for (int i = 0; i < arrayListPlayer.size(); i++) {
                    if(i == selectedPosition){
                        arrayListPlayer.get(selectedPosition).setSelected(true);
                    }else {
                        arrayListPlayer.get(i).setSelected(false);
                    }
                }
                notifyDataSetChanged();
                /* single selection for checkbox*/

               /* for multiple selection of checkbox */
                /*CheckBox cb = (CheckBox) v;
                PlayerListModel model1 = (PlayerListModel) cb.getTag();

                model1.setSelected(cb.isChecked());
                arrayListPlayer.get(pos).setSelected(cb.isChecked());
                Toast.makeText(
                        v.getContext(),
                        "Selected Employees: " + cb.getText() + " is "
                                + cb.isChecked(), Toast.LENGTH_LONG).show();*/
                /* end for multiple selection of checkbox */
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return arrayListPlayer.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public CheckBox chkSelected;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            tvName = (TextView) itemLayoutView.findViewById(R.id.textview_player_item);
            chkSelected = (CheckBox) itemLayoutView.findViewById(R.id.checkBox);
        }
    }
    public List<PlayerListModel> getPlayerList()
    {
        return arrayListPlayer;
    }
}