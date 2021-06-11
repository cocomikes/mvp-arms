package io.ganguo.library.mvp;

import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import io.ganguo.library.mvp.arouter.ARouterUtils;
import io.ganguo.library.mvp.ui.mvp.base.App;
import io.ganguo.library.mvp.ui.mvp.base.delegate.AppComponentDelegate;
import io.ganguo.library.mvp.ui.mvp.base.delegate.AppLifecycles;
import io.ganguo.library.mvp.ui.mvp.di.component.AppComponent;
import io.ganguo.library.mvp.util.Preconditions;
import io.ganguo.library.klog.KLog;

import androidx.annotation.NonNull;

/**
 * App上下文环境
 */
public class BaseContext extends Application implements App {
    private AppLifecycles mAppDelegate;

    protected static BaseContext instance = null;

    public BaseContext() {
        instance = this;
    }

    /**
     * 获取Application
     *
     * @return instance
     */
    public static <T extends BaseContext> T getInstance() {
        return (T) instance;
    }

    /**
     * 将 {@link io.ganguo.library.mvp.ui.mvp.di.component.AppComponent} 返回出去, 供其它地方使用,
     * {@link io.ganguo.library.mvp.ui.mvp.di.component.AppComponent} 接口中声明的方法所返回的实例,
     * 在 {@link #getAppComponent()} 拿到对象后都可以直接使用
     *
     * @see io.ganguo.library.mvp.util.CBaseUtils#obtainAppComponentFromContext(Context) 可直接获取 {@link io.ganguo.library.mvp.ui.mvp.di.component.AppComponent}
     * @return App
     */
    @NonNull
    @Override
    public AppComponent getAppComponent() {
        Preconditions.checkNotNull(mAppDelegate, "%s cannot be null", AppComponentDelegate.class.getName());
        Preconditions.checkState(mAppDelegate instanceof App, "%s must be implements %s", mAppDelegate.getClass().getName(), App.class.getName());
        return ((App) mAppDelegate).getAppComponent();
    }

    /**
     * 这里会在 {@link BaseContext#onCreate} 之前被调用,可以做一些较早的初始化
     * 常用于 MultiDex 以及插件化框架的初始化
     *
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        if (mAppDelegate == null)
            this.mAppDelegate = new AppComponentDelegate(base);
        this.mAppDelegate.attachBaseContext(base);
    }

    /**
     * 应用启动
     */
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (mAppDelegate != null)
            this.mAppDelegate.onCreate(this);
    }

    /**
     * 在模拟环境中程序终止时会被调用
     */
    @Override
    public void onTerminate() {
        KLog.i(BuildConfig.CBASE_LOG_TAG, "onTerminate.");
        super.onTerminate();
        ARouterUtils.destroy();

        if (mAppDelegate != null)
            this.mAppDelegate.onTerminate(this);
    }

    /**
     * 低内存的时候执行
     */
    @Override
    public void onLowMemory() {
        KLog.i(BuildConfig.CBASE_LOG_TAG, "onLowMemory.");
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    /**
     * HOME键退出应用程序
     * 程序在内存清理的时候执行
     */
    @Override
    public void onTrimMemory(int level) {
        KLog.i(BuildConfig.CBASE_LOG_TAG, "onTrimMemory");
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN){
            Glide.get(this).clearMemory();
        }
        Glide.get(this).trimMemory(level);
    }
}
