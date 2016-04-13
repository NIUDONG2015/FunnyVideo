package com.simaben.funnyvideo.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.simaben.autoswaprefresh.OnItemClickListener;
import com.simaben.autoswaprefresh.OnItemLongClickListener;
import com.simaben.funnyvideo.R;
import com.simaben.funnyvideo.presenter.ILocalFragmentView;
import com.simaben.funnyvideo.presenter.ILocalPresenter;
import com.simaben.funnyvideo.presenter.LocalPresenterImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by simaben on 7/4/16.
 */
public class LocalFragment extends BaseFragment implements ILocalFragmentView, OnItemClickListener,
        OnItemLongClickListener {

    @Bind(R.id.localRecyclerView)
    RecyclerView localRecyclerView;
    @Bind(R.id.emptyView)
    TextView emptyView;

    FileAdapter fileAdapter;

    ILocalPresenter iLocalPresenter;

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
        iLocalPresenter = new LocalPresenterImpl(this);

        RecyclerView.LayoutManager layoutParams = new LinearLayoutManager(mAct);
        localRecyclerView.setLayoutManager(layoutParams);
        fileAdapter = new FileAdapter(mAct, new ArrayList<File>());
        localRecyclerView.setAdapter(fileAdapter);
        fileAdapter.setOnItemClickListener(this);
        fileAdapter.setOnItemLongClick(this);
    }


    @Override
    protected void loadData() {
        iLocalPresenter.refreshFile(null);
    }

    public boolean onBackPressed() {
        return iLocalPresenter.onBackPressed();
    }

    @Override
    public void setEmpty(boolean showEmpty) {
        localRecyclerView.setVisibility(showEmpty ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void clearSelelctList() {
        fileAdapter.clearSelelctList();
    }

    @Override
    public ArrayList<File> getSelectFiles() {
        return fileAdapter.getSelectFiles();
    }

    @Override
    public List<File> getData() {
        return fileAdapter.getData();
    }

    @Override
    public void addSelectFile(File file) {
        fileAdapter.addSelectFile(file);
    }

    @Override
    public void deleteSelectFile(File file) {
        fileAdapter.deleteSelectFile(file);
    }

    @Override
    public void setData(List data) {
        fileAdapter.setData(data);
    }

    @Override
    public void mstartActivity(Intent intent) {
        mAct.startActivity(intent);
    }

    @Override
    public ActionMode mstartActionMode(ActionMode.Callback callback) {
        return mAct.startActionMode(callback);
    }

    @Override
    public AlertDialog.Builder getBuild() {
        return new AlertDialog.Builder(mAct);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(mAct, "删除文件失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(View view, int position) {
        iLocalPresenter.onItemClickListener(view, position);
    }

    @Override
    public boolean onItemLongClickListener(View view, int position) {
        return iLocalPresenter.onItemLongClickListener(view, position);
    }
}
