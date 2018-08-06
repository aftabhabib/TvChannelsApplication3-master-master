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
import com.example.item.ItemCategory;

import java.util.List;

public class CategorySelectionAdapter extends RecyclerView.Adapter<CategorySelectionAdapter.ViewHolder> {
    private List<ItemCategory> categoryList;
    private int selectedPosition = -1;


    public CategorySelectionAdapter(List<ItemCategory> employees) {
        this.categoryList = employees;
    }

    @Override
    public CategorySelectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.category_item, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final int pos = position;
        ItemCategory categoryModel = categoryList.get(position);
        viewHolder.category.setText(categoryModel.getCategoryName());

        /* for multiple selection*/
        viewHolder.chkSelected.setChecked(categoryModel.isSelected());
        viewHolder.chkSelected.setTag(categoryList.get(position));

        /* for single selection */
        /*viewHolder.chkSelected.setTag(position);
        if (position == selectedPosition) {
            viewHolder.chkSelected.setChecked(true);
        } else {
            viewHolder.chkSelected.setChecked(false);
        }*/
        /* for single selection */

        viewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /* single selection for checkbox*/
                /*CheckBox cb = (CheckBox) v;
                if(cb.isChecked()){
                    selectedPosition = (int) cb.getTag();
                }else {
                    selectedPosition = -1;
                }
                notifyDataSetChanged();*/
                /* single selection for checkbox*/

               /* for multiple selection of checkbox */
                CheckBox cb = (CheckBox) v;
                ItemCategory emp = (ItemCategory) cb.getTag();

                emp.setSelected(cb.isChecked());
                categoryList.get(pos).setSelected(cb.isChecked());

                Toast.makeText(
                        v.getContext(),
                        "Selected Employees: " + cb.getText() + " is "
                                + cb.isChecked(), Toast.LENGTH_LONG).show();
                /* end for multiple selection of checkbox */
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView category;
        public CheckBox chkSelected;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            category = (TextView) itemLayoutView.findViewById(R.id.category_name);
            chkSelected = (CheckBox) itemLayoutView.findViewById(R.id.checkbox);
        }
    }
    public List<ItemCategory> getCategoryList() {
        return categoryList;
    }
}