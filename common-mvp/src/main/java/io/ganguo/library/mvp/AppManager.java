package io.ganguo.library.mvp;

import android.app.Activity;
import android.util.Log;

import java.util.Stack;

import androidx.annotation.NonNull;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 */
public class AppManager {
    private static final String TAG = AppManager.class.getName();

    /**
     * Activity记录栈
     */
    private static Stack<Activity> activityStack = new Stack<Activity>();
    /**
     * AppManager单例
     */
    private static AppManager singleton = new AppManager();

    /**
     * 单例
     */
    private AppManager() {
    }

    /**
     * 获取AppManager单一实例
     */
    public static AppManager getInstance() {
        return singleton;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (activityStack.isEmpty()) {
            return null;
        }
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        if (activityStack.isEmpty()) {
            return;
        }
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定类名的Activity
     */
    public Activity getFirstActivity() {
        if (activityStack.isEmpty()) {
            return null;
        }
        return activityStack.firstElement();
    }

    /**
     * 结束指定类名的Activity
     */
    public Activity getActivity(Class<?> cls) {
        if (activityStack.isEmpty()) {
            return null;
        }
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }

    /**
     * 结束指定的Activity
     */
    public void removeActivity(Activity activity) {
        if (activityStack.isEmpty()) {
            return;
        }
        if (activity != null) {
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activityStack.isEmpty()) {
            return;
        }
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (activityStack.isEmpty()) {
            return;
        }
        Activity waitFinishActivity = null;
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                waitFinishActivity = activity;
            }
        }
        if(waitFinishActivity != null){
            finishActivity(waitFinishActivity);
        }
    }

    /**
     * 结束指定类名的Activity
     * 从栈底开始
     */
    public void revertFinishActivity(@NonNull Class<?> cls) {
        if (activityStack.isEmpty()) {
            return;
        }

        Activity waitFinishActivity = null;
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            if (cls.equals(activityStack.get(i).getClass())) {
                waitFinishActivity = activityStack.get(i);
            }
        }
        if(waitFinishActivity != null){
            finishActivity(waitFinishActivity);
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack.isEmpty()) {
            return;
        }
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit(BaseContext context, boolean killProcess) {
        try {
            finishAllActivity();
        } catch (Exception e) {
            Log.e(TAG, "退出应用失败", e);
        } finally {
            // 不要执行这个，好像对推送有影响
            if(killProcess){
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }
    }
}
