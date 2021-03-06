package com.qemasoft.alhabibshop.app.controller;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qemasoft.alhabibshop.app.R;
import com.qemasoft.alhabibshop.app.Utils;
import com.qemasoft.alhabibshop.app.model.Slideshow;
import com.qemasoft.alhabibshop.app.view.fragments.FragProduct;
import com.qemasoft.alhabibshop.app.view.fragments.FragProductDetail;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Inzimam on 23-Oct-17.
 */

public class MyPagerAdapter extends PagerAdapter {

    private ArrayList<Slideshow> slideshowArrayList;
    private LayoutInflater inflater;
    private Utils utils;
    private boolean isClickListenerSet;


    public MyPagerAdapter(Context context, ArrayList<Slideshow> slideshowArrayList,
                          boolean isClickListenerSet) {
        this.slideshowArrayList = slideshowArrayList;
        this.isClickListenerSet = isClickListenerSet;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return slideshowArrayList.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup view, int position) {

        View myImageLayout = inflater.inflate(R.layout.slide, view, false);

        ImageView myImage = myImageLayout.findViewById(R.id.image);
        final Slideshow slideshow = slideshowArrayList.get(position);
        utils = new Utils(view.getContext());

        if (!slideshow.getImage().isEmpty())
            Picasso.with(view.getContext()).load(slideshow.getImage()).into(myImage);
        view.addView(myImageLayout, 0);

        if (isClickListenerSet) {

            myImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = slideshow.getId();
                    String type = slideshow.getBannerType();
                    Bundle bundle = new Bundle();
                    if (type.equals("0")) {
                        bundle.putString("id", id);
                        utils.switchFragment(new FragProduct(), bundle);
                    } else if (type.equals("2")) {
                        bundle.putString("id", id);
                        bundle.putString("from", "mainActivity");
                        utils.switchFragment(new FragProductDetail(), bundle);
                    } else {
                        bundle.putString("id", id);
                        utils.switchFragment(new FragProductDetail(), bundle);
                    }
                }
            });
        }

        return myImageLayout;
    }

    @Override
    public int getItemPosition(Object object) {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}