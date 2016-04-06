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
import android.os.Bundle;

import com.simaben.funnyvideo.R;
import com.simaben.funnyvideo.common.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoPlayActivity extends Activity {

    @Bind(R.id.surface_view)
    public VideoView mVideoView;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (!LibsChecker.checkVitamioLibs(this)) {
            finish();
            return;
        }
        setContentView(R.layout.videoview);
        ButterKnife.bind(this);

        String path = getIntent().getStringExtra(Constants.ARG_VIDEO_PATH);
        String name = getIntent().getStringExtra(Constants.ARG_VIDEO_NAME);
        path = "http://live.hkstv.hk.lxdns.com/live/hks/playlist.m3u8";
        mVideoView.setVideoPath(path);
        MediaController controller = new MediaController(this);
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


}
