package com.simaben.funnyvideo.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.lidroid.xutils.BitmapUtils;
import com.simaben.autoswaprefresh.BaseRecyclerAdapter;
import com.simaben.autoswaprefresh.BaseRecyclerViewHolder;
import com.simaben.funnyvideo.R;
import com.simaben.funnyvideo.bean.QiubaiVideo.ShowapiResBodyBean.PagebeanBean.ContentlistBean;
import com.socks.library.KLog;

import java.util.List;
import java.util.concurrent.ExecutionException;

import io.vov.vitamio.widget.VideoView;

public class HotRecyclerViewAdapter extends BaseRecyclerAdapter<ContentlistBean> {

    BitmapUtils bitmapUtils = null;
    BaseRecyclerViewHolder currentHolder = null;
    int mPosition = 0;

    public HotRecyclerViewAdapter(Context context, List<ContentlistBean> data) {
        this(context, data, true);
    }

    public HotRecyclerViewAdapter(Context context, List<ContentlistBean> data, boolean useAnimation) {
        this(context, data, useAnimation, new LinearLayoutManager(context));
    }

    public HotRecyclerViewAdapter(Context context, List<ContentlistBean> data, boolean useAnimation, RecyclerView.LayoutManager layoutManager) {
        super(context, data, useAnimation, layoutManager);
        bitmapUtils = new BitmapUtils(context);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.fragment_qiubai_item;
    }

    @Override
    public void bindData(final BaseRecyclerViewHolder holder, final int position, final ContentlistBean item) {

        if (currentHolder == null || mPosition != position) {

            holder.getTextView(R.id.hit).setText("👍" + item.getLove().trim());
            holder.getTextView(R.id.hate).setText("👎" + item.getHate().trim());
            holder.getTextView(R.id.title).setText(item.getText().trim());
            holder.getButton(R.id.play).setOnClickListener(new PlayOnClickListener(position, holder, item));

            String imgUrl = "";
            if (TextUtils.isEmpty(imgUrl = item.getImage3())) {
                imgUrl = item.getProfile_image();
            }
            imgUrl = imgUrl.trim();
            holder.getImageView(R.id.video_img).setTag(imgUrl);
            bitmapUtils.display(holder.getImageView(R.id.video_img), imgUrl);

            showVideoOrContent(holder, 0);

        } else {
            showVideoOrContent(holder, 1);
        }


    }

    LinearLayoutManager layoutManager;

    public void setLayoutManager(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public void restCurrentItem() {
        stopVideo(currentHolder);
        currentHolder = null;
        mPosition = 0;
    }

    class PlayOnClickListener implements View.OnClickListener {
        int position;
        ContentlistBean item;
        BaseRecyclerViewHolder holder;

        public PlayOnClickListener(int position, BaseRecyclerViewHolder holder, ContentlistBean item) {
            this.position = position;
            this.item = item;
            this.holder = holder;
        }

        @Override
        public void onClick(View v) {
            stopVideo(currentHolder);
            showVideoOrContent(holder, 1);
            currentHolder = holder;
            mPosition = position;
            startVideo(currentHolder);
        }
    }

    private void startVideo(BaseRecyclerViewHolder holder) {
        if (holder == null) return;
        VideoView videoView = (VideoView) holder.getView(R.id.videoview);
        try {
            videoView.start();
            String url = getData().get(mPosition).getVideo_uri().trim();
            videoView.setVideoPath(url);
            videoView.requestFocus();
        } catch (Exception e) {
            KLog.i("test", "视频播放失败");
        }
    }

    private void stopVideo(BaseRecyclerViewHolder holder) {
        if (holder == null) return;
        VideoView videoView = (VideoView) holder.getView(R.id.videoview);
        try {
            videoView.stopPlayback();
            showVideoOrContent(holder, 0);
        } catch (Exception e) {
            KLog.i("test", "视频暂停失败");
        }

    }

    private void showVideoOrContent(BaseRecyclerViewHolder holder, int type) {
        holder.getView(R.id.content).setVisibility(type == 0 ? View.VISIBLE : View.GONE);
        holder.getView(R.id.videoview).setVisibility(type == 0 ? View.GONE : View.VISIBLE);
    }

    public int getCurrentPosition() {
        return mPosition;
    }
}
