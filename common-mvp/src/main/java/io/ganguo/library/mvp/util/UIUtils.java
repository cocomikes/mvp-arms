package io.ganguo.library.mvp.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import io.ganguo.library.mvp.BaseContext;

/**
 * Created by mikes on 2019/4/5.
 */
public class UIUtils {
    private static int screenWidth;
    private static int screenHeight;//屏幕高度，不包含虚拟按键
    private static int realScreenHeight;//屏幕高度，包含虚拟按键
    private static int screenMin; // 宽高中，小的一边
    private static int screenMax; // 宽高中，较大的值
    private static float density;
    private static float scaleDensity;
    private static float xdpi;
    private static float ydpi;
    private static int densityDpi;
    private static int statusbarheight;
    private static int navbarheight;

    private UIUtils(){
        GetInfo(getContext());
    }

    /**
     * 得到上下文
     * 使用Application Context在多语言APP中会引起语言混乱问题
     * 可以注入Activity Context解决此问题
     * @return
     */
    public static Context getContext() {
        return BaseContext.getInstance();
    }

    /**
     * 得到resources对象
     *
     * @return
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 得到应用程序的包名
     *
     * @return
     */
    public static String getPackageName() {
        return getContext().getPackageName();
    }

    public static int getDisplayWidth() {
        if (screenWidth == 0) {
            GetInfo(getContext());
        }
        return screenWidth;
    }

    public static int getDisplayHeight() {
        if (screenHeight == 0) {
            GetInfo(getContext());
        }
        return screenHeight;
    }

    public static int getRealScreenHeight(){
        if(realScreenHeight == 0){
            GetInfo(getContext());
        }
        return realScreenHeight;
    }

    public static float getDensity(){
        if(density == 0){
            GetInfo(getContext());
        }
        return density;
    }

    public static float getDensityDpi(){
        if(densityDpi == 0){
            GetInfo(getContext());
        }
        return densityDpi;
    }

    public static int getStatusBarHeight(){
        if(statusbarheight == 0){
            GetInfo(getContext());
        }
        return statusbarheight;
    }

    public static int getNavBarHeight(){
        if(navbarheight == 0){
            GetInfo(getContext());
        }
        return navbarheight;
    }

    /**
     * 得到string.xml中的字符串
     *
     * @param resId
     * @return
     */
    public static String getString(int resId)

    {
        return getResources().getString(resId);
    }

    /**
     * 得到string.xml中的字符串，带点位符
     *
     * @return
     */
    public static String getString(int id, Object... formatArgs) {
        return getResources().getString(id, formatArgs);
    }

    /**
     * 得到string.xml中和字符串数组
     *
     * @return
     */
    public static String[] getStringArr(int resId)

    {
        return getResources().getStringArray(resId);
    }

    /**
     * 得到colors.xml中的颜色
     *
     * @param colorId
     * @return
     */
    public static int getColor(int colorId)

    {
        return getResources().getColor(colorId);
    }

    /**
     * dip--px
     */
    public static int dip2Px(int dip)

    {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);
    }

    /**
     * px--dip
     */
    public static int px2dip(int px) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f);
    }

    /**
     * sp--px
     */
    public static int sp2px(int sp)

    {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics()) + 0.5f);
    }

    /**
     * px--dip
     */
    public static int px2sp(int px)

    {

        float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / fontScale + 0.5f);
    }

    private static void GetInfo(Context context) {
        if (null == context) {
            return;
        }
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        if (screenWidth > screenHeight) screenMin = screenHeight;
        else screenMin = screenWidth;
        if (screenWidth < screenHeight) screenMax = screenHeight;
        else screenMax = screenWidth;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 可能有虚拟按键的情况
            display.getRealSize(outPoint);
        } else {
            // 不可能有虚拟按键
            display.getSize(outPoint);
        }
        realScreenHeight = outPoint.y; //手机屏幕真实高度，包含虚拟按键

        density = dm.density;
        scaleDensity = dm.scaledDensity;
        xdpi = dm.xdpi;
        ydpi = dm.ydpi;
        densityDpi = dm.densityDpi;
        statusbarheight = StatusBarUtils.getStatusHeight(context);
        navbarheight = getNavBarHeight(context);
    }

    public static int getNavBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        } else return 0;
    }

    public static int getSystemActionBarHeight(Context context) {
        TypedArray actionbarSizeTypedArray = context.obtainStyledAttributes(new int[] {
                android.R.attr.actionBarSize
        });
        return actionbarSizeTypedArray.getDimensionPixelSize(0, 0);
    }
}
