package com.tcn.sdk.springdemo.Recycler;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tcn.sdk.springdemo.DBUtils.CartDBHandler;
import com.tcn.sdk.springdemo.DBUtils.PorductDBHandler;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.ProductModel;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.TypeProfuctActivity;
import com.squareup.picasso.Picasso;
import com.tcn.sdk.springdemo.Utilities.SharedPref;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProductRecycler extends RecyclerView.Adapter<ProductRecycler.ViewHolder> {

    public Context context;
    //CartDBHandler db;
    PorductDBHandler porductDBHandler;
    CartDBHandler db;
    TypeProfuctActivity profuctActivity;
    private List<ProductModel> productDbModelList;
    private List<CartListModel> cartListModels;

    public ProductRecycler(List<ProductModel> productDbModelList, TypeProfuctActivity hh) {

        this.productDbModelList = productDbModelList;
        this.profuctActivity = hh;

    }


    @NonNull
    @Override
    public ProductRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productlist, parent, false);
        context = parent.getContext();
        // db = new CartDBHandler(context);
        porductDBHandler = new PorductDBHandler(context);
        cartListModels = profuctActivity.getCartListModels();
        db = new CartDBHandler(context);
        return new ViewHolder(view);
    }

    public void update(List<ProductModel> porductDBHandler1) {
        productDbModelList = porductDBHandler1;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductRecycler.ViewHolder holder, final int position) {


        holder.setIsRecyclable(false);

        final String itemnumber = String.valueOf(productDbModelList.get(position).Item_Number);
        final String itemname = productDbModelList.get(position).Name;
        final String itemsize = productDbModelList.get(position).Size;
        final String itemqty = String.valueOf(productDbModelList.get(position).Quantity);
        final String itemprice = String.format("%.2f", productDbModelList.get(position).Price);
        final String itemserial = productDbModelList.get(position).Serial_Port;
        final String itemserialcom = productDbModelList.get(position).Serial_Port_Code;
        final String itemrrp = String.valueOf(productDbModelList.get(position).RRP_Percent);
        final String itemurl = productDbModelList.get(position).Image;
        final int itemid = productDbModelList.get(position).PID;
        final int itemfpid = productDbModelList.get(position).getId();
        final int temp = productDbModelList.get(position).Temperature;

        holder.setdata(itemprice, itemname, itemurl, itemnumber, itemqty);

        if (productDbModelList.get(position).getPosition() == 0) {
            holder.bottomview.setVisibility(View.INVISIBLE);
        } else {
            holder.bottomview.setVisibility(View.VISIBLE);
        }

        View.OnClickListener oc = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profuctActivity.getEnableDisableAddProduct()) {
                    if (profuctActivity.paymentInProgress) {
                        //profuctActivity.displayDialogUnableAddProduct();
                        return;
                    }
                    boolean avaible = false;
                    boolean alreadyexist = false;
                    int itemcount = 0;
                    String quantity = itemqty;
                    //cartListModels = db.getAllItems();

                    if (cartListModels.isEmpty()) {

                        if (Integer.parseInt(quantity) >= 1) {
                            avaible = true;
                        }
                    } else {
                        for (CartListModel cn : cartListModels) {

                            if (itemnumber.equals(cn.getItemnumber())) {

                                alreadyexist = true;

                                break;
                            }
                        }
                    }

                    SharedPref.init(context);
                    String LcartEnable = SharedPref.read(SharedPref.LcartEnable, "true");
                    if (!alreadyexist) {
                        int quantityAllow = 0;
                        if (LcartEnable.equalsIgnoreCase("true")) {
                            quantityAllow = 3;
                        }
                        if (Integer.parseInt(quantity) >= 1) {
                            avaible = true;
                        }

                        if (avaible) {
                            if (cartListModels.size() <= quantityAllow) {

                                //  holder.bottomview.setBackgroundColor(Color.parseColor("#422B3B"));
                                // holder.border.setBackgroundColor(Color.parseColor("#ECECEC"));
                                holder.bottomview.setVisibility(View.VISIBLE);

//                                db.addItem(new CartListModel(
//                                        itemfpid,
//                                        temp,
//                                        itemnumber,
//                                        itemname,
//                                        itemsize,
//                                        "1",
//                                        itemprice,
//                                        itemserial,
//                                        itemserialcom,
//                                        itemrrp,
//                                        itemurl,
//                                        null,
//                                        String.valueOf(itemid)));
                                CartListModel obj = new CartListModel();
                                obj.setFprodid(itemfpid);
                                obj.setTemp(temp);
                                obj.setItemnumber(itemnumber);
                                obj.setItemname(itemname);
                                obj.setItemsize(itemsize);
                                obj.setItemqty("1");
                                obj.setItemprice(itemprice);
                                obj.setSerial_port(itemserial);
                                obj.setSerial_port_com(itemserialcom);
                                obj.setRrp_percent(itemrrp);
                                obj.setImg(itemurl);
                                obj.setVoucher(null);
                                obj.setProdid(String.valueOf(itemid));
                                cartListModels.add(obj);
                                profuctActivity.showprice();
                                productDbModelList.get(position).setPosition(1);


                            } else {
                                String textQuantity = "Only 1 items are allowed, remove any item from cart to add new item";
                                if (LcartEnable.equalsIgnoreCase("true")) {
                                    textQuantity = "Only 4 items are allowed, remove any item from cart to add new item";
                                }
                                final SweetAlertDialog sd = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Cart is Full")
                                        .setContentText(textQuantity);
                                sd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {

                                    }
                                });
                                sd.show();


                            }

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
                    } else {

                        final SweetAlertDialog sd = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Already Added")
                                .setContentText("Quantity can be increase from cart");
                        sd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {

                            }
                        });
                        sd.show();


                    }
                }
            }
        };
        holder.pname.setOnClickListener(oc);
        holder.price.setOnClickListener(oc);
        holder.mView.setOnClickListener(oc);

        /*holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



        };

    });*/


    }

    @Override
    public int getItemCount() {
        return productDbModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private final View mView;

        private final TextView price;
        private final TextView number;
        private final TextView pname;
        private final ImageView pimage;
        private final ImageView sold;
        private final View bottomview;
        private final ConstraintLayout border;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            mView = itemView;

            number = mView.findViewById(R.id.textView);
            price = mView.findViewById(R.id.textView37);
            pname = mView.findViewById(R.id.textView38);
            pimage = mView.findViewById(R.id.imageView25);
            sold = mView.findViewById(R.id.imageView22);
            bottomview = mView.findViewById(R.id.view2);
            border = mView.findViewById(R.id.constraintLayout15);

        }

        public void setdata(String pprice, String name, String img, String itemnumber, String qty) {

            pname.setText(name);
            price.setText("RM " + pprice);
            Picasso.get().load(img).into(pimage);
            number.setText(itemnumber);
            if (Integer.parseInt(qty) < 1) {
                sold.setVisibility(View.VISIBLE);
            }


        }


    }
}
