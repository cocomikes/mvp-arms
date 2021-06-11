package io.ganguo.library.mvp.arouter;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import io.ganguo.library.mvp.BuildConfig;
import io.ganguo.library.mvp.R;
import io.ganguo.library.mvp.util.ActivityUtils;
import io.ganguo.library.klog.KLog;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * Created by Mikes at 2019/4/15
 * ARouter路由跳转工具类
 *
 * 如果navigation带参数Context，ARouter启动的新Activity时会在一个Task里
 * 否则ARouter将使用Application's context启动Activity并附加上Flag = Activity.NEW_TASK
 *
 * 本工具类封装时对path是String类型，默认加上Activity context
 * 对Uri类型不加context. 如果需要对Uri加Context，请直接调用ARouter API
 */
public class ARouterUtils {
    public static final int INVALID_FLAG = -1;

    /**
     * 在activity中添加
     *
     * @param activity activity
     */
    public static void injectActivity(FragmentActivity activity) {
        if (activity == null) {
            return;
        }
        ARouter.getInstance().inject(activity);
    }

    /**
     * 在fragment中添加
     *
     * @param fragment fragment
     */
    public static void injectFragment(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        ARouter.getInstance().inject(fragment);
    }

    /**
     * 销毁资源
     */
    public static void destroy() {
        KLog.i(BuildConfig.CBASE_LOG_TAG, "销毁路由资源");
        ARouter.getInstance().destroy();
    }

    /**
     * 简单的跳转页面
     *
     * @param path 目标界面对应的路径
     */
    public static void navigation(String path) {
        if (path == null) {
            return;
        }
        ARouter.getInstance()
                .build(path)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 简单的跳转页面
     *
     * @param path 目标界面对应的路径
     * @param flag Activity's flag
     */
    public static void navigation(String path, int flag) {
        if (path == null) {
            return;
        }
        ARouter.getInstance()
                .build(path)
                .withFlags(flag)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 携带参数跳转页面
     *
     * @param path   目标界面对应的路径
     * @param bundle 参数
     */
    public static void navigation(String path, @Nullable Bundle bundle) {
        if (path == null) {
            return;
        }
        ARouter.getInstance()
                .build(path)
                .with(bundle)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 简单的跳转页面
     *
     * @param path   目标界面对应的路径
     * @param flag   Activity's flag
     * @param bundle 参数
     */
    public static void navigation(String path, int flag, @Nullable Bundle bundle) {
        if (path == null) {
            return;
        }
        ARouter.getInstance()
                .build(path)
                .with(bundle)
                .withFlags(flag)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 简单的跳转页面
     * @param path      目标界面对应的路径
     * @param flag      Activity's flag
     * @param bundle    参数
     * @param enterAnim 进入时候动画
     * @param exitAnim  退出动画
     */
    public static void navigation(String path, int flag, @Nullable Bundle bundle, int enterAnim, int exitAnim) {
        if (path == null) {
            return;
        }
        Postcard postcard = ARouter.getInstance()
                .build(path);
        if (flag > INVALID_FLAG) {
            postcard.withFlags(flag);
        }
        postcard.with(bundle)
                .withTransition(enterAnim, exitAnim)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * @param path
     * @param flag   you can set INVALID_FLAG to ignore the method
     * @param bundle you can set null
     */
    public static void navigationWithDefaultAnim(String path, int flag, @Nullable Bundle bundle) {
        if (path == null) {
            return;
        }
        Postcard postcard = ARouter.getInstance()
                .build(path);
        if (flag > INVALID_FLAG) {
            postcard.withFlags(flag);
        }
        postcard.with(bundle)
                .withTransition(R.anim.cbase__slide_in_from_right, R.anim.cbase__slide_out_to_left)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 简单的跳转页面
     *
     * @param uri uri
     */
    public static void navigation(Uri uri) {
        if (uri == null) {
            return;
        }
        ARouter.getInstance()
                .build(uri)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 简单的跳转页面
     *
     * @param uri  目标界面对应的路径
     * @param flag Activity's flag
     */
    public static void navigation(Uri uri, int flag) {
        if (uri == null) {
            return;
        }
        ARouter.getInstance()
                .build(uri)
                .withFlags(flag)
                .navigation();
    }

    /**
     * 携带参数跳转页面
     *
     * @param uri    目标界面对应的路径
     * @param bundle 参数
     */
    public static void navigation(Uri uri, @Nullable Bundle bundle) {
        if (uri == null) {
            return;
        }
        ARouter.getInstance()
                .build(uri)
                .with(bundle)
                .navigation();
    }

    /**
     * 简单的跳转页面
     *
     * @param uri    目标界面对应的路径
     * @param flag   Activity's flag
     * @param bundle 参数
     */
    public static void navigation(Uri uri, int flag, @Nullable Bundle bundle) {
        if (uri == null) {
            return;
        }
        ARouter.getInstance()
                .build(uri)
                .with(bundle)
                .withFlags(flag)
                .navigation();
    }

    /**
     * 简单的跳转页面
     *
     * @param uri       目标界面对应的路径
     * @param flag      Activity's flag , INVALID_FLAG ignore it.
     * @param bundle    参数
     * @param enterAnim 进入时候动画
     * @param exitAnim  退出动画
     */
    public static void navigation(Uri uri, int flag, @Nullable Bundle bundle, int enterAnim, int exitAnim) {
        if (uri == null) {
            return;
        }
        Postcard postcard = ARouter.getInstance()
                .build(uri);
        if (flag > INVALID_FLAG) {
            postcard.withFlags(flag);
        }
        postcard.with(bundle)
                .withTransition(enterAnim, exitAnim)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * @param uri
     * @param flag   you can set INVALID_FLAG to ignore the method
     * @param bundle you can set null
     */
    public static void navigationWithDefaultAnim(Uri uri, int flag, @Nullable Bundle bundle) {
        if (uri == null) {
            return;
        }
        Postcard postcard = ARouter.getInstance()
                .build(uri);

        if (flag > INVALID_FLAG) {
            postcard.withFlags(flag);
        }
        postcard.with(bundle)
                .withTransition(R.anim.cbase__slide_in_from_right, R.anim.cbase__slide_out_to_left)
                .navigation(ActivityUtils.getTopActivity());
    }

    /**
     * 简单的跳转页面, 可以获取单次跳转的结果
     *
     * @param path     string目标界面对应的路径
     * @param callback 监听路由过程
     */
    public static void navigation(Context context, String path, NavigationCallback callback) {
        if (path == null) {
            return;
        }
        ARouter.getInstance()
                .build(path)
                .navigation(context, callback);
    }

    /**
     * 跨模块实现ForResult返回数据（activity中使用）,在fragment中使用不起作用
     * 携带参数跳转页面
     *
     * @param path     path目标界面对应的路径
     * @param bundle   bundle参数
     * @param callback can be null
     */
    public static void navigation(Activity activity, String path, @Nullable Bundle bundle, int requestCode, NavigationCallback callback) {
        if (path == null) {
            return;
        }
        ARouter.getInstance()
                .build(path)
                .with(bundle)
                .navigation(activity, requestCode, callback);
    }
}
