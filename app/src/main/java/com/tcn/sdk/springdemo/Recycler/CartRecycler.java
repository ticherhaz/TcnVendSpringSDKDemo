package com.tcn.sdk.springdemo.Recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tcn.sdk.springdemo.DBUtils.CartDBHandler;
import com.tcn.sdk.springdemo.DBUtils.PorductDBHandler;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.ProductDbModel;
import com.tcn.sdk.springdemo.Model.ProductModel;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.TypeProfuctActivity;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CartRecycler extends RecyclerView.Adapter<CartRecycler.ViewHolder> {


    public List<CartListModel> cartListModels;
    public Context context;
    CartDBHandler db;
    PorductDBHandler porductDBHandler;
    TypeProfuctActivity profuctActivity;
    ProductRecycler productRecycler;
    List<ProductModel> productapiModelList;
    private List<ProductDbModel> productDbModelList;

    public CartRecycler(List<CartListModel> cartListModels, TypeProfuctActivity hh, ProductRecycler productRecycler1, List<ProductModel> productapiModelList1) {

        this.cartListModels = cartListModels;
        this.profuctActivity = hh;
        this.productRecycler = productRecycler1;
        this.productapiModelList = productapiModelList1;
    }

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

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profuctActivity.paymentInProgress) {
                    return;
                }

                if (cartListModels.size() > 0) {
                    CartListModel n = cartListModels.get(position);
                    cartListModels.set(position, n);
                    cartListModels.remove(position);
                    notifyDataSetChanged();
                    profuctActivity.showprice();
                    for (int i = 0; i < productapiModelList.size(); i++) {
                        if (n.getItemnumber().equalsIgnoreCase(String.valueOf(productapiModelList.get(i).Item_Number))) {
                            productapiModelList.get(i).setPosition(0);
                        }
                    }
                    productRecycler.update(productapiModelList);
                }
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (profuctActivity.paymentInProgress) {
                    return;
                }

                boolean avaible = false;
                int count = 0;
                String amt = "0";
                String singleitem = "0";
                count = Integer.parseInt(holder.iqty.getText().toString());
                CartListModel n = cartListModels.get(position);

                for (int i = 0; i < productapiModelList.size(); i++) {
                    if (n.getItemnumber().equals(String.valueOf(productapiModelList.get(i).getItem_Number()))) {
                        singleitem = String.valueOf(productapiModelList.get(i).getPrice());
                        if (productapiModelList.get(i).getQuantity() > count) {
                            avaible = true;
                            break;
                        }
                    }
                }

                if (avaible) {

                    amt = holder.iprice.getText().toString();
                    count++;
                    BigDecimal svalue = new BigDecimal(singleitem);
                    BigDecimal avalue = new BigDecimal(amt);
                    n.setItemqty(String.valueOf(count));
                    n.setItemprice((avalue.add(svalue)) + "");

                    holder.iqty.setText(String.valueOf(count));
                    holder.iprice.setText((avalue.add(svalue)) + "");
                    cartListModels.set(position, n);
                    profuctActivity.showprice();
                } else {
                    final SweetAlertDialog sd = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Not enough stock")
                            .setContentText("The selected item is not enough stock");
                    sd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {

                        }
                    });
                    sd.show();
                }


            }
        });
        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profuctActivity.paymentInProgress) {
                    return;
                }

                int count;
                String amt = "0";
                String singleitem = "0";

                CartListModel n = cartListModels.get(position);
                for (ProductModel cn : productapiModelList) {

                    if (n.getItemnumber().equals(String.valueOf(cn.getItem_Number()))) {

                        singleitem = String.format("%.2f", cn.getPrice());
                    }
                }
                count = Integer.parseInt(holder.iqty.getText().toString());

                if (count > 1) {

                    amt = holder.iprice.getText().toString();
                    BigDecimal svalue = new BigDecimal(singleitem);
                    BigDecimal avalue = new BigDecimal(amt);
//                    BigDecimal total = avalue.subtract(svalue);
//                    if(total.compareTo(BigDecimal.ZERO)<0){
//                        return;
//                    }

                    count--;

                    n.setItemqty(String.valueOf(count));
                    n.setItemprice((avalue.subtract(svalue)) + "");

                    db.updateitem(n);
                    db.close();
                    holder.iqty.setText(String.valueOf(count));
                    holder.iprice.setText((avalue.subtract(svalue)) + "");

                    cartListModels.set(position, n);
                    profuctActivity.showprice();
                }

            }
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
            Picasso.get().load(url).into(innum);
        }
    }
}
