package com.simaben.funnyvideo.ui;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.simaben.funnyvideo.R;
import com.simaben.funnyvideo.bean.QiubaiVideo.ShowapiResBodyBean.PagebeanBean.ContentlistBean;
import com.simaben.funnyvideo.common.ShowApplication;
import com.simaben.funnyvideo.ui.ItemFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private List contentList = new ArrayList<>();
    private OnListFragmentInteractionListener mListener;

    public void setListener(OnListFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ContentlistBean bean = (ContentlistBean) contentList.get(position);
        holder.hit.setText("👍"+bean.getLove());
        holder.hate.setText("👎" + bean.getHate());
        holder.title.setText(bean.getText());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onListFragmentInteraction(bean);
                }
            }
        });
        holder.videoImg.setTag(bean.getImage3());
        ImageLoader.getInstance().displayImage(bean.getImage3(),holder.videoImg);
    }

    public void clear() {
        contentList.clear();
        notifyDataSetChanged();
    }

    public void addAll(Collection collection) {
        contentList.addAll(collection);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.content)
        public View mView;
        @Bind(R.id.video_img)
        public ImageView videoImg;
        @Bind(R.id.hate)
        public TextView hate;
        @Bind(R.id.hit)
        public TextView hit;
        @Bind(R.id.title)
        public TextView title;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
