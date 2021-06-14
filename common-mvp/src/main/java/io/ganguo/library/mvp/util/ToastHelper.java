package io.ganguo.library.mvp.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.dovar.dtoast.DToast;
import io.ganguo.library.mvp.R;

/**
 * Created by mikes on 2019/4/5.
 */
public class ToastHelper {
    public static void toastMessage(int resId) {
        toastMessage(resId, Gravity.CENTER, DToast.DURATION_SHORT);
    }

    public static void toastMessage(int resId, int gravity) {
        toastMessage(resId, gravity, DToast.DURATION_SHORT);
    }

    public static void toastMessage(int resId, int gravity, @DToast.Duration int time) {
        Context context = AppUtils.getTopActivityOrApp();
        toastMessage(context.getResources().getText(resId), gravity, time);
    }

    @SuppressLint("ShowToast")
    public static void toastMessage(CharSequence msg) {
        toastMessage(msg, Gravity.CENTER, DToast.DURATION_SHORT);
    }

    @SuppressLint("ShowToast")
    public static void toastMessage(CharSequence msg, int gravity) {
        toastMessage(msg, gravity, DToast.DURATION_SHORT);
    }

    @SuppressLint("ShowToast")
    public static void toastMessage(CharSequence msg, int gravity, @DToast.Duration int time) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }

        int yOffset;
        switch (gravity){
            case Gravity.CENTER:
                yOffset = 0;
                break;
            case Gravity.BOTTOM:
                yOffset = UIUtils.dip2Px(50);
                break;
            case Gravity.TOP:
                yOffset = UIUtils.dip2Px(50) * -1;
                break;
            default:
                yOffset = 0;
                break;
        }

        try {
            Context context = AppUtils.getTopActivityOrApp();
            DToast.make(context)
                    .setView(View.inflate(context, R.layout.cbase__view_custom_toast, null))
                    .setText(R.id.tv_content_custom, msg.toString())
                    .setDuration(time)
                    .setGravity(gravity, 0, yOffset)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出APP时调用
     */
    public static void cancelAllToast() {
        DToast.cancel();
    }
}
