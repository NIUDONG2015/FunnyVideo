/*
 * Copyright (C) 2013 yixia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simaben.funnyvideo.ui;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.golshadi.majid.core.DownloadManagerPro;
import com.golshadi.majid.report.listener.DownloadManagerListener;
import com.simaben.funnyvideo.R;
import com.simaben.funnyvideo.common.Constants;
import com.simaben.funnyvideo.utils.Util;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoPlayActivity extends Activity implements DownloadManagerListener {


    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder builder;
    private Intent intentReuslt;

    @Bind(R.id.surface_view)
    public VideoView mVideoView;
    String name = "";
    String path = "";
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (!LibsChecker.checkVitamioLibs(this)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_videoview);
        ButterKnife.bind(this);

        Uri uri = getIntent().getData();
        if (uri!=null){
            name = uri.getLastPathSegment();
            path = uri.toString();
        }else{
            path = getIntent().getStringExtra(Constants.ARG_VIDEO_PATH);
            name = getIntent().getStringExtra(Constants.ARG_VIDEO_NAME);
        }

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mVideoView.setVideoPath(path);
        MediaController controller = new MediaController(this);
        if (path.startsWith("http")) {
            controller.setDonwloadView(true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadManagerPro dm = new DownloadManagerPro(v.getContext());
                    dm.init("downloadManager/", 3, VideoPlayActivity.this);
                    int token = dm.addTask(name + ".mp4", path, false, false);
                    try {
                        dm.startDownload(token);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        controller.setFileName(name);
        mVideoView.setMediaController(controller);
        mVideoView.requestFocus();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });


    }

    public static Intent startSelf(Context ctx, String path, String name) {
        Intent intent = new Intent(ctx, VideoPlayActivity.class);
        intent.putExtra(Constants.ARG_VIDEO_PATH, path);
        intent.putExtra(Constants.ARG_VIDEO_NAME, name);
        return intent;
    }


    @Override
    public void OnDownloadStarted(long taskId) {
        builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(name)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setOngoing(true)
                .setContentText("开始下载...")
                .setContentTitle(name);
        Notification notification = builder.build();
        notification.vibrate = null;
        notification.sound = null;
        mNotificationManager.notify((int) taskId,notification );
    }

    @Override
    public void OnDownloadPaused(long taskId) {

    }

    @Override
    public void onDownloadProcess(long taskId, double percent, long downloadedLength) {
        Log.i("test", "taskId:" + taskId + "| percent:" + percent + "|downloadedLength:" + downloadedLength);
//        int c = (int) (downloadedLength * 100 / percent);
        builder.setProgress(100, (int) percent, false);
        builder.setContentText((int) percent + "%");
        Notification notification = builder.build();
        notification.vibrate = null;
        notification.sound = null;
        mNotificationManager.notify((int) taskId, notification);
    }

    @Override
    public void OnDownloadFinished(long taskId) {

    }

    @Override
    public void OnDownloadRebuildStart(long taskId) {

    }

    @Override
    public void OnDownloadRebuildFinished(long taskId) {

    }

    @Override
    public void OnDownloadCompleted(long taskId) {
        intentReuslt = new Intent();
        intentReuslt.setType("application/mp4");
        intentReuslt.setAction(Intent.ACTION_VIEW);
        intentReuslt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),name + ".mp4"));
        intentReuslt.setData(uri);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentReuslt, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentText("已下载至:" + uri.toString());
        builder.setContentIntent(pendingIntent);

        builder.setProgress(0, 0, false);
        Notification notification = builder.build();
        notification.vibrate = null;
        notification.sound = null;
        mNotificationManager.notify((int) taskId, notification);
    }

    @Override
    public void connectionLost(long taskId) {

    }
}
