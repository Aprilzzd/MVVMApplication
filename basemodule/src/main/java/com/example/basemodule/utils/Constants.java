package com.example.basemodule.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by lw on 2018/1/19.
 */

public class Constants {

    /**
     * 每页数量
     */
    public static final int PAGE_SIZE = 10,PAGE_NUM=1;

    public static final int LOAD_SUCCESS=0,LOAD_FAIL=1;

    public final static String APK_NAME = "dangjian.apk";
    public final static String APP_NAME = "dangjian";
    public final static String APP_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()+File.separator +APP_NAME+ File.separator;
    public final static String HEAD_PATH =  APP_PATH
            + "head" + File.separator;
    public final static String IMAGE_PATH =  APP_PATH
            + "image" + File.separator;
    public final static String VIDEO_PATH =  APP_PATH
            + "video" + File.separator;

    //渠道
    public final static String CHANNEL="1";
    public final static String CHANNELNAME="应用宝";

    public final static String WXAPPID="wx40eb9ab3869be94d";
    public final static String QQAPPID="1106022716";
    public final static String WEIBOKEY="2375816580";
    public final static String WEIBO_S="49e30b0b0d6bd3617d6167e1d3162ff4";

    public final static String PASSRULE="\\w{6,12}";
    public final static String PHONERULE="[1]\\d{10}";

    //first
    public final static String FIRST_FRAGMENT="/first/FirstFragment";
    public final static String APT_FRAGMENT="/main/AptTestFragment";
}
