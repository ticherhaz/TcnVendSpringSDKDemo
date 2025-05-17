package com.tcn.sdk.springdemo.DispenseTesting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tcn.sdk.springdemo.R;

import java.util.Collections;
import java.util.List;

public class GridAdapter extends ArrayAdapter<List<reuseObj>> {

    List<reuseObj> items_list;
    int custom_layout_id;
    Context context;

    public GridAdapter(@NonNull Context context, int resource, @NonNull List<reuseObj> objects) {
        super(context, resource, Collections.singletonList(objects));
        items_list = objects;
        this.context = context;
        custom_layout_id = resource;
    }

    public void update(List<reuseObj> arr) {
        items_list = arr;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items_list.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(custom_layout_id, null);
        }

        TextView tv = v.findViewById(R.id.tv_name);
        RelativeLayout rl = v.findViewById(R.id.rl_body);
        tv.setText(items_list.get(position).getname1());
        if (items_list.get(position).getname2().equals("1")) {
            rl.setBackground(context.getResources().getDrawable(R.drawable.item_selector));
        } else {
            rl.setBackground(null);
        }
        return v;
    }

}
