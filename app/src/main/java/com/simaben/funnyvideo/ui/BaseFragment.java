package com.simaben.funnyvideo.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by simaben on 7/4/16.
 */
public abstract class BaseFragment extends Fragment {

    protected Activity mAct;
    protected View decorView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAct = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        decorView = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, decorView);
        initView();
        loadData();
        return decorView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected abstract  int getLayoutId();

    protected abstract void initView();

    protected abstract void loadData();
    public boolean onBackPressed() {
        return false;
    }
}
