package com.simaben.funnyvideo.presenter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.simaben.funnyvideo.R;
import com.simaben.funnyvideo.bean.SDCardNotFoundException;
import com.simaben.funnyvideo.ui.VideoPlayActivity;
import com.simaben.funnyvideo.utils.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by simaben on 13/4/16.
 */
public class LocalPresenterImpl implements ILocalPresenter {

    File currentDir;
    String rootPath;
    ActionMode actionMode;
    boolean isNotActionMode;

    ILocalFragmentView iLocalFragmentView;

    public LocalPresenterImpl(ILocalFragmentView iLocalFragmentView) {
        this.iLocalFragmentView = iLocalFragmentView;
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.local_menu_delete:
                    deleteFiles();
                    break;
                default:
                    return false;
            }
            return true;
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            iLocalFragmentView.clearSelelctList();
        }
    };

    private void deleteFiles() {
        AlertDialog.Builder builder = iLocalFragmentView.getBuild();
        builder.setTitle("删除文件").setMessage("确认删除？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Observable.create(new Observable.OnSubscribe<ArrayList<File>>() {
                    @Override
                    public void call(Subscriber<? super ArrayList<File>> subscriber) {
                        subscriber.onStart();
                        subscriber.onNext(iLocalFragmentView.getSelectFiles());
                        subscriber.onCompleted();
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Func1<ArrayList<File>, Observable<File>>() {
                            @Override
                            public Observable<File> call(ArrayList<File> files) {
                                return Observable.from(files);
                            }
                        })
                        .subscribe(new Subscriber<File>() {
                            @Override
                            public void onCompleted() {
                                mActionModeCallback.onDestroyActionMode(null);
                                refreshFile(currentDir);
                            }

                            @Override
                            public void onError(Throwable e) {
                                iLocalFragmentView.showToast("删除文件失败");

                                mActionModeCallback.onDestroyActionMode(null);
                            }

                            @Override
                            public void onNext(File file) {
                                FileUtil.deleteDir(file);
                            }
                        });


            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mActionModeCallback.onDestroyActionMode(null);
            }
        }).create().show();
    }

    @Override
    public void refreshFile(final File file) {
        Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                if (FileUtil.isSDCardExists()) {
                    if (file == null) {
                        subscriber.onNext(Environment.getExternalStorageDirectory().getParentFile());
                    } else {
                        subscriber.onNext(file);
                    }
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
                        iLocalFragmentView.clearSelelctList();
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof SDCardNotFoundException) {
                            iLocalFragmentView.setEmpty(true);
                        }
                    }

                    @Override
                    public void onNext(File file) {
                        if (file != null) {
                            if (file.isDirectory()) {
                                reloadRecyclerView(file);
                            }
                        } else {
                            iLocalFragmentView.setEmpty(false);
                        }
                    }
                });
    }

    @Override
    public boolean onItemLongClickListener(View view, int position) {
        if (actionMode != null) {
            return false;
        }
        actionMode = iLocalFragmentView.mstartActionMode(mActionModeCallback);
        view.setSelected(true);
        iLocalFragmentView.addSelectFile(iLocalFragmentView.getData().get(position));
        return true;
    }

    @Override
    public void onItemClickListener(View view, int position) {
        File item = iLocalFragmentView.getData().get(position);
        if (actionMode == null || isNotActionMode) {

            if (item.isDirectory()) {
                reloadRecyclerView(item);
            } else {
                Intent intent = VideoPlayActivity.startSelf(view.getContext(), item.getAbsolutePath(), item.getName());
                iLocalFragmentView.mstartActivity(intent);
            }
        } else {
            if (view.isSelected()) {
                view.setSelected(false);
                iLocalFragmentView.deleteSelectFile(item);
            } else {
                view.setSelected(true);
                iLocalFragmentView.addSelectFile(item);
            }
        }
    }

    @Override
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

    private void reloadRecyclerView(File file) {
        currentDir = file;
        File[] fileArray = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return !pathname.isHidden();
            }
        });
        if (fileArray != null) {
            Arrays.sort(fileArray, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return lhs.getName().substring(0, 1).toLowerCase().compareTo(rhs.getName().substring(0, 1).toLowerCase());
                }
            });
            iLocalFragmentView.setData(Arrays.asList(fileArray));
        }

    }

}
