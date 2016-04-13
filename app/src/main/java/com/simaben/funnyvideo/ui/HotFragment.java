package com.simaben.funnyvideo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.simaben.autoswaprefresh.AutoLoadMoreRecyclerView;
import com.simaben.autoswaprefresh.OnItemClickListener;
import com.simaben.funnyvideo.R;
import com.simaben.funnyvideo.bean.QiubaiVideo.ShowapiResBodyBean.PagebeanBean.ContentlistBean;
import com.simaben.funnyvideo.presenter.IHotFragmentView;
import com.simaben.funnyvideo.presenter.IHotPresenter;
import com.simaben.funnyvideo.presenter.HotPresenterImpl;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HotFragment extends BaseFragment implements IHotFragmentView,
        AutoLoadMoreRecyclerView.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener, OnItemClickListener {

    private static final int M_COLUMN_COUNT = 2;

    HotRecyclerViewAdapter adapter = null;
    IHotPresenter iHotPresenter;

    @Bind(R.id.recycler_view)
    public AutoLoadMoreRecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    public SwipeRefreshLayout mRefreshLayout;


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
        iHotPresenter = new HotPresenterImpl(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_qiubai_list;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this, decorView);
        initAdapter(null);
    }

    @Override
    protected void loadData() {
        iHotPresenter.refresh();
    }

    private void initAdapter(List data) {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), M_COLUMN_COUNT);
        adapter = new HotRecyclerViewAdapter(getActivity(), data, false, layoutManager);
        adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setAutoLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new ItemDecorationAlbumColumns(getResources().getDimensionPixelSize(R.dimen.photos_list_spacing), M_COLUMN_COUNT));
        mRecyclerView.setOnLoadMoreListener(this);
        mRefreshLayout.setOnRefreshListener(this);
    }


    @Override
    public void setRefreshing(boolean refreshing) {
        mRefreshLayout.setRefreshing(refreshing);
    }

    @Override
    public void notifyMoreLoaded() {
        mRecyclerView.notifyMoreLoaded();
    }

    @Override
    public void setRefreshViewData(List data) {
        adapter.setData(data);
    }

    @Override
    public void setRefreshViewMoreData(List data) {
        adapter.addMoreData(data);
    }

    @Override
    public void loadMore() {
        iHotPresenter.loadMore();
    }

    @Override
    public void onRefresh() {
        iHotPresenter.refresh();
    }

    @Override
    public void onItemClick(View view, int position) {
        ContentlistBean item = adapter.getData().get(position);
        Intent intent = VideoPlayActivity.startSelf(view.getContext(), item.getVideo_uri(), item.getText());
        startActivity(intent);
    }
}
