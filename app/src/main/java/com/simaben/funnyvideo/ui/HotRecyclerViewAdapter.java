package com.simaben.funnyvideo.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.simaben.autoswaprefresh.BaseRecyclerAdapter;
import com.simaben.autoswaprefresh.BaseRecyclerViewHolder;
import com.simaben.funnyvideo.R;
import com.simaben.funnyvideo.bean.QiubaiVideo.ShowapiResBodyBean.PagebeanBean.ContentlistBean;

import java.util.List;

public class HotRecyclerViewAdapter extends BaseRecyclerAdapter<ContentlistBean>{


    public HotRecyclerViewAdapter(Context context, List<ContentlistBean> data) {
        super(context, data);
    }

    public HotRecyclerViewAdapter(Context context, List<ContentlistBean> data, boolean useAnimation) {
        super(context, data, useAnimation);
    }

    public HotRecyclerViewAdapter(Context context, List<ContentlistBean> data, boolean useAnimation, RecyclerView.LayoutManager layoutManager) {
        super(context, data, useAnimation, layoutManager);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.fragment_qiubai_item;
    }

    @Override
    public void bindData(BaseRecyclerViewHolder holder, int position, ContentlistBean item) {

        holder.getTextView(R.id.hit).setText("👍" + item.getLove().trim());
        holder.getTextView(R.id.hate).setText("👎" + item.getHate().trim());
        holder.getTextView(R.id.title).setText(item.getText().trim());
        String imgUrl = "";
        if (TextUtils.isEmpty(imgUrl=item.getImage3())){
            imgUrl = item.getProfile_image();
        }
        imgUrl = imgUrl.trim();
        holder.getImageView(R.id.video_img).setTag(imgUrl);
        ImageLoader.getInstance().displayImage(imgUrl, holder.getImageView(R.id.video_img));
    }
}
