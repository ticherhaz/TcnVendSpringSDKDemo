package com.tcn.sdk.springdemo.Recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tcn.sdk.springdemo.DBUtils.CartDBHandler;
import com.tcn.sdk.springdemo.DBUtils.PorductDBHandler;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.ProductDbModel;
import com.tcn.sdk.springdemo.Model.ProductModel;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.TypeProfuctActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CartRecycler extends RecyclerView.Adapter<CartRecycler.ViewHolder> {

    private final List<CartListModel> cartListModels;
    private final TypeProfuctActivity profuctActivity;
    private final ProductRecycler productRecycler;
    private final List<ProductModel> productapiModelList;
    private Context context;
    private CartDBHandler db;
    private PorductDBHandler porductDBHandler;
    private List<ProductDbModel> productDbModelList;

    public CartRecycler(List<CartListModel> cartListModels, TypeProfuctActivity hh, ProductRecycler productRecycler1, List<ProductModel> productapiModelList1) {

        this.cartListModels = cartListModels;
        this.profuctActivity = hh;
        this.productRecycler = productRecycler1;
        this.productapiModelList = productapiModelList1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newcartlist, parent, false);
        context = parent.getContext();
        db = new CartDBHandler(context);
        porductDBHandler = new PorductDBHandler(context);
        productDbModelList = new ArrayList<ProductDbModel>();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.setIsRecyclable(false);

        String itemnumber = cartListModels.get(position).getItemnumber();
        String itemname = cartListModels.get(position).getItemname();
        String itemsize = cartListModels.get(position).getItemsize();
        String itemqty = cartListModels.get(position).getItemqty();
        String itemprice = cartListModels.get(position).getItemprice();
        String itemserial = cartListModels.get(position).getSerial_port();
        String itemurl = cartListModels.get(position).getImg();
        int itemid = cartListModels.get(position).getId();


        holder.setdata(itemnumber, itemname, itemsize, itemqty, itemprice, itemserial, itemurl);

        // Remove button click listener
        holder.remove.setOnClickListener(v -> {
            if (profuctActivity.paymentInProgress) {
                return;
            }

            // Get current position fresh each time
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }

            // Verify we have items to remove
            if (cartListModels.isEmpty() || adapterPosition >= cartListModels.size()) {
                return;
            }

            // Get the item to be removed
            CartListModel removedItem = cartListModels.get(adapterPosition);

            // Remove from data source
            cartListModels.remove(adapterPosition);

            // Update the adapter
            notifyItemRemoved(adapterPosition);

            // Notify about remaining items
            notifyItemRangeChanged(adapterPosition, cartListModels.size() - adapterPosition);

            // Update total price
            profuctActivity.showPrice();

            // Reset position in product list
            for (ProductModel product : productapiModelList) {
                if (removedItem.getItemnumber().equalsIgnoreCase(String.valueOf(product.Item_Number))) {
                    product.setPosition(0);
                    break; // Exit loop once found
                }
            }

            // Update product recycler
            productRecycler.notifyDataSetChanged();

            // If list is now empty, show empty state
            if (cartListModels.isEmpty()) {
                notifyDataSetChanged(); // Full refresh to show empty view
            }
        });

        holder.plus.setOnClickListener(v -> {
            if (profuctActivity.paymentInProgress) return;

            int currentPosition = holder.getAbsoluteAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION ||
                    currentPosition < 0 ||
                    currentPosition >= cartListModels.size()) return;

            CartListModel item = cartListModels.get(currentPosition);
            int currentQty = Integer.parseInt(holder.iqty.getText().toString());
            BigDecimal currentPrice = new BigDecimal(holder.iprice.getText().toString());

            // Find matching product
            ProductModel product = null;
            for (ProductModel p : productapiModelList) {
                if (item.getItemnumber().equals(String.valueOf(p.getItem_Number()))) {
                    product = p;
                    break;
                }
            }

            if (product == null) return;

            // Check stock availability
            if (product.getQuantity() > currentQty) {
                currentQty++;
                BigDecimal singlePrice = BigDecimal.valueOf(product.getPrice());
                BigDecimal newPrice = currentPrice.add(singlePrice);

                // Update item
                item.setItemqty(String.valueOf(currentQty));
                item.setItemprice(newPrice.setScale(2, RoundingMode.HALF_UP).toString());

                // Format price for display
                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                String formattedPrice = decimalFormat.format(newPrice);

                // Update views
                holder.iqty.setText(String.valueOf(currentQty));
                holder.iprice.setText(formattedPrice);

                // Notify changes
                notifyItemChanged(currentPosition);
                profuctActivity.showPrice();
            } else {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Not enough stock")
                        .setContentText("The selected item is not enough stock")
                        .show();
            }
        });

        holder.minus.setOnClickListener(v -> {
            if (profuctActivity.paymentInProgress) return;

            int currentPosition = holder.getAbsoluteAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION ||
                    currentPosition < 0 ||
                    currentPosition >= cartListModels.size()) return;

            CartListModel item = cartListModels.get(currentPosition);
            int currentQty = Integer.parseInt(holder.iqty.getText().toString());

            if (currentQty <= 1) return;  // Minimum quantity reached

            // Find matching product
            ProductModel product = null;
            for (ProductModel p : productapiModelList) {
                if (item.getItemnumber().equals(String.valueOf(p.getItem_Number()))) {
                    product = p;
                    break;
                }
            }

            if (product == null) return;

            // Calculate new values
            currentQty--;
            BigDecimal currentPrice = new BigDecimal(holder.iprice.getText().toString());
            BigDecimal singlePrice = BigDecimal.valueOf(product.getPrice());
            BigDecimal newPrice = currentPrice.subtract(singlePrice);

            // Ensure price doesn't go negative
            if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
                newPrice = BigDecimal.ZERO;
            }

            // Update item
            item.setItemqty(String.valueOf(currentQty));
            item.setItemprice(newPrice.toString());

            // Update database
            db.updateitem(item);

            // Update views
            holder.iqty.setText(String.valueOf(currentQty));
            holder.iprice.setText(newPrice.toString());

            // Notify changes
            notifyItemChanged(currentPosition);
            profuctActivity.showPrice();
        });
    }

    @Override
    public int getItemCount() {
        return cartListModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private final View mView;

        private final TextView iprice;
        private final TextView iqty;
        private final TextView ipositionno;
        private final TextView iname;
        private final ImageView innum;
        private final ImageButton plus;
        private final ImageButton minus;
        private final ImageView remove;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            innum = mView.findViewById(R.id.itemnum);
            iname = mView.findViewById(R.id.iname);
            ipositionno = mView.findViewById(R.id.textView31);
            iqty = mView.findViewById(R.id.textView22);
            iprice = mView.findViewById(R.id.textView30);
            plus = mView.findViewById(R.id.plus);
            minus = mView.findViewById(R.id.minus);
            remove = mView.findViewById(R.id.remove);
        }

        public void setdata(String itemnumber, String itemname, String itemsize, String itemqty, String itemprice, String serial, String url) {

            iname.setText(itemname);
            ipositionno.setText("Item no : " + itemnumber);
            iqty.setText(itemqty);
            iprice.setText(itemprice);

            Glide.with(context)
                    .load(url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)  // Cache both original & resized
                    .into(innum);
        }
    }
}
