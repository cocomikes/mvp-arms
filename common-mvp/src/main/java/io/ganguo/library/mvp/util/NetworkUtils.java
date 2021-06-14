package io.ganguo.library.mvp.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * 网络工具类
 * Created by mikes on 14-8-19.
 */
public class NetworkUtils {
    /**
     * Open the settings of wireless.
     */
    public static void openWirelessSettings() {
        AppUtils.getApp().startActivity(
                new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        );
    }

    /**
     * 检查网络是否可用，包括GPRS和Wifi
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) AppUtils.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm == null) return false;

        NetworkInfo[] info = cm.getAllNetworkInfo();
        if(info != null){
            for(NetworkInfo anInfo : info){
                if(anInfo.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }

        return false;
    }
}
