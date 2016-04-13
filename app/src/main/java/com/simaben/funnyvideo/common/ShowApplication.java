package com.simaben.funnyvideo.common;

import android.app.Application;

/**
 * Created by simaben on 24/3/16.
 */
public class ShowApplication extends Application {
    public static ShowApplication application = null;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.mipmap.bili_default_image_tv) // resource or drawable
//                .showImageForEmptyUri(R.mipmap.bili_default_image_tv) // resource or drawable
//                .showImageOnFail(R.mipmap.bili_default_image_tv) // resource or drawable
//                .resetViewBeforeLoading(false)  // default
//                .delayBeforeLoading(1000)
//                .cacheInMemory(true) // default
//                .cacheOnDisk(true) // default
//                .imageScaleType(ImageScaleType.EXACTLY) // default
//                .bitmapConfig(Bitmap.Config.RGB_565) // default
//                .displayer(new SimpleBitmapDisplayer()) // default
//                .build();
//
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(options).build();
//        ImageLoader.getInstance().init(config);


        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

    }

    public static ShowApplication getApplication() {
        return application;
    }
}
