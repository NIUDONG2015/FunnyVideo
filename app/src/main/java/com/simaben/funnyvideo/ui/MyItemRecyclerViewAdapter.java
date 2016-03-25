package com.simaben.funnyvideo.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.simaben.autoswaprefresh.BaseRecyclerAdapter;
import com.simaben.autoswaprefresh.BaseRecyclerViewHolder;
import com.simaben.funnyvideo.R;
import com.simaben.funnyvideo.bean.QiubaiVideo.ShowapiResBodyBean.PagebeanBean.ContentlistBean;
import com.socks.library.KLog;

import java.util.List;

public class MyItemRecyclerViewAdapter extends BaseRecyclerAdapter<ContentlistBean>{


    public MyItemRecyclerViewAdapter(Context context, List<ContentlistBean> data) {
        super(context, data);
    }

    public MyItemRecyclerViewAdapter(Context context, List<ContentlistBean> data, boolean useAnimation) {
        super(context, data, useAnimation);
    }

    public MyItemRecyclerViewAdapter(Context context, List<ContentlistBean> data, boolean useAnimation, RecyclerView.LayoutManager layoutManager) {
        super(context, data, useAnimation, layoutManager);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.fragment_item;
    }

    @Override
    public void bindData(BaseRecyclerViewHolder holder, int position, ContentlistBean item) {

        holder.getTextView(R.id.hit).setText("👍" + item.getLove());
        holder.getTextView(R.id.hate).setText("👎" + item.getHate());
        holder.getTextView(R.id.title).setText(item.getText());
        holder.getImageView(R.id.video_img).setTag(item.getImage3());
        ImageLoader.getInstance().displayImage(item.getImage3(), holder.getImageView(R.id.video_img));
    }
}
