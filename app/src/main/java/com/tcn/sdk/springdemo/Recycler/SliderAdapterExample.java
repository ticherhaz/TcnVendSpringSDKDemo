package com.tcn.sdk.springdemo.Recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.tcn.sdk.springdemo.Model.SliderItem;
import com.tcn.sdk.springdemo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

    private List<SliderItem> mSliderItems = new ArrayList<>();

    public SliderAdapterExample(Context context) {
    }

    public void renewItems(List<SliderItem> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        final View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliderlist, parent);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        final String itemUrl = mSliderItems.get(position).getImg();
        viewHolder.setData(itemUrl);
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    public static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View mView;
        ImageView imageViewBackground;


        public SliderAdapterVH(View itemView) {
            super(itemView);
            mView = itemView;

            imageViewBackground = mView.findViewById(R.id.imageView7);
        }

        public void setData(String url) {
            File f = new File(url);
            Glide.with(imageViewBackground.getContext())
                    .load(f)
                    .override(500, 500)  // Resize (similar to Picasso)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)  // Cache both original & resized
                    .format(DecodeFormat.PREFER_RGB_565)  // Use less memory (like Bitmap.Config.RGB_565)
                    .skipMemoryCache(false)  // Optional: Set to true if memory pressure is high
                    .into(imageViewBackground);
        }
    }
}