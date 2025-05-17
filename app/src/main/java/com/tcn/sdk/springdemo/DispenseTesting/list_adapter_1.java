package com.tcn.sdk.springdemo.DispenseTesting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tcn.sdk.springdemo.R;

import java.util.List;

public class list_adapter_1 extends BaseAdapter {

    private final Context context;
    private List<reuseObj> arr;

    public list_adapter_1(Context context, List<reuseObj> arr) {
        this.context = context;
        this.arr = arr;
    }

    public void update(List<reuseObj> arr) {
        this.arr = arr;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return arr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_mylist, parent, false);

            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_status = convertView.findViewById(R.id.tv_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(arr.get(position).getname1());
        if (arr.get(position).getname3().equals("1")) {
            holder.tv_status.setText("Dispensed");
        } else {
            holder.tv_status.setText("Pending");
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv_name, tv_status;
    }

}