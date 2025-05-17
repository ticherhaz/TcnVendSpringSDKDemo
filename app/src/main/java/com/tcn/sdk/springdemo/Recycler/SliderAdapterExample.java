package com.tcn.sdk.springdemo.Recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;
import com.tcn.sdk.springdemo.Model.SliderItem;
import com.tcn.sdk.springdemo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {
    private final Context context;
    int count = 0;
    private List<SliderItem> mSliderItems = new ArrayList<>();

    public SliderAdapterExample(Context context) {
        this.context = context;
    }

    public void renewItems(List<SliderItem> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliderlist, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        String itemurl = mSliderItems.get(position).getImg();
        viewHolder.setdata(itemurl);


       /* viewHolder.imageViewBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(count ==7)
                {
                    Intent cart = new Intent(context, configact.class);
                    context.startActivity(cart);
                    count =0;
                }
                else
                {
                    count++;
                }
            }
        });*/


    }


    @Override
    public int getCount() {
        return mSliderItems.size();
    }


    public class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View mView;
        ImageView imageViewBackground;


        public SliderAdapterVH(View itemView) {
            super(itemView);
            mView = itemView;

            imageViewBackground = mView.findViewById(R.id.imageView7);
        }

        public void setdata(String url) {

            File f = new File(url);
            Picasso.get().load(f).resize(500, 500).into(imageViewBackground);

        }
    }

}