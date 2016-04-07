package com.simaben.funnyvideo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.simaben.autoswaprefresh.OnItemClickListener;
import com.simaben.funnyvideo.R;
import com.simaben.funnyvideo.bean.SDCardNotFoundException;
import com.simaben.funnyvideo.utils.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by simaben on 7/4/16.
 */
public class LocalFragment extends BaseFragment {

    @Bind(R.id.localRecyclerView)
    RecyclerView localRecyclerView;
    @Bind(R.id.emptyView)
    TextView emptyView;

    FileAdapter fileAdapter;
    File currentDir;
    String rootPath;

    public LocalFragment() {
    }

    public static LocalFragment newInstance(Bundle args) {
        LocalFragment fragment = new LocalFragment();
        if (args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_local_list;
    }

    @Override
    protected void initView() {
        RecyclerView.LayoutManager layoutParams = new LinearLayoutManager(mAct);
        localRecyclerView.setLayoutManager(layoutParams);
        fileAdapter = new FileAdapter(mAct, new ArrayList<File>());
        localRecyclerView.setAdapter(fileAdapter);
        fileAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                File item = fileAdapter.getData().get(position);
                if (item.isDirectory()) {
                    reloadRecyclerView(item);
                } else {
                    Intent intent = VideoPlayActivity.startSelf(view.getContext(), item.getAbsolutePath(), item.getName());
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    protected void loadData() {
        Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                if (FileUtil.isSDCardExists()) {
                    rootPath = Environment.getExternalStorageDirectory().getParentFile().getAbsolutePath();
                    subscriber.onNext(Environment.getExternalStorageDirectory());
                } else {
                    subscriber.onError(new SDCardNotFoundException());
                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<File>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof SDCardNotFoundException) {
                            setEmpty(true);
                        }
                    }

                    @Override
                    public void onNext(File file) {
                        if (file != null) {
                            if (file.isDirectory()) {
                                reloadRecyclerView(file);
                            }
                        } else {
                            setEmpty(false);
                        }
                    }
                });
    }

    private void reloadRecyclerView(File file) {
        currentDir = file;
        File[] fileArray = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isHidden();
            }
        });
        if (fileArray!=null){
            Arrays.sort(fileArray, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return lhs.getName().substring(0, 1).toLowerCase().compareTo(rhs.getName().substring(0, 1).toLowerCase());
                }
            });
            fileAdapter.setData(Arrays.asList(fileArray));
        }

    }

    public boolean onBackPressed() {
        if (currentDir != null) {
            if (currentDir.getAbsolutePath().equals("/")) {
                return false;
            } else {
                reloadRecyclerView(currentDir.getParentFile());
                return true;
            }

        }
        return false;
    }

    private void setEmpty(boolean showEmpty) {
        localRecyclerView.setVisibility(showEmpty ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
    }
}