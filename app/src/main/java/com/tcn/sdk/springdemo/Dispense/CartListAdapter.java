package com.tcn.sdk.springdemo.Dispense;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartListAdapter extends BaseAdapter {

    public static final String STATUS_SOLD = "1";
    public static final String STATUS_CROSSED_OUT = "2";
    private final Context context;
    private final List<CartListModel> cartItems;
    private CartListAdapterCallback callback;
    private boolean isLoading = true;

    public CartListAdapter(Context context, List<CartListModel> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    public void update(List<CartListModel> newCartItems) {
        isLoading = false;
        this.cartItems.clear();
        Log.d("???", "newCartItems:: " + newCartItems.size());
        this.cartItems.addAll(newCartItems);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dispnsingrecyclercard, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Hide loading indicator based on isLoading flag
        //holder.loading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        Log.d("???", "cartItems.get(position).getPosition():: " + cartItems.get(position).getPosition());
        holder.pname.setText(cartItems.get(position).getItemname());
        Picasso.get().load(cartItems.get(position).getImg()).into(holder.pimage);
        holder.number.setText("Product Number - " + cartItems.get(position).getItemnumber());

        switch (cartItems.get(position).getPosition()) {
            case STATUS_SOLD:
                holder.loading.setVisibility(View.GONE);
                holder.sold.setVisibility(View.VISIBLE);
                break;
            case STATUS_CROSSED_OUT:
                holder.loading.setVisibility(View.GONE);
                holder.sold.setVisibility(View.VISIBLE);
                holder.sold.setImageDrawable(context.getResources().getDrawable(R.drawable.crossa));
                holder.sold.setColorFilter(Color.argb(255, 214, 19, 19));
                break;
            default:
                if (callback != null) {
                    callback.onDefaultStatus();
                }

                break;
        }

        return convertView;
    }

    public void handleDefaultStatusCallback(CartListAdapterCallback callback) {
        this.callback = callback;
    }

    public interface CartListAdapterCallback {
        void onDefaultStatus();
    }

    private static class ViewHolder {
        TextView number;
        TextView pname;
        ImageView pimage;
        ImageView sold;
        ProgressBar loading;
        View bottomview;
        ConstraintLayout border;

        public ViewHolder(View itemView) {
            number = itemView.findViewById(R.id.textView);
            pname = itemView.findViewById(R.id.textView38);
            pimage = itemView.findViewById(R.id.imageView25);
            sold = itemView.findViewById(R.id.imageView26);
            loading = itemView.findViewById(R.id.progressBar);
            bottomview = itemView.findViewById(R.id.view2);
            border = itemView.findViewById(R.id.constraintLayout15);
        }
    }
}