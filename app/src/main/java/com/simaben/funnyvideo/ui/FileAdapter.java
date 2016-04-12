package com.simaben.funnyvideo.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.simaben.autoswaprefresh.BaseRecyclerAdapter;
import com.simaben.autoswaprefresh.BaseRecyclerViewHolder;
import com.simaben.funnyvideo.R;
import com.simaben.funnyvideo.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by simaben on 7/4/16.
 */
public class FileAdapter extends BaseRecyclerAdapter<File> {
    private ArrayList<File> selectFiles = new ArrayList<>();

    private static ArrayList<String> endList = new ArrayList<>();
    static {
        endList.add(".avi");
        endList.add(".mkv");
        endList.add(".mp4");
        endList.add(".rm");
        endList.add(".rmvb");
        endList.add(".flv");
        endList.add(".divx");
        endList.add(".xvid");
        endList.add(".mov");
        endList.add(".ts");
        endList.add(".tp");
        endList.add(".wmv");
        endList.add(".m4v");
    }
    protected void addSelectFile(File file){
        selectFiles.add(file);
    }

    protected void deleteSelectFile(File file){
        selectFiles.remove(file);
    }


    public FileAdapter(Context context, List<File> data) {
        super(context, data);
    }

    public FileAdapter(Context context, List<File> data, boolean useAnimation) {
        super(context, data, useAnimation);
    }

    public FileAdapter(Context context, List<File> data, boolean useAnimation, RecyclerView.LayoutManager layoutManager) {
        super(context, data, useAnimation, layoutManager);
    }

    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.fragment_local_item;
    }

    @Override
    public void bindData(BaseRecyclerViewHolder holder, int position, File item) {
        if (item!=null){
            holder.getTextView(R.id.title).setText(item.getName().trim());
            if (item.isDirectory()){
                holder.getImageView(R.id.video_img).setImageResource(R.mipmap.file_folder);
                String sum = null;
                if (item.list()!=null){
                    sum = String.format(mContext.getString(R.string.local_item_dir_count),item.list().length);
                }else{
                    sum = "0个条目";
                }
                holder.getTextView(R.id.tv).setText(sum);
            }else{
                holder.getTextView(R.id.tv).setText(FileUtil.formatFileSize(item.length()));
                String name = item.getName();
                if (name.contains(".")){
                    String end = name.substring(name.lastIndexOf("."));
                    if (endList.contains(end)){
                        holder.getImageView(R.id.video_img).setImageResource(R.mipmap.file_video);
                    }else{
                        holder.getImageView(R.id.video_img).setImageResource(R.mipmap.file_unrecog);
                    }
                }else{
                    holder.getImageView(R.id.video_img).setImageResource(R.mipmap.file_unrecog);
                }
            }
        }
        holder.itemView.setSelected(selectFiles.contains(item));
    }

    public void clearSelelctList() {
        selectFiles.clear();
        notifyDataSetChanged();
    }

    public ArrayList<File> getSelectFiles() {
        return selectFiles;
    }
}
