package com.tcn.sdk.springdemo.Recycler;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tcn.sdk.springdemo.Model.CartListModel;
import com.tcn.sdk.springdemo.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductsDispensingAdapter extends RecyclerView.Adapter<ProductsDispensingAdapter.ViewHolder> {

    public Context context;
    private List<CartListModel> cartListModels;
    //CartDBHandler db;


    public ProductsDispensingAdapter(List<CartListModel> productDbModelList) {
        this.cartListModels = productDbModelList;
    }

    public void updatedata(List<CartListModel> productDbModelList) {

        System.out.println("updating" + productDbModelList.get(0).getTemp());
        System.out.println("updating" + productDbModelList.get(1).getTemp());
        this.cartListModels = productDbModelList;


    }


    @NonNull
    @Override
    public ProductsDispensingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dispnsingrecyclercard, parent, false);
        context = parent.getContext();
        // db = new CartDBHandler(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductsDispensingAdapter.ViewHolder holder, final int position) {


        // holder.setIsRecyclable(false);

        final String itemnumber = cartListModels.get(position).getItemnumber();
        final String itemname = cartListModels.get(position).getItemname();
        final String itemqty = cartListModels.get(position).getItemqty();
        final String itemurl = cartListModels.get(position).getImg();
        final int temp = cartListModels.get(position).getTemp();

        holder.setdata(temp, itemname, itemurl, itemnumber, itemqty);


    }

    @Override
    public int getItemCount() {
        return cartListModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private final View mView;

        private final TextView number;
        private final TextView pname;
        private final ImageView pimage;
        private final ImageView sold;
        private final ProgressBar loading;
        private final View bottomview;
        private final ConstraintLayout border;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            mView = itemView;

            loading = mView.findViewById(R.id.progressBar);
            number = mView.findViewById(R.id.textView);
            pname = mView.findViewById(R.id.textView38);
            pimage = mView.findViewById(R.id.imageView25);
            sold = mView.findViewById(R.id.imageView26);
            bottomview = mView.findViewById(R.id.view2);
            border = mView.findViewById(R.id.constraintLayout15);

        }

        public void setdata(int dispensed, String name, String img, String itemnumber, String qty) {

            pname.setText(name);
            Picasso.get().load(img).into(pimage);
            number.setText("Product Number - " + itemnumber);
            if (dispensed == 8) {
                loading.setVisibility(View.GONE);
                sold.setVisibility(View.VISIBLE);
            } else if (dispensed == 9) {
                loading.setVisibility(View.GONE);
                sold.setVisibility(View.VISIBLE);
                sold.setImageDrawable(mView.getResources().getDrawable(R.drawable.crossa));
                sold.setColorFilter(Color.argb(255, 214, 19, 19));
            }
            System.out.println("updating dispensig check test3 " + dispensed + "product=" + itemnumber);


        }


    }
}
