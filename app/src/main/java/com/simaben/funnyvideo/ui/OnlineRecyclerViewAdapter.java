package com.simaben.funnyvideo.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.simaben.autoswaprefresh.BaseRecyclerAdapter;
import com.simaben.autoswaprefresh.BaseRecyclerViewHolder;
import com.simaben.funnyvideo.R;
import com.simaben.funnyvideo.bean.OnlineChannel;

import java.util.List;

public class OnlineRecyclerViewAdapter extends BaseRecyclerAdapter<OnlineChannel> {


    public OnlineRecyclerViewAdapter(Context context, List<OnlineChannel> data) {
        super(context, data);
    }

    public OnlineRecyclerViewAdapter(Context context, List<OnlineChannel> data, boolean useAnimation) {
        super(context, data, useAnimation);
    }

    public OnlineRecyclerViewAdapter(Context context, List<OnlineChannel> data, boolean useAnimation, RecyclerView.LayoutManager layoutManager) {
        super(context, data, useAnimation, layoutManager);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.fragment_online_item;
    }

    @Override
    public void bindData(BaseRecyclerViewHolder holder, int position, OnlineChannel item) {

        holder.getTextView(R.id.title).setText(item.getTitle());
        holder.getTextView(R.id.address).setText(item.getAddress());
    }
}
