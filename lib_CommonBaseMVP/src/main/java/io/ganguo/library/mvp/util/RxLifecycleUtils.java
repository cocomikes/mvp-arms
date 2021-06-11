package io.ganguo.library.mvp.util;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

/**
 * Created by Mikes at 2019/4/30 10:17 AM
 */
public class RxLifecycleUtils {
    private RxLifecycleUtils() {
        throw new IllegalStateException("Can't instance the RxLifecycleUtils");
    }

    public static <T> AutoDisposeConverter<T> bindLifecycle(LifecycleOwner lifecycleOwner) {
        if(lifecycleOwner == null){
            throw new NullPointerException("lifecycleOwner == null");
        }
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner));
    }

    public static <T> AutoDisposeConverter<T> bindUntilEvent(LifecycleOwner lifecycleOwner, Lifecycle.Event untilEvent){
        if(lifecycleOwner == null){
            throw new NullPointerException("lifecycleOwner == null");
        }
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(lifecycleOwner, untilEvent));
    }
}
