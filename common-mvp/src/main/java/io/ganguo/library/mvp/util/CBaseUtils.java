package io.ganguo.library.mvp.util;

import android.app.ActivityManager;
import android.content.Context;

import io.ganguo.library.mvp.ui.mvp.base.App;
import io.ganguo.library.mvp.ui.mvp.di.component.AppComponent;

/**
 * Created by Mikes at 2019/4/30 9:33 AM
 * 一些框架常用的工具
 */
public class CBaseUtils {
    public static AppComponent obtainAppComponentFromContext(Context context) {
        Preconditions.checkNotNull(context, "%s cannot be null", Context.class.getName());
        Preconditions.checkState(context.getApplicationContext() instanceof App, "%s must be implements %s", context.getApplicationContext().getClass().getName(), App.class.getName());
        return ((App) context.getApplicationContext()).getAppComponent();
    }


    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager == null){
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public static boolean isMainProcess(Context context){
        return context.getApplicationInfo().packageName.equals(getCurProcessName(context));
    }
}
