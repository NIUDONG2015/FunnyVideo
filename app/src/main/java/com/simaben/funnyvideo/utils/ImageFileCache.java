package com.simaben.funnyvideo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.LruCache;


import com.android.volley.toolbox.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by taoyq on 16/2/29.
 */
public class ImageFileCache implements ImageLoader.ImageCache {
    private LruCache<String, Bitmap> mCache;
    private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 100;//缓存所需SD卡所剩的最小容量
    private static final String CHCHEDIR = "ImageChace";//缓存目录
    private static final int MB = 1024 * 1024;
    private static final String WHOLESALE_CONV = ".cache";//缓存文件后缀名

    public ImageFileCache() {
        //这个取单个应用最大使用内存的1/8
        int maxSize = (int) Runtime.getRuntime().maxMemory() / 8;
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //这个方法一定要重写，不然缓存没有效果
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        Bitmap bitmap = getImage(url);
        if (bitmap == null) {
            return mCache.get(url);
        } else {
            return bitmap;
        }
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        saveBitmap(bitmap, url);
    }


    /**
     * 从文件缓存中获取图片
     *
     * @param url
     * @return
     */
    public Bitmap getImage(final String url) {
        final String path = getDirectory() + "/" + convertUrlToFileName(url);
        File file = new File(path);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (bitmap == null) {
                file.delete();
                return null;
            } else {
                updateFileTime(path);//更新文件最新访问时间
                return bitmap;
            }
        } else {
            return null;
        }
    }

    /**
     * 修改文件的最后修改时间
     *
     * @param path
     */
    private void updateFileTime(String path) {
        File file = new File(path);
        long newModeifyTime = System.currentTimeMillis();
        file.setLastModified(newModeifyTime);
    }

    /**
     * 将url转成文件名
     *
     * @param url
     * @return
     */
    private String convertUrlToFileName(String url) {
        String[] strs = url.split("/");
        return strs[strs.length - 1] + WHOLESALE_CONV;
    }

    public void saveBitmap(Bitmap bitmap, String url) {
        if (bitmap == null) {
            return;
        }
        //判断SD卡上的空间
        if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
            return;
        }
        String fileName = convertUrlToFileName(url);
        String dir = getDirectory();
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File file = new File(dir + "/" + fileName);
        try {
            file.createNewFile();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            FileOutputStream os = new FileOutputStream(file);
            os.write(stream.toByteArray());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得缓存目录
     *
     * @return
     */
    private String getDirectory() {
        String dir = getSDPath() + "/" + CHCHEDIR;
        return dir;
    }

    private String getSDPath() {
        File sdDir = null;
        boolean adCardExit = Environment.getExternalStorageState()
                .endsWith(Environment.MEDIA_MOUNTED);//判断SD卡是否挂载
        if (adCardExit) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return "";
        }
    }

    /**
     * 计算sd卡上的剩余空间
     *
     * @return
     */
    private int freeSpaceOnSd() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdFreeMB = ((double) statFs.getAvailableBlocks() * (double) statFs.getBlockSize()) / MB;
        return (int) sdFreeMB;
    }


}
