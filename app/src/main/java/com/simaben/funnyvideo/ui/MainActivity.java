package com.simaben.funnyvideo.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.simaben.funnyvideo.R;
import com.simaben.funnyvideo.bean.QiubaiVideo.ShowapiResBodyBean.PagebeanBean.ContentlistBean;
import com.simaben.funnyvideo.common.Constants;

public class MainActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener<ContentlistBean> {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replace(ItemFragment.newInstance(2));
    }

    @Override
    public void onListFragmentInteraction(ContentlistBean item) {
        Intent intent = new Intent(this, VideoPlayActivity.class);
        intent.putExtra(Constants.ARG_VIDEO_PATH, item.getVideo_uri());
        startActivity(intent);
    }

    public void replace(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, fragment)
                .commit();
    }
}
