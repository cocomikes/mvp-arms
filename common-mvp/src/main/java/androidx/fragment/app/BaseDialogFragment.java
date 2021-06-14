/*
 * Copyright © 2019 iFund. All rights reserved.
 */

package androidx.fragment.app;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.ganguo.library.klog.KLog;
import io.ganguo.library.mvp.BaseContext;
import io.ganguo.library.mvp.BuildConfig;
import io.ganguo.library.mvp.R;

/**
 * 对话框 - 基类
 * <p>
 * Created by zhihui_chen on 14-9-9.
 */
public abstract class BaseDialogFragment extends DialogFragment {
    private static int screenWidth;

    @Override
    public void onStart() {
        super.onStart();

        //设置对话框显示在底部
        setLocation(Gravity.CENTER);
        //设置让对话框宽度充满屏幕
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window dialogWindow = getDialog().getWindow();
            setSize(getDisplayWidth(), dialogWindow.getAttributes().height);
        }
    }

    protected View.OnClickListener onClickListener;

    public void setOnButtonClickListener(View.OnClickListener listener) {
        this.onClickListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置窗口以对话框样式显示
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BaseDialogFragmentStyle);
        Window dialogWindow = getDialog().getWindow();
        if (dialogWindow != null) {
            //设置对话框背景色，否则有虚框
            dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置对话框弹出动画，默认从底部滑入，从底部滑出
            setAnim(R.style.DialogFromBottomAnim);
        }
        getDialog().setCancelable(true);
        getDialog().setCanceledOnTouchOutside(false);

        View dialogView = inflater.inflate(getLayoutResId(), null);
        initView(dialogView);
        initData();

        return dialogView;
    }


    protected int getDisplayWidth() {
        if (screenWidth == 0) {
            DisplayMetrics dm = BaseContext.getInstance().getResources().getDisplayMetrics();
            screenWidth = dm.widthPixels;
        }
        return screenWidth;
    }

    @Override
    public void dismiss() {
        super.dismissAllowingStateLoss();
    }

    /**
     * 对话框位置,  默认底部
     */
    public void setLocation(int gravity) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window dialogWindow = getDialog().getWindow();
            dialogWindow.setGravity(gravity);
        }
    }

    /**
     * 对话框大小
     */
    public void setSize(int width, int height) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window dialogWindow = getDialog().getWindow();
            dialogWindow.setLayout(width, height);
        }
    }

    public void setSizeWidth(int width) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window dialogWindow = getDialog().getWindow();
            setSize(width, dialogWindow.getAttributes().height);
        }
    }

    public void setAnim(int resId) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window dialogWindow = getDialog().getWindow();
            dialogWindow.getAttributes().windowAnimations = resId;
        }
    }

    /**
     * 加载layout xml
     */
    protected abstract int getLayoutResId();

    /**
     * 加载UI
     */
    protected abstract void initView(@NonNull View dialogView);

    /**
     * 加载网络数据
     */
    protected abstract void initData();
}
