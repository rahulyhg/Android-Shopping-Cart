package com.qemasoft.alhabibshop.app.controller;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.qemasoft.alhabibshop.app.R;

/**
 * Created by Inzimam on 22-Nov-17.
 */

public class ViewHolder1 extends RecyclerView.ViewHolder {

    private TextView title;
    private RecyclerView mRecyclerView;

    public ViewHolder1(View v) {
        super(v);
        title = v.findViewById(R.id.category_title_tv);
        mRecyclerView = v.findViewById(R.id.main_cat_recycler_view);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    public RecyclerView getmRecyclerView() {
        return mRecyclerView;
    }

    public void setmRecyclerView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
    }
}
