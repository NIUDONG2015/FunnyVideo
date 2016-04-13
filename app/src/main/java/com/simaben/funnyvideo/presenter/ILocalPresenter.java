package com.simaben.funnyvideo.presenter;

import android.view.View;

import java.io.File;

/**
 * Created by simaben on 13/4/16.
 */
public interface ILocalPresenter {
    void refreshFile(File file);
    boolean onItemLongClickListener(View view, int position);

    void onItemClickListener(View view, int position);

    boolean onBackPressed();
}
