package com.simaben.funnyvideo.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.simaben.autoswaprefresh.OnItemClickListener;
import com.simaben.autoswaprefresh.OnItemLongClickListener;
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
import rx.functions.Action1;
import rx.functions.Func1;
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
    ActionMode actionMode;
    boolean isNotActionMode;

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
                if (actionMode == null || isNotActionMode) {
                    File item = fileAdapter.getData().get(position);
                    if (item.isDirectory()) {
                        reloadRecyclerView(item);
                    } else {
                        Intent intent = VideoPlayActivity.startSelf(view.getContext(), item.getAbsolutePath(), item.getName());
                        startActivity(intent);
                    }
                } else {
                    if (view.isSelected()) {
                        view.setSelected(false);
                        fileAdapter.deleteSelectFile(fileAdapter.getData().get(position));
                    } else {
                        view.setSelected(true);
                        fileAdapter.addSelectFile(fileAdapter.getData().get(position));
                    }

                }

            }
        });
        fileAdapter.setOnItemLongClick(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClickListener(View view, int position) {
                if (actionMode != null) {
                    return false;
                }
                actionMode = mAct.startActionMode(mActionModeCallback);
                view.setSelected(true);
                fileAdapter.addSelectFile(fileAdapter.getData().get(position));
                return true;
            }
        });
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
            fileAdapter.clearSelelctList();
        }
    };

    private void deleteFiles() {
        new AlertDialog.Builder(mAct).setTitle("删除文件").setMessage("确认删除？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Observable.create(new Observable.OnSubscribe<ArrayList<File>>() {
                    @Override
                    public void call(Subscriber<? super ArrayList<File>> subscriber) {
                        subscriber.onStart();
                        subscriber.onNext(fileAdapter.getSelectFiles());
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
                                Toast.makeText(mAct, "删除文件失败", Toast.LENGTH_SHORT).show();
                                mActionModeCallback.onDestroyActionMode(null);
                            }

                            @Override
                            public void onNext(File file) {
                                deleteDir(file);
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
    protected void loadData() {
        rootPath = Environment.getExternalStorageDirectory().getParentFile().getAbsolutePath();
        refreshFile(null);
    }

    private void refreshFile(final File file) {
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
                        fileAdapter.clearSelelctList();
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
        if (fileArray != null) {
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

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete(); // The directory is empty now and can be deleted.
    }
}
