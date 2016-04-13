package com.simaben.funnyvideo.presenter;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.ActionMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by simaben on 13/4/16.
 */
public interface ILocalFragmentView {
    void setEmpty(boolean empty);

    void clearSelelctList();

    ArrayList<File> getSelectFiles();
    List<File> getData();

    void addSelectFile(File file);

    void deleteSelectFile(File file);

    void setData(List data);
    void mstartActivity(Intent intent);

    ActionMode mstartActionMode(ActionMode.Callback callback);

    AlertDialog.Builder getBuild();

    void showToast(String msg);
}
