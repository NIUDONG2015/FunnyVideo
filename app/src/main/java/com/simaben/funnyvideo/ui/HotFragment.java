package com.simaben.funnyvideo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simaben.autoswaprefresh.AutoLoadMoreRecyclerView;
import com.simaben.autoswaprefresh.OnItemClickListener;
import com.simaben.funnyvideo.R;
import com.simaben.funnyvideo.bean.QiubaiVideo;
import com.simaben.funnyvideo.bean.QiubaiVideo.ShowapiResBodyBean.PagebeanBean.ContentlistBean;
import com.simaben.funnyvideo.common.Constants;
import com.simaben.funnyvideo.retrofit.ShowService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HotFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;

    private HotRecyclerViewAdapter adapter = null;
    @Bind(R.id.recycler_view)
    public AutoLoadMoreRecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    public SwipeRefreshLayout mRefreshLayout;
    private int currentPage = 1;

    public HotFragment() {
    }

    // TODO: Customize parameter initialization
    public static HotFragment newInstance(Bundle args) {
        HotFragment fragment = new HotFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qiubai_list, container, false);
        ButterKnife.bind(this, view);
        initAdapter(null);
        refresh();

        return view;
    }

    private void refresh() {
        currentPage = 1;
        loadVideo(currentPage, "");
    }


    private void initAdapter(List data) {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), mColumnCount);
        adapter = new HotRecyclerViewAdapter(getActivity(), data, false, layoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setAutoLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new ItemDecorationAlbumColumns(getResources().getDimensionPixelSize(R.dimen.photos_list_spacing), mColumnCount));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mRecyclerView.setOnLoadMoreListener(new AutoLoadMoreRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                currentPage += 1;
                loadVideo(currentPage, "");
            }
        });
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ContentlistBean item = adapter.getData().get(position);
                Intent intent = VideoPlayActivity.startSelf(view.getContext(), item.getVideo_uri(), item.getText());
                startActivity(intent);
            }
        });

    }

    private void loadVideo(final int page, String title) {
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
        String timestamp = df.format(new Date());
        rx.Observable<QiubaiVideo> videoObservable = ShowService.createService().video(page + "", "249", timestamp, title, "41", "39d5945c3c3248cc91e20b73fb9d619c");
        videoObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<QiubaiVideo, List<ContentlistBean>>() {
                    @Override
                    public List<ContentlistBean> call(QiubaiVideo qiubaiVideo) {
                        return qiubaiVideo.getShowapi_res_body().getPagebean().getContentlist();
                    }
                })
                .subscribe(new Subscriber<List<ContentlistBean>>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onCompleted() {
                        mRefreshLayout.setRefreshing(false);
                        if (currentPage != 1) {
                            mRecyclerView.notifyMoreLoaded();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRefreshLayout.setRefreshing(false);
                        if (currentPage != 1) {
                            mRecyclerView.notifyMoreLoaded();
                        }
                    }

                    @Override
                    public void onNext(List<ContentlistBean> contentlistBeans) {
                        if (page == 1) {
                            adapter.setData(contentlistBeans);
                        } else {
                            adapter.addMoreData(contentlistBeans);
                        }
                    }
                });
    }

}
