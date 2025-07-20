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

        holder.remove.setOnClickListener(v -> {
            if (profuctActivity.paymentInProgress) return;

            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;
            if (cartListModels.isEmpty() || currentPosition >= cartListModels.size()) return;

            CartListModel n = cartListModels.get(currentPosition);
            cartListModels.remove(currentPosition);
            notifyItemRemoved(currentPosition);
            profuctActivity.showPrice();

            for (int i = 0; i < productapiModelList.size(); i++) {
                if (n.getItemnumber().equalsIgnoreCase(String.valueOf(productapiModelList.get(i).Item_Number))) {
                    productapiModelList.get(i).setPosition(0);
                }
            }
            productRecycler.update(productapiModelList);
        });

        holder.plus.setOnClickListener(v -> {
            if (profuctActivity.paymentInProgress) return;

            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION ||
                    currentPosition < 0 ||
                    currentPosition >= cartListModels.size()) return;

            boolean available = false;
            String singleitem = "0";
            CartListModel n = cartListModels.get(currentPosition);
            int count = Integer.parseInt(holder.iqty.getText().toString());

            for (ProductModel product : productapiModelList) {
                if (n.getItemnumber().equals(String.valueOf(product.getItem_Number()))) {
                    singleitem = String.valueOf(product.getPrice());
                    // Use compareTo for accurate BigDecimal comparison
                    if (new BigDecimal(product.getQuantity()).compareTo(new BigDecimal(count)) > 0) {
                        available = true;
                        break;
                    }
                }
            }

            if (available) {
                String amt = holder.iprice.getText().toString();
                count++;

                BigDecimal svalue = new BigDecimal(singleitem);
                BigDecimal avalue = new BigDecimal(amt);
                BigDecimal newValue = avalue.add(svalue);

                n.setItemqty(String.valueOf(count));
                n.setItemprice(newValue.toString());

                holder.iqty.setText(String.valueOf(count));
                holder.iprice.setText(newValue.toString());
                cartListModels.set(currentPosition, n);
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

            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION ||
                    currentPosition < 0 ||
                    currentPosition >= cartListModels.size()) return;

            CartListModel n = cartListModels.get(currentPosition);
            int count = Integer.parseInt(holder.iqty.getText().toString());

            if (count <= 1) return;  // Minimum quantity reached

            String singleitem = "0";
            for (ProductModel product : productapiModelList) {
                if (n.getItemnumber().equals(String.valueOf(product.getItem_Number()))) {
                    singleitem = String.format("%.2f", product.getPrice());
                    break;
                }
            }

            String amt = holder.iprice.getText().toString();
            BigDecimal svalue = new BigDecimal(singleitem);
            BigDecimal avalue = new BigDecimal(amt);
            BigDecimal newValue = avalue.subtract(svalue);

            count--;
            n.setItemqty(String.valueOf(count));
            n.setItemprice(newValue.toString());

            // Update database without closing connection
            db.updateitem(n);

            holder.iqty.setText(String.valueOf(count));
            holder.iprice.setText(newValue.toString());
            cartListModels.set(currentPosition, n);
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
