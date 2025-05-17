package com.tcn.sdk.springdemo.Dispense;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.R;

import java.util.ArrayList;

public class CartListAdapter2025 extends RecyclerView.Adapter<CartListAdapter2025.CartListAdapterViewHolder> {

    public static final String STATUS_SOLD = "1";
    public static final String STATUS_CROSSED_OUT = "2";

    private final ArrayList<CartListModel> cartItems;
    private CartListAdapterCallback callback;
    private boolean check = false;

    public CartListAdapter2025(ArrayList<CartListModel> cartItems) {
        this.cartItems = cartItems;
    }

    public void update(ArrayList<CartListModel> newCartItems) {
        if (newCartItems == null) {
            Log.e("CartListAdapter", "update() received a NULL list!");
            return;
        }

        Log.d("CartListAdapter", "Received items: " + newCartItems.size());

        if (newCartItems.isEmpty()) {
            Log.e("CartListAdapter", "update() received an EMPTY list!");
            return;
        }

        // Track if cartItems is holding items before clearing
        Log.d("CartListAdapter", "Before clear, cartItems size: " + cartItems.size());
        this.check = true;
        ArrayList<CartListModel> tempList = new ArrayList<>(newCartItems);
        cartItems.clear();  // Clearing existing items
        Log.d("CartListAdapter", "After clear, cartItems size: " + cartItems.size());

        // Debugging contents of newCartItems before adding
        for (int i = 0; i < newCartItems.size(); i++) {
            Log.d("CartListAdapter", "Adding item: " + newCartItems.get(i).getItemname());
        }

        cartItems.addAll(tempList); // Add new items
        Log.d("CartListAdapter", "After addAll, cartItems size: " + cartItems.size());

        notifyDataSetChanged();
    }


    public void updateItemChange(final int position) {
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public CartListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dispnsingrecyclercardnew, parent, false);
        return new CartListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartListAdapterViewHolder holder, int position) {
        final CartListModel item = cartItems.get(position);
        final Context context = holder.itemView.getContext();

        if (check) {
            String result = TextUtils.join("\n", item.getItemStatus());
            holder.getPname().setText(item.getItemname() + " x " + item.getItemqty() + " Unit" + "\n" + result);
        } else {
            holder.getPname().setText(item.getItemname() + " x " + item.getItemqty() + " Unit");

            final String productNumber = item.getItemnumber();
            holder.getNumber().setText("No." + productNumber);
            Picasso.get().load(item.getImg()).into(holder.pimage);
        }

        switch (item.getPosition()) {
            case STATUS_SOLD:
                holder.getLoading().setVisibility(View.GONE);
                holder.getSold().setVisibility(View.VISIBLE);
                break;
            case STATUS_CROSSED_OUT:
                holder.getLoading().setVisibility(View.GONE);
                holder.getSold().setVisibility(View.VISIBLE);
                holder.getSold().setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.crossa));
                holder.getSold().setColorFilter(Color.argb(255, 214, 19, 19));
                break;
            default:
                if (callback != null) {
                    callback.onDefaultStatus();
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void handleDefaultStatusCallback(CartListAdapterCallback callback) {
        this.callback = callback;
    }

    public interface CartListAdapterCallback {
        void onDefaultStatus();
    }

    public static class CartListAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView number;
        private TextView pname;
        private ImageView pimage;
        private ImageView sold;
        private ProgressBar loading;
        private View bottomview;
        private ConstraintLayout border;

        public CartListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.textView);
            pname = itemView.findViewById(R.id.textView38);
            pimage = itemView.findViewById(R.id.imageView25);
            sold = itemView.findViewById(R.id.imageView26);
            loading = itemView.findViewById(R.id.progressBar);
            bottomview = itemView.findViewById(R.id.view2);
            border = itemView.findViewById(R.id.constraintLayout15);
        }

        public TextView getNumber() {
            return number;
        }

        public void setNumber(TextView number) {
            this.number = number;
        }

        public TextView getPname() {
            return pname;
        }

        public void setPname(TextView pname) {
            this.pname = pname;
        }

        public ImageView getPimage() {
            return pimage;
        }

        public void setPimage(ImageView pimage) {
            this.pimage = pimage;
        }

        public ImageView getSold() {
            return sold;
        }

        public void setSold(ImageView sold) {
            this.sold = sold;
        }

        public ProgressBar getLoading() {
            return loading;
        }

        public void setLoading(ProgressBar loading) {
            this.loading = loading;
        }

        public View getBottomview() {
            return bottomview;
        }

        public void setBottomview(View bottomview) {
            this.bottomview = bottomview;
        }

        public ConstraintLayout getBorder() {
            return border;
        }

        public void setBorder(ConstraintLayout border) {
            this.border = border;
        }
    }
}
