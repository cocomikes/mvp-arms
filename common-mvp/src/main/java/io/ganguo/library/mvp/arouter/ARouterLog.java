package io.ganguo.library.mvp.arouter;

import com.alibaba.android.arouter.facade.template.ILogger;
import io.ganguo.library.mvp.BuildConfig;
import io.ganguo.library.klog.KLog;

/**
 * Created by Mikes at 2019/4/16
 */
public class ARouterLog implements ILogger {
    @Override
    public void showLog(boolean isShowLog) {

    }

    @Override
    public void showStackTrace(boolean isShowStackTrace) {

    }

    @Override
    public void debug(String tag, String message) {
        KLog.d(getDefaultTag(), message);
    }

    @Override
    public void info(String tag, String message) {
        KLog.i(getDefaultTag(), message);
    }

    @Override
    public void warning(String tag, String message) {
        KLog.w(getDefaultTag(), message);
    }

    @Override
    public void error(String tag, String message) {
        KLog.e(getDefaultTag(), message);
    }

    @Override
    public void monitor(String message) {
        KLog.i(getDefaultTag(),message);
    }

    @Override
    public boolean isMonitorMode() {
        return false;
    }

    @Override
    public String getDefaultTag() {
        return BuildConfig.CBASE_LOG_TAG + "_ARouter$$";
    }
}
