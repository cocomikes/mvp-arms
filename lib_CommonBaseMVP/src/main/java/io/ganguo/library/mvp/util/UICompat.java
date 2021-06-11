package io.ganguo.library.mvp.util;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Mikes at 2020-01-18 16:45
 * Android 页面操作适配
 * 1. Android P 全屏页面水滴屏适配
 * 2. Android 导航栏隐藏
 */
public class UICompat {
    /**
     * Android P 全屏页面水滴屏适配
     */
    public static void norchScreenCompat(AppCompatActivity activity){
        Preconditions.checkNotNull(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            activity.getWindow().setAttributes(lp);
        }
    }

    /**
     * 导航栏隐藏
     */
    public static void hideNavigationBar(AppCompatActivity activity){
        Preconditions.checkNotNull(activity);
        //SYSTEM_UI_FLAG_FULLSCREEN 全屏时，状态栏被隐藏
        //SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 全屏时，布局不会占用状态栏
        //SYSTEM_UI_FLAG_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE 状态栏隐藏，再出现时，不会重新计算布局
        //SYSTEM_UI_FLAG_HIDE_NAVIGATION 导航栏不显示，布局延伸到导航栏
        //SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION 导航栏显示，布局延伸到导航栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        } else {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    public static void forceActivityNotFullScreen(AppCompatActivity activity){
        Preconditions.checkNotNull(activity);
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
        );
    }


}
