package com.simaben.funnyvideo.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by simaben on 7/4/16.
 */
public class Util {
    public static final SimpleDateFormat df7 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 读取 显示度量
     */
    private static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 获取屏幕宽度
     */
    public final static int getWindowsWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public final static int getWindowsHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    public static String dateStr2formatWithSenconds() {
        try {
            Date date = new Date();
            return df7.format(date.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
