package com.simaben.funnyvideo.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.simaben.autoswaprefresh.OnItemClickListener;
import com.simaben.funnyvideo.R;
import com.simaben.funnyvideo.bean.OnlineChannel;
import com.simaben.funnyvideo.bean.QiubaiVideo.ShowapiResBodyBean.PagebeanBean.ContentlistBean;
import com.simaben.funnyvideo.utils.FileUtil;
import com.simaben.funnyvideo.utils.Util;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class OnlineFragment extends BaseFragment {


    private OnlineRecyclerViewAdapter adapter = null;
    @Bind(R.id.onlineRecyclerView)
    public RecyclerView mRecyclerView;

    public OnlineFragment() {
    }

    // TODO: Customize parameter initialization
    public static OnlineFragment newInstance(Bundle args) {
        OnlineFragment fragment = new OnlineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_online_list;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this, decorView);
        initAdapter(null);
    }

    @Override
    protected void loadData() {
        Observable.create(new Observable.OnSubscribe<List<OnlineChannel>>() {
            @Override
            public void call(Subscriber<? super List<OnlineChannel>> subscriber) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(mAct.getAssets().open("channel.txt")));
                    String line = null;
                    List<OnlineChannel> channelList = new ArrayList<>();
                    while ((line = reader.readLine()) != null) {
                        String[] map = line.split(",");
                        if (map.length > 1) {
                            OnlineChannel channel = new OnlineChannel();
                            channel.setTitle(map[0].trim());
                            channel.setAddress(map[1].trim());
                            channelList.add(channel);
                        }
                    }
                    subscriber.onNext(channelList);

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).map(new Func1<List<OnlineChannel>, List<OnlineChannel>>() {
            @Override
            public List<OnlineChannel> call(List<OnlineChannel> onlineChannels) {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "tv.txt");
                if (file != null && file.exists() && onlineChannels != null) {
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new FileReader(file));
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            String[] map = line.split(",");
                            if (map.length > 1) {
                                OnlineChannel channel = new OnlineChannel();
                                channel.setTitle(map[0].trim());
                                channel.setAddress(map[1].trim());
                                onlineChannels.add(channel);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else{
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return onlineChannels;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<OnlineChannel>>() {
                    @Override
                    public void call(List<OnlineChannel> onlineChannels) {
                        if (onlineChannels != null && !onlineChannels.isEmpty()) {
                            adapter.setData(onlineChannels);
                        }
                    }
                });

    }

    private void initAdapter(List data) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        adapter = new OnlineRecyclerViewAdapter(getActivity(), data, false, layoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new ItemDecorationAlbumColumns(getResources().getDimensionPixelSize(R.dimen.photos_list_spacing), 1));
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                OnlineChannel item = adapter.getData().get(position);
                Intent intent = VideoPlayActivity.startSelf(view.getContext(), item.getAddress(), item.getTitle());
                startActivity(intent);
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.online_menu_layout, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_online_address:
                showInputDialog();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showInputDialog() {
        View view = mAct.getLayoutInflater().inflate(R.layout.address_add_layout, null);
        final EditText addressTV = (EditText) view.findViewById(R.id.dialog_address);
        final EditText nameTV = (EditText) view.findViewById(R.id.dialog_name);
        final AlertDialog dialog = new AlertDialog.Builder(mAct).setTitle("添加地址").setNegativeButton("扫描二维码", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //打开扫描界面扫描条形码或二维码
                Intent openCameraIntent = new Intent(mAct, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String address = addressTV.getText().toString().trim();
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(mAct, "地址不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    String name = nameTV.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        name = "自定义地址" + Util.dateStr2formatWithSenconds();
                    }
                    FileUtil.appendAddress(address, name);
                    Intent intent = VideoPlayActivity.startSelf(mAct, address, name);
                    startActivity(intent);
                }

            }
        }).setView(view).create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == mAct.RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            if (TextUtils.isEmpty(scanResult)) {
                Toast.makeText(mAct, "地址不能为空", Toast.LENGTH_SHORT).show();
            } else {
                String name = "自定义地址" + Util.dateStr2formatWithSenconds();
                FileUtil.appendAddress(scanResult, name);
                Intent intent = VideoPlayActivity.startSelf(mAct, scanResult, name);
                startActivity(intent);
            }
        }
    }
}
