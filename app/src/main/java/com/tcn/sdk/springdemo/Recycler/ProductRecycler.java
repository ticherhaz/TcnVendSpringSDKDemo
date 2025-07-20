package com.tcn.sdk.springdemo.Recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tcn.sdk.springdemo.DBUtils.CartDBHandler;
import com.tcn.sdk.springdemo.DBUtils.PorductDBHandler;
import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.Model.ProductModel;
import com.tcn.sdk.springdemo.R;
import com.tcn.sdk.springdemo.TypeProfuctActivity;
import com.tcn.sdk.springdemo.Utilities.SharedPref;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProductRecycler extends RecyclerView.Adapter<ProductRecycler.ViewHolder> {
    private final Context context;
    private final PorductDBHandler porductDBHandler;
    private final CartDBHandler db;
    private final TypeProfuctActivity profuctActivity;
    private List<ProductModel> productDbModelList;
    private final List<CartListModel> cartListModels;

    public ProductRecycler(Context context, List<ProductModel> productDbModelList, TypeProfuctActivity activity) {
        this.context = context;
        this.productDbModelList = productDbModelList;
        this.profuctActivity = activity;
        this.porductDBHandler = new PorductDBHandler(context);
        this.db = new CartDBHandler(context);
        this.cartListModels = activity.getCartListModels();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = productDbModelList.get(position);
        holder.bind(product, cartListModels, profuctActivity);
    }

    @Override
    public int getItemCount() {
        return productDbModelList.size();
    }

    public void update(List<ProductModel> products) {
        productDbModelList = products;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView price, number, pname;
        private final ImageView pimage, sold;
        private final View bottomview;
        private final ConstraintLayout border;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pname = itemView.findViewById(R.id.textView38);
            price = itemView.findViewById(R.id.textView37);
            pimage = itemView.findViewById(R.id.imageView25);
            sold = itemView.findViewById(R.id.imageView22);
            number = itemView.findViewById(R.id.textView);
            bottomview = itemView.findViewById(R.id.view2);
            border = itemView.findViewById(R.id.constraintLayout15);
        }

        public void bind(ProductModel product, List<CartListModel> cartList, TypeProfuctActivity activity) {
            // Format data once
            String formattedPrice = String.format("RM %.2f", product.Price);

            pname.setText(product.Name);
            price.setText(formattedPrice);
            number.setText(String.valueOf(product.Item_Number));

            // Load image with Glide
            Glide.with(itemView.getContext())
                    .load(product.Image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(pimage);

            // Update visibility
            sold.setVisibility(product.Quantity < 1 ? View.VISIBLE : View.GONE);
            bottomview.setVisibility(product.getPosition() == 0 ? View.INVISIBLE : View.VISIBLE);

            // Set click listener
            View.OnClickListener clickListener = v -> handleItemClick(product, cartList, activity);
            pname.setOnClickListener(clickListener);
            price.setOnClickListener(clickListener);
            itemView.setOnClickListener(clickListener);
        }

        private void handleItemClick(ProductModel product, List<CartListModel> cartList, TypeProfuctActivity activity) {
            if (!activity.getEnableDisableAddProduct() || activity.paymentInProgress) {
                return;
            }

            boolean alreadyExists = false;
            for (CartListModel item : cartList) {
                if (String.valueOf(product.Item_Number).equals(item.getItemnumber())) {
                    alreadyExists = true;
                    break;
                }
            }

            if (alreadyExists) {
                showAlreadyAddedDialog(itemView.getContext());
                return;
            }

            SharedPref.init(itemView.getContext());
            String cartEnable = SharedPref.read(SharedPref.LcartEnable, "true");
            int quantityAllow = cartEnable.equalsIgnoreCase("true") ? 3 : 0;

            if (product.Quantity < 1) {
                showNotEnoughStockDialog(itemView.getContext());
                return;
            }

            if (cartList.size() > quantityAllow) {
                showCartFullDialog(itemView.getContext(), cartEnable);
                return;
            }

            // Add to cart
            CartListModel newItem = createCartItem(product);
            cartList.add(newItem);
            product.setPosition(1);
            bottomview.setVisibility(View.VISIBLE);
            activity.showPrice();
        }

        private CartListModel createCartItem(ProductModel product) {
            CartListModel item = new CartListModel();
            item.setFprodid(product.getId());
            item.setTemp(product.Temperature);
            item.setItemnumber(String.valueOf(product.Item_Number));
            item.setItemname(product.Name);
            item.setItemsize(product.Size);
            item.setItemqty("1");
            item.setItemprice(String.format("%.2f", product.Price));
            item.setSerial_port(product.Serial_Port);
            item.setSerial_port_com(product.Serial_Port_Code);
            item.setRrp_percent(String.valueOf(product.RRP_Percent));
            item.setImg(product.Image);
            item.setVoucher(null);
            item.setProdid(String.valueOf(product.PID));
            return item;
        }

        private void showAlreadyAddedDialog(Context context) {
            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Already Added")
                    .setContentText("Quantity can be increased from cart")
                    .show();
        }

        private void showNotEnoughStockDialog(Context context) {
            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Not enough stock")
                    .setContentText("The selected item is not in stock")
                    .show();
        }

        private void showCartFullDialog(Context context, String cartEnable) {
            String message = cartEnable.equalsIgnoreCase("true")
                    ? "Only 4 items are allowed, remove any item from cart to add new item"
                    : "Only 1 item is allowed, remove any item from cart to add new item";

            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Cart is Full")
                    .setContentText(message)
                    .show();
        }
    }
}