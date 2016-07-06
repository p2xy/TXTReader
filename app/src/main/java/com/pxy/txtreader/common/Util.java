package com.pxy.txtreader.common;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by pxy on 2016/3/23.
 */
public class Util {
    /**
     * 获取屏幕的宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取当前时间的字符串
     *
     * @return
     */
    public static String getCurrentTimeString() {
        Date date = new Date(System.currentTimeMillis());
        return new SimpleDateFormat("hh:mm").format(date);
    }


    /**
     * 获取两个数的百分比
     *
     * @param a 分母
     * @param b 分子
     * @return
     */
    public static String getPercentString(long a, long b) {
        if (a == 0) {
            return "0%";
        } else {
            float r = 100 * (((float) b) / ((float) a));
            return String.format("%.1f", r) + "%";
        }
    }

    /**
     * 文件大小转化成字符串
     * @param size
     * @return
     */
    public static String capacitySizeToString(long size) {
        if (size / 1024 > 1024) {
            return String.format("%.1fMB", (float)size / (1024 * 1024));
        } else if (size / 1024 > 0) {
            return String.format("%.1fKBb", (float)size / 1024);
        } else {
            return size + "B";
        }
    }
}
